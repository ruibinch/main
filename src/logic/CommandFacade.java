package logic;

import common.*;
import logic.add.Add;
import logic.delete.Delete;
import logic.display.Display;
import logic.edit.Edit;
import logic.help.Help;
import logic.mark.Done;
import logic.mark.Incomplete;
import logic.mark.Mark;
import logic.mark.Overdue;
import logic.save.Save;
import logic.search.Search;
import logic.undoredo.UndoRedo;
import static logic.constants.Index.*;
import static logic.constants.Strings.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Deque;

import com.sun.media.jfxmedia.logging.Logger;

/**
 * This class represents a facade pattern to parse the CommandObject that has
 * been returned by the parser. A new CommandFacade class is initialised with
 * each new user input, and all relevant arguments are passed to this class. The
 * variables that will be actually used depends on the input of the user.
 * 
 * @author RuiBin
 *
 */
public class CommandFacade {

	private ArrayList<TaskObject> taskList;
	private Deque<CommandObject> undoList;
	private Deque<CommandObject> redoList;
	private ArrayList<TaskObject> lastOutputTaskList;
	private ArrayList<String> output;

	private CommandObject commandObj;
	private int commandType;
	private TaskObject taskObj;
	private int index;
	private int lastSearchedIndex; // stores the index of the last recurring task searched	
	
	boolean isUndoAction;
	boolean isRedoAction;

	/**
	 * Constructor called by Logic which passes all arguments that might be used
	 * 
	 * @param taskList
	 *            The default taskList storing all the tasks
	 * @param undoList
	 *            The deque of CommandObjects which stores all undo actions
	 * @param redoList
	 *            The deque of CommandObjects which stores all redo actions
	 * @param lastOutputTaskList
	 *            The ArrayList which keeps track of what is currently being
	 *            displayed to the user
	 * @param commandObj
	 *            The CommandObject returned by the Parser class which returns
	 *            the processed information
	 * @param isUndoAction
	 *            Tracks if this call is an undo action
	 * @param isRedoAction
	 *            Tracks if this call is an undo action
	 */
	public CommandFacade(ArrayList<TaskObject> taskList, Deque<CommandObject> undoList, Deque<CommandObject> redoList,
			ArrayList<TaskObject> lastOutputTaskList, CommandObject commandObj, boolean isUndoAction, boolean isRedoAction) {
		this.taskList = taskList;
		this.undoList = undoList;
		this.redoList = redoList;
		this.lastOutputTaskList = lastOutputTaskList;
		this.commandObj = commandObj;
		this.isUndoAction = isUndoAction;
		this.isRedoAction = isRedoAction;
		setCommandObjectValues();
	}

	/**
	 * The run method is called by Logic after each initialisation of the
	 * CommandFacade class. It parses the command type and calls the appropriate
	 * function. Responsible for manipulating the undoList and determining
	 * whether the redoList should be cleared. <br>
	 * The redoList will be cleared as long as the command given is not an undo
	 * or redo. <br>
	 * A "reverse" CommandObject will be created and pushed into the undoList if
	 * the current CommandObject is an action which manipulates the existing
	 * task list.
	 */
	public void run() {

		// FOR TESTING
		// System.out.println("isUndoAction = " + isUndoAction + ", undo size =
		// "
		// + undoList.size() + ", redo size = " + redoList.size());
		// System.out.println("commandObj command type = " +
		// commandObj.getCommandType());
		// if (taskObj != null) printTaskObjectFields(taskObj);
		// System.out.println("commandObj index = " + commandObj.getIndex());
		// System.out.println();

		// Clears the redo stack if it is a new command which modifies the task
		// list
		if (!redoList.isEmpty() && isListOperation(commandType) && !isUndoAction && !isRedoAction) {
			clearRedoList();
		}

		switch (commandType) {
			case INDEX_ADD :
				addFunction();
				break;
			case INDEX_SEARCH_DISPLAY :
				checkDisplayOrSearch();
				break;
			case INDEX_EDIT :
				editFunction();
				break;
			case INDEX_DELETE :
				deleteFunction();
				break;
			case INDEX_UNDO :
			case INDEX_REDO :
				undoRedoFunction();
				break;
			case INDEX_SAVE :
				saveFunction();
				break;
			case INDEX_EXIT :
				exitFunction();
				break;
			case INDEX_HELP :
				helpFunction();
				break;
			case INDEX_COMPLETE :
				doneFunction();
				break;
			case INDEX_INCOMPLETE :
				incompleteFunction();
				break;
			case INDEX_OVERDUE :
				overdueFunction();
				break;
			default :
				printInvalidCommandMessage();
				break;
		}
	}

	
	// ----------------------- FUNCTIONS -------------------------
	
