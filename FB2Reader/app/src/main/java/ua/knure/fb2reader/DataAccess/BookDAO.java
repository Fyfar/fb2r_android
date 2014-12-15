package ua.knure.fb2reader.DataAccess;

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
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public int getLastChar() {
        return lastChar;
    }

    public void setLastChar(int lastChar) {
        this.lastChar = lastChar;
    }
}
