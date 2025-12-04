package view;

import controller.CommandController;

import java.util.Objects;
import java.util.Scanner;

/**
 * The interactView.
 * Uses a human input to run the program of the calendar.
 */
public final class InteractiveView {

  private final CommandController controller;

  /**
   * The constructor for the class.
   *
   * @param controller testing using controller.
   */
  public InteractiveView(CommandController controller) {
    this.controller = Objects.requireNonNull(controller);
  }

  /**
   * run the program in interactive mode.
   */
  public void run() {
    try (Scanner in = new Scanner(System.in)) {
      System.out.println("Calendar – interactive mode. Type quit to exit");
      while (true) {
        System.out.print("> ");
        if (!in.hasNextLine()) {
          break;
        }
        String line = in.nextLine().trim();
        if (line.equalsIgnoreCase("q") || line.equalsIgnoreCase("quit")) {
          break;
        }
        try {
          String out = controller.handle(line);
          if (!out.isBlank()) {
            System.out.println(out);
          }
        } catch (IllegalArgumentException ex) {
          System.out.println(ex.getMessage());
        }
      }
    }
    System.out.println("Calender Closed");
  }
}
