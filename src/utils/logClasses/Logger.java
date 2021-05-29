package utils.logClasses;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * belongs to 'mafia game'
 * a class to log events
 *
 * @author Sepehr Noey
 * @version 1.0
 */
public class Logger {
    private static final Path curPath = Paths.get("");

    public synchronized static void log(String msg ,LogLevels level , String className){
        try (FileWriter fileWriter = new FileWriter(curPath.toString() + "log/log.txt" , true)){
            fileWriter.append(logPattern(msg , level.toString() , className ));

        }catch (IOException e){
            System.out.println("Error in working with log.txt");
            e.printStackTrace();
        }
    }


    /**
     * Make The Log using special Pattern
     *
     * @param level is the log level (can be ERROR , INFO or WARN)
     * @param msg is the content of log
     * @return the patterned log
     */
    private static String logPattern(String msg, String level, String className) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return "{" + formatter.format(date) + " ," + " [" + level + "] ," + className
                + " ,msg: " + msg+ " }\n";
    }


}
