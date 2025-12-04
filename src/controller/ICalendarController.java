package controller;

/**
 * Interface for a calendar controller used in a graphical user interface (GUI).
 * This controller processes user actions from the view and delegates operations
 * to the model accordingly.
 */
public interface ICalendarController {

  /**
   * Handles the creation of a new event.
   * This method is typically triggered by a user action in the view (e.g., clicking "Add Event").
   * Errors displayed for invalid inputs.
   */
  void handleAddEvent();

  /**
   * Handles the request to view the schedule starting from a given date.
   * This method retrieves and displays upcoming events to the user.
   * Errors displayed for invalid inputs.
   */
  void handleViewSchedule();
}
