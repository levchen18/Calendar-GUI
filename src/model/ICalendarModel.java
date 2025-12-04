package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Interface for CalendarModel.
 * Allows abstraction over event storage and retrieval operations.
 */
public interface ICalendarModel {
  /**
   * Add the events to the calendar.
   * @param event of the calendar.
   * @return the events added to the calendar.
   */
  boolean addEvent(IEvent event);

  /**
   * Add the events to the calendar.
   * @param event of calendar.
   * @return the removed events.
   */
  boolean removeEvent(IEvent event);

  /**
   * The date event is happening.
   * @param date date of the calendar.
   * @return the date the event is happening.
   */
  List<IEvent> getEventsOn(LocalDate date);

  /**
   * The events happening between time.
   * @param start start of the event.
   * @param end end of the event.
   * @return the events between time.
   */
  List<IEvent> getEventsBetween(ZonedDateTime start, ZonedDateTime end);

  /**
   * The zoneID the event take place.
   * @return the zone the event is in.
   */
  ZoneId getZoneId();

  /**
   * Set the time zone for event.
   * @param zoneId zone for the events.
   */
  void setZoneId(ZoneId zoneId);

  /**
   * Finding events based on the event name and start time.
   * @param subject name of the events
   * @param startTime start time of the events.
   * @return the event founded based on start time and name.
   */
  Optional<IEvent> findEvent(String subject, LocalDateTime startTime);

  /**
   * Edit events based on name, start time, and replacement.
   * @param subject name of the event.
   * @param startTime starting time of event.
   * @param replacement the new name or time of event.
   * @return the updated event.
   */
  boolean editEvent(String subject,
                    ZonedDateTime startTime,
                    IEvent replacement);

  /**
   * Event between events.
   * @param from from start to end.
   * @param to start to end.
   * @param mutator changing the events.
   * @return the edit between events.
   */
  boolean editEventBetween(ZonedDateTime from, ZonedDateTime to,
                           java.util.function.Consumer<IEvent> mutator);

  /**
   * The edited series of events.
   * @param subject name of the event.
   * @param from from start to end.
   * @param mutator changes to the event.
   * @return the edited series of event.
   */
  boolean editSeriesFrom(String subject, ZonedDateTime from,
                         java.util.function.Consumer<IEvent> mutator);

  /**
   * The event is busy or not.
   * @param when the event is happening.
   * @return if the event is busy or not.
   */
  boolean busyDuring(ZonedDateTime when);
}
