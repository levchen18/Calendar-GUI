package model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Event class that organizes the events within a calendar.
 * Subject, start and end date of event.
 * Location and status are not required.
 * Adds a single event to the calendar.
 */
public class Event implements IEvent {
  private final String subject;
  private final ZonedDateTime startDateTime;
  private final ZonedDateTime endDateTime;
  private final String location;
  private final Status status;
  private final String description;
  private final boolean allDay;

  /**
   * Required constructor for an Event.
   * Throw Illegal Arguments if requirements are not entered.
   */
  public Event(String subject, ZonedDateTime startDateTime,
               ZonedDateTime endDateTime, String location,
               Status status, String description) {
    if (subject == null || startDateTime == null) {
      throw new IllegalArgumentException("Subject and startDateTime are required");
    }

    boolean allDayCheck = false;
    if (endDateTime == null) {
      allDayCheck = true;
      LocalDate date = startDateTime.toLocalDate();

      ZoneId zoneId = startDateTime.getZone();
      startDateTime = ZonedDateTime.of(date, LocalTime.of(8, 0), zoneId);
      endDateTime = ZonedDateTime.of(date, LocalTime.of(17, 0), zoneId);
    }

    if (endDateTime.isBefore(startDateTime)) {
      throw new IllegalArgumentException("End DateTime must be after Start DateTime");
    }

    this.subject = subject.trim();
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.location = location;
    this.status = status;
    this.description = description;
    this.allDay = allDayCheck;
  }

  /**
   * Simple getSubject method.
   *
   * @return subject.
   */
  public String getSubject() {
    return subject;
  }

  /**
   * Simple getStartDateTime method.
   *
   * @return startDateTime.
   */
  public ZonedDateTime getStartDateTime() {
    return startDateTime;
  }

  /**
   * Simple getEndDateTime method.
   *
   * @return EndDateTime.
   */
  public ZonedDateTime getEndDateTime() {
    return endDateTime;
  }

  /**
   * Simple isAllDay boolean method.
   *
   * @return allDay.
   */
  public boolean isAllDay() {
    return allDay;
  }

  /**
   * Simple getDescription method.
   *
   * @return description.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Simple getLocation method.
   *
   * @return location.
   */
  public String getLocation() {
    return location;
  }

  /**
   * Simple getStatus method.
   *
   * @return status.
   */
  public Status getStatus() {
    return status;
  }

  /**
   * Equals method. Checks if two events are the same.
   *
   * @param other variable used to compare 2 objects.
   * @return true or false.
   */
  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof Event)) {
      return false;
    }
    return subject.equals(((Event) other).subject) &&
            startDateTime.equals(((Event) other).startDateTime) &&
            endDateTime.equals(((Event) other).endDateTime);
  }

  /**
   * toString method. Puts together an event as a string.
   *
   * @return string.
   */
  @Override
  public String toString() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z");
    String start = startDateTime.format(formatter);
    String end = endDateTime != null ? endDateTime.format(formatter) : "(All Day)";
    String loc = (location != null && !location.isBlank()) ? " at " + location : "";
    String stat = status != null ? " [" + status + "]" : "";
    String desc = (description != null && !description.isBlank())
            ? "\nDescription: " + description : "";
    return String.format("%s%s\nStart: %s\nEnd: %s%s",
            subject,
            stat,
            start,
            end,
            loc + desc);
  }

  /**
   * Hashcode Method.
   * Setting up the object by having a name, startDateTime, and endDateTime.
   *
   * @return int.
   */
  @Override
  public int hashCode() {
    return Objects.hash(subject, startDateTime, endDateTime);
  }

  /**
   * occursDate helper method. Checks if an event occurs then.
   *
   * @return true or false.
   */
  public boolean occursDate(LocalDate date) {
    return !startDateTime.toLocalDate().isAfter(date)
            && !endDateTime.toLocalDate().isBefore(date);
  }

  @Override
  public boolean occursBetween(ZonedDateTime from, ZonedDateTime to) {
    return false;
  }

  /**
   * busyDuring helper method. Checks if the slot is busy
   *
   * @return true or false.
   */
  public boolean busyDuring(ZonedDateTime time) {
    return !time.isBefore(startDateTime) && time.isBefore(endDateTime);
  }

  /**
   * Copy the events of from the calendar.
   *
   * @param duration time the events last.
   * @param newZone  new time zone.
   * @return copy version of the events.
   */
  @Override
  public IEvent shiftCopy(Duration duration, ZoneId newZone) {
    ZonedDateTime newStart = this.startDateTime.plus(duration)
            .withZoneSameInstant(newZone);
    ZonedDateTime newEnd = this.endDateTime.plus(duration)
            .withZoneSameInstant(newZone);

    return new Event(this.subject,
            newStart, newEnd, this.location, this.status, this.description);
  }
}

