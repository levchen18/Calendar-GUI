import model.IEvent;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import model.Event;
import model.CalendarModel;
import model.Status;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * Test class for the CalenderModel Implementation.
 */
public class CalenderModelTest {
  private CalendarModel calendar;
  private final ZoneId zone = ZoneId.of("America/New_York");

  /**
   * Setup of a new calendar.
   */
  @Before
  public void setUp() {
    calendar = new CalendarModel(zone);
  }

  /**
   * Tests duplicate Events.
   */
  @Test
  public void addEventThenDuplicate() {
    ZoneId zone = ZoneId.of("America/New_York");
    CalendarModel model = new CalendarModel(zone);
    LocalDateTime startLocal = LocalDateTime.parse("2025-01-01T08:00");
    LocalDateTime endLocal = LocalDateTime.parse("2025-01-01T09:00");
    ZonedDateTime startTime = startLocal.atZone(zone);
    ZonedDateTime endTime = endLocal.atZone(zone);
    Event original = new Event(
            "Meeting", startTime, endTime,
            "Office", Status.Public, "Team sync-up"
    );
    assertTrue(model.addEvent(original));
    Event duplicate = new Event(
            "Meeting", startTime, endTime,
            "Office", Status.Public, "Team sync-up"
    );
    assertFalse(model.addEvent(duplicate));
  }

  /**
   * Tests the sorting.
   */
  @Test
  public void eventsOnSortedByStart() {
    ZonedDateTime earlyStart = LocalDateTime.of(2025, 6, 10, 9, 0).atZone(zone);
    ZonedDateTime earlyEnd = LocalDateTime.of(2025, 6, 10, 10, 0).atZone(zone);
    ZonedDateTime lateStart = LocalDateTime.of(2025, 6, 10, 11, 0).atZone(zone);
    ZonedDateTime lateEnd = LocalDateTime.of(2025, 6, 10, 12, 0).atZone(zone);

    Event early = new Event("Early", earlyStart, earlyEnd, null, Status.Public, null);
    Event late = new Event("Late", lateStart, lateEnd, null, Status.Private, null);

    calendar.addEvent(late);
    calendar.addEvent(early);

    List<IEvent> list = calendar.getEventsOn(LocalDate.of(2025, 6, 10));
    assertEquals(List.of(early, late), list);
  }

  /**
   * Tests event boundaries.
   */
  @Test
  public void busyBoundary() {
    ZonedDateTime start = LocalDateTime.of(2025, 6, 10, 9, 0).atZone(zone);
    ZonedDateTime end = LocalDateTime.of(2025, 6, 10, 10, 0).atZone(zone);

    Event event = new Event("Busy", start, end, null, Status.Public, null);
    calendar.addEvent(event);

    ZonedDateTime insideTime = LocalDateTime.of(2025, 6, 10, 9, 30).atZone(zone);
    ZonedDateTime boundaryTime = LocalDateTime.of(2025, 6, 10, 10, 0).atZone(zone);

    assertTrue(calendar.busyDuring(insideTime));
    assertFalse(calendar.busyDuring(boundaryTime));
  }
}
