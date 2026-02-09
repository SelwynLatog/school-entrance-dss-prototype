//Categorizes item threat levels
package engine;
public enum ThreatLevel {
    NONE,       // No policy violation - item may proceed to risk evaluation
    LOW,        // Minor violations (tobacco, vaping products)
    MEDIUM,     // Moderate violations (alcohol, prohibited items)
    HIGH,       // Serious violations (weapons, sharp objects)
    CRITICAL    // Emergency-level violations (firearms, illegal substances)
}