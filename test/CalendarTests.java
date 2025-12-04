import controller.CommandController;
import model.CalendarManager;
import model.CalendarModel;
import model.Event;
import model.EventSeries;
import model.IEvent;
import model.Status;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


/**
 * Testing calendar.
 */
public class CalendarTests {

  @Test
  public void scriptParsesWithoutError() {
    String script =
            "create calendar --name test --timezone America/New_York\n" +
                    "use calendar --name test\n" +
                    "create event \"foo\" from 2025-09-01T10:00 to 2025-09-01T11:00\n" +
                    "quit\n";

    CalendarManager cm = new CalendarManager();
    CommandController cc = new CommandController(cm);

    try {
      cc.runScript(new StringReader(script));
    } catch (Exception ex) {
      fail("Script parsing threw an unexpected exception: " + ex);
    }
  }

  @Test
  public void testChangeCalendarName() {
    CalendarManager mgr = new CalendarManager();
    ZoneId zone = ZoneId.of("America/New_York");
    mgr.createCalendar("old", zone);
    mgr.useCalendar("old");

    assertTrue(mgr.changingCalendarName("old", "new"));
    assertTrue(mgr.getCalendarNames().contains("new"));
    assertFalse(mgr.getCalendarNames().contains("old"));
    assertEquals("new", mgr.getCurrentCalendarName());
  }

  @Test(expected = IllegalArgumentException.class)
  public void runScriptsInvalidCommand() {
    String badScript =
            "create calendar --name test --timezone America/New_York\n" +
                    "use calendar --name test\n" +
                    "bad_command right here\n";

    CalendarManager cm = new CalendarManager();
    CommandController cc = new CommandController(cm);

    cc.runScript(new StringReader(badScript));
  }

  @Test
  public void testChangeCalendarTimeZone() {
    CalendarManager mgr = new CalendarManager();
    ZoneId ny = ZoneId.of("America/New_York");
    ZoneId la = ZoneId.of("America/Los_Angeles");

    mgr.createCalendar("cal", ny);
    mgr.useCalendar("cal");

    assertTrue(mgr.editTimeZones("cal", la));
    assertEquals(la, ((CalendarModel) mgr.getCurrentCalendar()).getZoneId());
  }

  @Test
  public void testInvalidTimeZoneChange() {
    CalendarManager mgr = new CalendarManager();
    ZoneId ny = ZoneId.of("America/New_York");

    mgr.createCalendar("cal", ny);

    assertThrows(DateTimeException.class, () -> ZoneId.of("Not/AZone"));
    assertFalse(mgr.editTimeZones("cal", null));
  }

  @Test
  public void eventsSameDay() {
    EventSeries es = new EventSeries(
            "class",
            LocalTime.of(9, 0),
            LocalTime.of(10, 0),
            "", "", Status.Public,
            Set.of(DayOfWeek.MONDAY),
            LocalDate.of(2025, 1, 6),
            8, null);
    assertTrue(es.generateEvents(ZoneId.systemDefault())
            .stream()
            .allMatch(e -> e.getStartDateTime().toLocalDate()
                    .equals(e.getEndDateTime().toLocalDate())));
  }

  @Test
  public void testPrintEventsAfterTZChange() {
    CalendarManager mgr = new CalendarManager();
    CommandController ctrl = new CommandController(mgr);

    ctrl.handle("create calendar --name tz --timezone America/New_York");
    ctrl.handle("use calendar --name tz");
    ctrl.handle("create event \"foo\" from 2025-05-01T09:00 to 2025-05-01T10:00");

    ctrl.handle("edit calendar --name tz --property timezone America/Los_Angeles");

    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    PrintStream oldOut = System.out;
    System.setOut(new PrintStream(bout));

    ctrl.handle("print events on 2025-05-01");
    System.setOut(oldOut);

    String out = bout.toString();
    assertTrue("time should be converted to LA zone", out.contains("06:00"));
    assertTrue(out.contains("foo"));
  }


  @Test
  public void editSingleEventLocation() {
    CalendarModel cm = new CalendarModel(ZoneId.systemDefault());
    ZonedDateTime st = ZonedDateTime.now().plusDays(1);
    Event old = new Event("m", st, st.plusHours(1), "", Status.Public, "");
    cm.addEvent(old);

    Event edited = new Event("m", st, st.plusHours(1), "Zoom", Status.Public, "");
    assertTrue(cm.editEvent("m", st, edited));
    assertEquals("Zoom",
            cm.findEvent("m", st.toLocalDateTime()).get().getLocation());
  }

  @Test
  public void editAllEventsSameSubject() {
    CalendarModel cm = new CalendarModel(ZoneId.systemDefault());
    ZonedDateTime t1 = ZonedDateTime.now().plusDays(1);
    ZonedDateTime t2 = t1.plusDays(2);
    cm.addEvent(new Event("lab", t1, t1.plusHours(1), "", Status.Public, ""));
    cm.addEvent(new Event("lab", t2, t2.plusHours(1), "", Status.Public, ""));

    cm.getEventsBetween(t1.minusDays(1), t2.plusDays(1))
            .forEach(ev -> cm.editEvent(ev.getSubject(), ev.getStartDateTime(),
                    new Event("lab", ev.getStartDateTime(), ev.getEndDateTime(),
                            "", Status.Private, "")));

    assertTrue(cm.getEventsBetween(t1.minusDays(1), t2.plusDays(1))
            .stream().allMatch(ev -> ev.getStatus() == Status.Private));
  }

