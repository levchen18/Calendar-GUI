import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import controller.CommandController;
import model.CalendarManager;
import view.HeadlessView;

import static org.junit.Assert.assertTrue;

/**
 * Testing the headless view of the program.
 */
public class HeadlessViewTest {

  @Test
  public void runsScript() throws Exception {
    File script = Files.createTempFile("calendar-script", ".txt").toFile();
    try (PrintWriter pw = new PrintWriter(script)) {
      pw.println("create calendar --name test --timezone America/New_York");
      pw.println("use calendar test");
      pw.println("add event Meeting from 2025-06-10T09:00 to 2025-06-10T10:00");
      pw.println("print events on 2025-06-10");
      pw.println("quit");
    }

    ByteArrayOutputStream output = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    System.setOut(new PrintStream(output));

    try {
      CalendarManager manager = new CalendarManager();
      HeadlessView hv = new HeadlessView(new CommandController(manager), script);
      hv.run();
    } finally {
      System.setOut(originalOut);
    }

    String result = output.toString();
    assertTrue(result.contains("Meeting"));
    assertTrue(script.delete());
  }
}
