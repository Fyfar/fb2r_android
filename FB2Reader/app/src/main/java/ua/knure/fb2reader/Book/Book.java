package ua.knure.fb2reader.Book;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Collection;


/**
 * Created by Александр on 28.10.2014.
 */
public class Book {
    private Document book;
    private BookInfo info;
    private Collection<BookPage> pages;
    private int lastPage;
    private int charactersPerLine;
    private int linesPerPage;

    public Book(Document book, int charactersPerLine, int linesPerPage, int lastPage) {//Parsed document(book)
        this.book = book;
        this.charactersPerLine = charactersPerLine;
        this.linesPerPage = linesPerPage;
        if (lastPage >= 0) {
            this.lastPage = lastPage;
        }
        info = new BookInfo(book);
        pages = new ArrayList<>();
        createPages();
    }

    private void createPages() {
        BookPageBuilder builder = new BookPageBuilder(book, charactersPerLine, linesPerPage);
        pages = builder.buildPages();
    }

    public Collection<BookPage> getPages() {
        return pages;
    }

    public BookInfo getBookInfo() {
        return info;
    }

}
