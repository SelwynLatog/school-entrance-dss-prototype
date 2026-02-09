package ui;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.screen.*;
import com.googlecode.lanterna.input.*;
import service.ItemService;
import enums.*;
import java.io.IOException;

/**
 * Interactive form for adding new items to the system.
 * Provides field-by-field navigation with validation and enum dropdowns.
 */
public class AddItemScreen {
    
    private final Screen screen;
    private final ItemService itemService;
    
    // Form fields
    private String itemName;
    private String brand;
    private PrimaryCategory primaryCategory;
    private SecondaryCategory secondaryCategory;
    private ItemFunction function;
    private ConsumptionContext context;
    private UsageType usageType;
    private Replaceability replaceability;
    private int quantity;
    
    // Form state
    private int currentFieldIndex;
    private static final int TOTAL_FIELDS = 9;
    
    // Layout constants
    private static final int FORM_WIDTH = 120;
    private static final int FORM_HEIGHT = 22;
    private static final int SCREEN_WIDTH = 120;
    private static final int SCREEN_HEIGHT = 24;
    
    public AddItemScreen(Screen screen, ItemService itemService) {
        this.screen = screen;
        this.itemService = itemService;
        
        // Initialize with default values
        this.itemName = "";
        this.brand = "";
        this.primaryCategory = PrimaryCategory.values()[0];
        this.secondaryCategory = SecondaryCategory.values()[0];
        this.function = ItemFunction.values()[0];
        this.context = ConsumptionContext.values()[0];
        this.usageType = UsageType.values()[0];
        this.replaceability = Replaceability.values()[0];
        this.quantity = 1;
        this.currentFieldIndex = 0;
    }
    
    public boolean show() throws IOException {
        boolean running = true;
        boolean saved = false;
        
        while (running) {
            render();
            
            KeyStroke keyStroke = screen.readInput();
            
            if (keyStroke == null) {
                continue;
            }
            
            switch (keyStroke.getKeyType()) {
                case ArrowUp:
                    if (currentFieldIndex > 0) {
                        currentFieldIndex--;
                    }
                    break;
                    
                case ArrowDown:
                    if (currentFieldIndex < TOTAL_FIELDS - 1) {
                        currentFieldIndex++;
                    }
                    break;
                    
                case Enter:
                    handleFieldEdit();
                    break;
                    
                case Escape:
                    running = false;
                    saved = false;
                    break;
                    
                case Character:
                    char c = Character.toLowerCase(keyStroke.getCharacter());
                    if (c == 's') {
                        if (validateAndSave()) {
                            running = false;
                            saved = true;
                        }
                    } else if (c == 'c') {
                        running = false;
                        saved = false;
                    }
                    break;
                    
                default:
                    break;
            }
        }
        
        return saved;
    }
    
    private void render() throws IOException {
        screen.clear();
        
        int startX = (SCREEN_WIDTH - FORM_WIDTH);
        int startY = (SCREEN_HEIGHT - FORM_HEIGHT);
        
        // Draw border with footer separator
        UIHelpers.drawBoxWithFooter(screen, startX, startY, FORM_WIDTH, FORM_HEIGHT);
        
        // Draw title
        String title = "ADD NEW ITEM";
        int titleX = startX + UIHelpers.centerTextX(title, FORM_WIDTH);
        UIHelpers.writeText(screen, titleX, startY, title, TextColor.ANSI.CYAN_BRIGHT, SCREEN_WIDTH);
        
        // Draw fields
        int fieldY = startY + 3;
        
        drawField(startX + 2, fieldY, 0, "Item Name:", itemName, false);
        fieldY += 2;
        
        drawField(startX + 2, fieldY, 1, "Brand (optional):", brand, false);
        fieldY += 2;
        
        drawField(startX + 2, fieldY, 2, "Primary Category:", primaryCategory.toString(), true);
        fieldY += 2;
        
        drawField(startX + 2, fieldY, 3, "Secondary Category:", secondaryCategory.toString(), true);
        fieldY += 2;
        
        drawField(startX + 2, fieldY, 4, "Function:", function.toString(), true);
        fieldY += 2;
        
        drawField(startX + 2, fieldY, 5, "Context:", context.toString(), true);
        fieldY += 2;
        
        drawField(startX + 2, fieldY, 6, "Usage Type:", usageType.toString(), true);
        fieldY += 2;
        
        drawField(startX + 2, fieldY, 7, "Replaceability:", replaceability.toString(), true);
        fieldY += 2;
        
        drawField(startX + 2, fieldY, 8, "Quantity:", String.valueOf(quantity) + " (↑↓ to adjust)", false);
        
        // Draw footer
        String footer = "[Enter: Edit] [↑↓: Navigate] [S: Save] [C: Cancel]";
        int footerX = startX + UIHelpers.centerTextX(footer, FORM_WIDTH);
        UIHelpers.writeText(screen, footerX, startY + FORM_HEIGHT - 1, footer, 
            TextColor.ANSI.CYAN, SCREEN_WIDTH);
        
        screen.refresh();
    }
    
