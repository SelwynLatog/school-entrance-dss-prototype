//TestLoader.java
package test;

import storage.ItemLog;
import service.ItemService;
import ui.ViewLogScreen;
import enums.*;
import java.io.IOException;

public class TestLoader {

    public static void loadViewLog() {
        try {
            ItemLog itemLog = new ItemLog();
            ItemService itemService = new ItemService(itemLog);
            
            //sample test data
            addSampleItems(itemService);

            //ui
            ViewLogScreen viewer = new ViewLogScreen(itemService);
            viewer.show();
            
            System.out.println("Program terminated.");
            
        } catch (IOException e) {
            System.err.println("Error initializing terminal: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
     public static void loadDSSMenu() {
        System.out.println("DSS log wip");
        try {
            System.in.read();
        } catch (IOException ignored) {}
    }
    
    public static void addSampleItems(ItemService itemService) {
        try {
            // Sample 1: Plastic water bottle (HELD)
            itemService.registerNewItem(
                "Plastic Water Bottle",
                "Nestle",
                PrimaryCategory.SINGLE_USE_PLASTIC,
                SecondaryCategory.BEVERAGE_CONTAINER,
                ItemFunction.CONTAINER,
                ConsumptionContext.BEVERAGE,
                UsageType.SINGLE_USE,
                Replaceability.HIGH,
                1
            );
            
            // Sample 2: Styrofoam food container (HELD)
            itemService.registerNewItem(
                "Styrofoam Lunch Box",
                "Generic",
                PrimaryCategory.SINGLE_USE_PLASTIC,
                SecondaryCategory.FOOD_CONTAINER,
                ItemFunction.CONTAINER,
                ConsumptionContext.TAKEOUT,
                UsageType.SINGLE_USE,
                Replaceability.HIGH,
                1
            );
            
            // Sample 3: Cigarettes (HELD)
            itemService.registerNewItem(
                "Cigarette Pack",
                "Marlboro",
                PrimaryCategory.TOBACCO,
                SecondaryCategory.SMOKING_PRODUCT,
                ItemFunction.CONSUMABLE,
                ConsumptionContext.PERSONAL_USE,
                UsageType.SINGLE_USE,
                Replaceability.MEDIUM,
                1
            );
            
            // Sample 4: Vape device
            int vapeId = itemService.registerNewItem(
                "Vape Pen",
                "Juul",
                PrimaryCategory.TOBACCO,
                SecondaryCategory.ELECTRONIC_SMOKING,
                ItemFunction.CONSUMABLE,
                ConsumptionContext.PERSONAL_USE,
                UsageType.REUSABLE,
                Replaceability.MEDIUM,
                1
            );
        
            
            // Sample 5: Pocket knife (HELD)
            itemService.registerNewItem(
                "Pocket Knife",
                "Swiss Army",
                PrimaryCategory.WEAPON,
                SecondaryCategory.SHARP_OBJECT,
                ItemFunction.TOOL,
                ConsumptionContext.PERSONAL_USE,
                UsageType.REUSABLE,
                Replaceability.LOW,
                1
            );
            
            // Sample 6: Beer can (HELD)
            itemService.registerNewItem(
                "Beer Can",
                "San Miguel",
                PrimaryCategory.ALCOHOL,
                SecondaryCategory.ALCOHOLIC_BEVERAGE,
                ItemFunction.CONSUMABLE,
                ConsumptionContext.BEVERAGE,
                UsageType.SINGLE_USE,
                Replaceability.HIGH,
                6
            );
            
            // Sample 7: Plastic spork (HELD)
            itemService.registerNewItem(
                "Plastic Spork",
                null,
                PrimaryCategory.SINGLE_USE_PLASTIC,
                SecondaryCategory.FOOD_ACCESSORY,
                ItemFunction.UTENSIL,
                ConsumptionContext.FOOD,
                UsageType.SINGLE_USE,
                Replaceability.HIGH,
                5
            );
            
            // Sample 8: Allowed reusable container (HELD)
            itemService.registerNewItem(
                "Reusable Tumbler",
                "Aquaflask",
                PrimaryCategory.ALLOWED,
                SecondaryCategory.BEVERAGE_CONTAINER,
                ItemFunction.CONTAINER,
                ConsumptionContext.BEVERAGE,
                UsageType.REUSABLE,
                Replaceability.LOW,
                1
            );
            
        } catch (IllegalArgumentException e) {
            System.err.println("Error adding sample items: " + e.getMessage());
        }
    }
}