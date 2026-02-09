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
 * - Link items to students through StudentService
 * 
 * Does NOT handle:
 * - Terminal input/output (that's UI layer)
 * - Direct storage manipulation (that's ItemLog)
 */
public class ItemService {
    private final ItemLog itemLog;
    private final StudentService studentService;
    
    //Constructs w dependency injection
    public ItemService(ItemLog itemLog, StudentService studentService) {
        if (itemLog == null) {
            throw new IllegalArgumentException("ItemLog cannot be null");
        }
        if (studentService == null) {
            throw new IllegalArgumentException("StudentService cannot be null");
        }
        
        this.itemLog = itemLog;
        this.studentService = studentService;
    }
    
    public int registerNewItem(
            String studentId,
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
        
        LocalDateTime timestamp = LocalDateTime.now();
        ItemStatus status = ItemStatus.HELD; 
          
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
                timestamp,
                studentId
        );
        
        // Add item to storage and get assigned ID
        int itemId = itemLog.addItem(item);
        
        // Link to student if studentId provided
        if (studentId != null && !studentId.trim().isEmpty()) {
            boolean linked = studentService.linkItemToStudent(studentId, itemId);
            if (!linked) {
                // Student not found - log warning but don't fail
                System.err.println("Warning: Could not link item " + itemId + 
                                 " to student " + studentId + " (student not found)");
            }
        }
        
        return itemId;
    }
   
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
        
        // Validate enums
        requireNonNull(primaryCategory, "Primary category");
        requireNonNull(secondaryCategory, "Secondary category");
        requireNonNull(function, "Item function");
        requireNonNull(context, "Consumption context");
        requireNonNull(usageType, "Usage type");
        requireNonNull(replaceability, "Replaceability");
        
        //Validate quantity
        requirePositive(quantity, "Quantity");
    }
    
   //Null check
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
    
    //Int>0 check
    private void requirePositive(int value, String fieldName) {
        if (value <= 0) {
            throw new IllegalArgumentException(fieldName + " must be greater than 0, got: " + value);
        }
    }
    
    //Marks item as released
    public boolean releaseItem(int id) {
        return itemLog.updateItemStatus(id, ItemStatus.RELEASED);
    }
    
    //Search methods
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
    
    public boolean removeItemById(int id) {
        return itemLog.removeItemById(id);
    }
}