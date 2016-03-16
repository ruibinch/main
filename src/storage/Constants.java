package storage;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class Constants {

    public static final String DEFAULT_DIRECTORY = ".";
    public static final String ATF_DIRECTORY = "atf_files";
    
    public static final String DATA_FILENAME = "data.txt";
    
    public static final String SAVE_FILENAME = "saveInfo.txt";
    public static final Path SAVE_FILEPATH = Paths.get(ATF_DIRECTORY, 
            SAVE_FILENAME);
    public static final Path DEFAULT_SAVE_PATH = Paths.get(DEFAULT_DIRECTORY, 
            ATF_DIRECTORY , SAVE_FILENAME);
    
    public static final String LOG_FILENAME = "log.txt";
    public static final Path LOG_DIR = Paths.get(DEFAULT_DIRECTORY, 
            ATF_DIRECTORY);
    public static final Path LOG_FILEPATH = Paths.get(LOG_DIR.toString(), LOG_FILENAME);
    
    public static final String LOG_SAVED = "Tasks saved to: %s";
    public static final String LOG_LOADED = "Tasks loaded from: %s";
    public static final String LOG_MKDIR = "Directory created: %s";
    public static final String LOG_CHANGE_PREFERED_DIR = "Directory changed to: %s";
    

    
    
    
}
