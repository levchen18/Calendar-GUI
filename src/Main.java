import controller.CommandController;
import controller.GUIController;
import model.CalendarManager;
import view.InteractiveView;
import view.HeadlessView;
import view.CalendarGUI;

import java.io.File;

/**
 * Main class for the Calendar app.
 * Supports three modes: Headless, Interactive, GUI.
 * - Interactive mode: Runs a live terminal interface.
 * - Headless mode: Runs a list of commands from a script file.
 * - GUI mode: Launches a graphical interface using Java Swing.
 */
public class Main {
  /**
   * Main Method that allows the program to start in your desired mode.
   * Supports three modes: Headless, Interactive, GUI.
   * - Interactive mode: Runs a live terminal interface.
   * - Headless mode: Runs a list of commands from a script file.
   * - GUI mode: Launches a graphical interface using Java Swing.
   *
   * @param args the command that chooses the desired mode.
   */
  public static void main(String[] args) {
    if (args.length < 2 || !args[0].equalsIgnoreCase("--mode")) {
      System.err.println("Usage: --mode interactive | headless <file> | gui");
      System.exit(1);
    }

    CalendarManager calendarManager = new CalendarManager();
    String mode = args[1].toLowerCase();

    switch (mode) {
      case "interactive":
        CommandController ctrl = new CommandController(calendarManager);
        new InteractiveView(ctrl).run();
        break;

      case "headless":
        if (args.length < 3) {
          System.err.println("Headless mode needs a file to run");
          System.exit(1);
        }
        File script = new File(args[2]);
        CommandController headlessCtrl = new CommandController(calendarManager);
        new HeadlessView(headlessCtrl, script).run();
        break;

      case "gui":
        CalendarGUI gui = new CalendarGUI();
        GUIController guiCtrl = new GUIController(calendarManager, gui);
        gui.setController(guiCtrl);
        gui.setVisible(true);
        break;

      default:
        System.err.println("Invalid mode: " + args[1]);
        System.exit(1);
    }
  }
}
