package model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * This class will manage the action of creating multiple calendars.
 * Users are able to create, delete, change names, and etc.
 * Creating more than 1 calendar, as it has many possibilities as well.
 */
public class CalendarManager {
  private Map<String, ICalendarModel> calendars = new HashMap<>();
  private String calendarName;

  /**
   * Constructor for the Calendar Manager class.
   * It's set to default because no information is entered yet.
   * Set up of creating a new calendar.
   */
  public CalendarManager() {
    createCalendar("default", ZoneId.systemDefault());
    calendarName = "default";
  }

  /**
   * This will create a new calendar.
   * Now users are allowed to have multiple calendars.
   *
   * @param subject the subject or name of the calendar.
   * @param zoneId  the time zone the event is taking place in.
   * @return the calendar that was being created.
   */
  public boolean createCalendar(String subject, ZoneId zoneId) {
    if (subject == null || subject.isEmpty() || calendars.containsKey(subject)) {
      return false;
    }
    calendars.put(subject, new CalendarModel(zoneId));
    return true;
  }

  /**
   * Deletes the specified calendar if it exists.
   *
   * @param subject the name of the calendar to delete.
   * @return true if it was deleted successfully, false otherwise.
   */
  public boolean deleteCalendar(String subject) {
    if (!calendars.containsKey(subject)) {
      return false;
    }
    calendars.remove(subject);
    if (subject.equals(calendarName)) {
      calendarName = calendars.keySet().stream()
              .filter(n -> !n.equals(subject))
              .findFirst()
              .orElse(null);
    }
    return true;
  }

  /**
   * Simple get method to get the current calendar.
   *
   * @return the current calendar name.
   */
  public String getCurrentCalendarName() {
    return calendarName;
  }


  /**
   * This allows users to edit the calendar.
   * Users are allowed to change time zones and other parts of editing.
   *
   * @param subject the name of the event/calendar.
   * @param zoneId  the zone the calendar is in.
   * @return true or false if editing was successful.
   */
  public boolean editTimeZones(String subject, ZoneId zoneId) {
    if (!calendars.containsKey(subject)) {
      return false;
    }
    ICalendarModel calendarModel = calendars.get(subject);
    calendarModel.setZoneId(zoneId);
    return true;
  }

  /**
   * This allows users to change the name of the calendar.
   * The old Name will get replaced by the new one if valid.
   *
   * @param oldName old calendar name.
   * @param newName new calendar name.
   * @return the updated calendar name if valid.
   */
  public boolean changingCalendarName(String oldName, String newName) {
    if (!calendars.containsKey(oldName) || calendars.containsKey(newName)
            || newName == null || newName.isBlank()) {
      return false;
    }
    ICalendarModel calendar = calendars.remove(oldName);
    calendars.put(newName, calendar);
    if (oldName.equals(calendarName)) {
      calendarName = newName;
    }
    return true;
  }

  /**
   * Get the name of the calendar.
   *
   * @return the name of the calendar.
   */
  public Set<String> getCalendarNames() {
    return Collections.unmodifiableSet(calendars.keySet());
  }

  /**
   * Get the calendar that was created.
   *
   * @param subject name of the calendar.
   * @return the calendar that was created.
   */
  public ICalendarModel getCalendar(String subject) {
    return calendars.get(calendarName);
  }

  /**
   * This allows users to use the calendar.
   *
   * @param subject name of the calendar.
   * @return if a user is able to use the calendar.
   */
  public boolean useCalendar(String subject) {
    if (!calendars.containsKey(subject)) {
      return false;
    }
    calendarName = subject;
    return true;
  }

  /**
   * Gets the current calendar the user is using.
   * The Current calendar user is using.
   *
   * @return the current calendar that's being used/
   */
  public ICalendarModel getCurrentCalendar() {
    if (calendarName == null) {
      return null;
    }
    return calendars.get(calendarName);
  }

  /**
   * Gets the name of the calendar.
   *
   * @return the name of the calendar.
   */
  public String getCalendarName() {
    return calendarName;
  }

  /**
   * Checks if the event is busy.
   *
   * @param time check the time when it occurs.
   * @return if the event is busy or not.
   */
  public boolean busyDuring(ZonedDateTime time) {
    ICalendarModel calendar = getCurrentCalendar();
    return calendar != null && calendar.busyDuring(time);
  }

  /**
   * Finds events within a calendar.
   * Find events based on start time.
   *
   * @param subject   the name of the events.
   * @param startTime start time of the event.
   * @return the event that is within the calendar.
   */
  public Optional<IEvent> findEvent(String subject, LocalDateTime startTime) {
    ICalendarModel calendar = getCurrentCalendar();
    return calendar == null ? Optional.empty() : calendar.findEvent(subject, startTime);
  }

  /**
   * Gets the event time between events.
   *
   * @param start start time.
   * @param end   end time.
   * @return return the events within a certain time.
   */
  public List<IEvent> getEventsBetween(ZonedDateTime start, ZonedDateTime end) {
    ICalendarModel calendar = getCurrentCalendar();
    return calendar == null ? List.of() : calendar.getEventsBetween(start, end);
  }

