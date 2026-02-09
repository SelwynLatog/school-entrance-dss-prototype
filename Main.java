//Main.java
import test.TestLoader;
import ui.RoleMenu;
//TODO: Student Model, Analysis Engine

public class Main {
    public static void main(String[] args) {
        try {
            RoleMenu.start();
        } catch (Exception e) {
            System.err.println("Fatal error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}