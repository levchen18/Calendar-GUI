package view;

import controller.GUIController;
import model.IEvent;
import model.Status;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.BoxLayout;
import javax.swing.SpinnerDateModel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import java.awt.BorderLayout;
import javax.swing.JOptionPane;
import javax.swing.DefaultComboBoxModel;
import java.awt.Color;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * This is the CalendarGUI, it is responsible in creating the Calendar.
 * It will have text boxes, check boxes, and drop down selection for uses.
 * Users can add events and enter information, as well as view events on that day.
 */
public class CalendarGUI extends JFrame implements ICalendarView {

  private final JTextField eventNameField;
  private final JComboBox<Status> statusDropdown;
  private final JCheckBox allDayCheckbox;
  private final JButton addButton;
  private final JSpinner startSpinner;
  private final JSpinner endSpinner;
  private final JSpinner dateFilterSpinner;
  private final JLabel messageLabel;
  private final JTextField locationField;
  private final JTextArea descriptionArea;
  private GUIController controller;
  private final JComboBox<String> calendarBox = new JComboBox<>();
  private final JButton newCalBtn = new JButton("+");
  private final JButton saveButton = new JButton("Save");
  private final DefaultListModel<IEvent> eventListModel = new DefaultListModel<>();
  private final JList<IEvent> eventList = new JList<>(eventListModel);


  /**
   * Creating the GUI of the Calendar.
   * Responsible for setting up the Calendar size.
   * Format of the Calendar, allowing users to enter information.
   */
  public CalendarGUI() {
    super("Calendar GUI");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(800, 600);
    JTextArea scheduleArea = new JTextArea();
    scheduleArea.setEditable(false);
    setLayout(new BorderLayout());

    JPanel calBar = new JPanel(new BorderLayout(5, 0));
    calBar.add(calendarBox, BorderLayout.CENTER);
    calBar.add(newCalBtn, BorderLayout.EAST);

    JPanel controls = new JPanel(new GridLayout(0, 2));
    controls.add(new JLabel("Event Name:"));
    eventNameField = new JTextField();
    controls.add(eventNameField);

    JPanel north = new JPanel();
    north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
    north.add(calBar);
    north.add(controls);

    add(north, BorderLayout.NORTH);

    controls.add(new JLabel("Start Date/Time:"));
    startSpinner = new JSpinner(new SpinnerDateModel());
    startSpinner.setEditor(new JSpinner.DateEditor(startSpinner, "yyyy-MM-dd HH:mm"));
    controls.add(startSpinner);

    controls.add(new JLabel("End Date/Time:"));
    endSpinner = new JSpinner(new SpinnerDateModel());
    endSpinner.setEditor(new JSpinner.DateEditor(endSpinner, "yyyy-MM-dd HH:mm"));
    controls.add(endSpinner);

    controls.add(new JLabel("All Day:"));
    allDayCheckbox = new JCheckBox();
    controls.add(allDayCheckbox);

    controls.add(new JLabel("Status:"));
    statusDropdown = new JComboBox<>(Status.values());
    controls.add(statusDropdown);

    controls.add(new JLabel("Select Date to View:"));
    dateFilterSpinner = new JSpinner(new SpinnerDateModel());
    dateFilterSpinner.setEditor(new JSpinner.DateEditor(dateFilterSpinner, "yyyy-MM-dd"));
    controls.add(dateFilterSpinner);

    eventList.setVisibleRowCount(12);
    eventList.setCellRenderer((lst, e, i, sel, foc) -> {
      String s = String.format("%s  (%s–%s)",
              e.getSubject(),
              e.getStartDateTime().toLocalTime(),
              e.getEndDateTime().toLocalTime());
      JLabel l = new JLabel(s);
      l.setOpaque(true);
      l.setBackground(sel ? new Color(0xD0E4FF) : new Color(0xF8F8F8));
      return l;
    });
    add(new JScrollPane(eventList), BorderLayout.CENTER);
    JPanel bottomPanel = new JPanel();

    controls.add(new JLabel("Location:"));
    locationField = new JTextField();
    controls.add(locationField);

    addButton = new JButton("Add Event");
    JButton refreshButton = new JButton("View Schedule");
    messageLabel = new JLabel();

    controls.add(new JLabel("Description:"));
    descriptionArea = new JTextArea(2, 5);
    descriptionArea.setLineWrap(true);
    descriptionArea.setWrapStyleWord(true);
    JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
    controls.add(descriptionScroll);

    bottomPanel.add(addButton);
    bottomPanel.add(saveButton);
    bottomPanel.add(refreshButton);
    bottomPanel.add(messageLabel);

    saveButton.setEnabled(false);
    saveButton.addActionListener(e -> {
      if (controller != null) {
        controller.handleSaveEvent();
      }
    });

    add(bottomPanel, BorderLayout.SOUTH);
    addButton.addActionListener(e -> {
      if (controller != null) {
        controller.handleAddEvent();
      }
    });

    refreshButton.addActionListener(e -> {
      if (controller != null) {
        controller.handleViewSchedule();
      }
    });

    calendarBox.addActionListener(e -> {
      if (controller != null) {
        controller.handleSwitchCalendar((String) calendarBox.getSelectedItem());
      }
    });

    newCalBtn.addActionListener(e -> {
      if (controller != null) {
        controller.handleCreateCalendar();
      }
    });

    eventList.addListSelectionListener(ls -> {
      Object val = eventList.getSelectedValue();
      if (!(val instanceof IEvent)) {
        return;
      }
      IEvent ev = (IEvent) val;
      controller.loadEventIntoEditor(ev);
      saveButton.setEnabled(true);
      addButton.setEnabled(false);
    });

  }

