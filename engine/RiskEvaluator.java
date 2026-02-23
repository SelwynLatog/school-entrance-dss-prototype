//RiskEvaluator.java
/*Responsibility- Rubric System Implementation
Hold all risk constants
Calculate total risk score
Brain of the DSS

 Implements the policy risk scoring rubric for single-use plastic items.
 
 RiskEvaluator takes an item and calculates a comprehensive risk score based on
 multiple factors defined in the campus plastic policy rubric. Each factor
 contributes to the total score, and all contributions are tracked for transparency.
  
 The evaluator only processes SINGLE_USE_PLASTIC items - other categories are
 handled by PolicyGate or allowed without scoring.
 
 Scoring Range: Typically 7-120 (updated after rebalancing)
 - Low Risk: ≤30 (items with reusable characteristics)
 - Moderate Risk: 31-70 (disallow, consider policy eval meetings)
 - High Risk: ≥71 (policy violation, disallow)
 
 CHANGELOG (Rebalancing Update):
 - Reduced BASE_RISK from 12 to maintain scoring range
 - Adjusted USAGE_TYPE constants for better discrimination
 - Reduced REPLACEABILITY values to avoid over-penalization
 - Simplified SECONDARY_CATEGORY to reduce overlap
 - Reduced FUNCTION values to prevent double-counting
 - Rebalanced CONTEXT values for clearer distinctions
 - Added QUANTITY bonus for bulk violations
 */

package engine;

import model.Item;
import enums.*;
import java.util.*;

public class RiskEvaluator {
    
    //═══════════════════════════════════════════════════════════════
    // REBALANCED RUBRIC CONSTANTS
    // Goal: Maintain zero-tolerance policy with improved accuracy
    //═══════════════════════════════════════════════════════════════
    
    // Base Risk (reduced - not every plastic starts at maximum)
    private static final int BASE_RISK = 12;
    
    // Usage Type contributions (STILL HEAVY - your school's priority)
    private static final int USAGE_SINGLE_USE = 30; // Was 30 - kept strong
    private static final int USAGE_OTHER = 18; // Was 20 - slightly reduced
    private static final int USAGE_REUSABLE = -20; // Was -25 - still strong benefit
    
    // Replaceability contributions (REDUCED - to avoid over-penalization)
    // Note: HIGH replaceability = easy to replace = higher penalty
    private static final int REPLACE_HIGH = 25; // Was 25 - kept same
    private static final int REPLACE_MEDIUM = 13; // Was 15 - reduced
    private static final int REPLACE_LOW = 4; // Was 5 - reduced
    
    // Secondary Category contributions (SIMPLIFIED - less overlap)
    private static final int SECONDARY_FOOD_ACCESSORY = 18; // Was 20
    private static final int SECONDARY_BEVERAGE_CONTAINER = 14; // Was 15
    private static final int SECONDARY_FOOD_CONTAINER = 10; // Same
    private static final int SECONDARY_PACKAGING = 4; // Was 5
    private static final int SECONDARY_OTHER = 8; // Was 10
    
    // Function contributions (REDUCED - to avoid double-counting)
    private static final int FUNCTION_UTENSIL = 12; // Was 15
    private static final int FUNCTION_CONTAINER = 8; // Was 10
    private static final int FUNCTION_PACKAGING = 4; // Was 5
    private static final int FUNCTION_TOOL = 2; // Same
    private static final int FUNCTION_OTHER = 6; // Was 8
    
    // Consumption Context contributions (REBALANCED)
    private static final int CONTEXT_SCHOOL_USE = 13; // Was 15 - on campus = visible
    private static final int CONTEXT_TAKEOUT = 9; // Was 10 - commercial source
    private static final int CONTEXT_FOOD = 7; // Was 8
    private static final int CONTEXT_BEVERAGE = 7; // Was 8
    private static final int CONTEXT_PERSONAL_USE = 4; // Was 5
    private static final int CONTEXT_UNKNOWN = 12; // Was 15 - assume school use

