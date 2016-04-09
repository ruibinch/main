package test;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import common.TaskObject;

public class AssertHelper {

    public static void assertTaskEquals(String testDescription, TaskObject task1, TaskObject task2) {
        assertEquals(testDescription + " title", task1.getTitle(), task2.getTitle());
        assertEquals(testDescription + " start dateTime", task1.getStartDateTime(), task2.getEndDateTime());
        assertEquals(testDescription + " end dateTime", task1.getEndDateTime(), task2.getEndDateTime());
        assertEquals(testDescription + " category", task1.getCategory(), task2.getCategory());
        assertEquals(testDescription + " Status", task1.getStatus(), task2.getStatus());
        assertEquals(testDescription + " taskId", task1.getTaskId(), task2.getTaskId());
    }
    
    public static void assertTaskListEquals(String testDescription, 
            ArrayList<TaskObject> expectedTaskList, ArrayList<TaskObject> actualTaskList) {
        assertEquals(testDescription + "listSize" , expectedTaskList.size(), actualTaskList.size());
        int size = expectedTaskList.size();
        for (int i = 0; i < size; i++) {
            assertTaskEquals( testDescription+ "dataListContent" , expectedTaskList.get(i) , actualTaskList.get(i));
        }
    }
    
    public static void assertFileEquals(String testDescription, Path path, ArrayList<String> expectedTaskDataList) {
        assertEquals(testDescription + " File Exist" , true , Files.exists(path));
        
        ArrayList<String> actualTaskDataList = new ArrayList<String>();
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(path.toString()));
            String line = null;
            while ((line = fileReader.readLine()) != null) {
                actualTaskDataList.add(line);
            }
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertArrayListEquals(testDescription + "dataList", expectedTaskDataList.size() , expectedTaskDataList, actualTaskDataList);
    }
    
    public static void assertFileEquals(String testDescription, String filePath, ArrayList<String> expectedTaskDataList) {
        Path path = Paths.get(filePath);
        assertEquals(testDescription + " File Exist" , true , Files.exists(path));
        
        ArrayList<String> actualTaskDataList = new ArrayList<String>();
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(filePath));
            String line = null;
            while ((line = fileReader.readLine()) != null) {
                actualTaskDataList.add(line);
            }
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertArrayListEquals(testDescription + "dataList", expectedTaskDataList.size() , expectedTaskDataList, actualTaskDataList);
    }

    public static void assertArrayListEquals(String message, int size, ArrayList<String> expectedDataList, ArrayList<String> dataList) {
        assertEquals(message + "listSize" , expectedDataList.size(), dataList.size());
        for (int i = 0; i < size; i++) {
            assertEquals( message+ "dataListContent" , expectedDataList.get(i) , dataList.get(i));
        }
    }
    
    public static void assertArrayListEquals(String message, ArrayList<String> expectedDataList, ArrayList<String> dataList) {
        assertEquals(message + "listSize" , expectedDataList.size(), dataList.size());
        int size = expectedDataList.size();
        for (int i = 0; i < size; i++) {
            assertEquals( message+ "dataListContent" , expectedDataList.get(i) , dataList.get(i));
        }
    }
    
    }