  @Test
  public void editSeriesFromDate() {
    CalendarModel cm = new CalendarModel(ZoneId.systemDefault());

    EventSeries es = new EventSeries(
            "lecture",
            LocalTime.of(9, 0),
            LocalTime.of(10, 0),
            "", "", Status.Public,
            Set.of(DayOfWeek.MONDAY),
            LocalDate.of(2025, 2, 3),
            5, null);
    es.addTo(cm);

    ZonedDateTime cutoff = ZonedDateTime.of(
            2025, 2, 17, 9, 0, 0, 0, ZoneId.systemDefault());

    cm.getEventsBetween(cutoff, cutoff.plusWeeks(3))
            .stream()
            .filter(ev -> ev.getSubject().equals("lecture"))
            .forEach(ev -> {
              IEvent shorter = new Event(
                      ev.getSubject(),
                      ev.getStartDateTime(),
                      ev.getStartDateTime().plusMinutes(30),
                      ev.getLocation(),
                      ev.getStatus(),
                      "");
              cm.editEvent(ev.getSubject(), ev.getStartDateTime(), shorter);
            });

    long shortened = cm.getEventsBetween(cutoff, cutoff.plusWeeks(3))
            .stream()
            .filter(ev -> ev.getEndDateTime()
                    .equals(ev.getStartDateTime().plusMinutes(30)))
            .count();

    assertEquals(3, shortened);
  }

  @Test
  public void invalidEdit() {
    CalendarManager mgr = new CalendarManager();
    mgr.createCalendar("c", ZoneId.systemDefault());
    mgr.useCalendar("c");

    ZonedDateTime st = ZonedDateTime.now().plusDays(1);
    mgr.addEvent(new Event("foo", st, st.plusHours(1), "", Status.Public, ""));
    assertThrows(IllegalArgumentException.class, () -> mgr.editEvent("foo", st, null));
  }


  @Test
  public void printFormatContainsSubjectAndTime() {
    CalendarManager cm = new CalendarManager();
    CommandController ctrl = new CommandController(cm);
    ctrl.handle("create calendar --name p --timezone America/New_York");
    ctrl.handle("use calendar --name p");
    ctrl.handle("create event \"foo\" from 2025-05-01T09:00 to 2025-05-01T10:00");

    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    PrintStream oldOut = System.out;
    System.setOut(new PrintStream(bout));

    ctrl.handle("print events on 2025-05-01");
    System.setOut(oldOut);

    String out = bout.toString();
    assertTrue(out.contains("foo") && out.contains("09:00"));
  }


  @Test
  public void timeZoneTest1() {
    CalendarManager manager = new CalendarManager();
    ZoneId ny = ZoneId.of("America/New_York");
    manager.createCalendar("src", ny);
    manager.createCalendar("dst", ny);
    manager.useCalendar("src");

    ZonedDateTime s1 = ZonedDateTime.of(2025, 3, 1, 9, 0, 0, 0, ny);
    manager.addEvent(new Event("A", s1, s1.plusHours(1), "", Status.Public, ""));
    ZonedDateTime s2 = ZonedDateTime.of(2025, 3, 1, 14, 0, 0, 0, ny);
    manager.addEvent(new Event("B", s2, s2.plusHours(2), "", Status.Public, ""));

    ZonedDateTime from = ZonedDateTime.of(2025, 3, 1, 0, 0, 0, 0, ny);
    ZonedDateTime to = from.plusDays(1).minusNanos(1);
    ZonedDateTime dstStart = ZonedDateTime.of(2026, 3, 1, 0, 0, 0, 0, ny);

    assertEquals(2, manager.copyEventsBetween(from, to, "dst", dstStart));

    manager.useCalendar("dst");
    assertEquals(2,
            manager.getEventsBetween(dstStart, dstStart.plusDays(1).minusNanos(1)).size());
  }

  @Test
  public void timeZoneTest2() {
    CalendarManager manager = new CalendarManager();
    ZoneId ny = ZoneId.of("America/New_York");
    ZoneId la = ZoneId.of("America/Los_Angeles");
    manager.createCalendar("src", ny);
    manager.createCalendar("dst", la);
    manager.useCalendar("src");

    ZonedDateTime st = ZonedDateTime.of(2025, 2, 10, 10, 0, 0, 0, ny);
    manager.addEvent(new Event("Meeting", st, st.plusHours(1), "", Status.Public, ""));

    ZonedDateTime from = st.toLocalDate().atStartOfDay(ny);
    ZonedDateTime to = from.plusDays(1).minusNanos(1);
    ZonedDateTime dstStart = ZonedDateTime.of(2025, 3, 15, 0, 0, 0, 0, la);

    assertEquals(1, manager.copyEventsBetween(from, to, "dst", dstStart));

    manager.useCalendar("dst");
    IEvent ev = manager.getEventsBetween(dstStart, dstStart.plusDays(1).minusNanos(1)).get(0);
    assertEquals(7, ev.getStartDateTime().getHour());
  }

  @Test
  public void controllerCreatesEvent() {
    CalendarManager cm = new CalendarManager();
    CommandController cc = new CommandController(cm);
    cc.handle("create calendar --name h --timezone America/New_York");
    cc.handle("use calendar --name h");
    cc.handle("create event \"demo\" from 2026-01-10T12:00 to 2026-01-10T13:00");

    ZoneId ny = ZoneId.of("America/New_York");
    ZonedDateTime day = ZonedDateTime.of(2026, 1, 10, 0, 0, 0, 0, ny);
    assertEquals(1, cm.getEventsOn(day, day.plusDays(1).minusNanos(1)).size());
  }


}
