# Homework-6-FINAL
Question 1: We didn't change any files within our Assignment 5. Instead, for this part of the assignment we
extended the program by creating new files. 

I created the CalendarGUI in my view to handle the display of the program within the GUI. This will
display options of text-box, drop down, and check boxes. ICalendarView interface was created to support 
the implementation of the Calendar GUI.

The GUI Controller is responsible for testing the code, displaying any errors as users entered invalid
commands. The ICalendarController interface was created to help with the implementation of the GUIController. Messgaes
will be displayed for invalid inputs. For example, if end time was before start time.

Question 2: Run the program from the folder that contains the calendar.jar file by typing
java -jar calendar.jar --mode interactive for a live and responsive mode, or
java -jar calendar.jar --mode headless path/to/script.txt to execute all commands in a script and exit.
java -jar calendar.jar --mode gui for the gui mode.
Both modes require quit or q to end gracefully.

Question 3: All the features work along with the GUI that was implemented.
There may be some severe edge cases that will cause trouble for our implementation.

Question 4: The work was evenly distributed as we helped each other on parts each other was stuck on.
We divided the work, I was in charge of setting up the GUI and completing the assignment requirements.
While my partner is working on test cases and completing the extra credit requirements. We helped each other
on parts the others weren't able to complete.

Question 5: Nothing. 