    private void drawField(int x, int y, int fieldIndex, String label, String value, boolean isDropdown) {
        boolean isSelected = (currentFieldIndex == fieldIndex);
        
        // Draw label
        UIHelpers.writeText(screen, x, y, label, TextColor.ANSI.WHITE, SCREEN_WIDTH);
        
        // Draw value with highlighting if selected
        int valueX = x + 22;
        String displayValue = String.format("%-25s", value);
        if (isDropdown) {
            displayValue = displayValue + " ▼";
        }
        
        TextColor valueColor = isSelected ? TextColor.ANSI.YELLOW_BRIGHT : TextColor.ANSI.WHITE;
        
        // Draw selection indicator
        if (isSelected) {
            UIHelpers.writeText(screen, x - 2, y, "►", TextColor.ANSI.YELLOW_BRIGHT, SCREEN_WIDTH);
        }
        
        UIHelpers.writeText(screen, valueX, y, "[" + displayValue + "]", valueColor, SCREEN_WIDTH);
    }
    
    /**
     * Handles editing the currently selected field.
     */
    private void handleFieldEdit() throws IOException {
        switch (currentFieldIndex) {
            case 0: // Item Name
                itemName = editTextField("Enter Item Name", itemName);
                break;
            case 1: // Brand
                brand = editTextField("Enter Brand (optional)", brand);
                break;
            case 2: // Primary Category
                primaryCategory = selectFromEnum("Select Primary Category", PrimaryCategory.class, primaryCategory);
                break;
            case 3: // Secondary Category
                secondaryCategory = selectFromEnum("Select Secondary Category", SecondaryCategory.class, secondaryCategory);
                break;
            case 4: // Function
                function = selectFromEnum("Select Function", ItemFunction.class, function);
                break;
            case 5: // Context
                context = selectFromEnum("Select Context", ConsumptionContext.class, context);
                break;
            case 6: // Usage Type
                usageType = selectFromEnum("Select Usage Type", UsageType.class, usageType);
                break;
            case 7: // Replaceability
                replaceability = selectFromEnum("Select Replaceability", Replaceability.class, replaceability);
                break;
            case 8: // Quantity
                quantity = editQuantity();
                break;
        }
    }
    
    private String editTextField(String title, String currentValue) throws IOException {
        StringBuilder input = new StringBuilder(currentValue);
        
        while (true) {
            // Draw input dialog
            drawInputDialog(title, input.toString());
            
            KeyStroke keyStroke = screen.readInput();
            
            if (keyStroke == null) {
                continue;
            }
            
            if (keyStroke.getKeyType() == KeyType.Enter) {
                return input.toString();
            } else if (keyStroke.getKeyType() == KeyType.Escape) {
                return currentValue; // Cancel
            } else if (keyStroke.getKeyType() == KeyType.Backspace) {
                if (input.length() > 0) {
                    input.deleteCharAt(input.length() - 1);
                }
            } else if (keyStroke.getKeyType() == KeyType.Character) {
                if (input.length() < 30) {
                    input.append(keyStroke.getCharacter());
                }
            }
        }
    }
    
