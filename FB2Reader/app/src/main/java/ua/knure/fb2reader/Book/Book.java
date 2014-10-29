package ua.knure.fb2reader.Book;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


/**
 * Created by Александр on 28.10.2014.
 */
public class Book {
    private Document book;
    private BookInfo info;
    private Collection<BookPage> pages;
    private int lastPage;
    private int characterWidth;
    private int characterHeight;
    private int screenWidth;
    private int screenHeight;

    public Book(Document book, int lastPage) {//Parsed document(book)
        this.book = book;
        if (lastPage >= 0) {
            this.lastPage = lastPage;
        }
        info = new BookInfo(book);
        pages = new ArrayList<>();
        //createPages();
    }

    public void setMetrics(int characterWidth, int characterHeight, int screenWidth, int screenHeight) {
        this.characterWidth = characterWidth;
        this.characterHeight = characterHeight;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public void createPages() {
        BookPageBuilder builder = new BookPageBuilder(book, characterWidth, characterHeight, screenWidth, screenHeight);
        builder.buildPages();
        pages = builder.getPages();
    }

    public Collection<BookPage> getPages() {
        return pages;
    }

    public BookInfo getBookInfo() {
        return info;
    }

    private void setInfo(String tag, Collection<String> collection) {//this method only for testing
        Element root = book.getDocumentElement();

        int count = root.getElementsByTagName(tag).getLength();
        for (int i = 0; i < count; i++) {
            Element message = (Element) root.getElementsByTagName(tag).item(i);
            collection.add(message.getTextContent());
        }
    }

    public String getAllText() {//this method only for testing
        Collection<String> col = new ArrayList<>();
        setInfo("section", col);
        Iterator<String> iter = col.iterator();
        StringBuilder str = new StringBuilder();
        while (iter.hasNext()) {
            str.append(iter.next());
        }
        return str.toString();
    }
}
