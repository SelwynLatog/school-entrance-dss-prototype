/*
 Classifies the threat level of items based on their category.
 This determines the urgency of response and whether alerts should
 be triggered. Threat classification is independent of risk scoring -
 even low-risk items can be high-threat if they violate safety policies.
TLDR- Exactly what it says. A Threat Classifier
 */
package engine;

import model.Item;
import enums.*;

public class ThreatClassifier {
    
    public static ThreatLevel classify(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        
        PrimaryCategory primary = item.getPrimaryCategory();
        SecondaryCategory secondary = item.getSecondaryCategory();
        
        // CRITICAL: Immediate security/legal threat
        if (secondary == SecondaryCategory.FIREARM || 
            secondary == SecondaryCategory.ILLEGAL_SUBSTANCE) {
            return ThreatLevel.CRITICAL;
        }
        
        // HIGH: Serious safety concern
        if (primary == PrimaryCategory.WEAPON || 
            secondary == SecondaryCategory.SHARP_OBJECT ||
            secondary == SecondaryCategory.CHEMICAL_SUBSTANCE) {
            return ThreatLevel.HIGH;
        }
        
        // MEDIUM: Policy violation requiring confiscation
        if (primary == PrimaryCategory.ALCOHOL || 
            primary == PrimaryCategory.PROHIBITED_SUBSTANCE ||
            secondary == SecondaryCategory.ALCOHOLIC_BEVERAGE) {
            return ThreatLevel.MEDIUM;
        }
        
        // LOW: Health policy violation
        if (primary == PrimaryCategory.TOBACCO ||
            secondary == SecondaryCategory.SMOKING_PRODUCT ||
            secondary == SecondaryCategory.ELECTRONIC_SMOKING) {
            return ThreatLevel.LOW;
        }
        
        return ThreatLevel.NONE;
    }
}