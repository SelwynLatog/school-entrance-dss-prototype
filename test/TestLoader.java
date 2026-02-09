//TestLoader.java
package test;

import storage.ItemLog;
import service.ItemService;
import service.StudentService;
import ui.ViewLogScreen;
import enums.*;
import java.io.IOException;

public class TestLoader {

    public static void loadViewLog() {
        try {
            ItemLog itemLog = new ItemLog();
            ItemService itemService = new ItemService(itemLog, null); // TODO: wire up StudentService
            
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
    
    /**
     * Adds sample students to the system.
     * Creates a mix of enrolled students and outsiders with different programs.
     */
    public static void addSampleStudents(StudentService studentService) {
        try {
           
            studentService.registerNewStudent(
                "2026-1001",
                "Ernest Canlas",
                "Computer Science",
                3,
                StudentStatus.ENROLLED
            );
            
            studentService.registerNewStudent(
                "2026-1002",
                "Yesh Uwu",
                "Information Technology",
                2,
                StudentStatus.ENROLLED
            );
            
            studentService.registerNewStudent(
                "2026-1003",
                "Jubay Francis",
                "Computer Engineering",
                4,
                StudentStatus.ENROLLED
            );
            
            studentService.registerNewStudent(
                "2026-0505",
                "Carlos Mendoza",
                "Information Technology",
                5,
                StudentStatus.SUSPENDED
            );
            
            studentService.registerNewStudent(
                "V-2026-001",
                "Robert Chen",
                "N/A",
                0,
                StudentStatus.OUTSIDER
            );
            
            studentService.registerNewStudent(
                "2026-1004",
                "Anna Garcia",
                "Computer Science",
                1,
                StudentStatus.ENROLLED
            );
            
        } catch (IllegalArgumentException e) {
            System.err.println("Error adding sample students: " + e.getMessage());
        }
    }
    
    public static void addSampleItems(ItemService itemService) {
        try {
            // Sample 1: Plastic water bottle (Juan's item)
            itemService.registerNewItem(
                "2026-1001", 
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
            
            // Sample 2: Styrofoam food container
            itemService.registerNewItem(
                "2026-1002",
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
            
            // Sample 3: Cigarettes
            itemService.registerNewItem(
                "2026-1003",
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
            itemService.registerNewItem(
                "2026-0505",
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
            
            // Sample 5: Pocket knife
            itemService.registerNewItem(
                "V-2026-001", //Outsider
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
            
            // Sample 6: Beer cans
            itemService.registerNewItem(
                "2026-1004",
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
            
            // Sample 7: Plastic spork
            itemService.registerNewItem(
                "2026-1001", 
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
            
            itemService.registerNewItem(
                "2026-1002",
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