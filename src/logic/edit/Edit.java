package logic.edit;

import storage.FileStorage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.logging.*;

import common.CommandObject;
import common.Interval;
import common.LocalDateTimePair;
import common.TaskObject;
import logic.Recurring;

import static logic.constants.Index.*;
import static logic.constants.Strings.*;

/**
 * Creates an Edit object which will edit the title of a selected task to the
 * desired title. <br>
 * 
 * All the edit information is first set based on the information in the CommandObject that is 
 * passed in the constructor. This is done by having boolean checks for each attribute. The 
 * attributes that can be edited are:
 * 1. Title
 * 2. Start date
 * 3. Start time
 * 4. End date
 * 5. End time
 * 6. Interval <br>
 * 
 * If the task to be edited is a recurring task, there are 2 ways that the edit can be processed:
 * (a) Edit only the upcoming occurrence - this will be the default option, where only the date/time 
 * data for the first occurrence in the ArrayList<LocalDateTimePair> will be modified.
 * (b) Edit all occurrences - this will be called if the user input contains the 'all' keyword, i.e.
 * 'edit all <index> ...'. This will edit the date/time details for all occurrences in the ArrayList.
 * 
 * @param CommandObject
 *     	commandObj - Contains information regarding what to change. The TaskObject contained 
 *      within this attribute contains the desired title of a particular object, while the index 
 *      contained within this attribute contains the relative position of the task in the last 
 *      output task list.
 * @param ArrayList<TaskObject>
 * 		lastOutputTaskList - Contains the task list that is currently being displayed to the user.
 * @param ArrayList<TaskObject>
 * 		taskList - Contains the entire list of all tasks.
 * 	
 * @author ChongYan, RuiBin
 *
 */

public class Edit {
	
	private static final Logger LOGGER = Logger.getLogger(Edit.class.getName());
	
	private CommandObject commandObj;
	private int editItemIndex;
	private ArrayList<TaskObject> lastOutputTaskList;
	private ArrayList<TaskObject> taskList;
	private ArrayList<String> tempOutput = new ArrayList<String>();
	private ArrayList<String> output = new ArrayList<String>();
	
	private TaskObject editTask; // task to be edited
	private int editTaskId;
	private String originalTitle = "";
	private LocalDateTime originalStartDateTime = LocalDateTime.MAX;
	private LocalDate originalStartDate = LocalDate.MAX;
	private LocalTime originalStartTime = LocalTime.MAX;
	private LocalDateTime originalEndDateTime = LocalDateTime.MAX;
	private LocalDate originalEndDate = LocalDate.MAX;
	private LocalTime originalEndTime = LocalTime.MAX;
	private Interval originalInterval = new Interval();
	private String editTitle;
	private LocalDate editStartDate;
	private LocalTime editStartTime;
	private LocalDate editEndDate;
	private LocalTime editEndTime;
	private Interval editInterval;
	
	boolean isEditTitle = false;
	boolean isEditStartDate = false;
	boolean isEditStartTime = false;
	boolean isEditEndDate = false;
	boolean isEditEndTime = false;
	boolean isEditInterval = false;
	// for recurring tasks
	boolean isEditAllRecurring = false;
	boolean isRecurringTask = false;
	boolean isEditStartDateTimeForAllOccurrences = false;
	boolean isEditStartDateForAllOccurrences = false;
	boolean isEditStartTimeForAllOccurrences = false;
	boolean isEditEndDateTimeForAllOccurrences = false;
	boolean isEditEndDateForAllOccurrences = false;
	boolean isEditEndTimeForAllOccurrences = false;
	
	public Edit(CommandObject commandObj, ArrayList<TaskObject> lastOutputTaskList, ArrayList<TaskObject> taskList) {
		this.commandObj = commandObj;
		this.lastOutputTaskList = lastOutputTaskList;
		this.taskList = taskList;
	}
	
