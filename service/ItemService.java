package service;

import model.Item;
import storage.ItemLog;
import enums.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for Item operations in the school entrance DSS.
 * Handles business logic, validation, and orchestration between UI and storage.
 * 
 * Responsibilities:
 * - Validate item input data
 * - Apply business rules (default status, timestamp)
 * - Create Item objects
 * - Delegate storage operations to ItemLog
 * 
 * Does NOT handle:
 * - Terminal input/output (that's UI layer)
 * - Direct storage manipulation (that's ItemLog)
 */
public class ItemService {
    private final ItemLog itemLog;
    
    /**
     * Constructs ItemService with dependency injection.
     */
    public ItemService(ItemLog itemLog) {
        if (itemLog == null) {
            throw new IllegalArgumentException("ItemLog cannot be null");
        }
        this.itemLog = itemLog;
    }
    
    /**
     * Registers a new item in the system.
     * Validates input, applies business rules (default status, timestamp),
     * creates the Item object, and stores it in the log.
     * 
     * @return the unique ID assigned to the newly registered item
     */
    public int registerNewItem(
            String itemName,
            String brand,
            PrimaryCategory primaryCategory,
            SecondaryCategory secondaryCategory,
            ItemFunction function,
            ConsumptionContext context,
            UsageType usageType,
            Replaceability replaceability,
            int quantity
    ) {
        // Validate all inputs
        validateItemDetails(
                itemName,
                primaryCategory,
                secondaryCategory,
                function,
                context,
                usageType,
                replaceability,
                quantity
        );
        
        // Apply business rules
        LocalDateTime timestamp = LocalDateTime.now();
        ItemStatus status = ItemStatus.HELD; // Default status for new items
        
        // Create the Item object
        Item item = new Item(
                itemName,
                brand,
                primaryCategory,
                secondaryCategory,
                function,
                context,
                usageType,
                replaceability,
                status,
                quantity,
                timestamp
        );
        
        // Delegate to storage layer and return assigned ID
        return itemLog.addItem(item);
    }
    
    /**
     * Validates all required fields for item registration.
     * Throws IllegalArgumentException with descriptive message if validation fails.
     */
    private void validateItemDetails(
            String itemName,
            PrimaryCategory primaryCategory,
            SecondaryCategory secondaryCategory,
            ItemFunction function,
            ConsumptionContext context,
            UsageType usageType,
            Replaceability replaceability,
            int quantity
    ) {
        // Validate item name
        requireNonBlank(itemName, "Item name");
        
        // Validate enums (must not be null)
        requireNonNull(primaryCategory, "Primary category");
        requireNonNull(secondaryCategory, "Secondary category");
        requireNonNull(function, "Item function");
        requireNonNull(context, "Consumption context");
        requireNonNull(usageType, "Usage type");
        requireNonNull(replaceability, "Replaceability");
        
        // Validate quantity
        requirePositive(quantity, "Quantity");
    }
    
    /**
     * Ensures a string value is not null or blank.
     */
    private void requireNonBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
    }
    
    /**
     * Ensures an object is not null.
     */
    private void requireNonNull(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }
    }
    
    /**
     * Ensures an integer value is greater than zero.
     */
    private void requirePositive(int value, String fieldName) {
        if (value <= 0) {
            throw new IllegalArgumentException(fieldName + " must be greater than 0, got: " + value);
        }
    }
    
    /**
     * Marks an item as released by updating its status.
     * 
     * @param id the unique ID of the item to release
     * @return true if item was found and updated, false if ID doesn't exist
     */
    public boolean releaseItem(int id) {
        return itemLog.updateItemStatus(id, ItemStatus.RELEASED);
    }
    
    // ========== Delegated Search Methods ==========
    // These provide controlled access to ItemLog functionality
    // without exposing the entire ItemLog object
    
    public Optional<Item> findItemById(int id) {
        return itemLog.findItemById(id);
    }
    
    public List<Item> getAllItems() {
        return itemLog.getAllItems();
    }
    
    public List<ItemLog.ItemEntry> getAllItemsWithIds() {
        return itemLog.getAllItemsWithIds();
    }
    
    public List<Item> findItemsByCategory(PrimaryCategory category) {
        return itemLog.findItemsByCategory(category);
    }
    
    public List<Item> findItemsByStatus(ItemStatus status) {
        return itemLog.findItemsByStatus(status);
    }
    
    public List<Item> findItemsByBrand(String brand) {
        return itemLog.findItemsByBrand(brand);
    }
    
    /**
     * Removes an item from the log by ID.
     * 
     * @param id the unique ID of the item to remove
     * @return true if item existed and was removed, false if ID not found
     */
    public boolean removeItemById(int id) {
        return itemLog.removeItemById(id);
    }
}