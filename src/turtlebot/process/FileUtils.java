package turtlebot.process; /**
 * Created by ekansh_gupta on 10/10/16.
 */

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The class contains file utilities.
 * These utilities can be used to get the fileNames.
 */
public class FileUtils {

    public static String fileName = "";

    public static String getFileName() {
        if (FileUtils.fileName.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
            FileUtils.fileName = "Turtlebot Initializer -" + sdf.format(new Date()) + ".acme";
        }
        return FileUtils.fileName;
    }
}
