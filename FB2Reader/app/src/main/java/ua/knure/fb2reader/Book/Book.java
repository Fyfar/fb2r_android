package ua.knure.fb2reader.Book;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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
        //cover = getImageFromBook();
    }

    private Bitmap getImageFromBook() {
        NodeList element = book.getElementsByTagName("binary");
        int count = element.getLength();
        for (int i = 0; i< count; i++) {
            String format = "";
            format = ((Element)element.item(i)).getAttribute("content-type");
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
            if (((Element)element.item(i)).getAttribute("id").equals("cover" + format)) {
                byte[] bin = ((Element)element.item(i)).getTextContent().getBytes();
                String tmp = ((Element)element.item(i)).getTextContent();
                Bitmap b;
                b = BitmapFactory.decodeByteArray(bin, 0, bin.length);
                if (b!=null){
                    Bitmap finalImage = b.copy(Bitmap.Config.ARGB_8888, true);
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

}
