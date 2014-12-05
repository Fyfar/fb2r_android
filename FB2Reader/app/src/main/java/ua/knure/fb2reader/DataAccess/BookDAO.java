package ua.knure.fb2reader.DataAccess;

import ua.knure.fb2reader.Book.Book;

/**
 * Class needed to extract data from db
 * Created by evilcorp on 27.11.14.
 */
public class BookDAO {
    private String bookName;
    private int lastChar;

    public BookDAO() {
        super();
    }

    public BookDAO(String bookName, int lastChar) {
        this.bookName = bookName;
        this.lastChar = lastChar;
    }

    public String getBookName() {
        return  bookName;
    }

    public int getLastChar() {
        return lastChar;
    }

    public  void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public  void setLastChar(int lastChar) {
        this.lastChar = lastChar;
    }
}
