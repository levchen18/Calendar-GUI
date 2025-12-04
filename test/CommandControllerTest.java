import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import controller.CommandController;
import model.CalendarManager;
import model.Event;
import model.Status;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * CommandController Test.
 * Testing if the controller is working properly.
 * Testing is it works with the view and model.
 */
public class CommandControllerTest {
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private CalendarManager manager;

  @Before
  public void setUp() {
    System.setOut(new PrintStream(outContent));
    manager = new CalendarManager();
    manager.createCalendar("Main", ZoneId.of("America/New_York"));
    manager.useCalendar("Main");
    new CommandController(manager);
  }

  /**
   * Tests printing Events Between Dates.
   */
  @Test
  public void testPrintEventsBetweenDates() {
    ZonedDateTime start1 = ZonedDateTime.of(LocalDateTime.
            of(2025, 6, 1, 9, 0),
            ZoneId.of("America/New_York"));
    ZonedDateTime end1 = ZonedDateTime.of(LocalDateTime.
            of(2025, 6, 1, 10, 0),
            ZoneId.of("America/New_York"));
    ZonedDateTime start2 = ZonedDateTime.of(LocalDateTime.
            of(2025, 6, 3, 15, 0),
            ZoneId.of("America/New_York"));
    ZonedDateTime end2 = ZonedDateTime.of(LocalDateTime.
            of(2025, 6, 3, 16, 0),
            ZoneId.of("America/New_York"));

    Event e1 = new Event("Event1", start1, end1, "", Status.Public, "");
    Event e2 = new Event("Event2", start2, end2, "", Status.Public, "");

    manager.addEvent(e1);
    manager.addEvent(e2);
    String printCommand = "print events from 2025-06-01T00:00 to 2025-06-04T00:00";
    CommandController.handle(printCommand);
    String output = outContent.toString().trim();
    assertTrue(output.contains("Event1"));
    assertTrue(output.contains("Event2"));
  }

  /**
   * Tests when not busy.
   */
  @Test
  public void testShowingEventNotBusy() {
    outContent.reset();
    String showCommand = "show status on 2025-06-05T12:00";
    CommandController.handle(showCommand);
    String output = outContent.toString().trim();
    assertEquals("Not busy", output);
  }

  /**
   * Tests when busy.
   */
  @Test
  public void testShowingEventBusy() {
    ZoneId zone = ZoneId.of("America/New_York");
    CalendarManager manager = new CalendarManager();
    manager.createCalendar("Main", zone);
    manager.useCalendar("Main");
    Event e = new Event("TestEvent",
            ZonedDateTime.of(LocalDateTime.of(2025, 6, 5, 9, 0), zone),
            ZonedDateTime.of(LocalDateTime.of(2025, 6, 5, 10, 0), zone),
            "", Status.Public, "");
    manager.addEvent(e);
    CommandController controller = new CommandController(manager);
    String showCommand = "show status on 2025-06-05T09:30";
    CommandController.handle(showCommand);
    String output = outContent.toString().trim();
    assertEquals("Busy", output);
  }

  /**
   * Tests Creating Event.
   */
  @Test
  public void testCreateEvent() {
    ZoneId zone = ZoneId.of("America/New_York");
    CalendarManager manager = new CalendarManager();
    manager.createCalendar("Main", zone);
    manager.useCalendar("Main");
    CommandController controller = new CommandController(manager);

    String createCommand = "add event \"Meeting\" from 2025-06-05T09:00 to 2025-06-05T10:00";
    CommandController.handle(createCommand);

    ZonedDateTime start = ZonedDateTime.of(LocalDateTime.of(2025, 6, 5, 9, 0), zone);
    ZonedDateTime end = ZonedDateTime.of(LocalDateTime.of(2025, 6, 5, 10, 0), zone);

    var events = manager.getEventsBetween(start, end);
    boolean found = events.stream().anyMatch(e -> e.getSubject().equals("Meeting")
            && e.getStartDateTime().equals(start)
            && e.getEndDateTime().equals(end));
    assertTrue("Event 'Meeting' should be added", found);
  }
}