    /**
     * Draws an input dialog for text entry.
     */
    private void drawInputDialog(String title, String text) throws IOException {
        int dialogWidth = 50;
        int dialogHeight = 5;
        int startX = (SCREEN_WIDTH - dialogWidth) / 2;
        int startY = (SCREEN_HEIGHT - dialogHeight) / 2;
        
        // Draw dialog box
        UIHelpers.drawBox(screen, startX, startY, dialogWidth, dialogHeight);
        
        // Title
        int titleX = startX + UIHelpers.centerTextX(title, dialogWidth);
        UIHelpers.writeText(screen, titleX, startY + 1, title, TextColor.ANSI.CYAN_BRIGHT, SCREEN_WIDTH);
        
        // Input field
        String displayText = text + "_";
        int inputX = startX + 2;
        UIHelpers.writeText(screen, inputX, startY + 2, displayText, TextColor.ANSI.YELLOW_BRIGHT, SCREEN_WIDTH);
        
        // Helper
        String helper = "[Enter: Save] [ESC: Cancel]";
        int helperX = startX + UIHelpers.centerTextX(helper, dialogWidth);
        UIHelpers.writeText(screen, helperX, startY + 3, helper, TextColor.ANSI.WHITE, SCREEN_WIDTH);
        
        screen.refresh();
    }
    
    /**
     * Shows a dropdown selection dialog for enums.
     */
    private <T extends Enum<T>> T selectFromEnum(String title, Class<T> enumClass, T currentValue) throws IOException {
        T[] values = enumClass.getEnumConstants();
        int selectedIndex = 0;
        
        // Find current value index
        for (int i = 0; i < values.length; i++) {
            if (values[i] == currentValue) {
                selectedIndex = i;
                break;
            }
        }
        
        while (true) {
            drawEnumDialog(title, values, selectedIndex);
            
            KeyStroke keyStroke = screen.readInput();
            
            if (keyStroke == null) {
                continue;
            }
            
            switch (keyStroke.getKeyType()) {
                case ArrowUp:
                    if (selectedIndex > 0) {
                        selectedIndex--;
                    }
                    break;
                case ArrowDown:
                    if (selectedIndex < values.length - 1) {
                        selectedIndex++;
                    }
                    break;
                case Enter:
                    return values[selectedIndex];
                case Escape:
                    return currentValue; // Cancel
                default:
                    break;
            }
        }
    }
    
    /**
     * Draws a dropdown dialog for enum selection.
     */
    private <T extends Enum<T>> void drawEnumDialog(String title, T[] values, int selectedIndex) throws IOException {
        int dialogWidth = 50;
        int dialogHeight = Math.min(values.length + 5, 18);
        int startX = (SCREEN_WIDTH - dialogWidth) / 2;
        int startY = (SCREEN_HEIGHT - dialogHeight) / 2;
        
        // Draw border
        UIHelpers.drawBox(screen, startX, startY, dialogWidth, dialogHeight);
        
        // Title
        int titleX = startX + UIHelpers.centerTextX(title, dialogWidth);
        UIHelpers.writeText(screen, titleX, startY + 1, title, TextColor.ANSI.CYAN_BRIGHT, SCREEN_WIDTH);
        
        // Options
        int optionY = startY + 3;
        int maxVisible = Math.min(values.length, dialogHeight - 5);
        
        for (int i = 0; i < Math.min(values.length, maxVisible); i++) {
            String indicator = (i == selectedIndex) ? "►" : " ";
            String option = indicator + " " + values[i].toString();
            
            TextColor color = (i == selectedIndex) 
                ? TextColor.ANSI.YELLOW_BRIGHT 
                : TextColor.ANSI.WHITE;
            
            UIHelpers.writeText(screen, startX + 2, optionY + i, option, color, SCREEN_WIDTH);
        }
        
        // Helper
        String helper = "[↑↓: Navigate] [Enter: Select] [ESC: Cancel]";
        int helperX = startX + UIHelpers.centerTextX(helper, dialogWidth);
        UIHelpers.writeText(screen, helperX, startY + dialogHeight - 2, helper, 
            TextColor.ANSI.WHITE, SCREEN_WIDTH);
        
        screen.refresh();
    }
    
