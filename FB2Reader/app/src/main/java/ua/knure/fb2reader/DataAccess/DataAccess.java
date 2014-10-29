package ua.knure.fb2reader.DataAccess;

import java.io.File;

/**
 * Created by Александр on 28.10.2014.
 */
public class DataAccess {
    public static File openBook(String path) {
        if (path.equals("") || path == null || path.length() < 1) {
            throw new IllegalArgumentException("Path is not correct");
        }
        File file = new File(path);
        return file;
    }
    public static void saveSynchronizedData(Object data){
        //To Do
        throw new RuntimeException("");
    }
}
