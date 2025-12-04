package controller;

import java.io.Reader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Scanner;

import model.CalendarManager;
import model.Event;
import model.Status;

/**
 * The controller of the program.
 * Handles the commands of the program.
 * Show, print, and etc. of the events.
 */
public class CommandController {

  private static CalendarManager model;

  /**
   * The constructor of the CommandController.
   *
   * @param model the model design of the program.
   */
  public CommandController(CalendarManager model) {
    this.model = model;
  }

  /**
   * Method that controls the controller and uses the
   * user's input to do so.
   *
   * @param userInput the command of the user.
   * @return the update on the command status.
   */
  public static String handle(String userInput) {
    String[] events = userInput.split(" ");
    if (events.length == 0) {
      throw new IllegalArgumentException("No command entered");
    }
    String command = events[0].toLowerCase();
    switch (command) {
      case "create":
        creatingEvent(events);
        break;
      case "print":
        printingEvent(events);
        break;
      case "use":
        usingCalendar(events);
        break;
      case "show":
        showingEvent(events);
        break;
      case "edit":
        editingCalendar(events);
        break;
      case "copy":
        copyCommand(events);
        break;
      case "exit":
      case "quit":
        break;
      default:
        throw new IllegalArgumentException("Invalid command");
    }
    return command;
  }

  /**
   * This allows users to edit events within a calendar.
   * Users are able to change the names, zone, and value.
   * Throws illegal arguments if it's not allowed.
   *
   * @param events string of events within a calendar.
   */
  private static void editingCalendar(String[] events) {
    String name = null;
    String property = null;
    String value = null;
    for (int i = 2; i < events.length; i++) {
      if ("--name".equalsIgnoreCase(events[i]) && i + 1 < events.length) {
        name = events[i++];
      } else if ("--property".equalsIgnoreCase(events[i]) && i + 2 < events.length) {
        property = events[i++];
        value = events[i++];
      }
    }
    if (name == null || property == null || value == null) {
      throw new IllegalArgumentException("Invalid command");
    }
    boolean successful = false;
    if ("name".equalsIgnoreCase(property)) {
      successful = model.changingCalendarName(name, value);
    } else if ("timezone".equalsIgnoreCase(property)) {
      try {
        ZoneId zoneId = ZoneId.of(value);
        successful = model.editTimeZones(name, zoneId);
      } catch (IllegalArgumentException e) {
        System.out.println("Invalid time format");
        return;
      }
    } else {
      throw new IllegalArgumentException("Invalid property " + property);
    }
    if (successful) {
      System.out.println("Successfully edited calendar");
    } else {
      System.out.println("Failed to edit calendar");
    }
  }

  /**
   * This allows the user to get access to the calendar.
   * Users will give the calendar a name.
   * Users can use the calendar based on the name.
   *
   * @param events string of events within a calendar.
   */
  public static void usingCalendar(String[] events) {
    String name = null;
    for (int i = 2; i < events.length; i++) {
      if (events[i].equalsIgnoreCase("--name") && i + 1 < events.length) {
        name = events[i + 1];
        i++;
      }
    }
    if (name == null) {
      throw new IllegalArgumentException("Invalid command");
    }
    try {
      model.useCalendar(name);
      System.out.println("Using Calendar " + name);
    } catch (IllegalArgumentException e) {
      System.out.println("Error switching calendar " + e.getMessage());
    }
  }

