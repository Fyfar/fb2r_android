package ua.knure.fb2reader.DataAccess;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ua.knure.fb2reader.Book.Book;
import ua.knure.fb2reader.Book.Parser;
import ua.knure.fb2reader.Book.SimpleSyllables;

public class DataAccess {
    public static File STANDART_BOOK_FOLDER_DIRECTORY = new File(Environment.getExternalStorageDirectory() + "/CloudReader");
    public static File STANDART_BOOK_SETTINGS_FOLDER_DIRECTORY = new File(Environment.getExternalStorageDirectory() + "/CloudReader/.settings");
    private static List<File> bookList;

    public static File openBook(String path) {
        if (path == null || path.equals("") || path.length() < 1) {
            throw new IllegalArgumentException("Path is not correct");
        }
        File file = new File(path);
        return file;
    }

    public static List<File> getAllFilesInBooksFolder(File dir) {
        bookList = new ArrayList<>();

        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                getAllFilesInBooksFolder(file);
            } else {
                bookList.add(file);
            }
        }
        return bookList;
    }

    public static org.w3c.dom.Document openBookDocumentFromFile(String path) {
        File currentBook = DataAccess.openBook(path);
        org.w3c.dom.Document doc = null;
        try {
            doc = Parser.getParsedBook(currentBook);
            return doc;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return doc;
    }

    public static Book openBookFromDocument(org.w3c.dom.Document doc, int lineLength, int linesPerScreen, String path) {
        Book book = null;
        try {
            book = new Book(doc, lineLength, linesPerScreen, 0, new SimpleSyllables(), path);
            return book;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return book;
    }
}