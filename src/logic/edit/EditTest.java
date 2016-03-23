package logic.edit;
import static org.junit.Assert.*;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import common.CommandObject;
import common.LocalDateTimePair;
import common.TaskObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import static logic.constants.Index.*;
import static logic.constants.Strings.*;

// Can consider test cases for situations where a time is added, i.e. from MAX to a specified time - how to display that?
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class EditTest {
	
	private static ArrayList<TaskObject> testList = new ArrayList<TaskObject> ();
	private static ArrayList<String> actualOutput = new ArrayList<String>();
	private static ArrayList<String> correctOutput = new ArrayList<String>();
	private ArrayList<LocalDateTimePair> testTimings = new ArrayList<LocalDateTimePair>();
	
	
	private CommandObject testCommandObject;
	private TaskObject testTaskObject;

	@Test // Populate the task list
	public void populate() {
	
		testList.add(new TaskObject("Study hard for finals", LocalDateTime.of(LocalDate.parse("2016-05-25"), LocalTime.parse("09:00")),
				"deadline", "incomplete", 1));
		testList.add(new TaskObject("Find internship in Germany", LocalDateTime.of(LocalDate.parse("2016-12-31"), LocalTime.parse("23:59")),
				"deadline", "incomplete", 2));
		testList.add(new TaskObject("Travel Eastern Europe", "floating", "incomplete", 3));
		testList.add(new TaskObject("CS2106 Assignment 2", LocalDateTime.of(LocalDate.parse("2016-12-01"), LocalTime.parse("18:00")),
				"deadline", "incomplete", 4));
		testList.add(new TaskObject("CS2103 v0.2", LocalDateTime.of(LocalDate.parse("2017-03-24"), LocalTime.parse("19:00")), "deadline", "incomplete", 5));
		testList.add(new TaskObject("SSS1207 CA2", LocalDateTime.of(LocalDate.parse("2016-03-31"), LocalTime.parse("23:59")), "deadline", "incomplete", 6));
		testList.add(new TaskObject("Spring break", LocalDateTime.of(LocalDate.parse("2001-01-11"), LocalTime.parse("00:00")),
				LocalDateTime.of(LocalDate.parse("2016-05-06"), LocalTime.parse("23:59")), "event", "incomplete", 7));
		testList.add(new TaskObject("Overseas paradise", LocalDateTime.of(LocalDate.parse("2012-01-31"), LocalTime.parse("22:24")), 
				LocalDateTime.of(LocalDate.parse("2013-11-29"), LocalTime.parse("17:00")), "event", "complete", 8));
		testList.add(new TaskObject("Hiking trip", LocalDateTime.of(LocalDate.parse("2014-07-11"), LocalTime.parse("16:00")), 
				LocalDateTime.of(LocalDate.parse("2016-07-15"), LocalTime.parse("17:00")), "event", "incomplete", 9));
		testList.add(new TaskObject("Confinement", LocalDateTime.of(LocalDate.parse("2012-02-14"), LocalTime.parse("08:00")),
				LocalDateTime.of(LocalDate.parse("2012-02-21"), LocalTime.parse("12:00")), "event", "incomplete", 10));
		
		// Tests for editing between categories
		testList.add(new TaskObject("CS2107 presentation", "floating", "incomplete", 11));
		testList.add(new TaskObject("Europe trip", "floating", "incomplete", 12));
		
		// Tests for editing recurrence events
		LocalDateTime startOne = LocalDateTime.of(LocalDate.parse("2016-03-25"), LocalTime.parse("16:00"));
		LocalDateTime endOne = LocalDateTime.of(LocalDate.parse("2016-03-25"), LocalTime.parse("18:00"));
		LocalDateTime startTwo = LocalDateTime.of(LocalDate.parse("2016-04-01"), LocalTime.parse("16:00"));
		LocalDateTime endTwo = LocalDateTime.of(LocalDate.parse("2016-04-01"), LocalTime.parse("18:00"));
		LocalDateTime startThree = LocalDateTime.of(LocalDate.parse("2016-04-08"), LocalTime.parse("16:00"));
		LocalDateTime endThree = LocalDateTime.of(LocalDate.parse("2016-04-08"), LocalTime.parse("18:00"));
		LocalDateTime startFour = LocalDateTime.of(LocalDate.parse("2016-04-15"), LocalTime.parse("16:00"));
		LocalDateTime endFour = LocalDateTime.of(LocalDate.parse("2016-04-15"), LocalTime.parse("18:00"));
		LocalDateTimePair pairOne = new LocalDateTimePair(startOne, endOne);
		LocalDateTimePair pairTwo = new LocalDateTimePair(startTwo, endTwo);		
		LocalDateTimePair pairThree = new LocalDateTimePair(startThree, endThree);		
		LocalDateTimePair pairFour = new LocalDateTimePair(startFour, endFour);
		testTimings.add(pairOne);
		testTimings.add(pairTwo);
		testTimings.add(pairThree);
		testTimings.add(pairFour);
		
		testList.add(new TaskObject("CS2103 lecture", startOne, endFour, "event", "incomplete", 13, true, testTimings));
		testList.add(new TaskObject("CS2103 lecture", startOne, endFour, "event", "incomplete", 14, true, testTimings));
		
	}
	
	/*
	 * Summary of test cases:
	 * TestA - TITLE	STARTDATE	STARTTIME
	 * TestB - 			STARTDATE	STARTTIME
	 * TestC - 			STARTDATE	STARTTIME (same old and new startdate)
	 * TestD - 			STARTDATE	STARTTIME (same old and new starttime)
	 * TestE - 			STARTDATE
	 * TestF - 						STARTTIME
	 * TestG - TITLE				STARTTIME
	 * TestH - TITLE
	 * TestI - TITLE	STARTDATE
	 * TestJ - TITLE	STARTDATE	STARTTIME	ENDDATE		ENDTIME
	 */
	
	@Test // Test edit for title + start date + start time
	public void testA() {
		LocalDateTime testStartDateTime = LocalDateTime.of(LocalDate.parse("2016-07-11"), LocalTime.parse("10:00"));
		testTaskObject = new TaskObject("Reservist", testStartDateTime, "", "", -1);
		testCommandObject = new CommandObject(INDEX_EDIT, testTaskObject, 9);
		
		Edit testEdit = new Edit(testCommandObject, testList, testList);
		actualOutput = testEdit.run();
		correctOutput.add("Title edited from 'Hiking trip' to 'Reservist'. Start date edited from '2014-07-11' to '2016-07-11'. Start time edited from '16:00' to '10:00'.");
		
		assertEquals(actualOutput, correctOutput);
		correctOutput.clear();
	}

	@Test // Test edit for start date + start time
	public void testB() {
		LocalDateTime testStartDateTime = LocalDateTime.of(LocalDate.parse("2016-03-24"), LocalTime.parse("11:00"));
		testTaskObject = new TaskObject("", testStartDateTime, "", "", -1);
		testCommandObject = new CommandObject(INDEX_EDIT, testTaskObject, 5);
		
		Edit testEdit = new Edit(testCommandObject, testList, testList);
		actualOutput = testEdit.run();
		correctOutput.add("Date edited from '2017-03-24' to '2016-03-24'. Time edited from '19:00' to '11:00'.");
		
		assertEquals(actualOutput, correctOutput);
		correctOutput.clear();
	}
	
	@Test // Test edit for start date + start time, but with same old and new start date, so only start time should be edited
	public void testC() {
		LocalDateTime testStartDateTime = LocalDateTime.of(LocalDate.parse("2016-12-31"), LocalTime.parse("18:00"));
		testTaskObject = new TaskObject("", testStartDateTime, "", "", -1);
		testCommandObject = new CommandObject(INDEX_EDIT, testTaskObject, 2);
		
		Edit testEdit = new Edit(testCommandObject, testList, testList);
		actualOutput = testEdit.run();
		correctOutput.add("Time edited from '23:59' to '18:00'.");
		
		assertEquals(actualOutput, correctOutput);
		correctOutput.clear();
	}
	
	
	@Test // Test edit for start date + start time, but with same old and new start time, so only start date should be edited
	public void testD() {
		LocalDateTime testStartDateTime = LocalDateTime.of(LocalDate.parse("2016-04-25"), LocalTime.parse("09:00"));
		testTaskObject = new TaskObject("", testStartDateTime, "", "", -1);
		testCommandObject = new CommandObject(INDEX_EDIT, testTaskObject, 1);
		
		Edit testEdit = new Edit(testCommandObject, testList, testList);
		actualOutput = testEdit.run();
		correctOutput.add("Date edited from '2016-05-25' to '2016-04-25'.");
		
		assertEquals(actualOutput, correctOutput);
		correctOutput.clear();
	}
	
	@Test // Test edit for start date
	public void testE() {
		LocalDateTime testStartDateTime = LocalDateTime.of(LocalDate.parse("2016-04-01"), LocalTime.MAX);
		testTaskObject = new TaskObject("", testStartDateTime, "", "", -1);
		testCommandObject = new CommandObject(INDEX_EDIT, testTaskObject, 4);
		
		Edit testEdit = new Edit(testCommandObject, testList, testList);
		actualOutput = testEdit.run();
		correctOutput.add("Date edited from '2016-12-01' to '2016-04-01'.");
		
		assertEquals(actualOutput, correctOutput);
		correctOutput.clear();
	}
	
	@Test // Test edit for start time
	public void testF() {
		LocalDateTime testStartDateTime = LocalDateTime.of(LocalDate.MAX, LocalTime.parse("16:00"));
		testTaskObject = new TaskObject("", testStartDateTime, "", "", -1);
		testCommandObject = new CommandObject(INDEX_EDIT, testTaskObject, 6);
		
		Edit testEdit = new Edit(testCommandObject, testList, testList);
		actualOutput = testEdit.run();
		correctOutput.add("Time edited from '23:59' to '16:00'.");
		
		assertEquals(actualOutput, correctOutput);
		correctOutput.clear();
	}
	
	@Test // Test edit for title + start time
	public void testG() {
		LocalDateTime testStartDateTime = LocalDateTime.of(LocalDate.MAX, LocalTime.parse("10:00"));
		testTaskObject = new TaskObject("Army", testStartDateTime, "", "", -1);
		testCommandObject = new CommandObject(INDEX_EDIT, testTaskObject, 8);
		
		Edit testEdit = new Edit(testCommandObject, testList, testList);
		actualOutput = testEdit.run();
		correctOutput.add("Title edited from 'Overseas paradise' to 'Army'. Start time edited from '22:24' to '10:00'.");
		
		assertEquals(actualOutput, correctOutput);
		correctOutput.clear();
	}
	
	@Test // Test edit for title
	public void testH() {
		testTaskObject = new TaskObject("Travel Eastern Europe and Iceland", "", "", -1);
		testCommandObject = new CommandObject(INDEX_EDIT, testTaskObject, 3);
		
		Edit testEdit = new Edit(testCommandObject, testList, testList);
		actualOutput = testEdit.run();
		correctOutput.add("Title edited from 'Travel Eastern Europe' to 'Travel Eastern Europe and Iceland'.");
		
		assertEquals(actualOutput, correctOutput);
		correctOutput.clear();
	}
	
	@Test // Test edit for title + start date
	public void testI() {
		LocalDateTime testStartDateTime = LocalDateTime.of(LocalDate.parse("2016-01-11"), LocalTime.MAX);
		testTaskObject = new TaskObject("AY2016/17 Sem 2", testStartDateTime, "", "", -1);
		testCommandObject = new CommandObject(INDEX_EDIT, testTaskObject, 7);
		
		Edit testEdit = new Edit(testCommandObject, testList, testList);
		actualOutput = testEdit.run();
		correctOutput.add("Title edited from 'Spring break' to 'AY2016/17 Sem 2'. Start date edited from '2001-01-11' to '2016-01-11'.");
		
		assertEquals(actualOutput, correctOutput);
		correctOutput.clear();
	}
	
	@Test // Test edit for title + start date + start time + end date + end time
	public void testJ() {
		LocalDateTime testStartDateTime = LocalDateTime.of(LocalDate.parse("2016-01-05"), LocalTime.parse("14:00"));
		LocalDateTime testEndDateTime = LocalDateTime.of(LocalDate.parse("2016-01-09"), LocalTime.parse("13:00"));
		testTaskObject = new TaskObject("HK trip", testStartDateTime, testEndDateTime, "", "", -1);
		testCommandObject = new CommandObject(INDEX_EDIT, testTaskObject, 10);
		
		Edit testEdit = new Edit(testCommandObject, testList, testList);
		actualOutput = testEdit.run();
		correctOutput.add("Title edited from 'Confinement' to 'HK trip'. Start date edited from '2012-02-14' to '2016-01-05'. " + 
				"Start time edited from '08:00' to '14:00'. End date edited from '2012-02-21' to '2016-01-09'. End time edited from '12:00' to '13:00'.");
		
		assertEquals(actualOutput, correctOutput);
		correctOutput.clear();
	}
	
	@Test // Test edit for modifying floating -> deadline - adding a start date and time
	public void testK() {
		LocalDateTime testStartDateTime = LocalDateTime.of(LocalDate.parse("2016-04-18"), LocalTime.parse("09:00"));
		testTaskObject = new TaskObject("", testStartDateTime, "", "", -1);
		testCommandObject = new CommandObject(INDEX_EDIT, testTaskObject, 11);
		
		Edit testEdit = new Edit(testCommandObject, testList, testList);
		actualOutput = testEdit.run();
		correctOutput.add("Date edited from '+999999999-12-31' to '2016-04-18'. "
				+ "Time edited from '23:59:59.999999999' to '09:00'.");
		
		String actualCategory = testEdit.getEditTask().getCategory();
		String correctCategory = "deadline";
		
		assertEquals(actualCategory, correctCategory);
		assertEquals(actualOutput, correctOutput);
		correctOutput.clear();
	}
	
	@Test // Test edit for modifying floating -> event - adding a start & end date and time
	public void testL() {
		LocalDateTime testStartDateTime = LocalDateTime.of(LocalDate.parse("2016-06-18"), LocalTime.parse("10:00"));
		LocalDateTime testEndDateTime = LocalDateTime.of(LocalDate.parse("2016-08-08"), LocalTime.parse("20:00"));
		testTaskObject = new TaskObject("", testStartDateTime, testEndDateTime, "", "", -1);		
		testCommandObject = new CommandObject(INDEX_EDIT, testTaskObject, 12);
				
		Edit testEdit = new Edit(testCommandObject, testList, testList);
		actualOutput = testEdit.run();
		correctOutput.add("Start date edited from '+999999999-12-31' to '2016-06-18'. Start time edited from '23:59:59.999999999' to '10:00'. " + 
				"End date edited from '+999999999-12-31' to '2016-08-08'. End time edited from '23:59:59.999999999' to '20:00'.");
		
		String actualCategory = testEdit.getEditTask().getCategory();
		String correctCategory = CATEGORY_EVENT;
		
		assertEquals(actualCategory, correctCategory);
		assertEquals(actualOutput, correctOutput);
		correctOutput.clear();

		testCommandObject = new CommandObject(INDEX_EDIT, testTaskObject, 12);
		
	}
	
	@Test // Test edit for recurrence event - edit for start dates
	public void testM() {
		// 1st assert - check output
		LocalDateTime testStartDateTime = LocalDateTime.of(LocalDate.parse("2016-03-26"), LocalTime.MAX);
		testTaskObject = new TaskObject("", testStartDateTime, "", "", -1);
		testCommandObject = new CommandObject(INDEX_EDIT, testTaskObject, 13);
		
		Edit testEdit = new Edit(testCommandObject, testList, testList);
		actualOutput = testEdit.run();
		correctOutput.add("Start date edited from '2016-03-25' to '2016-03-26'.");

		assertEquals(actualOutput, correctOutput);
		correctOutput.clear();
		
		// 2nd assert - check timings; only the first date should be modified
		TaskObject editedTask = testEdit.getEditTask();
		ArrayList<LocalDateTimePair> actualTimings = editedTask.getTaskDateTimes();
		LocalDateTime actualFirstTiming = actualTimings.get(0).getStartDateTime();
		LocalDateTime correctFirstTiming = LocalDateTime.of(LocalDate.parse("2016-03-26"), LocalTime.parse("16:00"));
		LocalDateTime actualSecondTiming = actualTimings.get(1).getStartDateTime();
		LocalDateTime correctSecondTiming = LocalDateTime.of(LocalDate.parse("2016-04-01"), LocalTime.parse("16:00"));
		LocalDateTime actualThirdTiming = actualTimings.get(2).getStartDateTime();
		LocalDateTime correctThirdTiming = LocalDateTime.of(LocalDate.parse("2016-04-08"), LocalTime.parse("16:00"));
		LocalDateTime actualFourthTiming = actualTimings.get(3).getStartDateTime();
		LocalDateTime correctFourthTiming = LocalDateTime.of(LocalDate.parse("2016-04-15"), LocalTime.parse("16:00"));
		
		assertEquals(actualFirstTiming, correctFirstTiming);
		assertEquals(actualSecondTiming, correctSecondTiming);
		assertEquals(actualThirdTiming, correctThirdTiming);
		assertEquals(actualFourthTiming, correctFourthTiming);
	}
	
	
	@Test // Test edit for recurrence event - edit for start times
	public void testN() {
		// 1st assert - check output
		LocalDateTime testStartDateTime = LocalDateTime.of(LocalDate.MAX, LocalTime.parse("14:00"));
		testTaskObject = new TaskObject("", testStartDateTime, "", "", -1);
		testCommandObject = new CommandObject(INDEX_EDIT, testTaskObject, 14);
		
		Edit testEdit = new Edit(testCommandObject, testList, testList);
		actualOutput = testEdit.run();
		correctOutput.add("All start times edited to '14:00'.");
		
		assertEquals(actualOutput, correctOutput);
		
		// 2nd assert - check timings
		TaskObject editedTask = testEdit.getEditTask();
		ArrayList<LocalDateTimePair> actualTimings = editedTask.getTaskDateTimes();
		LocalDateTime actualFirstTiming = actualTimings.get(0).getStartDateTime();
		LocalDateTime correctFirstTiming = LocalDateTime.of(LocalDate.parse("2016-03-26"), LocalTime.parse("14:00"));
		LocalDateTime actualSecondTiming = actualTimings.get(1).getStartDateTime();
		LocalDateTime correctSecondTiming = LocalDateTime.of(LocalDate.parse("2016-04-01"), LocalTime.parse("14:00"));
		LocalDateTime actualThirdTiming = actualTimings.get(2).getStartDateTime();
		LocalDateTime correctThirdTiming = LocalDateTime.of(LocalDate.parse("2016-04-08"), LocalTime.parse("14:00"));
		LocalDateTime actualFourthTiming = actualTimings.get(3).getStartDateTime();
		LocalDateTime correctFourthTiming = LocalDateTime.of(LocalDate.parse("2016-04-15"), LocalTime.parse("14:00"));
		
		assertEquals(actualFirstTiming, correctFirstTiming);
		assertEquals(actualSecondTiming, correctSecondTiming);
		assertEquals(actualThirdTiming, correctThirdTiming);
		assertEquals(actualFourthTiming, correctFourthTiming);
		
	}

}
