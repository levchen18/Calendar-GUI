package model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Interface for an event in the calendar.
 */
public interface IEvent {
  /**
   * Get the name of events.
   * @return the subject of the events.
   */
  String getSubject();

  /**
   * Get the start time of the event.
   * @return the start time of the events.
   */
  ZonedDateTime getStartDateTime();

  /**
   * Get the end time of the events.
   * @return the end time of the events.
   */
  ZonedDateTime getEndDateTime();

  /**
   * Get the location of the events.
   * @return the location of the events.
   */
  String getLocation();

  /**
   * Get the status of the event.
   * Event can be public or private.
   * @return the status of the event.
   */
  Status getStatus();

  /**
   * Occurring events on the date.
   * @param date the events occurring.
   * @return the date the events occur.
   */
  boolean occursDate(LocalDate date);

  boolean occursBetween(ZonedDateTime from, ZonedDateTime to);

  /**
   * If the event is an all day event.
   * @return an all day event.
   */
  boolean isAllDay();

  /**
   * Gets the events if the events are busy or not.
   * @param time of the events.
   * @return if the event is busy or not.
   */
  boolean busyDuring(ZonedDateTime time);

  /**
   * Shift of the events.
   * @param duration duration of the events.
   * @param newZone the new time zones.
   * @return shift
   */
  IEvent shiftCopy(Duration duration, ZoneId newZone);

  /**
   * Simple getDescription method.
   *
   * @return description.
   */
  String getDescription();
}
