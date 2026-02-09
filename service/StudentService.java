package service;

import model.Student;
import storage.StudentLog;
import storage.ItemLog;
import enums.StudentStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for Student operations in the school entrance DSS.
 * Handles business logic, validation, and orchestration between StudentLog and ItemLog.
 * 
 * Responsibilities:
 * - Validate student input data
 * - Apply business rules (default status, enrollment date)
 * - Create Student objects
 * - Orchestrate operations across StudentLog and ItemLog
 * - Link students to confiscated items
 * 
 * Does NOT handle:
 * - Terminal input/output
 * - Direct storage manipulation*/

public class StudentService {
    
    private final StudentLog studentLog;
    private final ItemLog itemLog;
    
    //Constructs student service w dependency injection
    public StudentService(StudentLog studentLog, ItemLog itemLog) {
        if (studentLog == null) {
            throw new IllegalArgumentException("StudentLog cannot be null");
        }
        if (itemLog == null) {
            throw new IllegalArgumentException("ItemLog cannot be null");
        }
        
        this.studentLog = studentLog;
        this.itemLog = itemLog;
    }
    
   //Register new student
    public void registerNewStudent(
            String studentId,
            String name,
            String course,
            int year,
            StudentStatus status
    ) {
      
        validateStudentDetails(studentId, name, course, year, status);
        
        LocalDate enrollmentDate = LocalDate.now();
        List<Integer> itemIds = List.of();
        
        Student student = new Student(
                studentId,
                name,
                course,
                year,
                status,
                itemIds,
                enrollmentDate
        );
        
        studentLog.addStudent(student);
    }
    
   //Links confiscated item to student
    public boolean linkItemToStudent(String studentId, int itemId) {
        
        Optional<model.Item> item = itemLog.findItemById(itemId);
        if (item.isEmpty()) {
            return false; // Item doesn't exist - cannot link
        }
        
       
        return studentLog.linkItemToStudent(studentId, itemId);
    }
    
    //Unlinks an item from student violation history
    public boolean unlinkItemFromStudent(String studentId, int itemId) {
        return studentLog.unlinkItemFromStudent(studentId, itemId);
    }
    
    //Update student enrollment status
    public boolean updateStudentStatus(String studentId, StudentStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        
        // Find student
        Optional<Student> optStudent = studentLog.findStudentById(studentId);
        if (optStudent.isEmpty()) {
            return false; // Student not found
        }
        
       
        Student updatedStudent = optStudent.get().withStatus(newStatus);
        
        return studentLog.updateStudent(updatedStudent);
    }
    
   //Search methods
    public Optional<Student> findStudentById(String studentId) {
        return studentLog.findStudentById(studentId);
    }

    public List<Student> getAllStudents() {
        return studentLog.getAllStudents();
    }
    
    public List<Student> findStudentsByStatus(StudentStatus status) {
        return studentLog.findStudentsByStatus(status);
    }
  
    public List<Student> findStudentsByCourse(String course) {
        return studentLog.findStudentsByCourse(course);
    }
    
    public List<Student> findStudentsWithViolations() {
        return studentLog.findStudentsWithViolations();
    }
    
   //Remove student. Use w caution. could add auth layer/confirm method
    public boolean removeStudent(String studentId) {
        return studentLog.removeStudent(studentId);
    }
    
    private void validateStudentDetails(
            String studentId,
            String name,
            String course,
            int year,
            StudentStatus status
    ) {
        // Validate student ID
        requireNonBlank(studentId, "Student ID");
        
        // Validate name
        requireNonBlank(name, "Name");
        
        // Validate course
        requireNonBlank(course, "Course");
        
        // Validate status
        requireNonNull(status, "Status");
        
    }
    
   //Nullc check
    private void requireNonBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
    }
    
    private void requireNonNull(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }
    }
}