  public void setController(GUIController controller) {
    this.controller = controller;
  }

  @Override
  public String getEventName() {
    return eventNameField.getText().trim();
  }

  @Override
  public LocalDateTime getStartTime() {
    Date date = (Date) startSpinner.getValue();
    return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
  }

  public void refreshCalendarBox(java.util.Set<String> names, String current) {
    calendarBox.setModel(new DefaultComboBoxModel<>(names.toArray(new String[0])));
    calendarBox.setSelectedItem(current);
  }

  public void refreshEventList(java.util.List<IEvent> list) {
    eventListModel.clear();
    list.forEach(eventListModel::addElement);
  }

  @Override
  public LocalDateTime getEndTime() {
    if (allDayCheckbox.isSelected()) {
      LocalDate date = getStartTime().toLocalDate();
      return date.atTime(17, 0);
    }
    Date date = (Date) endSpinner.getValue();
    return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
  }

  @Override
  public LocalDate getDateFilter() {
    Date date = (Date) dateFilterSpinner.getValue();
    return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalDate();
  }


  @Override
  public void displaySchedule(String text) {
    JOptionPane.showMessageDialog(
            this,
            text,
            "Schedule",
            JOptionPane.INFORMATION_MESSAGE);
  }

  @Override
  public void showMessage(String message) {
    messageLabel.setForeground(Color.BLUE);
    messageLabel.setText(message);
  }

  @Override
  public void showError(String message) {
    messageLabel.setForeground(Color.RED);
    messageLabel.setText(message);
  }

  @Override
  public LocalDateTime getEventEnd() {
    if (allDayCheckbox.isSelected()) {
      LocalDate date = getEventStart().toLocalDate();
      return date.atTime(17, 0);
    }
    Date date = (Date) endSpinner.getValue();
    return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
  }

  @Override
  public String getEventDescription() {
    return descriptionArea.getText().trim();
  }

  @Override
  public String getEventLocation() {
    return locationField.getText().trim();
  }

  /**
   * Fill the editor box, which allows edits to be made in events.
   * Users can change the events freely.
   * @param e the event within the calendar.
   */
  public void fillEditor(model.IEvent e) {
    eventNameField.setText(e.getSubject());
    startSpinner.setValue(
            Date.from(e.getStartDateTime().toInstant()));
    endSpinner.setValue(
            Date.from(e.getEndDateTime().toInstant()));
    locationField.setText(e.getLocation());
    descriptionArea.setText(e.getDescription());
    statusDropdown.setSelectedItem(e.getStatus());
  }

  public javax.swing.JComboBox<?> getStatusDropdown() {
    return statusDropdown;
  }

  /**
   * Reset the editor for the edit function.
   * This allows the user to make new changes to the edit method.
   */
  public void resetEditor() {
    addButton.setEnabled(true);
    saveButton.setEnabled(false);
    eventList.clearSelection();
  }

  @Override
  public LocalDateTime getEventStart() {
    if (allDayCheckbox.isSelected()) {
      LocalDate date = ((Date) startSpinner.getValue())
              .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
      return date.atTime(8, 0);
    }
    Date date = (Date) startSpinner.getValue();
    return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
  }
}