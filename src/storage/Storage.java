package storage;

import java.util.ArrayList;

import logic.TaskObject;

public interface Storage {
    
    /**
     * Writes tasks to storage. Overwrites existing tasks stored in storage.
     * <p>
     * @param taskList - The list of tasksObjects to be written
     * @return Status. 
     * <li> 0 - If successful. 
     * <li> 1 - If error writing to storage.
     */
    public abstract int save(ArrayList<TaskObject> taskList);
    
    /**
     * Loads all saved tasks into storage from existing specified file.
     * <p>
     * @return Status.
     * <li> 0 - If successful or if existing data present.
     * <li> 1 - Invalid save location is specified
     * <li> 2 - Error reading existing file
     */
    public abstract int load();
    
    /**
     * Returns loaded tasks stored in storage
     * <p>
     * @return taskList - ArrayList of taskObjects stored in storage
     */
    public abstract ArrayList<TaskObject> getTaskList();
    
    /**
     * Creates a copy of the file containing all stored task information at the specified directory.
     * <p>
     * The tasks stored in storage should first be updated using <code>load</code> or <code>save</code> 
     * for the created copy to contain the most recent task information.
     * <p>
     * @param directory Path of directory to create the copy in
     * @param fileName Name of file to be created
     * @return Status
     * <li> 0 - If successful
     * <li> 1 - if existing file does not exist
     * <li> 2 - if unable to read existing file
     * <li> 3 - if unable to create copy
     */
    public abstract int createCopy(String directory, String fileName);
    
}
