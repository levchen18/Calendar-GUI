package view;

import controller.CommandController;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

/**
 * HeadlessView.
 * Uses a file to run the program of the calendar.
 */
public final class HeadlessView {

  private final CommandController controller;
  private final File scriptFile;

  /**
   * HeadlessView Constructor.
   *
   * @param controller the controller of the program.
   * @param scriptFile the file that is used to run the program.
   */
  public HeadlessView(CommandController controller, File scriptFile) {
    this.controller = Objects.requireNonNull(controller);
    this.scriptFile = Objects.requireNonNull(scriptFile);
  }

  /**
   * Run the program in headless mode.
   */
  public void run() {
    try (FileReader fileReader = new FileReader(scriptFile)) {
      controller.runScript(fileReader);
    } catch (IOException e) {
      System.err.println("Cannot read script: " + e.getMessage());
    }
  }
}
