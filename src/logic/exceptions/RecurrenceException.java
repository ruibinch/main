//@@author A0124052X

package logic.exceptions;

import common.TaskObject;
import common.Interval;
import static logic.constants.Strings.*;

import java.time.LocalDateTime;

@SuppressWarnings("serial")
public class RecurrenceException extends Exception {

	private int taskId = -1;
	private String title = "";
	
	public RecurrenceException() {
		
	}
	
	public RecurrenceException(TaskObject task) {
		super(String.format(MESSAGE_RECURRENCE_EXCEPTION, task.getTitle()));
		this.taskId = task.getTaskId();
		this.title = task.getTitle();
	}
	
	public RecurrenceException(String errorMessage) {
		super(String.format(MESSAGE_RECURRENCE_EXCEPTION, errorMessage));
	}
	
	public RecurrenceException(Interval interval) {
		super(String.format(MESSAGE_RECURRENCE_EXCEPTION, MESSAGE_RECURRENCE_EXCEPTION_INVALID_INTERVAL));
	}
	
	public RecurrenceException(LocalDateTime until) {
		super(String.format(MESSAGE_RECURRENCE_EXCEPTION, MESSAGE_RECURRENCE_EXCEPTION_INVALID_UNTIL));
	}
	
	public String getRecurrenceExceptionMessage() {
		return super.getMessage();
	}
	
	public String getTaskName() {
		return title;
	}
	
	public int getTaskId() {
		return taskId;
	}
}