    /**
     * Calculates quantity bonus for bulk violations.
     * 
     * Multiple items of the same type indicate systematic violation
     * or commercial-scale usage, warranting additional scrutiny.
     */
    private static int calculateQuantityBonus(int quantity) {
        if (quantity <= 1) return 0;
        // Scale: 2 items = +2, 5 items = +8, 10 items = +18, capped at +20
        return Math.min((quantity - 1) * 2, 20);
    }

    /**
     Evaluates a single-use plastic item and produces a complete risk breakdown.

     This method implements the full scoring rubric, calculating contributions
     from usage type, replaceability, secondary category, function, consumption
     context, and quantity. All factors are tracked for explainability.
     
     the item to evaluate (must be SINGLE_USE_PLASTIC category)
     @return RiskBreakdown containing all factors and total risk score
     if item is null or not SINGLE_USE_PLASTIC
     */
    public static RiskBreakdown evaluate(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        
        if (item.getPrimaryCategory() != PrimaryCategory.SINGLE_USE_PLASTIC) {
            throw new IllegalArgumentException(
                "RiskEvaluator only evaluates SINGLE_USE_PLASTIC items. " +
                "Item has category: " + item.getPrimaryCategory()
            );
        }
        
        List<RiskFactor> factors = new ArrayList<>();
        
        // Base risk (always applied)
        factors.add(new RiskFactor(
            "Base Risk",
            "SINGLE_USE_PLASTIC",
            BASE_RISK,
            "All plastic items carry inherent environmental policy risk"
        ));
        
        // Evaluate individual factors
        factors.add(evaluateUsageType(item));
        factors.add(evaluateReplaceability(item));
        factors.add(evaluateSecondaryCategory(item));
        factors.add(evaluateFunction(item));
        factors.add(evaluateConsumptionContext(item));
        
        // NEW: Quantity factor (for bulk violations)
        int quantityBonus = calculateQuantityBonus(item.getQuantity());
        if (quantityBonus > 0) {
            factors.add(new RiskFactor(
                "Quantity",
                String.valueOf(item.getQuantity()),
                quantityBonus,
                String.format("Multiple items detected (%d units) - bulk violation", item.getQuantity())
            ));
        }
        
        // Calculate total score
        int totalScore = factors.stream()
                               .mapToInt(RiskFactor::getContribution)
                               .sum();
        
        return new RiskBreakdown(factors, totalScore);
    }
    
    //═══════════════════════════════════════════════════════════════
    // FACTOR EVALUATION METHODS
    //═══════════════════════════════════════════════════════════════
    
    /**
     Evaluates the usage type factor.
     Maximum impact: +30 points (penalty) or -20 points (credit)
     */
    private static RiskFactor evaluateUsageType(Item item) {
        UsageType usage = item.getUsageType();
        
        switch (usage) {
            case SINGLE_USE:
                return new RiskFactor(
                    "Usage Type",
                    "SINGLE_USE",
                    USAGE_SINGLE_USE,
                    "Item is designed for single-use and disposal"
                );
            
            case REUSABLE:
                return new RiskFactor(
                    "Usage Type",
                    "REUSABLE",
                    USAGE_REUSABLE,
                    "Item can be reused, reducing environmental impact"
                );
            
            case OTHER:
                return new RiskFactor(
                    "Usage Type",
                    "OTHER",
                    USAGE_OTHER,
                    "Item has uncertain reusability profile"
                );
            
            default:
                throw new IllegalStateException("Unknown usage type: " + usage);
        }
    }
    
    /**
     * Evaluates the replaceability factor.
     * Maximum impact: +25 points
     * 
     * Note: HIGH replaceability means item is EASY to replace,
     * therefore alternatives exist and penalty is higher.
     */
    private static RiskFactor evaluateReplaceability(Item item) {
        Replaceability replace = item.getReplaceability();
        
        switch (replace) {
            case HIGH:
                return new RiskFactor(
                    "Replaceability",
                    "HIGH",
                    REPLACE_HIGH,
                    "Eco-friendly alternatives available"
                );
            
            case MEDIUM:
                return new RiskFactor(
                    "Replaceability",
                    "MEDIUM",
                    REPLACE_MEDIUM,
                    "Alternatives available but may require adjustment"
                );
            
            case LOW:
                return new RiskFactor(
                    "Replaceability",
                    "LOW",
                    REPLACE_LOW,
                    "Limited alternatives available, minimal penalty applied"
                );
            
            default:
                throw new IllegalStateException("Unknown replaceability: " + replace);
        }
    }
    
