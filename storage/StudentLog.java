package storage;

import model.Student;
import enums.StudentStatus;
import java.util.*;

/**
 * In-memory storage for Students in the school entrance DSS.
 * Manages student records and their associations with confiscated items.
 * 
 * Responsibilities:
 * - Store and retrieve Student objects by student ID
 * - Link students to confiscated items
 * - Provide search and filtering capabilities
 * - Manage student record updates
 * 
 * Storage Design:
 * Uses student ID as the primary key (unlike ItemLog which uses sequential integers).
 * This matches real-world school systems where student IDs are predetermined.
 */
public class StudentLog {
    
    // Core storage: studentId -> Student
    private final Map<String, Student> studentsById;
    
    public StudentLog() {
        this.studentsById = new HashMap<>();
    }
    
    //Adds a new student to the log
    public void addStudent(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }
        
        String studentId = student.getStudentId();
        if (studentsById.containsKey(studentId)) {
            throw new IllegalArgumentException("Student ID already exists: " + studentId);
        }
        
        studentsById.put(studentId, student);
    }
    
    //Updates existing student record
    public boolean updateStudent(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }
        
        String studentId = student.getStudentId();
        if (!studentsById.containsKey(studentId)) {
            return false; // Student not found
        }
        
        studentsById.put(studentId, student);
        return true;
    }
    
   //Find student by ID
    public Optional<Student> findStudentById(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be null or empty");
        }
        
        return Optional.ofNullable(studentsById.get(studentId));
    }
    
    //Returns all students to log
    public List<Student> getAllStudents() {
        return Collections.unmodifiableList(new ArrayList<>(studentsById.values()));
    }
    
   //Links held item to a student
    public boolean linkItemToStudent(String studentId, int itemId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be null or empty");
        }
        
        // Find the student
        Student student = studentsById.get(studentId);
        if (student == null) {
            return false; // Student not found
        }
        
        // Create updated student with item added
        Student updatedStudent = student.withAddedItem(itemId);
        
        // Replace in storage
        studentsById.put(studentId, updatedStudent);
        return true;
    }
    
    //Removes an item from student's linked item log
    public boolean unlinkItemFromStudent(String studentId, int itemId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be null or empty");
        }
        
        Student student = studentsById.get(studentId);
        if (student == null) {
            return false; // Student not found
        }
        
        // Create updated student with item removed
        Student updatedStudent = student.withRemovedItem(itemId);
        
        studentsById.put(studentId, updatedStudent);
        return true;
    }
    
    //Finds student by status (ENROLLED/OUTSIDER/SUSPENDED)
    public List<Student> findStudentsByStatus(StudentStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        
        return studentsById.values().stream()
                .filter(student -> student.getStatus() == status)
                .toList();
    }
    
    //Finds student by course
    public List<Student> findStudentsByCourse(String course) {
        if (course == null || course.trim().isEmpty()) {
            throw new IllegalArgumentException("Course cannot be null or empty");
        }
        
        return studentsById.values().stream()
                .filter(student -> student.getCourse().equalsIgnoreCase(course))
                .toList();
    }
    
    //Finds student with at least 1 HELD item
    public List<Student> findStudentsWithViolations() {
        return studentsById.values().stream()
                .filter(Student::hasViolations)
                .toList();
    }
    
    //Removes student from the log
    public boolean removeStudent(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be null or empty");
        }
        
        return studentsById.remove(studentId) != null;
    }
}