	/**
	 * Main method of Edit. Finds the target task and edits its title before
	 * saving it to the external file location.
	 * @return output
	 */
	public ArrayList<String> run() {
		setEditInformation();
		//checkEditInformation();
		getTaskIdOfTaskToBeEdited();
		editTask();
		updateCategory();
		saveExternal();
		
		setOutput();
		return output;
	}
	
	// Retrieves values from the data objects and sets the relevant edit information	
	private void setEditInformation() {
		assert (editItemIndex > 0);
		
		try {
			editItemIndex = commandObj.getIndex();
			editTitle = commandObj.getTaskObject().getTitle();
			if (!editTitle.equals("")) {
				isEditTitle = true;
			}
			editStartDate = commandObj.getTaskObject().getStartDateTime().toLocalDate();
			if (!editStartDate.isEqual(LocalDate.MAX)) {
				isEditStartDate = true;
			}
			editStartTime = commandObj.getTaskObject().getStartDateTime().toLocalTime();
			if (!editStartTime.equals(LocalTime.MAX)) {
				isEditStartTime = true;
			}
			editEndDate = commandObj.getTaskObject().getEndDateTime().toLocalDate();
			if (!editEndDate.isEqual(LocalDate.MAX)) {
				isEditEndDate = true;
			}
			editEndTime = commandObj.getTaskObject().getEndDateTime().toLocalTime();
			if (!editEndTime.equals(LocalTime.MAX)) {
				isEditEndTime = true;
			}
			editInterval = commandObj.getTaskObject().getInterval();
			//if (editInterval != null) {		// MIGHT NEED TO CHANGE THIS CHECK
			//	isEditInterval = true;
			//}
			isEditAllRecurring = commandObj.getTaskObject().getIsRecurring(); // checks if all recurring tasks are to be edited
			
		} catch (NullPointerException e) {
			LOGGER.log(Level.WARNING, "Error setting edit information");
		}
	}

	private void getTaskIdOfTaskToBeEdited() {
		assert (editItemIndex > 0 && editItemIndex <= lastOutputTaskList.size());
		LOGGER.log(Level.INFO, "Obtained task ID to be edited");
		
		editTaskId = lastOutputTaskList.get(editItemIndex-1).getTaskId();
	}

	/**
	 * Core method of Edit. Reads in the task ID to be edited and edits the respective information
	 * based on the boolean checks. The data is only edited if it is different from the current.
	 * @param editTaskId
	 */
	private void editTask() {
		for (int i = 0; i < taskList.size(); i++) {
			TaskObject task = taskList.get(i);
			if (task.getTaskId() == editTaskId) { 
				isRecurringTask = task.getIsRecurring();

				if (isEditTitle) {
					editTitle(task);
				} 
				if (isEditStartDate && isEditStartTime) {
					if (isRecurringTask && isEditAllRecurring) {
						editStartDateAndTimeForAllOccurrences(task);
					} else {
						editStartDateAndTime(task);
					}
				} else {
					if (isEditStartDate) {
						if (isRecurringTask && isEditAllRecurring) {
							editStartDateForAllOccurrences(task);
						} else {
							editStartDate(task);
						}
					}
					if (isEditStartTime) {
						if (isRecurringTask && isEditAllRecurring) {
							editStartTimeForAllOccurrences(task);
						} else {
							editStartTime(task);
						}
					} 
				}
				if (isEditEndDate && isEditEndTime) {
					if (isRecurringTask && isEditAllRecurring) {
						editEndDateAndTimeForAllOccurrences(task);
					} else {
						editEndDateAndTime(task);
					}
				} else {
					if (isEditEndDate) {
						if (isRecurringTask && isEditAllRecurring) {
							editEndDateForAllOccurrences(task);
						} else {
							editEndDate(task);
						}
					}
					if (isEditEndTime) {
						if (isRecurringTask && isEditAllRecurring) {
							editEndTimeForAllOccurrences(task);
						} else {
							editEndTime(task);
						}
					}
				}
				if (isEditInterval) {
					editInterval(task);
				}
				
				editTask = task;
			}
		}
	}

