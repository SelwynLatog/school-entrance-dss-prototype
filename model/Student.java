package model;

import enums.StudentStatus;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a person entering campus in the school entrance tracking system.
 * Immutable value object with identity, status, and violation history.
 * 
 * Students are linked to confiscated items through the itemIds list.
 * This allows tracking violation patterns per student without circular dependencies
 * between Student and Item objects.
 * 
 * Design pattern: Immutable with withX() methods for updates.
 * Same pattern as Item class for consistency.
 */
public class Student {
    
    // Identity fields
    private final String studentId;
    private final String name;
    private final String course;
    private final int year;
    
    // Status and tracking
    private final StudentStatus status;
    private final List<Integer> itemIds;  // References to ItemLog entries
    private final LocalDate enrollmentDate;
    
    /**
     * Creates a new Student with full validation.
     * 
     * @param studentId unique identifier (student number or visitor ID)
     * @param name full name of the student
     * @param course academic program (e.g., "Computer Science", "N/A" for outsiders)
     * @param year year level (1-6 for students, 0 for outsiders/visitors)
     * @param status enrollment status (ENROLLED, OUTSIDER, SUSPENDED)
     * @param itemIds list of item IDs associated with this student
     * @param enrollmentDate date when student first enrolled (or first entered campus)
     * @throws IllegalArgumentException if any validation fails
     */
    public Student(
            String studentId,
            String name,
            String course,
            int year,
            StudentStatus status,
            List<Integer> itemIds,
            LocalDate enrollmentDate
    ) {
        // Validate all inputs - fail fast with clear errors
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (course == null || course.trim().isEmpty()) {
            throw new IllegalArgumentException("Course cannot be null or empty");
        }
        // Validate year based on status
        // Outsiders get year = 0 (not enrolled), students get 1-6
        if (status == StudentStatus.OUTSIDER) {
            if (year != 0) {
                throw new IllegalArgumentException("Outsiders must have year = 0, got: " + year);
            }
        } else {
            if (year < 1 || year > 6) {
                throw new IllegalArgumentException("Year must be between 1 and 6 for students, got: " + year);
            }
        }
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        if (itemIds == null) {
            throw new IllegalArgumentException("Item IDs list cannot be null");
        }
        if (enrollmentDate == null) {
            throw new IllegalArgumentException("Enrollment date cannot be null");
        }
        
        this.studentId = studentId.trim();
        this.name = name.trim();
        this.course = course.trim();
        this.year = year;
        this.status = status;
        this.itemIds = new ArrayList<>(itemIds); // Defensive copy
        this.enrollmentDate = enrollmentDate;
    }
    
    
    public String getStudentId() {
        return studentId;
    }
    
    public String getName() {
        return name;
    }
    
    public String getCourse() {
        return course;
    }
    
    public int getYear() {
        return year;
    }
    
    public StudentStatus getStatus() {
        return status;
    }
    
    /**
     * Returns an unmodifiable view of the item IDs.
     * This prevents external code from modifying the student's violation history.
     * 
     * @return unmodifiable list of item IDs
     */
    public List<Integer> getItemIds() {
        return Collections.unmodifiableList(itemIds);
    }
    
    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }
    
    //Derived properties
    
    /**
     * Returns the number of items confiscated from this student.
     * This represents the student's total violation count.
     * 
     * @return count of associated items
     */
    public int getViolationCount() {
        return itemIds.size();
    }
    
    /**
     * Checks if this student has any violation history.
     * 
     * @return true if student has at least one confiscated item
     */
    public boolean hasViolations() {
        return !itemIds.isEmpty();
    }
    
    /**
     * Checks if this is an enrolled student (not outsider).
     * 
     * @return true if status is ENROLLED or SUSPENDED
     */
    public boolean isEnrolled() {
        return status == StudentStatus.ENROLLED || status == StudentStatus.SUSPENDED;
    }
    
    /**
     * Checks if this student is currently under suspension.
     * 
     * @return true if status is SUSPENDED
     */
    public boolean isSuspended() {
        return status == StudentStatus.SUSPENDED;
    }
    
   //Immutable update methods
    
    /**
     * Creates a new Student with an updated status.
     * Original student remains unchanged (immutable pattern).
     * 
     * @param newStatus the new status to apply
     * @return a new Student instance with the updated status
     */
    public Student withStatus(StudentStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        
        return new Student(
                this.studentId,
                this.name,
                this.course,
                this.year,
                newStatus,
                this.itemIds,
                this.enrollmentDate
        );
    }
    
    /**
     * Creates a new Student with an additional item ID in their history.
     * Used when a new item is confiscated from this student.
     * 
     * @param itemId the ID of the newly confiscated item
     * @return a new Student instance with the item added
     */
    public Student withAddedItem(int itemId) {
        List<Integer> updatedItems = new ArrayList<>(this.itemIds);
        updatedItems.add(itemId);
        
        return new Student(
                this.studentId,
                this.name,
                this.course,
                this.year,
                this.status,
                updatedItems,
                this.enrollmentDate
        );
    }
    
    /**
     * Creates a new Student with an item removed from their history.
     * Used when an item is released or a violation is expunged.
     * 
     * @param itemId the ID of the item to remove
     * @return a new Student instance with the item removed
     */
    public Student withRemovedItem(int itemId) {
        List<Integer> updatedItems = new ArrayList<>(this.itemIds);
        updatedItems.remove(Integer.valueOf(itemId)); // Remove by value, not index
        
        return new Student(
                this.studentId,
                this.name,
                this.course,
                this.year,
                this.status,
                updatedItems,
                this.enrollmentDate
        );
    }
    
    /**
     * Creates a new Student with updated year level.
     * Used for annual year progression.
     * 
     * @param newYear the new year level (1-6)
     * @return a new Student instance with the updated year
     */
    public Student withYear(int newYear) {
        // Validate year range (1-6 for students, 0 for outsiders)
        if (newYear < 0 || newYear > 6) {
            throw new IllegalArgumentException("Year must be between 0 and 6, got: " + newYear);
        }
        
        // Enforce status-year consistency
        if (status == StudentStatus.OUTSIDER && newYear != 0) {
            throw new IllegalArgumentException("Cannot set year for outsider to non-zero value");
        }
        if (status != StudentStatus.OUTSIDER && newYear == 0) {
            throw new IllegalArgumentException("Students must have year between 1-6, not 0");
        }
        
        return new Student(
                this.studentId,
                this.name,
                this.course,
                newYear,
                this.status,
                this.itemIds,
                this.enrollmentDate
        );
    }
    
    @Override
    public String toString() {
        return String.format(
                "Student[id=%s, name=%s, course=%s, year=%d, status=%s, violations=%d]",
                studentId,
                name,
                course,
                year,
                status,
                getViolationCount()
        );
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Student other = (Student) obj;
        return studentId.equals(other.studentId);
    }
    
    @Override
    public int hashCode() {
        return studentId.hashCode();
    }
}