	/**
	 * Calls Add function, which adds the task to the task list and writes it to
	 * storage. It then adds the reverse CommandObject to the undo list or the
	 * redo list.
	 */
	private void addFunction() {
		Add add = new Add(taskObj, index, lastSearchedIndex, taskList);
		setOutput(add.run());
		setLastOutputTaskList(taskList);

		if (isUndoAction) {
			addToList(commandObj, redoList);
		} else {
			addToList(commandObj, undoList);
		}
	}

	/**
	 * This method checks for the presence of a search keyword in TaskObject. If
	 * there is a keyword, search function will be called. If there is no
	 * keyword, display function will be called.
	 */
	private void checkDisplayOrSearch() {
		if (taskObj.isSearchKeywordPresent())
			searchFunction();
		else
			displayFunction();
	}

	// Calls Search function which outputs only the tasks that match the search keyword.
	private void searchFunction() {
		Search search = new Search(commandObj, taskList, lastOutputTaskList);
		setOutput(search.run());
		setLastOutputTaskList(search.getLastOutputTaskList());
		setLastSearchedIndex(search.getSearchIndex());	
	}

	// Calls Display function which outputs the entire task list.
	private void displayFunction() {
		Display display = new Display(taskList);
		setOutput(display.run());
		setLastOutputTaskList(display.getLastOutputTaskList());
		setLastSearchedIndex(-1);	
	}

	/**
	 * Calls Edit function which edits the task title, date, or both. It then
	 * adds the reverse CommandObject to the undo list or the redo list.
	 */
	private void editFunction() {
		Edit edit = new Edit(commandObj, lastOutputTaskList, taskList, lastSearchedIndex);
		setOutput(edit.run());
		setLastOutputTaskList(taskList);

		if (isUndoAction) {
			addToList(edit, redoList);
		} else {
			addToList(edit, undoList);
		}
	}

	/**
	 * The method checks if there is a task specified in the Delete command. If
	 * there is no task specified, quick delete is run, i.e. deletes the most
	 * recently added task. If there is a task specified, normal delete is run
	 * to remove the specified task.
	 */
	private void deleteFunction() {
		// 4 things to track
		TaskObject removedTask = new TaskObject();
		LocalDateTimePair removedOccurrenceTiming = new LocalDateTimePair(); // will be filled if it is a single-occurrence-delete
		Integer removedOccurrenceIndex = Integer.valueOf(-1);
		Boolean isDeleteAll = false;
		Quadruple<TaskObject, LocalDateTimePair, Integer, Boolean> quadruple = 
				new Quadruple<TaskObject, LocalDateTimePair, Integer, Boolean>();
		
		if (index == -1) { // no task specified
			quadruple = quickDelete(removedTask, removedOccurrenceTiming, removedOccurrenceIndex, isDeleteAll);
		} else {
			quadruple = normalDelete(removedTask, removedOccurrenceTiming, removedOccurrenceIndex, isDeleteAll);
		}
		
		isDeleteAll = quadruple.getFourth();
		if (!isDeleteAll) {
			processUndoForDelete(quadruple.getFirst(), quadruple.getSecond(), quadruple.getThird());
		}
	}

	private Quadruple<TaskObject, LocalDateTimePair, Integer, Boolean> quickDelete(
			TaskObject removedTask, LocalDateTimePair removedOccurrenceTiming, Integer removedOccurrenceIndex, Boolean isDeleteAll) {
		
		CommandObject commandObjForQuickDelete = new CommandObject(INDEX_DELETE, new TaskObject(), -1);
		Delete delete = new Delete(commandObjForQuickDelete, taskList, undoList);
		setOutput(delete.run());
		setLastOutputTaskList(taskList);

		removedTask = delete.getRemovedTask();
		return new Quadruple<TaskObject, LocalDateTimePair, Integer, Boolean>(
				removedTask, removedOccurrenceTiming, removedOccurrenceIndex, isDeleteAll);
	}