	private void editTitle(TaskObject task) {
		originalTitle = task.getTitle();
		
		if (!originalTitle.equals(editTitle)) {
			task.setTitle(editTitle);
			LOGGER.log(Level.INFO, "Title edited");
		} else {
			isEditTitle = false;
		}
	}
	
	// Edits the start date and time for all recurring occurrences 
	private void editStartDateAndTimeForAllOccurrences(TaskObject task) {
		ArrayList<LocalDateTimePair> taskDateTimes = task.getTaskDateTimes();
		
		try {
			for (int i = 0; i < taskDateTimes.size(); i++) {
				LocalDateTimePair taskDateTime = taskDateTimes.get(i);
				LocalDateTime taskNewStartDateTime = LocalDateTime.of(editStartDate, editStartTime);
				taskDateTime.setStartDateTime(taskNewStartDateTime);
			}
			LOGGER.log(Level.INFO, "Start dates and times edited for all occurrences of recurring task");
			isEditStartDateTimeForAllOccurrences = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// If recurring task, edits the first occurrence in the arraylist. If not, edit the TaskObject directly.
	private void editStartDateAndTime(TaskObject task) {
		if (isRecurringTask) {
			ArrayList<LocalDateTimePair> taskDateTimes = task.getTaskDateTimes();
			LocalDateTimePair taskDateTimeFirst = taskDateTimes.get(0); // gets the first occurrence
			originalStartDateTime = taskDateTimeFirst.getStartDateTime();
			originalStartDate = originalStartDateTime.toLocalDate();
			originalStartTime = originalStartDateTime.toLocalTime();
			
			if (!originalStartDate.isEqual(editStartDate) && !originalStartTime.equals(editStartTime)) {		
				task.setStartDateTime(LocalDateTime.of(editStartDate, editStartTime));
				LOGGER.log(Level.INFO, "Start date and time edited for first occurrence of recurring task");
			} else if (originalStartTime.equals(editStartTime)) { // if old and new times are the same, only edit date
				isEditStartTime = false;
				editStartDate(task);
			} else if (originalStartDate.isEqual(editStartDate)) { // if old and new dates are the same, only edit time
				isEditStartDate = false;
				editStartTime(task);
			} 
		} else {
			originalStartDateTime = task.getStartDateTime();
			originalStartDate = originalStartDateTime.toLocalDate();
			originalStartTime = originalStartDateTime.toLocalTime();
			
			if (!originalStartDate.isEqual(editStartDate) && !originalStartTime.equals(editStartTime)) {		
				task.setStartDateTime(LocalDateTime.of(editStartDate, editStartTime));
				LOGGER.log(Level.INFO, "Start date and time edited");
			} else if (originalStartTime.equals(editStartTime)) { // if old and new times are the same, only edit date
				isEditStartTime = false;
				editStartDate(task);
			} else if (originalStartDate.isEqual(editStartDate)) { // if old and new dates are the same, only edit time
				isEditStartDate = false;
				editStartTime(task);
			} 
		}
	}
	
	// Edits the start date for all recurring occurrences 
	private void editStartDateForAllOccurrences(TaskObject task) {
		ArrayList<LocalDateTimePair> taskDateTimes = task.getTaskDateTimes();
		
		try {
			for (int i = 0; i < taskDateTimes.size(); i++) {
				LocalDateTimePair taskDateTime = taskDateTimes.get(i);
				LocalDateTime taskOriginalStartDateTime = taskDateTime.getStartDateTime();
				LocalTime taskOriginalStartTime = taskOriginalStartDateTime.toLocalTime();
				
				// Sets the start time to be the new time
				LocalDateTime taskNewStartDateTime = LocalDateTime.of(editStartDate, taskOriginalStartTime);
				taskDateTime.setStartDateTime(taskNewStartDateTime);
			}
			LOGGER.log(Level.INFO, "Start dates edited for all occurrences of recurring task");
			isEditStartDateForAllOccurrences = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// If recurring task, edits the first occurrence in the arraylist. If not, edit the TaskObject directly.
	private void editStartDate(TaskObject task) {
		if (isRecurringTask) {
			ArrayList<LocalDateTimePair> taskDateTimes = task.getTaskDateTimes();
			LocalDateTimePair taskDateTimeFirst = taskDateTimes.get(0); // gets the first occurrence
			originalStartDateTime = taskDateTimeFirst.getStartDateTime();
			originalStartDate = originalStartDateTime.toLocalDate();
			originalStartTime = originalStartDateTime.toLocalTime();
			
			if (!originalStartDate.isEqual(editStartDate)) {
				taskDateTimeFirst.setStartDateTime(LocalDateTime.of(editStartDate, originalStartTime));
				LOGGER.log(Level.INFO, "Start date edited for first occurrence of recurring task");
			} else {
				isEditStartDate = false;
			}
		} else {
			originalStartDateTime = task.getStartDateTime();
			originalStartDate = originalStartDateTime.toLocalDate();
			originalStartTime = originalStartDateTime.toLocalTime();
			
			if (!originalStartDate.isEqual(editStartDate)) {		
				task.setStartDateTime(LocalDateTime.of(editStartDate, originalStartTime));
				LOGGER.log(Level.INFO, "Start date edited");
			} else {
				isEditStartDate = false;
			} 
		}
	}
	
	// Edits the start time for all recurring occurrences 
	private void editStartTimeForAllOccurrences(TaskObject task) {
		ArrayList<LocalDateTimePair> taskDateTimes = task.getTaskDateTimes();
		
		try {
			for (int i = 0; i < taskDateTimes.size(); i++) {
				LocalDateTimePair taskDateTime = taskDateTimes.get(i);
				LocalDateTime taskOriginalStartDateTime = taskDateTime.getStartDateTime();
				LocalDate taskOriginalStartDate = taskOriginalStartDateTime.toLocalDate();
				
				// Sets the start time to be the new time
				LocalDateTime taskNewStartDateTime = LocalDateTime.of(taskOriginalStartDate, editStartTime);
				taskDateTime.setStartDateTime(taskNewStartDateTime);
			}
			LOGGER.log(Level.INFO, "Start times edited for all occurrences of recurring task");
			isEditStartTimeForAllOccurrences = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// If recurring task, edits the first occurrence in the arraylist. If not, edit the TaskObject directly.
	private void editStartTime(TaskObject task) {
		if (isRecurringTask) {
			ArrayList<LocalDateTimePair> taskDateTimes = task.getTaskDateTimes();
			LocalDateTimePair taskDateTimeFirst = taskDateTimes.get(0); // gets the first occurrence
			originalStartDateTime = taskDateTimeFirst.getStartDateTime();
			originalStartDate = originalStartDateTime.toLocalDate();
			originalStartTime = originalStartDateTime.toLocalTime();
			
			if (!originalStartTime.equals(editStartTime)) {
				taskDateTimeFirst.setStartDateTime(LocalDateTime.of(originalStartDate, editStartTime));
				LOGGER.log(Level.INFO, "Start time edited for first occurrence of recurring task");
			} else {
				isEditStartTime = false;
			}
		} else {
			originalStartDateTime = task.getStartDateTime();
			originalStartDate = originalStartDateTime.toLocalDate();
			originalStartTime = originalStartDateTime.toLocalTime();
			
			if (!originalStartTime.equals(editStartTime)) {
				task.setStartDateTime(LocalDateTime.of(originalStartDate, editStartTime));
				LOGGER.log(Level.INFO, "Start time edited");
			} else {
				isEditStartTime = false;
			}
		}
	}
	
	// Edits the end date and time for all recurring occurrences 
	private void editEndDateAndTimeForAllOccurrences(TaskObject task) {
		ArrayList<LocalDateTimePair> taskDateTimes = task.getTaskDateTimes();
		
		try {
			for (int i = 0; i < taskDateTimes.size(); i++) {
				LocalDateTimePair taskDateTime = taskDateTimes.get(i);
				LocalDateTime taskNewEndDateTime = LocalDateTime.of(editEndDate, editEndTime);
				taskDateTime.setEndDateTime(taskNewEndDateTime);
			}
			LOGGER.log(Level.INFO, "End dates and times edited for all occurrences of recurring task");
			isEditEndDateTimeForAllOccurrences = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// If recurring task, edits the first occurrence in the arraylist. If not, edit the TaskObject directly.
	private void editEndDateAndTime(TaskObject task) {
		if (isRecurringTask) {
			ArrayList<LocalDateTimePair> taskDateTimes = task.getTaskDateTimes();
			LocalDateTimePair taskDateTimeFirst = taskDateTimes.get(0); // gets the first occurrence
			originalEndDateTime = taskDateTimeFirst.getEndDateTime();
			originalEndDate = originalEndDateTime.toLocalDate();
			originalEndTime = originalEndDateTime.toLocalTime();
			
			originalEndDateTime = task.getEndDateTime();
			originalEndDate = originalEndDateTime.toLocalDate();
			originalEndTime = originalEndDateTime.toLocalTime();
			
			if (!originalEndDate.isEqual(editEndDate) && !originalEndTime.equals(editEndTime)) {		
				task.setEndDateTime(LocalDateTime.of(editEndDate, editEndTime));
				LOGGER.log(Level.INFO, "End date and time edited");
			} else if (originalEndTime.equals(editEndTime)) { // if old and new times are the same, only edit date
				isEditEndTime = false;
				editEndDate(task);
			} else if (originalEndDate.isEqual(editEndDate)) { // if old and new dates are the same, only edit time
				isEditEndDate = false;
				editEndTime(task);
			} 
		} else {
			originalEndDateTime = task.getEndDateTime();
			originalEndDate = originalEndDateTime.toLocalDate();
			originalEndTime = originalEndDateTime.toLocalTime();
			
			if (!originalEndDate.isEqual(editEndDate) && !originalEndTime.equals(editEndTime)) {		
				task.setEndDateTime(LocalDateTime.of(editEndDate, editEndTime));
				LOGGER.log(Level.INFO, "End date and time edited");
			} else if (originalEndTime.equals(editEndTime)) { // if old and new times are the same, only edit date
				isEditEndTime = false;
				editEndDate(task);
			} else if (originalEndDate.isEqual(editEndDate)) { // if old and new dates are the same, only edit time
				isEditEndDate = false;
				editEndTime(task);
			} 
		}
	}
	
	// Edits the end date for all recurring occurrences 
	private void editEndDateForAllOccurrences(TaskObject task) {
		ArrayList<LocalDateTimePair> taskDateTimes = task.getTaskDateTimes();
		
		try {
			for (int i = 0; i < taskDateTimes.size(); i++) {
				LocalDateTimePair taskDateTime = taskDateTimes.get(i);
				LocalDateTime taskOriginalEndDateTime = taskDateTime.getEndDateTime();
				LocalTime taskOriginalEndTime = taskOriginalEndDateTime.toLocalTime();
				
				// Sets the start time to be the new time
				LocalDateTime taskNewEndDateTime = LocalDateTime.of(editEndDate, taskOriginalEndTime);
				taskDateTime.setStartDateTime(taskNewEndDateTime);
			}
			LOGGER.log(Level.INFO, "End dates edited for all occurrences of recurring task");
			isEditEndDateForAllOccurrences = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// If recurring task, edits the first occurrence in the arraylist. If not, edit the TaskObject directly.
	private void editEndDate(TaskObject task) {
		if (isRecurringTask) {
			ArrayList<LocalDateTimePair> taskDateTimes = task.getTaskDateTimes();
			LocalDateTimePair taskDateTimeFirst = taskDateTimes.get(0); // gets the first occurrence
			originalEndDateTime = taskDateTimeFirst.getEndDateTime();
			originalEndDate = originalEndDateTime.toLocalDate();
			originalEndTime = originalEndDateTime.toLocalTime();
			
			if (!originalEndDate.isEqual(editEndDate)) {
				taskDateTimeFirst.setEndDateTime(LocalDateTime.of(editEndDate, originalEndTime));
				LOGGER.log(Level.INFO, "End date edited for first occurrence of recurring task");
			} else {
				isEditEndDate = false;
			}
		} else {
			originalEndDateTime = task.getEndDateTime();
			originalEndDate = originalEndDateTime.toLocalDate();
			originalEndTime = originalEndDateTime.toLocalTime();
			
			if (!originalEndDate.isEqual(editEndDate)) {		
				task.setEndDateTime(LocalDateTime.of(editEndDate, originalEndTime));
				LOGGER.log(Level.INFO, "End date edited");
			} else {
				isEditEndDate = false;
			} 
		}
		
	}
	
	// Edits the end time for all recurring occurrences 
	private void editEndTimeForAllOccurrences(TaskObject task) {
		ArrayList<LocalDateTimePair> taskDateTimes = task.getTaskDateTimes();
		
		try {
			for (int i = 0; i < taskDateTimes.size(); i++) {
				LocalDateTimePair taskDateTime = taskDateTimes.get(i);
				LocalDateTime taskOriginalEndDateTime = taskDateTime.getEndDateTime();
				LocalDate taskOriginalEndDate = taskOriginalEndDateTime.toLocalDate();
				
				// Sets the end time to be the new time
				LocalDateTime taskNewEndDateTime = LocalDateTime.of(taskOriginalEndDate, editEndTime);
				taskDateTime.setEndDateTime(taskNewEndDateTime);
			}
			LOGGER.log(Level.INFO, "End times edited for all occurrences of recurring task");
			isEditEndTimeForAllOccurrences = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* If it is a recurring task, the timing details of the first occurrence will be retrieved
	 * from the TaskObject and edited. If not, the the timing details in the TaskObject will be
	 * edited directly.
	 */
	private void editEndTime(TaskObject task) {
		if (isRecurringTask) {
			ArrayList<LocalDateTimePair> taskDateTimes = task.getTaskDateTimes();
			LocalDateTimePair taskDateTimeFirst = taskDateTimes.get(0); // gets the first occurrence
			originalEndDateTime = taskDateTimeFirst.getEndDateTime();
			originalEndDate = originalEndDateTime.toLocalDate();
			originalEndTime = originalEndDateTime.toLocalTime();
			System.out.println("original end time in editEndTime = " + originalEndTime.toString());
			
			if (!originalEndTime.equals(editEndTime)) {
				taskDateTimeFirst.setEndDateTime(LocalDateTime.of(originalEndDate, editEndTime));
				LOGGER.log(Level.INFO, "End time edited for first occurrence of recurring task");
			} else {
				isEditEndTime = false;
			}
		} else {
			originalEndDateTime = task.getEndDateTime();
			originalEndDate = originalEndDateTime.toLocalDate();
			originalEndTime = originalEndDateTime.toLocalTime();
			
			if (!originalEndTime.equals(editEndTime)) {
				task.setEndDateTime(LocalDateTime.of(originalEndDate, editEndTime));
				LOGGER.log(Level.INFO, "End time edited");
			} else {
				isEditEndTime = false;
			}
		}
	}
	
	private void editInterval(TaskObject task) {
		originalInterval = task.getInterval();
		
		if (!originalInterval.equals(editInterval)) {
			task.setInterval(editInterval);
			Recurring.setAllRecurringEventTimes(task); // Calls the Recurring class to update the list of recurrence timings
			LOGGER.log(Level.INFO, "Interval edited");
		} else {
			isEditInterval = false;
		}
	}
	
	// Updates the category of the task in case it has been modified
	private void updateCategory() {
		LocalDateTime newStartDateTime = editTask.getStartDateTime();
		LocalDateTime newEndDateTime = editTask.getEndDateTime();
		
		if (newStartDateTime.equals(LocalDateTime.MAX) && newEndDateTime.equals(LocalDateTime.MAX)) {
			editTask.setCategory(CATEGORY_FLOATING);
		} else {
			if (newEndDateTime.equals(LocalDateTime.MAX)) {
				editTask.setCategory(CATEGORY_DEADLINE);
			} else {
				editTask.setCategory(CATEGORY_EVENT);
			}
		}
	}
	
	// Saves the updated file to Storage
	private void saveExternal() {
		try {
			FileStorage storage = FileStorage.getInstance();
			storage.save(taskList);
			LOGGER.log(Level.INFO, "Storage file updated");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	
	// FOR DEBUGGING
	private void checkEditInformation() {
		System.out.println("isEditTitle = " + isEditTitle);
		System.out.println("isEditStartDate = " + isEditStartDate);
		System.out.println("isEditStartTime = " + isEditStartTime);
		System.out.println("isEditStartTimeForAllOccurrences = " + isEditStartTimeForAllOccurrences);
		System.out.println("isEditEndDate = " + isEditEndDate);
		System.out.println("isEditEndTime = " + isEditEndTime);
		System.out.println("isEditEndTimeForAllOccurrences = " + isEditEndTimeForAllOccurrences);
		System.out.println("isEditInterval = " + isEditInterval);
		System.out.println("isRecurringTask = " + isRecurringTask);
	}
	
	// ------------------------- OUTPUT MESSAGES -------------------------
	

	/*
	 * The Deadline check is because output for deadlines is slightly different, as they should 
	 * not have start/end dates/times.
	 * If the original date/time is equal to MAX value, then there was no previous date/time so
	 * the output should be 'added' instead of 'edited'.
	 */
	private void setOutput() {
		if (isEditTitle) {
			outputTitleEditedMessage();
		}
		if (isEditStartDate) {
			if (editTask.getCategory().equals(CATEGORY_DEADLINE)) {
				if (originalStartDate.equals(LocalDate.MAX)) {
					outputDateAddedMessage();
				} else {
					outputDateEditedMessage();
				}
			} else {
				if (originalStartDate.equals(LocalDate.MAX)) {
					outputStartDateAddedMessage();
				} else {
					outputStartDateEditedMessage();
				}
			}
		}
		if (isEditStartTime) {
			if (isEditStartTimeForAllOccurrences) {
				if (editTask.getCategory().equals(CATEGORY_DEADLINE)) {
					outputTimeEditedForAllOccurrencesMessage();
				} else {
					outputStartTimeEditedForAllOccurrencesMessage();
				}
			} else {
				if (editTask.getCategory().equals(CATEGORY_DEADLINE)) {
					if (originalStartTime.equals(LocalTime.MAX)) {
						outputTimeAddedMessage();
					} else {
						outputTimeEditedMessage();
					}
				} else {
					if (originalStartTime.equals(LocalTime.MAX)) {
						outputStartTimeAddedMessage();
					} else {
						outputStartTimeEditedMessage();
					}
				}
			}
		}
		if (isEditEndDate) {
			if (originalEndDate.equals(LocalDate.MAX)) {
				outputEndDateAddedMessage();
			} else {
				outputEndDateEditedMessage();
			}
		}
		if (isEditEndTime) {
			if (isEditEndTimeForAllOccurrences) {
				outputEndTimeEditedForAllOccurrencesMessage();
			} else {
				if (originalEndTime.equals(LocalTime.MAX)) {
					outputEndTimeAddedMessage();
				} else {
					outputEndTimeEditedMessage();
				}
			}
		}
		if (isEditInterval) {
			outputIntervalEditedMessage();
		}
		
		concatenateOutput();
	}
	
	private void outputTitleEditedMessage() {
		tempOutput.add(String.format(MESSAGE_TITLE_EDIT, originalTitle, editTitle));
	}
	
	private void outputDateAddedMessage() {
		tempOutput.add(String.format(MESSAGE_DATE_ADD, editStartDate));
	}
	private void outputDateEditedMessage() {
		tempOutput.add(String.format(MESSAGE_DATE_EDIT, originalStartDate, editStartDate));
	}
	
	private void outputStartDateAddedMessage() {
		tempOutput.add(String.format(MESSAGE_START_DATE_ADD, editStartDate));
	}
	private void outputStartDateEditedMessage() {
		tempOutput.add(String.format(MESSAGE_START_DATE_EDIT, originalStartDate, editStartDate));
	}
	
	private void outputTimeEditedForAllOccurrencesMessage() {
		tempOutput.add(String.format(MESSAGE_TIME_FOR_ALL_OCCURRENCES_EDIT, editStartTime));
	}
	
	private void outputStartTimeEditedForAllOccurrencesMessage() {
		tempOutput.add(String.format(MESSAGE_START_TIME_FOR_ALL_OCCURRENCES_EDIT, editStartTime));
	}

	private void outputTimeAddedMessage() {
		tempOutput.add(String.format(MESSAGE_TIME_ADD, editStartTime));
	}
	private void outputTimeEditedMessage() {
		tempOutput.add(String.format(MESSAGE_TIME_EDIT, originalStartTime, editStartTime));
	}
	
	private void outputStartTimeAddedMessage() {
		tempOutput.add(String.format(MESSAGE_START_TIME_ADD, editStartTime));
	}
	private void outputStartTimeEditedMessage() {
		tempOutput.add(String.format(MESSAGE_START_TIME_EDIT, originalStartTime, editStartTime));
	}
	
	private void outputEndTimeEditedForAllOccurrencesMessage() {
		tempOutput.add(String.format(MESSAGE_END_TIME_FOR_ALL_OCCURRENCES_EDIT, editEndTime));
	}

	private void outputEndDateAddedMessage() {
		tempOutput.add(String.format(MESSAGE_END_DATE_ADD, editEndDate));
	}
	private void outputEndDateEditedMessage() {
		tempOutput.add(String.format(MESSAGE_END_DATE_EDIT, originalEndDate, editEndDate));
	}

	private void outputEndTimeAddedMessage() {
		tempOutput.add(String.format(MESSAGE_END_TIME_ADD, editEndTime));
	}
	private void outputEndTimeEditedMessage() {
		tempOutput.add(String.format(MESSAGE_END_TIME_EDIT, originalEndTime, editEndTime));
	}
	
	private void outputIntervalEditedMessage() {
		tempOutput.add(MESSAGE_INTERVAL_EDIT);
	}
	
	// Combines all the output strings into 1 string
	private void concatenateOutput() {
		if (tempOutput.isEmpty()) {
			output.add(MESSAGE_NO_EDIT);
		} else {
			String concatOutput = "";
			for (int i = 0; i < tempOutput.size(); i++) {
				concatOutput = concatOutput.concat(tempOutput.get(i));
			}
			
			output.add(concatOutput.trim());
		}
	}

	// ------------------------- GETTERS -------------------------
	
	public TaskObject getEditTask() {
		return editTask;
	}
	
	public int geteditItemIndex() {
		return editItemIndex;
	}

	public String getOriginalTitle() {
		return originalTitle;
	}
	
	public LocalDateTime getOriginalStartDateTime() {
		return originalStartDateTime;
	}
	
	public LocalDateTime getOriginalEndDateTime() {
		return originalEndDateTime;
	}
	
	public Interval getOriginalInterval() {
		return originalInterval;
	}
	
	public boolean getIsEditTitle() {
		return isEditTitle;
	}
	
	public boolean getIsEditStartDateAndTime() {
		return isEditStartDate || isEditStartTime;
	}
	
	public boolean getIsEditStartDate() {
		return isEditStartDate;
	}
	
	public boolean getIsEditStartTime() {
		return isEditStartTime;
	}
	
	public boolean getIsEditEndDateAndTime() {
		return isEditEndDate || isEditEndTime;
	}
	
	public boolean getIsEditEndDate() {
		return isEditEndDate;
	}
	
	public boolean getIsEditEndTime() {
		return isEditEndTime;
	}
}
