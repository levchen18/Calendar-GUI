package controller;

import model.CalendarManager;
import model.CalendarModel;
import model.Event;
import model.IEvent;
import model.Status;
import view.CalendarGUI;
import view.ICalendarView;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles GUI interactions and event processing for the calendar application.
 * Acts as a controller between the ICalendarView and the CalendarManager model.
 */
public class GUIController implements ICalendarController {

  private final CalendarManager manager;
  private final ICalendarView view;
  private IEvent eventBeingEdited = null;
  private ZoneId currentZone = ZoneId.systemDefault();

  /**
   * Constructs a GUIController with the given CalendarManager and View.
   * Setting up the GUI.
   *
   * @param manager The calendar manager handling multiple calendars and events.
   * @param view    The view component (e.g., CalendarGUI).
   */
  public GUIController(CalendarManager manager, ICalendarView view) {
    this.manager = manager;
    this.view = view;
  }

  /**
   * Handles the process of adding an event.
   */
  @Override
  public void handleAddEvent() {
    try {
      String name = view.getEventName();
      String description = view.getEventDescription();
      String location = view.getEventLocation();
      LocalDateTime start = view.getEventStart();
      LocalDateTime end = view.getEventEnd();
      Status status = (Status) ((JComboBox<?>) view.getStatusDropdown()).getSelectedItem();


        if (name == null || name.isEmpty() || start == null || end == null) {
        view.showError("Event name, start, and end time are required.");
        return;
      }

      if (end.isBefore(start)) {
        view.showError("End time must be after start time.");
        return;
      }
      ZonedDateTime startZoned = start.atZone(ZoneId.systemDefault());
      ZonedDateTime endZoned = end.atZone(ZoneId.systemDefault());
      Event event = new Event(
              name,
              startZoned,
              endZoned,
              location,
              status,
              description
      );
      CalendarModel currentCalendar = (CalendarModel) manager.getCurrentCalendar();
      boolean added = currentCalendar.addEvent(event);
      if (!added) {
        view.showError("Failed to add event. Duplicate or invalid.");
        return;
      }
      view.showMessage("Event added successfully!");
      refreshCalendarsAndEvents();
    } catch (Exception ex) {
      view.showError("Error adding event: " + ex.getMessage());
      ex.printStackTrace();
    }
  }

  /**
   * Handles the process of viewing the schedule.
   */
  @Override
  public void handleViewSchedule() {
    try {
      LocalDate date = view.getDateFilter();
      ZoneId zone = ZoneId.systemDefault();
      ZonedDateTime start = date.atStartOfDay(zone);
      ZonedDateTime end = start.plusDays(1);
      List<IEvent> events = manager.getEventsOn(start, end);
      String display = events.stream()
              .sorted(Comparator.comparing(IEvent::getStartDateTime))
              .limit(10)
              .map(IEvent::toString)
              .collect(Collectors.joining("\n\n"));
      if (display.isEmpty()) {
        view.displaySchedule("No events from " + date + ".");
      } else {
        view.displaySchedule(display);
      }
    } catch (Exception e) {
      view.showError("Could not load events.");
    }
  }

  /**
   * Handles the process of creating a calendar.
   */
  public void handleCreateCalendar() {
    String name = JOptionPane.showInputDialog(null,
            "Calendar name:", "Create calendar",
            JOptionPane.PLAIN_MESSAGE);
    if (name == null || name.isBlank()) {
      return;
    }

    String zoneStr = JOptionPane.showInputDialog(null,
            "Time-zone (e.g. America/New_York):", currentZone.toString());
    ZoneId zone = currentZone;
    if (zoneStr != null && !zoneStr.isBlank()) {
      try {
        zone = ZoneId.of(zoneStr);
      }
      catch (Exception ex) {
        view.showError("Bad zone id.");
        return;
      }
    }
    if (!manager.createCalendar(name, zone)) {
      view.showError("Calendar exists or name invalid.");
      return;
    }
    manager.useCalendar(name);
    currentZone = zone;
    refreshCalendarsAndEvents();
  }

  /**
   * Handles the process of switching the calendar.
   */
  public void handleSwitchCalendar(String calName) {
    if (manager.useCalendar(calName)) {
      currentZone = ((CalendarModel) manager.getCurrentCalendar()).getZoneId();
      refreshCalendarsAndEvents();
    }
  }

  /**
   * Handles the process of loading an event.
   */
  public void loadEventIntoEditor(IEvent ev) {
    eventBeingEdited = ev;
    view.fillEditor(ev);
  }

  /**
   * Handles the process of saving an event.
   */
  public void handleSaveEvent() {
    if (eventBeingEdited == null) {
      return;
    }

    IEvent oldEvt = eventBeingEdited;
    eventBeingEdited = null;

    String subj   = view.getEventName();
    ZonedDateTime start = view.getEventStart().atZone(currentZone);
    ZonedDateTime end   = view.getEventEnd().atZone(currentZone);

    Event updated = new Event(subj, start, end,
            view.getEventLocation(),
            (Status) ((JComboBox<?>) view.getStatusDropdown()).getSelectedItem(),
            view.getEventDescription());

    if (manager.editEvent(oldEvt.getSubject(),
            oldEvt.getStartDateTime(), updated)) {
      view.showMessage("Event updated.");
    } else {
      view.showError("Cannot Update");
    }
    refreshCalendarsAndEvents();
    ((CalendarGUI) view).resetEditor();
  }

  private void refreshCalendarsAndEvents() {
    view.refreshCalendarBox(manager.getCalendarNames(),
            manager.getCurrentCalendarName());
    LocalDate today = view.getDateFilter();
    ZonedDateTime s = today.atStartOfDay(currentZone);
    ZonedDateTime e = s.plusDays(1);
    view.refreshEventList(manager.getEventsOn(s, e));
  }
}