  /**
   * Creating events for the calendar.
   * Allows the user to enter events with time and time zone.
   * Invalid commands will throw errors.
   * Users will create events with name, time, and zone.
   *
   * @param events string of events within a calendar.
   */
  public static void creatingEvent(String[] events) {
    if (events.length < 3 || !events[1].equalsIgnoreCase("event")) {
      throw new IllegalArgumentException("Invalid command to create event");
    }
    int i = 2;
    StringBuilder subjectBuilder = new StringBuilder();
    if (events[i].startsWith("\"")) {
      while (i < events.length) {
        subjectBuilder.append(events[i]).append("");
        if (events[i].endsWith("\"")) {
          break;
        }
        i++;
      }
    }
    if (i < events.length && events[i].equalsIgnoreCase("from")) {
      if (i + 3 >= events.length || !events[i + 2].equalsIgnoreCase("to")) {
        throw new IllegalArgumentException("Expected: from <start> to <end>");
      }
      LocalDateTime startEvent = eventDateTime(events[i + 1]);
      LocalDateTime endEvent = eventDateTime(events[i + 3]);
    } else if (i < events.length && events[i].equalsIgnoreCase("on")) {
      if (i + 1 >= events.length) {
        throw new IllegalArgumentException("Expected: on <date>");
      }
      LocalDate date = LocalDate.parse(events[i + 1]);
      ZonedDateTime start = ZonedDateTime.from(LocalDateTime.of(date, LocalTime.of(8, 0)));
      ZonedDateTime end = ZonedDateTime.from(LocalDateTime.of(date, LocalTime.of(17, 0)));
      Event e = new Event("", start, end, "", Status.Public, "");
      if (!model.addEvent(e)) {
        throw new IllegalArgumentException("Duplicate event exists.");
      }
    } else {
      throw new IllegalArgumentException("Format incorrectly");
    }
  }

  private static LocalDateTime eventDateTime(String str) {
    try {
      return LocalDateTime.parse(str);
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid datetime format: " + str);
    }
  }

  /**
   * This will manage the time and date of events.
   *
   * @param events string of date and time.
   */
  public static void showingEvent(String[] events) {
    if (events.length != 4 || !events[1].equalsIgnoreCase("status") ||
            !events[2].equalsIgnoreCase("on")) {
      throw new IllegalArgumentException("Expected Status");
    }
    LocalDateTime localTime = LocalDateTime.parse(events[3]);
    ZoneId zone = model.getCurrentCalendar().getZoneId();
    ZonedDateTime date = ZonedDateTime.of(localTime, zone);
    boolean eventIsBusy = model.busyDuring(date);
    if (eventIsBusy) {
      System.out.println("Busy");
    } else {
      System.out.println("Not busy");
    }
  }

  /**
   * Printing events within a calendar.
   * Prints the events based on the time and zone.
   * Allows users to print events on a certain date.
   * Throws errors for invalid inputs.
   *
   * @param events String of events within a calendar.
   */
  public static void printingEvent(String[] events) {
    if (events.length < 4) {
      throw new IllegalArgumentException("Invalid number of events");
    }
    ZoneId zone = model.getCurrentCalendar().getZoneId();
    if (events[1].equalsIgnoreCase("events") && events[2].equalsIgnoreCase("on")) {
      LocalDate date = LocalDate.parse(events[3]);
      ZonedDateTime start = date.atStartOfDay(zone);
      ZonedDateTime end = date.plusDays(1).atStartOfDay(zone).minusNanos(1);
      var event = model.getEventsOn(start, end);
      if (event.isEmpty()) {
        System.out.println("No events on " + date);
      } else {
        for (var e : event) {
          System.out.printf("- %s (%s to %s)%s%n",
                  e.getSubject(),
                  e.getStartDateTime().toLocalTime(),
                  e.getEndDateTime().toLocalTime(),
                  e.getLocation().isEmpty() ? "" : " @ " + e.getLocation());
        }
      }
    } else if (events[1].equalsIgnoreCase("events") && events[2].equalsIgnoreCase("from")) {
      if (events.length < 6 || !events[4].equalsIgnoreCase("to")) {
        throw new IllegalArgumentException("Expected format: print events from <start> to <end>");
      }
    }
    LocalDateTime startLocalTime = LocalDateTime.parse(events[3]);
    LocalDateTime endLocalTime = LocalDateTime.parse(events[5]);
    ZonedDateTime startOfEvent = ZonedDateTime.of(startLocalTime, zone);
    ZonedDateTime endOfEvent = ZonedDateTime.of(endLocalTime, zone);
    var calendarEvents = model.getEventsBetween(startOfEvent, endOfEvent);
    if (calendarEvents.isEmpty()) {
      System.out.println("No events on " + startOfEvent);
    } else {
      for (var calendarEvent : calendarEvents) {
        System.out.printf("- %s (%s to %s)%s%n",
                calendarEvent.getSubject(),
                calendarEvent.getStartDateTime(),
                calendarEvent.getEndDateTime(),
                calendarEvent.getLocation().isEmpty() ? "" : " @ " + calendarEvent.getLocation());
      }
    }
  }