    /**
     * Handles quantity adjustment with up/down arrows.
     */
    private int editQuantity() throws IOException {
        int tempQuantity = quantity;
        
        while (true) {
            drawQuantityDialog(tempQuantity);
            
            KeyStroke keyStroke = screen.readInput();
            
            if (keyStroke == null) {
                continue;
            }
            
            switch (keyStroke.getKeyType()) {
                case ArrowUp:
                    if (tempQuantity < 100) {
                        tempQuantity++;
                    }
                    break;
                case ArrowDown:
                    if (tempQuantity > 1) {
                        tempQuantity--;
                    }
                    break;
                case Enter:
                    return tempQuantity;
                case Escape:
                    return quantity; // Cancel
                default:
                    break;
            }
        }
    }
    
    private void drawQuantityDialog(int qty) throws IOException {
        int dialogWidth = 40;
        int dialogHeight = 5;
        int startX = (SCREEN_WIDTH - dialogWidth) / 2;
        int startY = (SCREEN_HEIGHT - dialogHeight) / 2;
        
        // Draw border
        UIHelpers.drawBox(screen, startX, startY, dialogWidth, dialogHeight);
        
        // Title
        String title = "Adjust Quantity";
        int titleX = startX + UIHelpers.centerTextX(title, dialogWidth);
        UIHelpers.writeText(screen, titleX, startY + 1, title, TextColor.ANSI.CYAN_BRIGHT, SCREEN_WIDTH);
        
        // Quantity display
        String qtyDisplay = String.format("[ %3d ]", qty);
        int qtyX = startX + UIHelpers.centerTextX(qtyDisplay, dialogWidth);
        UIHelpers.writeText(screen, qtyX, startY + 2, qtyDisplay, TextColor.ANSI.YELLOW_BRIGHT, SCREEN_WIDTH);
        
        // Helper
        String helper = "[↑↓: Adjust] [Enter: Save] [ESC: Cancel]";
        int helperX = startX + UIHelpers.centerTextX(helper, dialogWidth);
        UIHelpers.writeText(screen, helperX, startY + 3, helper, TextColor.ANSI.WHITE, SCREEN_WIDTH);
        
        screen.refresh();
    }
    
    /**
     * Validates form and saves the item.
     */
    private boolean validateAndSave() throws IOException {
        // Validate item name
        if (itemName == null || itemName.trim().isEmpty()) {
            showError("Item name cannot be empty");
            return false;
        }
        
        try {
            // Call service to register new item
            itemService.registerNewItem(
                itemName.trim(),
                brand.trim().isEmpty() ? null : brand.trim(),
                primaryCategory,
                secondaryCategory,
                function,
                context,
                usageType,
                replaceability,
                quantity
            );
            
            showSuccess("Item added successfully!");
            return true;
            
        } catch (IllegalArgumentException e) {
            showError("Error: " + e.getMessage());
            return false;
        }
    }
    
    private void showError(String message) throws IOException {
        showMessage(message, false);
    }
    
    private void showSuccess(String message) throws IOException {
        showMessage(message, true);
    }
    
    private void showMessage(String message, boolean isSuccess) throws IOException {
        int dialogWidth = message.length() + 6;
        int dialogHeight = 3;
        int startX = (SCREEN_WIDTH - dialogWidth) / 2;
        int startY = (SCREEN_HEIGHT - dialogHeight) / 2;
        
        TextColor messageColor = isSuccess ? TextColor.ANSI.GREEN_BRIGHT : TextColor.ANSI.RED_BRIGHT;
        String icon = isSuccess ? "✓" : "X";
        
        // Draw dialog
        UIHelpers.drawBox(screen, startX, startY, dialogWidth, dialogHeight);
        
        // Message
        String fullMessage = icon + " " + message;
        int msgX = startX + UIHelpers.centerTextX(fullMessage, dialogWidth);
        UIHelpers.writeText(screen, msgX, startY + 1, fullMessage, messageColor, SCREEN_WIDTH);
        
        screen.refresh();
        
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            // Ignore interruption
        }
    }
}