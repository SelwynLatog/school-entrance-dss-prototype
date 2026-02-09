/**
Enforces absolute campus policy rules with zero tolerance. 
PolicyGate is the first checkpoint in the decision pipeline.
It performs binary policy checks - items either violate hard rules
(immediate disallow) or pass through to risk evaluation.

Hard policy violations (always blocked):
 - Weapons, sharp objects, and firearms
 - Illegal and prohibited substances  
 - Alcohol and tobacco products 
   No scoring. No context evaluation. Just strict rule enforcement.
 */
package engine;

import model.Item;
import enums.*;
import java.util.Optional;

public class PolicyGate {
    
    /**
     * Checks if an item violates absolute campus policies.
     * 
     * This is a pure function - same item always returns same result.
     * 
     * @param item the item to evaluate
     * @return Optional containing block reason if policy violated,
     *         Optional.empty() if item passes all hard policy checks
     * @throws IllegalArgumentException if item is null
     */
    public static Optional<String> checkHardPolicy(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        
        PrimaryCategory primary = item.getPrimaryCategory();
        SecondaryCategory secondary = item.getSecondaryCategory();
        
        // Primary category violations
        if (primary == PrimaryCategory.WEAPON) {
            return Optional.of("Weapons prohibited under campus safety policy.");
        }
        
        if (primary == PrimaryCategory.ALCOHOL) {
            return Optional.of("Alcoholic beverages prohibited on campus premises.");
        }
        
        if (primary == PrimaryCategory.TOBACCO) {
            return Optional.of("Tobacco products prohibited under campus health policy.");
        }
        
        if (primary == PrimaryCategory.PROHIBITED_SUBSTANCE) {
            return Optional.of("Prohibited substances not allowed on campus.");
        }
        
        // Secondary category violations
        if (secondary == SecondaryCategory.FIREARM) {
            return Optional.of("Firearms prohibited under campus safety policy");
        }
        
        if (secondary == SecondaryCategory.ILLEGAL_SUBSTANCE) {
            return Optional.of("Illegal substances prohibited by law");
        }
        
        if (secondary == SecondaryCategory.SHARP_OBJECT) {
            return Optional.of("Sharp objects are prohibited under campus safety policy");
        }
        
        if (secondary == SecondaryCategory.SMOKING_PRODUCT || 
            secondary == SecondaryCategory.ELECTRONIC_SMOKING) {
            return Optional.of("Smoking products are prohibited on campus premises.");
        }
        
        if (secondary == SecondaryCategory.ALCOHOLIC_BEVERAGE) {
            return Optional.of("Alcoholic beverages are prohibited on campus premises.");
        }
        
        if (secondary == SecondaryCategory.CHEMICAL_SUBSTANCE) {
            return Optional.of("Unregulated chemical substances are prohibited on campus premises.");
        }
        
        return Optional.empty();
    }
}