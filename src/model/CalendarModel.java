package model;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.time.LocalDateTime;


/**
 * Model part of the calendar.
 * It connects with other parts to make sure things are working.
 */
public class CalendarModel implements ICalendarModel {
  private final Set<IEvent> events;
  private ZoneId zoneId;

  /**
   * HashSet of the program.
   */
  public CalendarModel(ZoneId zoneId) {
    this.events = new HashSet<>();
    this.zoneId = zoneId;
  }

  /**
   * Zone of the event.
   *
   * @return the zoneId.
   */
  public ZoneId getZoneId() {
    return zoneId;
  }

  /**
   * Add events if it's not on the same day.
   *
   * @param event event within the calendar.
   * @return compared the event to make sure it's not on the same day.
   */
  public boolean addEvent(IEvent event) {
    return this.events.add(event);
  }

  /**
   * Remove the events.
   *
   * @param e events.
   * @return remove the events.
   */
  public boolean removeEvent(IEvent e) {
    return events.remove(e);
  }

  /**
   * Simple SetZoneId method to set zoneID.
   *
   * @param zoneId the zoneID to add.
   */
  public void setZoneId(ZoneId zoneId) {
    this.zoneId = zoneId;
  }

  /**
   * Get the events.
   *
   * @param date the date of the event.
   * @return the event/when is the event happening.
   */
  public List<IEvent> getEventsOn(LocalDate date) {
    List<IEvent> result = new ArrayList<>();
    for (IEvent e : events) {
      if (!e.getStartDateTime().toLocalDate().isAfter(date) &&
              !e.getEndDateTime().toLocalDate().isBefore(date)) {
        result.add(e);
      }
    }
    return result;
  }


  /**
   * Edit the events.
   *
   * @param subject   name of event.
   * @param startTime start date of the event.
   * @param newEvent  ending of the event.
   * @return the edited version of the event.
   */
  public boolean editEvent(String subject,
                           ZonedDateTime startTime,
                           IEvent newEvent) {
    Optional<IEvent> old = findEvent(subject, startTime.toLocalDateTime());
    return old.isPresent() && events.remove(old.get()) && events.add(newEvent);
  }

  /**
   * The events that are being changed between events.
   * Edit the events within time.
   *
   * @param from    from when the events end.
   * @param to      to a certain time.
   * @param mutator changing events.
   * @return the events that are being edited.
   */
  @Override
  public boolean editEventBetween(ZonedDateTime from, ZonedDateTime to, Consumer<IEvent> mutator) {
    boolean changed = false;
    for (IEvent e : events) {
      if (!(e.getEndDateTime().isBefore(from) || e.getStartDateTime().isAfter(to))) {
        mutator.accept(e);
        changed = true;
      }
    }
    return changed;
  }

  /**
   * Edit series that are being changed.
   *
   * @param subject name of the event.
   * @param from    from time to time
   * @param mutator changing events.
   * @return the edit version of the returned events.
   */
  @Override
  public boolean editSeriesFrom(String subject, ZonedDateTime from, Consumer<IEvent> mutator) {
    boolean changed = false;
    for (IEvent e : events) {
      if (e.getSubject().equals(subject) && !e.getStartDateTime().isBefore(from)) {
        mutator.accept(e);
        changed = true;
      }
    }
    return changed;
  }

  /**
   * Checks if the event is busy.
   *
   * @param time check the time when it occurs.
   * @return if the event is busy or not.
   */
  public boolean busyDuring(ZonedDateTime time) {
    return events.stream().anyMatch(ev -> ev.busyDuring(time));
  }

  /**
   * Searches the calendar for an event whose subject and start match.
   *
   * @param subject   the exact subject of the event.
   * @param startTime the start date-time of the event.
   * @return the matching event if found.
   */
  public Optional<IEvent> findEvent(String subject, LocalDateTime startTime) {
    return events.stream()
            .filter(e -> e.getSubject().equals(subject)
                    && e.getStartDateTime().toLocalDateTime().equals(startTime)).findFirst();
  }

  /**
   * Returns all events that overlap the time window.
   *
   * @param start the beginning of the interval.
   * @param end   the end of the interval.
   * @return a list of events occurring within or intersecting that interval.
   */
  public List<IEvent> getEventsBetween(ZonedDateTime start, ZonedDateTime end) {
    return events.stream()
            .filter(ev -> !ev.getEndDateTime().isBefore(start) &&
                    !ev.getStartDateTime().isAfter(end))
            .sorted(Comparator.comparing(IEvent::getStartDateTime))
            .collect(Collectors.toList());
  }
}