	private Quadruple<TaskObject, LocalDateTimePair, Integer, Boolean> normalDelete(
			TaskObject removedTask, LocalDateTimePair removedOccurrenceTiming, Integer removedOccurrenceIndex, Boolean isDeleteAll) {
		
		Delete delete = new Delete(commandObj, taskList, lastOutputTaskList, undoList, redoList);
		setOutput(delete.run());
		setTaskList(delete.getTaskList());
		setLastOutputTaskList(this.taskList);
		setUndoList(delete.getUndoList());
		setRedoList(delete.getRedoList());

		removedTask = delete.getRemovedTask();
		removedOccurrenceTiming = delete.getRemovedTaskOccurrenceDetails();
		removedOccurrenceIndex = delete.getRemovedOccurrenceIndex();
		isDeleteAll = delete.getIsDeleteAll();
		return new Quadruple<TaskObject, LocalDateTimePair, Integer, Boolean>(
				removedTask, removedOccurrenceTiming, removedOccurrenceIndex, isDeleteAll);
		
	}

	// If the command was 'delete all', all the 3 lists would be empty
	private boolean checkIfCommandIsDeleteAll() {
		return taskList.isEmpty() && undoList.isEmpty() && redoList.isEmpty();
	}

	// Checks that removedTask is not null, then adds the corresponding CommandObject to the
	// undo list or the redo list
	private void processUndoForDelete(TaskObject removedTask, LocalDateTimePair removedOccurrenceTiming, Integer removedOccurrenceIndex) {
		assert (!removedTask.isNull());

		if (isUndoAction) {
			addToList(removedTask, removedOccurrenceTiming, removedOccurrenceIndex, redoList);
		} else {
			addToList(removedTask, removedOccurrenceTiming, removedOccurrenceIndex, undoList);
		}
	}

	/**
	 * Calls the UndoRedo class, which is a parent of the Undo and Redo classes.
	 * The class reads in the command type and then calls the relevant child
	 * class.
	 */
	private void undoRedoFunction() {
		UndoRedo undoRedo = new UndoRedo(taskList, undoList, redoList);
		setOutput(undoRedo.run(commandType));

		// Update the lists
		setTaskList(undoRedo.getTaskList());
		setUndoList(undoRedo.getUndoList());
		setRedoList(undoRedo.getRedoList());
		setLastOutputTaskList(this.taskList);
	}

	/**
	 * Calls the Save function, which saves the task list to an appropriate
	 * storage place.
	 */
	private void saveFunction() {
		Save save = new Save(taskObj, taskList);
		setOutput(save.run());
	}

	/**
	 * Calls the Exit function, which exits the program
	 */
	private void exitFunction() {
		Exit exit = new Exit();
		exit.run();
	}

	/**
	 * Calls the Help function, which displays the user guide. If there is a
	 * search keyword entered, only the relevant topics will be displayed. If
	 * there is no search keyword entered, the entire user guide will be
	 * displayed.
	 */
	private void helpFunction() {
		String helpSearchKey = taskObj.getTitle();
		Help help = new Help(helpSearchKey);
		setOutput(help.run());
	}

	/**
	 * Calls the Done function, which marks a specified task as done.
	 */
	private void doneFunction() {
		Done done = new Done(commandObj, taskList, lastOutputTaskList);
		setOutput(done.run());
		setLastOutputTaskList(taskList);

		if (done.getTaskIdToMark() != -1) { // If successfully marked as done
			if (isUndoAction) {
				addToList(done, redoList);
			} else {
				addToList(done, undoList);
			}
		}
	}

	/**
	 * Calls the Overdue function, which marks a specified task as overdue.
	 */
	private void overdueFunction() {
		Overdue overdue = new Overdue(commandObj, taskList, lastOutputTaskList);
		setOutput(overdue.run());
		setLastOutputTaskList(taskList);

		if (overdue.getTaskIdToMark() != -1) {
			if (isUndoAction) {
				addToList(overdue, redoList);
			} else {
				addToList(overdue, undoList);
			}
		}
	}

	/**
	 * Calls the Incomplete function, which marks a specified task as overdue.
	 */
	private void incompleteFunction() {
		Incomplete incomplete = new Incomplete(commandObj, taskList, lastOutputTaskList);
		setOutput(incomplete.run());
		setLastOutputTaskList(taskList);

		if (incomplete.getTaskIdToMark() != -1) {
			if (isUndoAction) {
				System.out.println("CommandFacade:383 - Adding to redoList");
				addToList(incomplete, redoList);
			} else {
				addToList(incomplete, undoList);
			}
		}
	}

