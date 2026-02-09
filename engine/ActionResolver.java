/**
Provides actionable instructions for guards based on threat level.
ActionResolver translates threat levels into clear, immediate protocols
TLDR- Admin Action Recommendations
 */
package engine;

import model.Item;
import enums.Decision;

public class ActionResolver {
    
    /**
     * Generates guard-facing action instructions based on threat level.
     * 
     * @param level the classified threat level
     * @param item the item being evaluated (for context in message)
     * @return clear, actionable instructions for gate personnel
     * @throws IllegalArgumentException if level or item is null
     */
    public static String getActionRecommendation(ThreatLevel level, Item item) {
        if (level == null) {
            throw new IllegalArgumentException("Threat level cannot be null");
        }
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        
        switch (level) {
            case CRITICAL:
                return "[!]CRITICAL ALERT - CRITICAL SECURITY PROTOCOL REQUIRED\n" +
                       "ACTION: Do not allow entry\n" +
                       "1. Secure item immediately\n" +
                       "2. Contact provincial police immediately\n" +
                       "3. Detain individual for verification\n" +
                       "4. File incident report and secure item as evidence\n"+
                       "FOLLOW-UP: Contact student affairs within 24 hours";
            
            case HIGH:
                return "HIGH THREAT - IMMEDIATE ACTION REQUIRED\n" +
                       "ACTION: Confiscate and hold\n" +
                       "1. Confiscate item (do not return)\n" +
                       "2. Log student ID and details\n" +
                       "3. Consider student disciplinary action immediately\n" +
                       "FOLLOW-UP: Routine Processing";
            
            case MEDIUM:
                return "POLICY VIOLATION - CONFISCATION REQUIRED\n" +
                       "ACTION: Confiscate item\n" +
                       "1. Inform student of violation\n" +
                       "2. Confiscate item (issue receipt)\n" +
                       "3. Log violation details\n" +
                       "FOLLOW-UP: Routine Processing";
            
            case LOW:
                return " HEALTH POLICY VIOLATION\n" +
                       "ACTION: Confiscate and warn\n" +
                       "1. Confiscate item\n" +
                       "2. Issue verbal warning\n" +
                       "3. Log for records\n" +
                       "FOLLOW-UP: No further action required";
            
            case NONE:
                return ""; // No action needed for threat-based evaluation
            
            default:
                throw new IllegalStateException("Unknown threat level: " + level);
        }
    }
    
    public static String getPlasticPolicyAction(Decision decision, int riskScore) {
        if (decision == null) {
            throw new IllegalArgumentException("Decision cannot be null");
        }
        
        switch (decision) {
            case ALLOW:
                return "RECOMMENDED ACTION:\n" +
                       "• Allow item on campus\n" +
                       "• No confiscation required\n" +
                       "• Item meets policy standards";
            
            case CONDITIONAL:
                return "RECOMMENDED ACTION:\n" +
                       "• Confiscate item and issue receipt\n" +
                       "• Issue verbal warning about single-use plastics\n" +
                       "• Log violation for student records\n" +
                       "• Recommend policy review meeting if repeated violations";
            
            case DISALLOW:
                return "RECOMMENDED ACTION:\n" +
                       "• Confiscate item and issue receipt\n" +
                       "• Issue verbal warning about single-use plastics\n" +
                       "• Log violation for student records";
            
            default:
                throw new IllegalStateException("Unknown decision: " + decision);
        }
    }
    
    public static boolean requiresImmediateAlert(ThreatLevel level) {
        return level == ThreatLevel.CRITICAL || level == ThreatLevel.HIGH;
    }
    
    public static String getStatusLabel(ThreatLevel level) {
        switch (level) {
            case CRITICAL: return "[!] EMERGENCY";
            case HIGH:     return "[!] ALERT";
            case MEDIUM:   return "[*] HOLD";
            case LOW:      return "[*] HOLD";
            case NONE:     return "[✓] PROCEED";
            default:       return "UNKNOWN";
        }
    }
}