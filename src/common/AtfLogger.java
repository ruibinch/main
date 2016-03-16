package common;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import storage.Constants;

public class AtfLogger {

    static private FileHandler fileHandler;
    static private SimpleFormatter formatter;
    
    public static Logger logger = null;

    public static Logger getLogger() {
        if (logger == null) {
            logger = Logger.getLogger("AtfLogger");
            logger.setLevel(Level.INFO);
           
            try {
                File dir = new File(Constants.LOG_DIR.toString());
                dir.mkdirs();
                fileHandler = new FileHandler(Constants.LOG_FILEPATH.toString());
            } catch (SecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            logger.addHandler(fileHandler);

            formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
        }
        return logger;
            
    }

}
