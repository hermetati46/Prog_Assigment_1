package com.progassigment1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HospitalSystemTest {

    private WardManager wardManager;

    @BeforeEach
    public void setUp() {
        wardManager = new WardManager();
    }

    // Crud Operation

    @Test
    @DisplayName("Test Register Patient Successfully")
    public void testRegisterPatient() {
        Patient p = new Patient("P001", "Jane", "Doe", 30, "Female", "Fever", PatientCategory.OUTPATIENT);
        wardManager.registerPatient(p);

        Patient found = wardManager.searchPatient("P001");
        assertNotNull(found, "Registered patient should be found in system.");
        assertEquals("Jane", found.getFirstName());
        assertEquals("Doe", found.getLastName());
    }

    @Test
    @DisplayName("Test Search Patient")
    public void testSearchPatient() {
        Patient p = new Patient("P002", "Mark", "Taylor", 50, "Male", "Hypertension", PatientCategory.OUTPATIENT);
        wardManager.registerPatient(p);

        Patient found = wardManager.searchPatient("P002");
        assertNotNull(found);
        assertEquals("P002", found.getPatientId());

        Patient notFound = wardManager.searchPatient("P999");
        assertNull(notFound, "Searching non-existent patient should return null.");
    }

    @Test
    @DisplayName("Test Update Patient Details")
    public void testUpdatePatientDetails() {
        Patient p = new Patient("P003", "Sarah", "Conner", 35, "Female", "Asthma", PatientCategory.OUTPATIENT);
        wardManager.registerPatient(p);

        boolean updated = wardManager.updatePatientDetails("P003", "Sarah", "Conner-Smith", 36, "Female", "Asthma Controlled");
        assertTrue(updated, "Update method should return true on success.");

        Patient updatedPatient = wardManager.searchPatient("P003");
        assertEquals("Conner-Smith", updatedPatient.getLastName());
        assertEquals(36, updatedPatient.getAge());
        assertEquals("Asthma Controlled", updatedPatient.getMedicalCondition());
    }

    @Test
    @DisplayName("Test Delete Patient Record")
    public void testDeletePatient() {
        Patient p = new Patient("P004", "Bob", "Marley", 40, "Male", "Checkup", PatientCategory.OUTPATIENT);
        wardManager.registerPatient(p);

        boolean deleted = wardManager.deletePatient("P004");
        assertTrue(deleted, "Delete should return true when record is removed.");
        assertNull(wardManager.searchPatient("P004"), "Deleted patient should no longer exist.");
    }

    // Bed Management

    @Test
    @DisplayName("Test Bed Allocation to Inpatient")
    public void testAllocateBed() {
        Inpatient inp = new Inpatient("P005", "Charlie", "Brown", 25, "Male", "Observation", "Ward 1", "Unassigned");
        wardManager.registerPatient(inp);

        boolean allocated = wardManager.allocateBed("P005", "B01");
        assertTrue(allocated, "Bed B01 should be successfully allocated.");
        assertTrue(wardManager.getOccupiedBeds().stream().anyMatch(b -> b.contains("B01")));
    }

    @Test
    @DisplayName("Test Release Allocated Bed")
    public void testReleaseBed() {
        Inpatient inp = new Inpatient("P006", "Diana", "Prince", 28, "Female", "Recovery", "Ward 1", "Unassigned");
        wardManager.registerPatient(inp);
        wardManager.allocateBed("P006", "B02");

        boolean released = wardManager.releaseBed("B02");
        assertTrue(released, "Bed B02 should be successfully released.");
        assertTrue(wardManager.getAvailableBeds().contains("B02"));
    }

    // Validation and Boundary

    @Test
    @DisplayName("Test Prevent Duplicate Patient IDs")
    public void testPreventDuplicatePatientId() {
        Patient p1 = new Patient("P100", "Unique", "One", 20, "Male", "None", PatientCategory.OUTPATIENT);
        wardManager.registerPatient(p1);

        Patient p2 = new Patient("P100", "Duplicate", "Two", 22, "Female", "None", PatientCategory.OUTPATIENT);
        assertThrows(IllegalArgumentException.class, () -> wardManager.registerPatient(p2),
                "Registering a duplicate Patient ID should throw an exception.");
    }

    @Test
    @DisplayName("Test Prevent Allocating Already Occupied Bed")
    public void testPreventAllocatingOccupiedBed() {
        Inpatient p1 = new Inpatient("P010", "User1", "Test", 30, "Male", "Cond1", "Ward 1", "Unassigned");
        Inpatient p2 = new Inpatient("P011", "User2", "Test", 31, "Female", "Cond2", "Ward 1", "Unassigned");
        wardManager.registerPatient(p1);
        wardManager.registerPatient(p2);

        wardManager.allocateBed("P010", "B05");

        assertThrows(IllegalStateException.class, () -> wardManager.allocateBed("P011", "B05"),
                "Allocating an occupied bed should throw an IllegalStateException.");
    }

    @Test
    @DisplayName("Test Prevent Bed Allocation When Ward Full")
    public void testPreventBedAllocationWhenWardFull() {
        // Fill all 20 beds B01 to B20
        for (int i = 1; i <= 20; i++) {
            String id = String.format("P%03d", i);
            String bed = String.format("B%02d", i);
            Inpatient inp = new Inpatient(id, "Patient", "Num" + i, 30, "Male", "Routine", "Ward 1", "Unassigned");
            wardManager.registerPatient(inp);
            wardManager.allocateBed(id, bed);
        }

        assertEquals(20, wardManager.getOccupiedBedsCount());
        assertEquals(100.0, wardManager.getOccupancyPercentage(), 0.01);

        // Attempt to register 21st inpatient and allocate bed
        Inpatient extra = new Inpatient("P021", "Extra", "Patient", 40, "Female", "Emergency", "Ward 1", "Unassigned");
        wardManager.registerPatient(extra);

        assertThrows(IllegalStateException.class, () -> wardManager.allocateBed("P021", "B01"),
                "Allocating a bed when all 20 beds are full must throw an exception.");
    }

    @Test
    @DisplayName("Test Sorting Patients by Surname and Patient ID")
    public void testSortPatients() {
        Patient p1 = new Patient("P003", "Zack", "Zulu", 25, "Male", "Cold", PatientCategory.OUTPATIENT);
        Patient p2 = new Patient("P001", "Adam", "Apple", 30, "Male", "Fever", PatientCategory.OUTPATIENT);
        Patient p3 = new Patient("P002", "Beth", "Baker", 22, "Female", "Cough", PatientCategory.OUTPATIENT);

        wardManager.registerPatient(p1);
        wardManager.registerPatient(p2);
        wardManager.registerPatient(p3);

        // Sort by Surname
        List<Patient> sortedByName = wardManager.getPatientsSortedByLastName();
        assertEquals("Apple", sortedByName.get(0).getLastName());
        assertEquals("Baker", sortedByName.get(1).getLastName());
        assertEquals("Zulu", sortedByName.get(2).getLastName());

        // Sort by ID
        List<Patient> sortedById = wardManager.getPatientsSortedById();
        assertEquals("P001", sortedById.get(0).getPatientId());
        assertEquals("P002", sortedById.get(1).getPatientId());
        assertEquals("P003", sortedById.get(2).getPatientId());
    }
}