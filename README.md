# Student Information
Name: Hermenegildo Zuzi Tati

Student Number: ST10494935

Course: DISD0601

PROG6112: Assigment 1

Github Repository: https://github.com/hermetati46/Prog_Assigment_1

# Project Features
**Patient Management:**
- Register Patient: Capture base patient details (Patient ID, First Name, Last Name, Age, Gender, Medical Condition, and Category).

- Search Patient: Look up registered patients by their unique Patient ID.

- Update Patient: Modify existing patient details dynamically.

- Delete Patient: Delete patient records (automatically releases any assigned bed if the patient is an inpatient).

- Display Patients: List all active patient records formatted.

**Bed Management:**
- Bed Layout: Manages a single hospital ward with 20 beds arranged in a 4 x 5 matrix (B01 through B20).

- Bed Allocation: Assign beds exclusively to patients under the INPATIENT category.

- Bed Release: Release occupied beds upon patient discharge or deletion.

- Visual Ward Layout: Displays real-time bed status ( [FREE] or [Patient ID] ) using nested loops.

- Validation Rules: Prevents duplicate allocations, double-booking, and allocations when the ward is at 100% capacity.

**Reports and Analytics:**
- Patient Reports: Display all registered patients or filter views.

- Bed Occupancy: Displays total registered patients, total ward capacity, occupied bed count, available bed count, and total occupancy percentage.

- Sorting and Data Processing:
  - Sort patients alphabetically by Surname (Last Name).
  - Sort patients numerically/alphabetically by Patient ID.

**Object-Oriented Design:**
- Encapsulation: Private attributes with public getters and setters across all classes.

- Enums: PatientCategory enum representing INPATIENT, OUTPATIENT, and EMERGENCY.

**Unit Testing (JUnit5):**
Unit Testing suite verifying:
- Full CRUD operations.

- Bed allocation & release mechanics.

- Prevention of duplicate Patient IDs.

- Occupied bed double-allocation prevention.

- Full ward boundary handling (20/20 capacity limits).

- Sorting logic correctness.
