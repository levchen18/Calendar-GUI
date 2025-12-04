package view;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Interface for the ICalendarView.
 *
 * <p>Responsible for any view in the calendar app (GUI).
 */
public interface ICalendarView {
  /**
   * Retrieves the name of the event from user input.
   *
   * @return the name of the event within the calendar as a string.
   */
  String getEventName();

  /**
   * Gets the start date and time for an event from the UI input.
   * When is the event starting.
   *
   * @return a localTimeDate representing the event start time.
   */
  LocalDateTime getStartTime();

  /**
   * Gets the end date and time for an event from the UI input.
   * When is the event ending.
   *
   * @return a localTimeDate representing the event end time.
   */
  LocalDateTime getEndTime();

  /**
   * Gets the sorted events on days throughout the calendar.
   * Sorted based on that date of events.
   *
   * @return the filter dates of when events are happening on that day.
   */
  LocalDate getDateFilter();

  /**
   * Displaying the events on the calendar.
   * Information of its name, status, and time will be included.
   *
   * @param text display the events for the calendar.
   */
  void displaySchedule(String text);

  /**
   * Shows a message to the user in a non-intrusive manner.
   * The program will either be successful or fail.
   * Tell users the state of the program.
   *
   * @param message the message to display to users regarding the program.
   */
  void showMessage(String message);

  /**
   * Displays error messages that are not allowed or invalid for the program.
   * Message of what's the issue will be displayed.
   *
   * @param message message of invalid errors user entered.
   */
  void showError(String message);

  /**
   * User will enter a description regarding the event.
   * A text box where user can add anything to it.
   *
   * @return the description of the events.
   */
  String getEventDescription();

  /**
   * Retrieves the location for the event entered by the user.
   *
   * @return the event location as a string
   */
  String getEventLocation();

  /**
   * Gets the normalized start date/time for the event.
   * Useful when all-day events are selected.
   *
   * @return a localDateTime for events starting.
   */
  LocalDateTime getEventStart();

  /**
   * Gets the normalized end date/time for the event.
   * Useful when all-day events are selected.
   *
   * @return a localDateTime for events ending.
   */
  LocalDateTime getEventEnd();

  /**
   * Allows the user to edit the events within a calendar.
   *
   * @param event the event within the calendar.
   */
  void fillEditor(model.IEvent event);

  javax.swing.JComboBox<?> getStatusDropdown();

  /**
   * Resets the Editor that allows users to edit events.
   * Allows users to edit the events and reset the edited events.c
   */
  void resetEditor();

  void refreshCalendarBox(java.util.Set<String> names, String currentSelection);

  void refreshEventList(java.util.List<model.IEvent> list);
}
