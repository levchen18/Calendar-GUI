import org.junit.Before;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import model.CalendarModel;
import model.EventSeries;
import model.Status;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Testing event series.
 * Events repeating.
 */
public class EventSeriesTest {
  private CalendarModel cal;

  /**
   * Set up for the calendar.
   */
  @Before
  public void setUp() {
    cal = new CalendarModel(ZoneId.systemDefault());
  }

  @Test
  public void exactNumberOfOccurrences() {
    EventSeries series = new EventSeries("Test", LocalTime.of(10, 0),
            LocalTime.of(10, 30), null, null, Status.Public,
            EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY),
            LocalDate.parse("2025-05-05"), 6, null);
    int added = series.addTo(cal);
    assertEquals(6, added);

    int count = 0;
    LocalDate date = LocalDate.parse("2025-05-05");
    while (count < 6) {
      List<?> events = cal.getEventsOn(date);
      for (Object eObj : events) {
        model.Event e = (model.Event) eObj;
        if ("Test".equals(e.getSubject())) {
          count++;
        }
      }
      date = date.plusDays(1);
    }
    assertEquals(6, count);
  }

  @Test
  public void generatesUntilDate() {
    EventSeries series = new EventSeries("Test", LocalTime.of(9, 0),
            LocalTime.of(10, 0), null, null, Status.Public,
            EnumSet.of(DayOfWeek.MONDAY),
            LocalDate.parse("2025-09-01"), null, LocalDate.parse("2025-09-29"));
    int added = series.addTo(cal);
    assertEquals(5, added);

    List<?> lastDayEvents = cal.getEventsOn(LocalDate.parse("2025-09-29"));
    assertEquals(1, lastDayEvents.size());
    model.Event lastEvent = (model.Event) lastDayEvents.get(0);
    assertEquals("Test", lastEvent.getSubject());
  }

  @Test
  public void addingSeriesTwice() {
    EventSeries series = new EventSeries("Test", LocalTime.of(7, 0),
            LocalTime.of(8, 0), null, null, Status.Public,
            EnumSet.of(DayOfWeek.MONDAY),
            LocalDate.parse("2025-06-02"), 3, null);
    series.addTo(cal);
    try {
      series.addTo(cal);
      fail("Expected IllegalStateException");
    } catch (IllegalStateException expected) {
      int count = 0;
      LocalDate date = LocalDate.parse("2025-06-02");
      int checked = 0;
      while (checked < 3) {
        List<?> events = cal.getEventsOn(date);
        for (Object eObj : events) {
          model.Event e = (model.Event) eObj;
          if ("Test".equals(e.getSubject())) {
            count++;
          }
        }
        date = date.plusWeeks(1);
        checked++;
      }
      assertEquals(3, count);
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void constructor_throwsIfNoRepeatCountOrUntilDate() {
    new EventSeries("Test", LocalTime.of(10, 0), LocalTime.of(11, 0),
            "Loc", "Desc", Status.Public,
            Set.of(DayOfWeek.MONDAY), LocalDate.now(),
            null, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void constructor_throwsIfEndTimeNotAfterStartTime() {
    new EventSeries("Test", LocalTime.of(11, 0), LocalTime.of(10, 0),
            "Loc", "Desc", Status.Public,
            Set.of(DayOfWeek.MONDAY), LocalDate.now(),
            5, null);
  }
}