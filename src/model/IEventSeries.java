package model;

import java.time.ZoneId;
import java.util.List;

/**
 * Represents a series of events that repeat based on certain rules.
 */
public interface IEventSeries {
  /**
   * Generates the list of individual events in the series.
   *
   * @param zoneId the time zone to use when generating start and end times.
   * @return a list of generated events in the series.
   */
  List<Event> generateEvents(ZoneId zoneId);

  /**
   * Adds the generated event series to the given calendar model.
   *
   * @param calendar the calendar model to add events to.
   * @return the number of events successfully added.
   * @throws IllegalStateException if any event is a duplicate.
   */
  int addTo(ICalendarModel calendar);
}
