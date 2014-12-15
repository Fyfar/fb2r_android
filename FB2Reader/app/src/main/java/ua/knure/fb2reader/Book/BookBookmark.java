package ua.knure.fb2reader.Book;

public class BookBookmark {
    private int pageNumber;
    private int charsCounter;
    private String text;
    private String bookmarkName;
    private String bookName;

    public BookBookmark(int pageNumber, int charsCounter, String text, String bookmarkName) {
        this.bookmarkName = bookmarkName;
        this.charsCounter = charsCounter;
        this.pageNumber = pageNumber;
        this.text = text;
        this.bookName = "";
    }

    @Override
    public String toString() {
        return "[bookmarkName = " + bookmarkName + " charsCounter = "
                + charsCounter + " pageNumber = " + pageNumber + " text = " + text + "]";

    }

    public String getBookmarkName() {
        return bookmarkName;
    }

    public String getText() {
        return text;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getCharsCounter() {
        return charsCounter;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String name) {
        this.bookName = name;
    }
}