	// ------------------------- OVERLOADED METHODS TO POPULATE UNDO/REDO LIST -------------------------

	/**
	 * Method for adding a CommandObject containing "add"  to either the undoList or redoList, 
	 * which is previously determined by the caller.
	 * <br>
	 * A "delete" CommandObject will be pushed into the list.
	 * The index of the previously added TaskObject will be added into the CommandObject to 
	 * facilitate future deletion. <br>
	 * If the added task was a recurring task, 
	 */
	private void addToList(CommandObject commandObj, Deque<CommandObject> list) {
		assert (commandType == INDEX_ADD);
		
		CommandObject newCommandObj = new CommandObject();
		
		if (commandObj.getLastSearchedIndex() != -1) {	// it is addition of a single occurrence
			newCommandObj = new CommandObject(INDEX_DELETE, new TaskObject(), index, lastSearchedIndex);
			
		} else {
			if (index == -1) {	// if task was previously added to the end of the list
				if (commandObj.getTaskObject().getIsRecurring()) {
					newCommandObj = new CommandObject(INDEX_DELETE, new TaskObject(true), taskList.size()); // isEditAll set to 'true'
				} else {
				newCommandObj = new CommandObject(INDEX_DELETE, new TaskObject(), taskList.size());
				}
			} else {	// if task was previously added to a pre-determined location in the list
				if (commandObj.getTaskObject().getIsRecurring()) {
					newCommandObj = new CommandObject(INDEX_DELETE, new TaskObject(true), index); // isEditAll set to 'true'
				} else {
					newCommandObj = new CommandObject(INDEX_DELETE, new TaskObject(), index);
				}
			}
		}
		
		list.push(newCommandObj);
	}
	
	/**
	 * Method for adding a CommandObject containing "add" or "delete" to either
	 * the undoList or redoList, which is previously determined by the caller.
	 * <br>
	 * For command "add", 
	 * For command "delete", an "add" CommandObject will be pushed into the
	 * list, together with a copy of the task which was just deleted.
	 * 
	 * @param commandObj
	 *            The CommandObject to be added to the stated list.
	 * @param list
	 *            Either a undoList or a redoList
	 */
	private void addToList(TaskObject removedTask, LocalDateTimePair removedOccurrenceTiming, 
			Integer removedOccurrenceIndex, Deque<CommandObject> list) {
		assert (commandType == INDEX_DELETE);
		
		CommandObject newCommandObj = new CommandObject();

		/*
		 * 2 types of delete:
		 * 1. delete task
		 * 2. delete occurrence in ArrayList<LocalDateTimePair>
		 */
		if (removedOccurrenceTiming.isEmpty()) {
			newCommandObj = new CommandObject(INDEX_ADD, removedTask, index);
		} else {
			TaskObject taskObjWithRemovedOccurrenceTiming = new TaskObject(removedOccurrenceTiming);
			if (lastSearchedIndex == -1) {	// if it is a deletion of the most recent occurrence	
				newCommandObj = new CommandObject(INDEX_ADD, taskObjWithRemovedOccurrenceTiming, removedOccurrenceIndex, index);
			} else {
				newCommandObj = new CommandObject(INDEX_ADD, taskObjWithRemovedOccurrenceTiming, removedOccurrenceIndex, lastSearchedIndex);
			}
		}
		
		list.push(newCommandObj);
	}

	/**
	 * Method for adding a CommandObject containing edit to either the undoList
	 * or redoList, predetermined by the caller of this method. <br>
	 * 
	 * @param editOriginal
	 *            Contains an Edit object which stores information on retrieving
	 *            the original TaskObject prior to the edit.
	 * @param list
	 *            Either an undoList or redoList
	 */
	private void addToList(Edit editOriginal, Deque<CommandObject> list) {
		
		TaskObject originalTask = editOriginal.getOriginalTask();
		originalTask.setIsEditAll(editOriginal.getIsEditAll());
		int editTaskIndex = editOriginal.getEditTaskIndex();
		int editOccurrenceIndex = editOriginal.getEditOccurrenceIndex();
		
		CommandObject newCommandObj = new CommandObject();
		if (editOccurrenceIndex == -1) {
			newCommandObj = new CommandObject(INDEX_EDIT, originalTask, editTaskIndex);
		} else {
			newCommandObj = new CommandObject(INDEX_EDIT, originalTask, editOccurrenceIndex, editTaskIndex);
		}
		list.push(newCommandObj);
	}

