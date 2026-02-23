//Main.java
import test.TestLoader;
import ui.RoleMenu;
//TODO: Override Func -guard/admin can manually change the decision. So if the engine says DISALLOW but the guard/admin has context the system doesn't (e.g. the item is a teacher's, or it's a special event day), they can flip it to ALLOW or CONDITIONAL and log a reason.

//TODO: View Details - Audit trail log eg. Override Audits

//TODO:Analysis Engine- Pattern Detection/ Student Risk Profiling/ Trend Reports

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