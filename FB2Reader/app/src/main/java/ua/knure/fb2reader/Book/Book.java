package ua.knure.fb2reader.Book;

import android.graphics.Bitmap;

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
    private int numberOfLastPage;
    private int charactersPerLine;
    private int linesPerPage;
    private SyllablesPartitionable syllables;
    private Bitmap cover;

    public Book(Document book, int charactersPerLine, int linesPerPage, int numberOfLastPage, SyllablesPartitionable syllables) {//Parsed document(book)
        this.book = book;
        this.charactersPerLine = charactersPerLine;
        this.linesPerPage = linesPerPage;
        this.syllables = syllables;
        if (numberOfLastPage >= 0) {
            this.numberOfLastPage = numberOfLastPage;
        }
        cover = getImageFromBook();
        info = new BookInfo(book);
        pages = new ArrayList<>();
        createPages();
    }

    private Bitmap getImageFromBook() {
        return null;
    }

    private void createPages() {
        BookPageBuilder builder = new BookPageBuilder(book, charactersPerLine, linesPerPage, syllables);
        pages = builder.buildPages();
    }

    public Collection<BookPage> getPages() {
        return pages;
    }

    public BookInfo getBookInfo() {
        return info;
    }

    public Bitmap getBookCover() {
        return cover;
    }

}
