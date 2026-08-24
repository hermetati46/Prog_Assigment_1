package com.progassigment1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WardManager {
    public static final int ROWS = 4;
    public static final int COLS = 5;
    public static final int TOTAL_BEDS = ROWS * COLS;
    public static final String DEFAULT_WARD = "Ward 1";

    private final List<Patient> patientList = new ArrayList<>();
    private final String[][] bedLayout = new String[ROWS][COLS];
    private final String[][] bedOccupancy = new String[ROWS][COLS];

    public WardManager() {
        initializeBeds();
    }

    private void initializeBeds() {
        int count = 1;
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                bedLayout[r][c] = String.format("B%02d", count++);
                bedOccupancy[r][c] = null;
            }
        }
    }

    // Feature 1

    public void registerPatient(Patient patient) {
        if (searchPatient(patient.getPatientId()) != null) {
            throw new IllegalArgumentException("Error: Patient ID '" + patient.getPatientId() + "' already exists.");
        }
        patientList.add(patient);
    }

    public Patient searchPatient(String patientId) {
        for (Patient p : patientList) {
            if (p.getPatientId().equalsIgnoreCase(patientId)) {
                return p;
            }
        }
        return null;
    }

    public boolean updatePatientDetails(String patientId, String newFirstName, String newLastName,
                                        int newAge, String newGender, String newCondition) {
        Patient p = searchPatient(patientId);
        if (p == null) {
            return false;
        }
        p.setFirstName(newFirstName);
        p.setLastName(newLastName);
        p.setAge(newAge);
        p.setGender(newGender);
        p.setMedicalCondition(newCondition);
        return true;
    }

    public boolean deletePatient(String patientId) {
        Patient p = searchPatient(patientId);
        if (p == null) {
            return false;
        }

        // If patient is in a bed, release bed first
        if (p instanceof Inpatient) {
            Inpatient inp = (Inpatient) p;
            if (inp.getBedNumber() != null) {
                releaseBed(inp.getBedNumber());
            }
        }

        patientList.remove(p);
        return true;
    }

    public List<Patient> getAllPatients() {
        return new ArrayList<>(patientList);
    }

    // Feature 2

    public boolean allocateBed(String patientId, String bedCode) {
        Patient p = searchPatient(patientId);
        if (p == null) {
            throw new IllegalArgumentException("Error: Patient ID '" + patientId + "' not found.");
        }

        if (p.getCategory() != PatientCategory.INPATIENT) {
            throw new IllegalStateException("Error: Only INPATIENT category patients can be allocated a bed.");
        }

        if (getOccupiedBedsCount() >= TOTAL_BEDS) {
            throw new IllegalStateException("Error: All beds in the ward are currently occupied.");
        }

        // Validate Bed Code position
        int[] pos = findBedPosition(bedCode);
        if (pos == null) {
            throw new IllegalArgumentException("Error: Bed code '" + bedCode + "' does not exist.");
        }

        int r = pos[0];
        int c = pos[1];

        if (bedOccupancy[r][c] != null) {
            throw new IllegalStateException("Error: Bed " + bedCode.toUpperCase() + " is already occupied by Patient " + bedOccupancy[r][c] + ".");
        }

        // Check if patient is already assigned a bed
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (patientId.equalsIgnoreCase(bedOccupancy[row][col])) {
                    throw new IllegalStateException("Error: Patient " + patientId + " is already assigned to bed " + bedLayout[row][col] + ".");
                }
            }
        }

        bedOccupancy[r][c] = p.getPatientId();

        // Upgrade Patient to Inpatient object if necessary ; update bed details
        if (!(p instanceof Inpatient)) {
            int index = patientList.indexOf(p);
            Inpatient inpatient = new Inpatient(p.getPatientId(), p.getFirstName(), p.getLastName(),
                    p.getAge(), p.getGender(), p.getMedicalCondition(), DEFAULT_WARD, bedCode.toUpperCase());
            patientList.set(index, inpatient);
        } else {
            Inpatient inp = (Inpatient) p;
            inp.setWardNumber(DEFAULT_WARD);
            inp.setBedNumber(bedCode.toUpperCase());
        }

        return true;
    }

    public boolean releaseBed(String bedCode) {
        int[] pos = findBedPosition(bedCode);
        if (pos == null) {
            throw new IllegalArgumentException("Error: Bed code '" + bedCode + "' does not exist.");
        }

        int r = pos[0];
        int c = pos[1];

        String patientId = bedOccupancy[r][c];
        if (patientId == null) {
            throw new IllegalStateException("Error: Bed " + bedCode.toUpperCase() + " is already vacant.");
        }

        // Free bed in array
        bedOccupancy[r][c] = null;

        // Reset inpatient bed info
        Patient p = searchPatient(patientId);
        if (p instanceof Inpatient) {
            Inpatient inp = (Inpatient) p;
            inp.setBedNumber("Unassigned");
        }

        return true;
    }

    public void displayWardLayout() {
        System.out.println("\n------------------------------------------------------------");
        System.out.println("                 Ward 1 - Bed Layout (4 x 5)               ");
        System.out.println("------------------------------------------------------------");
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                String bed = bedLayout[r][c];
                String status = (bedOccupancy[r][c] == null) ? "[FREE]" : "[" + bedOccupancy[r][c] + "]";
                System.out.printf("%-5s %-10s\t", bed, status);
            }
            System.out.println();
        }
        System.out.println("---------------------------------------------------------------------------------------------\n");
    }

    public List<String> getAvailableBeds() {
        List<String> available = new ArrayList<>();
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (bedOccupancy[r][c] == null) {
                    available.add(bedLayout[r][c]);
                }
            }
        }
        return available;
    }

    public List<String> getOccupiedBeds() {
        List<String> occupied = new ArrayList<>();
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (bedOccupancy[r][c] != null) {
                    occupied.add(bedLayout[r][c] + " (" + bedOccupancy[r][c] + ")");
                }
            }
        }
        return occupied;
    }

    public int getOccupiedBedsCount() {
        return getOccupiedBeds().size();
    }

    public int getAvailableBedsCount() {
        return getAvailableBeds().size();
    }

    public double getOccupancyPercentage() {
        return ((double) getOccupiedBedsCount() / TOTAL_BEDS) * 100.0;
    }

    // Feature 3

    public List<Patient> getPatientsSortedByLastName() {
        List<Patient> sorted = new ArrayList<>(patientList);
        sorted.sort(Comparator.comparing(Patient::getLastName, String.CASE_INSENSITIVE_ORDER));
        return sorted;
    }

    public List<Patient> getPatientsSortedById() {
        List<Patient> sorted = new ArrayList<>(patientList);
        sorted.sort(Comparator.comparing(Patient::getPatientId, String.CASE_INSENSITIVE_ORDER));
        return sorted;
    }

    private int[] findBedPosition(String bedCode) {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (bedLayout[r][c].equalsIgnoreCase(bedCode)) {
                    return new int[]{r, c};
                }
            }
        }
        return null;
    }
}