import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import model.Event;
import model.Status;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Testing the single event class.
 * No repeats.
 */
public class EventTest {

  private final ZoneId zone = ZoneId.of("America/New_York");

  @Test(expected = IllegalArgumentException.class)
  public void rejectNullSubject() {
    new Event(null,
            ZonedDateTime.now(zone),
            ZonedDateTime.now(zone).plusHours(1),
            null, null, null);
  }

  @Test
  public void nullEndAllDay() {
    ZonedDateTime start = ZonedDateTime.of(LocalDateTime.parse("2025-12-25T00:00"), zone);
    Event e = new Event("Christmas", start, null, null, null, null);

    assertTrue(e.isAllDay());
    assertEquals(ZonedDateTime.of(start.toLocalDate().atTime(8, 0), zone), e.getStartDateTime());
    assertEquals(ZonedDateTime.of(start.toLocalDate().atTime(17, 0), zone), e.getEndDateTime());
  }

  @Test
  public void occursDateAndBusyDuringTest() {
    ZonedDateTime start = ZonedDateTime.of(LocalDateTime.parse("2025-06-10T09:00"), zone);
    ZonedDateTime end = ZonedDateTime.of(LocalDateTime.parse("2025-06-10T10:00"), zone);

    Event e = new Event("Meeting", start, end, null, null, null);

    assertTrue(e.occursDate(LocalDate.parse("2025-06-10")));
    assertFalse(e.occursDate(LocalDate.parse("2025-06-11")));

    assertTrue(e.busyDuring(ZonedDateTime.of(LocalDateTime.parse("2025-06-10T09:30"), zone)));
    assertFalse(e.busyDuring(ZonedDateTime.of(LocalDateTime.parse("2025-06-10T10:00"), zone)));
  }

  @Test
  public void equalityUsesSubjectStartEnd() {
    ZonedDateTime start = ZonedDateTime.of(LocalDateTime.parse("2025-06-10T09:00"), zone);
    ZonedDateTime end = start.plusHours(1);

    Event a = new Event("Test", start, end, null, Status.Public, "x");
    Event b = new Event("Test", start, end, "Room 1", Status.Private, null);
    Event c = new Event("Test", start.plusHours(1), end.plusHours(1), null, null, null);

    assertEquals(a, b);
    assertEquals(a.hashCode(), b.hashCode());
    assertNotEquals(a, c);
  }


  @Test
  public void occursDateFalseOutsideBounds() {
    ZonedDateTime start = ZonedDateTime.of(2025, 6, 10, 9, 0, 0, 0, zone);
    ZonedDateTime end = start.plusHours(1);
    Event e = new Event("Meeting", start, end, null, null, null);

    assertFalse(e.occursDate(LocalDate.of(2025, 6, 11)));
  }
}