    /**
     Evaluates the secondary category factor.
     Maximum impact: +18 points
     */
    private static RiskFactor evaluateSecondaryCategory(Item item) {
        SecondaryCategory secondary = item.getSecondaryCategory();
        
        // Map to appropriate contribution
        String description;
        int contribution;
        
        switch (secondary) {
            case FOOD_ACCESSORY:
                contribution = SECONDARY_FOOD_ACCESSORY;
                description = "Classified as food-related accessory";
                break;
            
            case BEVERAGE_CONTAINER:
                contribution = SECONDARY_BEVERAGE_CONTAINER;
                description = "Classified as beverage container";
                break;
            
            case FOOD_CONTAINER:
                contribution = SECONDARY_FOOD_CONTAINER;
                description = "Classified as food storage container";
                break;
            
            case PACKAGING:
                contribution = SECONDARY_PACKAGING;
                description = "Classified as packaging material";
                break;
            
            default:
                contribution = SECONDARY_OTHER;
                description = "Item category has standard policy impact";
                break;
        }
        
        return new RiskFactor(
            "Secondary Category",
            secondary.toString(),
            contribution,
            description
        );
    }
    
    /**
     Evaluates the function factor.
     Maximum impact: +12 points
     */
    private static RiskFactor evaluateFunction(Item item) {
        ItemFunction function = item.getFunction();
        
        String description;
        int contribution;
        
        switch (function) {
            case UTENSIL:
                contribution = FUNCTION_UTENSIL;
                description = "Item serves as eating utensil";
                break;
            
            case CONTAINER:
                contribution = FUNCTION_CONTAINER;
                description = "Item functions as storage or transport container";
                break;
            
            case PACKAGING:
                contribution = FUNCTION_PACKAGING;
                description = "Item serves packaging or wrapping purpose";
                break;
            
            case TOOL:
                contribution = FUNCTION_TOOL;
                description = "Item functions as utility tool";
                break;
            
            case CONSUMABLE:
            case OTHER:
            default:
                contribution = FUNCTION_OTHER;
                description = "Item has general functional purpose";
                break;
        }
        
        return new RiskFactor(
            "Function",
            function.toString(),
            contribution,
            description
        );
    }
    
    /**
     Evaluates the consumption context factor.
     Maximum impact: +13 points
     */
    private static RiskFactor evaluateConsumptionContext(Item item) {
        ConsumptionContext context = item.getContext();
        
        String description;
        int contribution;
        
        switch (context) {
            case SCHOOL_USE:
                contribution = CONTEXT_SCHOOL_USE;
                description = "Item intended for use within campus premises";
                break;
            
            case TAKEOUT:
                contribution = CONTEXT_TAKEOUT;
                description = "Item associated with takeout food service";
                break;
            
            case FOOD:
                contribution = CONTEXT_FOOD;
                description = "Item used in food consumption context";
                break;
            
            case BEVERAGE:
                contribution = CONTEXT_BEVERAGE;
                description = "Item used in beverage consumption context";
                break;
            
            case PERSONAL_USE:
                contribution = CONTEXT_PERSONAL_USE;
                description = "Item for general personal use";
                break;
            
            case UNKNOWN:
                contribution = CONTEXT_UNKNOWN;
                description = "Context unclear - treated as high risk due to uncertainty";
                break;
            
            default:
                throw new IllegalStateException("Unknown context: " + context);
        }
        
        return new RiskFactor(
            "Consumption Context",
            context.toString(),
            contribution,
            description
        );
    }
}