  /**
   * Edit the events.
   *
   * @param subject   name of event.
   * @param startTime start date of the event.
   * @param newEvent  ending of the event.
   * @return the edited version of the event.
   */
  public boolean editEvent(String subject, ZonedDateTime startTime, IEvent newEvent) {
    ICalendarModel calendar = getCurrentCalendar();
    return calendar != null ? calendar.editEvent(subject, startTime, newEvent) : false;
  }

  /**
   * Add events if it's not on the same day.
   *
   * @param event event within the calendar.
   * @return compared the event to make sure it's not on the same day.
   */
  public boolean addEvent(IEvent event) {
    ICalendarModel calendar = getCurrentCalendar();
    return calendar != null && calendar.addEvent(event);
  }

  /**
   * Remove the events.
   *
   * @param e events.
   * @return remove the events.
   */
  public boolean removeEvent(IEvent e) {
    ICalendarModel calendar = getCurrentCalendar();
    return calendar != null && calendar.removeEvent(e);
  }


  /**
   * Get the events.
   *
   * @param start the start of the event.
   * @param end   the end of the event.
   * @return the event/when is the event happening.
   */
  public List<IEvent> getEventsOn(ZonedDateTime start, ZonedDateTime end) {
    ICalendarModel calendar = getCurrentCalendar();
    return calendar == null ? List.of() : calendar.getEventsBetween(start, end);
  }

  /**
   * Copying events from a calendar.
   * Throws errors for illegal argument.
   *
   * @param subject    name of the event.
   * @param srcStart   start time of the event.
   * @param targetCal  target.
   * @param finalStart final start time.
   * @return the events that were copied.
   */
  public boolean copyEvent(String subject,
                           ZonedDateTime srcStart,
                           String targetCal,
                           ZonedDateTime finalStart) {

    ICalendarModel src = getCurrentCalendar();
    if (src == null) {
      throw new IllegalStateException("No calendar in use.");
    }

    ICalendarModel dst = calendars.get(targetCal);
    if (dst == null) {
      throw new IllegalArgumentException("Target not found.");
    }

    Optional<IEvent> found = src.findEvent(subject, srcStart.toLocalDateTime());
    if (found.isEmpty()) {
      return false;
    }

    IEvent original = found.get();
    ZoneId dstZone = dst.getZoneId();

    Duration duration = Duration.between(
            original.getStartDateTime().withZoneSameInstant(dstZone),
            finalStart);

    IEvent shifted = original.shiftCopy(duration, dstZone);

    return dst.addEvent(shifted);
  }

  /**
   * Copying events on certain days of the calendar.
   *
   * @param day       day of the week.
   * @param targetCal target
   * @param dstDay    day of the events.
   * @return the copy events on certain days.
   */
  public int copyEventsOn(LocalDate day,
                          String targetCal,
                          LocalDate dstDay) {

    ICalendarModel src = getCurrentCalendar();
    if (src == null) {
      throw new IllegalStateException("No calendar in use.");
    }
    ICalendarModel dst = calendars.get(targetCal);
    if (dst == null) {
      throw new IllegalArgumentException("Target not found.");
    }
    ZoneId srcZone = src.getZoneId();
    ZoneId dstZone = dst.getZoneId();
    ZonedDateTime dayStartSrc = day.atStartOfDay(srcZone);
    ZonedDateTime dayEndSrc = day.plusDays(1).atStartOfDay(srcZone).minusNanos(1);
    List<IEvent> today = src.getEventsBetween(dayStartSrc, dayEndSrc);
    Duration duration = Duration.between(
            dayStartSrc.withZoneSameInstant(dstZone),
            dstDay.atStartOfDay(dstZone));
    int added = copyBatch(dst, today, e -> e.shiftCopy(duration, dstZone));
    return added;
  }

  /**
   * Gets the events in between times.
   * Copy events.
   *
   * @param from           from start to end.
   * @param to             start to end.
   * @param targetCalendar taget calendar.
   * @param newStart       new start.
   * @return the copied events between times.
   */
  public int copyEventsBetween(ZonedDateTime from,
                               ZonedDateTime to,
                               String targetCalendar,
                               ZonedDateTime newStart) {

    ICalendarModel src = getCurrentCalendar();
    ICalendarModel dst = calendars.get(targetCalendar);
    if (src == null || dst == null) {
      throw new IllegalStateException("Bad calendars.");
    }

    ZoneId dstZone = dst.getZoneId();
    List<IEvent> list = src.getEventsBetween(from, to);

    Duration duration = Duration.between(
            from.withZoneSameInstant(dstZone),
            newStart);

    int added = copyBatch(dst, list, e -> e.shiftCopy(duration, dstZone));

    return added;
  }

  /**
   * Change the events that was copied.
   *
   * @param dst       destination.
   * @param originals original of the event.
   * @param changer   changing the events.
   * @return the events that copied and changes the events.
   */
  private int copyBatch(ICalendarModel dst,
                        List<IEvent> originals,
                        java.util.function.Function<IEvent, IEvent> changer) {

    java.util.List<IEvent> inserted = new java.util.ArrayList<>();

    for (IEvent srcEvent : originals) {
      IEvent clone = changer.apply(srcEvent);
      if (dst.addEvent(clone)) {
        inserted.add(clone);
      } else {
        inserted.forEach(dst::removeEvent);
        throw new IllegalStateException(
                "Copy Failed – conflict with " + clone.getSubject());
      }
    }
    return inserted.size();
  }
}
