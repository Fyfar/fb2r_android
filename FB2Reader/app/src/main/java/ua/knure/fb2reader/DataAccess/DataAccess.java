package ua.knure.fb2reader.DataAccess;

import android.os.Environment;

import java.io.File;

import ua.knure.fb2reader.Book.Parser;

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

    public static org.w3c.dom.Document openBookDocument(String path) {
        File currentBook = DataAccess.openBook(Environment.getExternalStorageDirectory() + path);
        try {
            org.w3c.dom.Document doc = Parser.getParsedBook(currentBook);
            return doc;
        } catch (Exception ex) {
            //Toast.makeText(this.getApplicationContext(), "" + ex.getMessage() + "\n" + ex.getStackTrace().toString(), Toast.LENGTH_LONG).show();
            //ex.printStackTrace();
        }
        return null;
    }
}
