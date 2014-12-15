package ua.knure.fb2reader.Book;

import android.graphics.Bitmap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ua.knure.fb2reader.DataAccess.ImageUtils;

public class Book implements Serializable {
    private Document bookDocument;
    private BookInfo bookInfo;
    private List<BookPage> bookPages;
    private List<BookBookmark> bookmarks;
    private int numberOfLastPage;
    private int numberOfPages;
    private int charsToLastPage;
    private int charactersPerLine;
    private int linesPerPage;
    private SyllablesPartitionable syllables;
    private Bitmap bookCoverBitmap;
    private String bookFullPathInStorage;

    public Book(Document bookDocument, int charactersPerLine, int linesPerPage, int numberOfLastPage, SyllablesPartitionable syllables, String bookFullPathInStorage) {//Parsed document(bookDocument)
        this.bookDocument = bookDocument;
        this.charactersPerLine = charactersPerLine;
        this.linesPerPage = linesPerPage;
        this.syllables = syllables;
        this.bookFullPathInStorage = bookFullPathInStorage;
        if (numberOfLastPage >= 0) {
            this.numberOfLastPage = numberOfLastPage;
        }
        bookCoverBitmap = getImageFromBook();
        bookInfo = new BookInfo(bookDocument);
        bookPages = new ArrayList<>();
        bookmarks = new ArrayList<>();
        createPages();
        bookCoverBitmap = getImageFromBook();
        charsToLastPage = 0;
    }

    private Bitmap getImageFromBook() {
        NodeList element = bookDocument.getElementsByTagName("binary");
        int count = element.getLength();
        Bitmap finalImage;
        Bitmap b;
        for (int i = 0; i < count; i++) {
            String format = "";
            format = ((Element) element.item(i)).getAttribute("content-type");
            switch (format) {
                case "image/jpeg":
                    format = ".jpg";
                    break;
                case "image/png":
                    format = ".png";
                    break;
                default:
                    format = "";
                    break;
            }
            if (((Element) element.item(i)).getAttribute("id").equals("cover" + format)) {
                String tmp = ((Element) element.item(i)).getTextContent();
                b = ImageUtils.decodeStringToImage(tmp);
                if (b != null) {
                    finalImage = b.copy(Bitmap.Config.ARGB_8888, true);
                    return finalImage;
                }
            }
        }
        return null;
    }

    private void createPages() {
        BookPageBuilder builder = new BookPageBuilder(bookDocument, charactersPerLine, linesPerPage, syllables);
        bookPages = builder.buildPages();
        numberOfPages = bookPages.size();
    }

    public List<BookPage> getBookPages() {
        return bookPages;
    }

    public BookInfo getBookInfo() {
        return bookInfo;
    }

    public Bitmap getBookCover() {
        return bookCoverBitmap;
    }

    public int getCharsPerLine() {
        return charactersPerLine;
    }

    public int getLinesPerPage() {
        return linesPerPage;
    }

    public int getNumberOfLastPage() {
        if (numberOfLastPage == 0 && charsToLastPage > 0) {
            numberOfLastPage = charsToLastPage / (charactersPerLine * linesPerPage);
        }
        return numberOfLastPage - 1;
    }

    public void setNumberOfLastPage(int number) {
        if (number > 0 && number < bookPages.size()) {
            numberOfLastPage = number;
        }
    }

    public String getBookFullPathInStorage() {
        return bookFullPathInStorage;
    }

    public int getCharsToLastPage() {
        return charsToLastPage;
    }

    public void setCharsToLastPage(int charsNumber) {
        charsToLastPage = charsNumber;
        setNumberOfLastPage(charsNumber / (charactersPerLine * linesPerPage));
    }

    public void addBookmark(int pageNumber, int charsCounter, String text, String name) {
        BookBookmark b = new BookBookmark(pageNumber, charsCounter, text, name);
        bookmarks.add(b);
    }

    public List<BookBookmark> getBookmarks() {
        return bookmarks;
    }
}
