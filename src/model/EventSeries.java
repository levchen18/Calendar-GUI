package model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * EventSeries Class.
 * A series of events that's occurring.
 * Some might last more than a day.
 * Some might repeat.
 * Adds a series of events to the calendar.
 */
public class EventSeries implements IEventSeries {
  private final String subject;
  private final LocalTime startTime;
  private final LocalTime endTime;
  private final String location;
  private final String description;
  private final Status status;
  private final Set<DayOfWeek> repeatDays;
  private final LocalDate startDate;
  private final Integer repeatCount;
  private final LocalDate untilDate;

  /**
   * Constructor for Event series.
   * Throw Illegal Arguments if requirements are not entered.
   *
   * @param subject     event name.
   * @param startTime   start time of event.
   * @param endTime     end time of event.
   * @param location    location of event.
   * @param description description of event.
   * @param status      public or private event.
   * @param repeatDays  repeating the days.
   * @param startDate   start time.
   * @param repeatCount repeating the count.
   * @param untilDate   when does it end?
   */
  public EventSeries(String subject, LocalTime startTime, LocalTime endTime,
                     String location, String description, Status status,
                     Set<DayOfWeek> repeatDays, LocalDate startDate, Integer repeatCount,
                     LocalDate untilDate) {

    if (repeatCount == null && untilDate == null) {
      throw new IllegalArgumentException("Either repeat date or until date is null");
    }

    if (subject.isBlank()) {
      throw new IllegalArgumentException("Subject blank");
    }
    if (repeatDays.isEmpty()) {
      throw new IllegalArgumentException("repeatDays empty");
    }
    if (!endTime.isAfter(startTime)) {
      throw new IllegalArgumentException("endTime must be after startTime");
    }

    if (repeatCount != null && repeatCount <= 0) {
      throw new IllegalArgumentException("repeatCount must be > 0");
    }
    if (untilDate != null && untilDate.isBefore(startDate)) {
      throw new IllegalArgumentException("untilDate before startDate");
    }

    this.subject = subject;
    this.startTime = startTime;
    this.endTime = endTime;
    this.location = location;
    this.description = description;
    this.status = status;
    this.repeatDays = repeatDays;
    this.startDate = startDate;
    this.repeatCount = repeatCount;
    this.untilDate = untilDate;
  }

  /**
   * Generate the events that are happening.
   * It might occur more than once.
   * Events can repeat for a number of days.
   *
   * @return the generated events.
   */
  public List<Event> generateEvents(ZoneId zoneId) {
    List<Event> events = new ArrayList<>();
    LocalDate currentDate = startDate;
    int numberOfTimes = 0;
    while ((repeatCount != null && numberOfTimes < repeatCount) ||
            (untilDate != null && !currentDate.isAfter(untilDate))) {
      if (repeatDays.contains(currentDate.getDayOfWeek())) {
        ZonedDateTime startDateTime = ZonedDateTime.of(currentDate, startTime, zoneId);
        ZonedDateTime endDateTime = ZonedDateTime.of(currentDate, endTime, zoneId);

        Event event = new Event(subject, startDateTime, endDateTime, location, status, description);
        events.add(event);
        numberOfTimes++;
      }
      currentDate = currentDate.plusDays(1);
    }
    return events;
  }

  /**
   * This method adds on a series of events to the calendar.
   * Error if duplicate event is founded.
   *
   * @param calendar calendar model.
   * @return return the adding of events to the calendar.
   */
  public int addTo(ICalendarModel calendar) {
    List<Event> list = generateEvents(calendar.getZoneId());
    List<Event> added = new ArrayList<>();

    for (Event e : list) {
      if (calendar.addEvent(e)) {
        added.add(e);
      } else {
        for (Event ev : added) {
          calendar.removeEvent(ev);
        }
        throw new IllegalStateException("Duplicate event found while adding series");
      }
    }
    return list.size();
  }
}
