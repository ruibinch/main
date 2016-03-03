package logic;

import static org.junit.Assert.*;

import org.junit.Test;
import java.util.ArrayList;
import java.util.Stack;

public class LogicTest {
	
	// Initialisation of Logic class
	ArrayList<TaskObject> taskList = new ArrayList<TaskObject>();
	Stack<CommandObject> undoList = new Stack<CommandObject>();
	Logic logic = new Logic(taskList, undoList);	

	ArrayList<String> output = new ArrayList<String>();
	ArrayList<String> expectedOutput = new ArrayList<String>();
	CommandObject commandObj;
	TaskObject taskObj;
	
	@Test
	public void test() {
		
		// ADD FUNCTION WORKS
		String testUserInput = "add float float float";
		logic.run(testUserInput);
		printOutput();

		String testUserInput2 = "add meowwoofmoo";
		logic.run(testUserInput2);
		printOutput();
				
		// SEARCH FUNCTION WORKS, BUT 'CATEGORY' AND 'STATUS' ARE NOT FILLED
		String testUserInput3 = "search";
		logic.run(testUserInput3);
		printOutput();
		
		// EDIT FUNCTION WORKS
		String testUserInput4 = "edit 2 changed to this";
		logic.run(testUserInput4);
		printOutput();
		
		String testUserInput5 = "display";
		logic.run(testUserInput5);
		printOutput();
		
		// SEARCH FOR SPECIFIC KEYWORD WORKS
		String testUserInput6 = "search float";
		logic.run(testUserInput6);
		printOutput();
		
		String testUserInput7 = "add water is healthy";
		logic.run(testUserInput7);
		printOutput();
		
		String testUserInput8 = "add water occupies 70% of your body";
		logic.run(testUserInput8);
		printOutput();
		
		// DELETE FUNCTION WORKS
		String testUserInput9 = "delete 4";
		logic.run(testUserInput9);
		printOutput();
		
/*		String testUserInput8 = "view";
		logic.run(testUserInput8);
		printOutput();
		
		String testUserInput9 = "undo";
		logic.run(testUserInput9);
		printOutput();
*/	
		
		
		
	}
		
	
	private void printersForDebugging() {
		printOutput();
		System.out.println();
		printTaskList();
		System.out.println();
		printUndoList();
		System.out.println();
	}
	
	private void printTaskObjectFields(TaskObject taskObj) {
		System.out.println("title = " + taskObj.getTitle());
		System.out.println("start date = " + taskObj.getStartDate());
		System.out.println("end date = " + taskObj.getEndDate());
		System.out.println("start time = " + taskObj.getStartTime());
		System.out.println("end time = " + taskObj.getEndTime());
		System.out.println("category = " + taskObj.getCategory());
		System.out.println("status = " + taskObj.getStatus());
		System.out.println("task id = " + taskObj.getTaskId());
	}

	private void printOutput() {
		output = logic.getOutput();
		for (int i = 0; i< output.size(); i++) {
			System.out.println(output.get(i));
		}
		System.out.println();
	}
	
	private void printTaskList() {
		System.out.println("Task list:");
		for (int i = 0; i < taskList.size(); i++) {
			System.out.println("i = " + i + ", task item = " + taskList.get(i) + ", task id = " + taskList.get(i).getTaskId());
			printTaskObjectFields(taskList.get(i));
		}
	}

	private void printUndoList() {
		System.out.println("Undo list:");
		for (int i = 0; i < undoList.size(); i++) {
			System.out.println("i = " + i + ", undo item = " + undoList.get(i));
			System.out.println("command is " + undoList.get(i).getCommandType());
			printTaskObjectFields(undoList.get(i).getTaskObject());
		}
	}
}