  /**
   * Allows users to copy events.
   * Users can copy the events and paste it on calendar.
   *
   * @param task task of the calendar.
   */
  private static void copyCommand(String[] task) {
    if (task.length < 2) {
      throw new IllegalArgumentException("Incomplete copy command");
    }

    if ("event".equalsIgnoreCase(task[1])) {
      int onIdx = find(task, "on");
      int tgtIdx = find(task, "--target");
      int toIdx = find(task, "to");
      if (onIdx == -1 || tgtIdx == -1 || toIdx == -1) {
        throw new IllegalArgumentException("Bad syntax");
      }
      String subject = String.join(" ", Arrays.copyOfRange(task, 2, onIdx));
      ZonedDateTime srcStart = ZonedDateTime.parse(task[onIdx + 1],
              DateTimeFormatter.ISO_DATE_TIME);
      String dstCal = task[tgtIdx + 1];
      ZonedDateTime dstStart = ZonedDateTime.parse(task[toIdx + 1],
              DateTimeFormatter.ISO_DATE_TIME);

      if (!model.copyEvent(subject, srcStart, dstCal, dstStart)) {
        System.out.println("Copy failed (duplicate?)");
      }
    } else if ("events".equalsIgnoreCase(task[1]) && "on".equalsIgnoreCase(task[2])) {
      int tgtIdx = find(task, "--target");
      int toIdx = find(task, "to");
      LocalDate srcDay = LocalDate.parse(task[3]);
      String dstCal = task[tgtIdx + 1];
      LocalDate dstDay = LocalDate.parse(task[toIdx + 1]);

      int n = model.copyEventsOn(srcDay, dstCal, dstDay);
      System.out.println(n + " events copied.");

    } else if ("events".equalsIgnoreCase(task[1]) && "between".equalsIgnoreCase(task[2])) {
      int and = find(task, "and");
      int tgt = find(task, "--target");
      int to = find(task, "to");
      ZonedDateTime srcFrom = ZonedDateTime.parse(task[3]);
      ZonedDateTime srcTo = ZonedDateTime.parse(task[and + 1]);
      String dstCal = task[tgt + 1];
      ZonedDateTime dstStart = ZonedDateTime.parse(task[to + 1]);

      int n = model.copyEventsBetween(srcFrom, srcTo, dstCal, dstStart);
      System.out.println(n + " events copied.");
    } else {
      throw new IllegalArgumentException("Wrong copy syntax");
    }
  }

  /**
   * Method that finds the event requested.
   *
   * @param arr  arranging the events.
   * @param word the word of the events.
   * @return the events that are founded.
   */
  private static int find(String[] arr, String word) {
    for (int i = 0; i < arr.length; i++) {
      if (word.equalsIgnoreCase(arr[i])) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Run the program.
   * When q or quit is entered, the user quits.
   *
   * @param reader reads the commands.
   */
  public void runScript(Reader reader) {
    try (Scanner in = new Scanner(reader)) {
      while (in.hasNextLine()) {
        String line = in.nextLine().trim();
        if (line.isEmpty()) {
          continue;
        }
        if (line.equalsIgnoreCase("q") ||
                line.equalsIgnoreCase("quit")) {
          break;
        }
        handle(line);
      }
    }
  }
}
