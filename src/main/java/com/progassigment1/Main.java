package com.progassigment1;

import java.util.List;
import java.util.Scanner;

public class Main {
    private static final WardManager wardManager = new WardManager();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        seedSampleData();

        boolean running = true;
        while (running) {
            displayMainMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> patientManagementMenu();
                case "2" -> bedManagementMenu();
                case "3" -> reportsMenu();
                case "4" -> {
                    System.out.println("Exiting Medicare Hospital Admission System. Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid option! Please enter a number from 1 to 4.");
            }
        }
    }

    private static void displayMainMenu() {
        System.out.println("\n--------------------------------------------------");
        System.out.println("      MEDICARE HOSPITAL PATIENT ADMISSION SYSTEM  ");
        System.out.println("--------------------------------------------------");
        System.out.println("1. Patient Management");
        System.out.println("2. Bed Management");
        System.out.println("3. Reports & Analytics");
        System.out.println("4. Exit System");
        System.out.print("Select an option (1-4): ");
    }

    // Patient Menu
    private static void patientManagementMenu() {
        System.out.println("\n### Patient Management ###");
        System.out.println("1. Register New Patient");
        System.out.println("2. Search Patient by ID");
        System.out.println("3. Update Patient Details");
        System.out.println("4. Delete Patient Record");
        System.out.println("5. Display All Registered Patients");
        System.out.print("Select an option (1-5): ");

        String choice = scanner.nextLine().trim();
        try {
            switch (choice) {
                case "1" -> registerPatientUI();
                case "2" -> searchPatientUI();
                case "3" -> updatePatientUI();
                case "4" -> deletePatientUI();
                case "5" -> displayPatients(wardManager.getAllPatients(), "ALL REGISTERED PATIENTS");
                default -> System.out.println("Invalid selection!");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void registerPatientUI() {
        System.out.print("Enter Patient ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("Enter First Name: ");
        String firstName = scanner.nextLine().trim();
        System.out.print("Enter Last Name: ");
        String lastName = scanner.nextLine().trim();

        System.out.print("Enter Age: ");
        int age = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Enter Gender (Male/Female/Other): ");
        String gender = scanner.nextLine().trim();

        System.out.print("Enter Medical Condition: ");
        String condition = scanner.nextLine().trim();

        System.out.println("Select Category: 1. INPATIENT  2. OUTPATIENT  3. EMERGENCY");
        System.out.print("Choice (1-3): ");
        int catChoice = Integer.parseInt(scanner.nextLine().trim());

        PatientCategory category = switch (catChoice) {
            case 1 -> PatientCategory.INPATIENT;
            case 2 -> PatientCategory.OUTPATIENT;
            case 3 -> PatientCategory.EMERGENCY;
            default -> throw new IllegalArgumentException("Invalid category choice!");
        };

        Patient patient;
        if (category == PatientCategory.INPATIENT) {
            patient = new Inpatient(id, firstName, lastName, age, gender, condition, WardManager.DEFAULT_WARD, "Unassigned");
        } else {
            patient = new Patient(id, firstName, lastName, age, gender, condition, category);
        }

        wardManager.registerPatient(patient);
        System.out.println("SUCCESS: Patient registered successfully!");
    }

    private static void searchPatientUI() {
        System.out.print("Enter Patient ID to search: ");
        String id = scanner.nextLine().trim();
        Patient p = wardManager.searchPatient(id);
        if (p != null) {
            System.out.println("\nPatient Found:");
            System.out.println(p.displayDetails());
        } else {
            System.out.println("No patient found with ID: " + id);
        }
    }

    private static void updatePatientUI() {
        System.out.print("Enter Patient ID to update: ");
        String id = scanner.nextLine().trim();
        Patient existing = wardManager.searchPatient(id);
        if (existing == null) {
            System.out.println("Patient not found!");
            return;
        }

        System.out.print("Enter New First Name (" + existing.getFirstName() + "): ");
        String fn = scanner.nextLine().trim();
        System.out.print("Enter New Last Name (" + existing.getLastName() + "): ");
        String ln = scanner.nextLine().trim();
        System.out.print("Enter New Age (" + existing.getAge() + "): ");
        int age = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Enter New Gender (" + existing.getGender() + "): ");
        String gender = scanner.nextLine().trim();
        System.out.print("Enter New Medical Condition (" + existing.getMedicalCondition() + "): ");
        String cond = scanner.nextLine().trim();

        if (wardManager.updatePatientDetails(id, fn, ln, age, gender, cond)) {
            System.out.println("SUCCESS: Patient updated successfully!");
        }
    }

    private static void deletePatientUI() {
        System.out.print("Enter Patient ID to delete: ");
        String id = scanner.nextLine().trim();
        if (wardManager.deletePatient(id)) {
            System.out.println("SUCCESS: Patient record deleted!");
        } else {
            System.out.println("Patient ID not found!");
        }
    }

    // Bed Menu
    private static void bedManagementMenu() {
        System.out.println("\n### Bed Management ###");
        System.out.println("1. Allocate Bed to Inpatient");
        System.out.println("2. Release Bed");
        System.out.println("3. Display Ward Bed Layout");
        System.out.println("4. View Available Beds");
        System.out.println("5. View Occupied Beds");
        System.out.print("Select an option (1-5): ");

        String choice = scanner.nextLine().trim();
        try {
            switch (choice) {
                case "1" -> {
                    System.out.print("Enter Inpatient ID: ");
                    String id = scanner.nextLine().trim();
                    System.out.print("Enter Bed Code to allocate (e.g., B01): ");
                    String bed = scanner.nextLine().trim();
                    wardManager.allocateBed(id, bed);
                    System.out.println("SUCCESS: Bed " + bed.toUpperCase() + " allocated to Patient " + id);
                }
                case "2" -> {
                    System.out.print("Enter Bed Code to release (e.g., B01): ");
                    String bed = scanner.nextLine().trim();
                    wardManager.releaseBed(bed);
                    System.out.println("SUCCESS: Bed " + bed.toUpperCase() + " is now released.");
                }
                case "3" -> wardManager.displayWardLayout();
                case "4" -> System.out.println("Available Beds: " + wardManager.getAvailableBeds());
                case "5" -> System.out.println("Occupied Beds: " + wardManager.getOccupiedBeds());
                default -> System.out.println("Invalid selection!");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // REPORTS MENU
    private static void reportsMenu() {
        System.out.println("\n### Reports & Analytics ###");
        System.out.println("1. Full Patient List Report");
        System.out.println("2. Ward Bed Occupancy Summary");
        System.out.println("3. Display Patients Sorted by Surname");
        System.out.println("4. Display Patients Sorted by ID");
        System.out.print("Select an option (1-4): ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> displayPatients(wardManager.getAllPatients(), "ALL REGISTERED PATIENTS REPORT");
            case "2" -> displayBedOccupancyReport();
            case "3" -> displayPatients(wardManager.getPatientsSortedByLastName(), "PATIENTS SORTED BY SURNAME");
            case "4" -> displayPatients(wardManager.getPatientsSortedById(), "PATIENTS SORTED BY PATIENT ID");
            default -> System.out.println("Invalid selection!");
        }
    }

    private static void displayPatients(List<Patient> patients, String title) {
        System.out.println("\n--------------------------------------------------");
        System.out.println(" " + title);
        System.out.println("--------------------------------------------------");
        if (patients.isEmpty()) {
            System.out.println("No patient records found.");
        } else {
            for (Patient p : patients) {
                System.out.println(p.displayDetails());
            }
        }
        System.out.println("Total Patients Listed: " + patients.size());
        System.out.println("------------------------------------------------------------------------------------------\n");
    }

    private static void displayBedOccupancyReport() {
        System.out.println("\n--------------------------------------------------");
        System.out.println("             BED OCCUPANCY REPORT                 ");
        System.out.println("--------------------------------------------------");
        System.out.println("Total Registered Patients : " + wardManager.getAllPatients().size());
        System.out.println("Total Ward Beds Capacity  : " + WardManager.TOTAL_BEDS);
        System.out.println("Occupied Beds Count       : " + wardManager.getOccupiedBedsCount());
        System.out.println("Available Beds Count      : " + wardManager.getAvailableBedsCount());
        System.out.printf("Ward Occupancy Percentage : %.2f%%\n", wardManager.getOccupancyPercentage());
        System.out.println("Available Beds List       : " + wardManager.getAvailableBeds());
        System.out.println("Occupied Beds Details     : " + wardManager.getOccupiedBeds());
        System.out.println("--------------------------------------------------\n");
    }

    private static void seedSampleData() {
        // Sample test data for initial system setup
        Inpatient p1 = new Inpatient("P101", "John", "Smith", 45, "Male", "Pneumonia", "Ward 1", "B01");
        Patient p2 = new Patient("P102", "Alice", "Brown", 29, "Female", "Migraine", PatientCategory.OUTPATIENT);
        Inpatient p3 = new Inpatient("P103", "David", "Adams", 62, "Male", "Cardiac Monitoring", "Ward 1", "B02");
        Patient p4 = new Patient("P104", "Emma", "Clark", 19, "Female", "Fracture", PatientCategory.EMERGENCY);

        wardManager.registerPatient(p1);
        wardManager.registerPatient(p2);
        wardManager.registerPatient(p3);
        wardManager.registerPatient(p4);

        wardManager.allocateBed("P101", "B01");
        wardManager.allocateBed("P103", "B02");
    }
}
