package enums;

/**
 * Represents the enrollment status of a person entering campus.
 * 
 * Status determines whether the person has full campus access rights
 * and whether violations are tracked in the student system.
 */
public enum StudentStatus {
    /**
     * Currently enrolled student with full campus access.
     * Violations are tracked and may trigger disciplinary action.
     */
    ENROLLED,
    
    /**
     * Non-student entering campus (visitor, guest, contractor).
     * Limited access rights. Items held but not tracked as student violations.
     */
    OUTSIDER,
    
    /**
     * Student under disciplinary suspension.
     * Campus access restricted. All violations logged and escalated.
     */
    SUSPENDED
}