	/**
	 * Constructs a CommandObject for either "done", "incomplete" or "overdue"
	 * and pushes it into the list.
	 * 
	 * @param mark
	 *            Mark object which performed the modification to the task list
	 * @param list
	 *            Either an undoList or redoList
	 */
	private void addToList(Mark mark, Deque<CommandObject> list) {
		CommandObject newCommandObj = new CommandObject();

		TaskObject originalTask = mark.getOriginalTask();
		int commandIndex = getCommandIndex(mark.getStatusToChange());
		if (commandIndex != 0) {
			newCommandObj.setCommandType(commandIndex);
			newCommandObj.setTaskObject(originalTask);
			newCommandObj.setIndex(index);
		}

		System.out.println("CommandFacade:512 - commandType = " + commandIndex + ", index = " + index);
		printTaskObjectFields(originalTask);
		list.push(newCommandObj);
	}

	// Returns the appropriate command index depending on the previous status
	private int getCommandIndex(String prevStatus) {
		System.out.println("prevStatus = " + prevStatus);
		if (prevStatus.equals("overdue")) {
			return INDEX_OVERDUE;
		} else {
			if (prevStatus.equals("completed")) {
				return INDEX_COMPLETE;
			} else {
				if (prevStatus.equals("incomplete")) {
					return INDEX_INCOMPLETE;
				}
			}
		}
		return -1;
	}

	/**
	 * Determines if the command involves editing of a task in the list.
	 * 
	 * @param command
	 *            Integer containing the command index of this command
	 * @return a boolean value indicating whether the command involves editing
	 */
	private boolean isListOperation(int command) {
		return command == INDEX_ADD || command == INDEX_EDIT || command == INDEX_DELETE || command == INDEX_COMPLETE
				|| command == INDEX_OVERDUE || command == INDEX_INCOMPLETE;
	}

	private void clearRedoList() {
		while (!redoList.isEmpty()) {
			redoList.pop();
		}
	}

	private void printInvalidCommandMessage() {
		output.clear();
		output.add(MESSAGE_INVALID_COMMAND);
	}

	// ------------------------- GETTERS AND SETTERS -------------------------
	
	public int getCommandType() {
		return commandType;
	}

	public ArrayList<TaskObject> getTaskList() {
		return taskList;
	}

	public Deque<CommandObject> getUndoList() {
		return undoList;
	}

	public Deque<CommandObject> getRedoList() {
		return redoList;
	}

	public ArrayList<TaskObject> getLastOutputTaskList() {
		return lastOutputTaskList;
	}

	public ArrayList<String> getOutput() {
		return output;
	}

	public CommandObject getCommandObject() {
		return commandObj;
	}
	
	public int getLastSearchedIndex() {
		return lastSearchedIndex;
	}

	public void setTaskList(ArrayList<TaskObject> newTaskList) {
		this.taskList = newTaskList;
	}

	public void setUndoList(Deque<CommandObject> newUndoList) {
		this.undoList = newUndoList;
	}

	public void setRedoList(Deque<CommandObject> newRedoList) {
		this.redoList = newRedoList;
	}

	public void setLastOutputTaskList(ArrayList<TaskObject> newLastOutputTaskList) {
		this.lastOutputTaskList = newLastOutputTaskList;
	}

	public void setOutput(ArrayList<String> newOutput) {
		this.output = newOutput;
	}

	public void setCommandObject(CommandObject newCommandObj) {
		this.commandObj = newCommandObj;
	}

	public void setCommandObjectValues() {
		setCommandType();
		setTaskObject();
		setIndex();
		setLastSearchedIndex();
	}

	private void setCommandType() {
		this.commandType = commandObj.getCommandType();
	}

	private void setTaskObject() {
		this.taskObj = commandObj.getTaskObject();
	}

	private void setIndex() {
		this.index = commandObj.getIndex();
	}
	
	public void setLastSearchedIndex() {
		this.lastSearchedIndex = commandObj.getLastSearchedIndex();
	}

	// Called by Search/Display
	public void setLastSearchedIndex(int lastSearchedIndex) {
		this.lastSearchedIndex = lastSearchedIndex;
		//commandObj.setLastSearchedIndex(lastSearchedIndex);
	}
}
