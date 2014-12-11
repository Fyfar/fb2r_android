package ua.knure.fb2reader.Book;

/**
 * Created by Александр on 10.12.2014.
 */
public class BookBookmark {
    private int pageNumber;
    private int charsCounter;
    private String text;
    private String bookmarkName;

    public BookBookmark(int pageNumber, int charsCounter, String text, String bookmarkName) {
        this.bookmarkName = bookmarkName;
        this.charsCounter = charsCounter;
        this.pageNumber = pageNumber;
        this.text = text;
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
}
