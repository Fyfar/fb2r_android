package ua.knure.fb2reader.Book;

import android.graphics.Bitmap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Collection;

import ua.knure.fb2reader.DataAccess.ImageUtils;


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
        cover = getImageFromBook();
    }

    private Bitmap getImageFromBook() {
        NodeList element = book.getElementsByTagName("binary");
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
                b = ImageUtils.decodeToImage(tmp);
                if (b != null) {
                    finalImage = b.copy(Bitmap.Config.ARGB_8888, true);
                    return finalImage;
                }
            }
        }
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

    public void setNumberOfLastPage(int numberOfLastPage) {
        this.numberOfLastPage = numberOfLastPage;
    }
}
