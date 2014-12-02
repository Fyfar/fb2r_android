package ua.knure.fb2reader.Book;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Александр on 28.10.2014.
 */
public class BookInfo {
    private List<String> authors;
    private List<String> genre;
    private List<String> translator;
    private List<String> publishInfo;
    private List<String> customInfo;
    private List<String> bookTitle;
    private List<String> bookName;
    private List<String> annotation;
    private List<String> bookYear;

    private Document document;

    public BookInfo(Document document) {
        if (document == null) {
            throw new IllegalArgumentException("Document is null");
        }
        authors = new ArrayList<>();
        genre = new ArrayList<>();
        translator = new ArrayList<>();
        publishInfo = new ArrayList<>();
        customInfo = new ArrayList<>();
        bookTitle = new ArrayList<>();
        bookName = new ArrayList<>();
        annotation = new ArrayList<>();
        bookYear = new ArrayList<>();
        this.document = document;
        setAuthorsInfo();
        setCustomInfo();
        setGenre();
        setPublishInfo();
        setTranslator();
        setBookTitle();
        setBookName();
        setAnnotation();
        setBookYear();
    }

    private void setInfo(String tag, Collection<String> collection) {
        Element root = document.getDocumentElement();
        int count = root.getElementsByTagName(tag).getLength();
        for (int i = 0; i < count; i++) {
            Element message = (Element) root.getElementsByTagName(tag).item(i);
            collection.add(message.getTextContent());
        }
    }

    private void setAuthorsInfo() {
        setInfo("author", authors);
    }

    private void setGenre() {
        setInfo("genre", genre);
    }

    private void setTranslator() {
        setInfo("translator", translator);
    }

    private void setPublishInfo() {
        setInfo("publish-info", publishInfo);
    }

    private void setCustomInfo() {
        setInfo("custom-info", customInfo);
    }

    private void setBookTitle() {
        setInfo("book-title", bookTitle);
    }

    private void setBookName() {
        setInfo("book-name", bookName);
    }

    private void setAnnotation() {
        setInfo("annotation", annotation);
    }

    private void setBookYear() {
        setInfo("year", bookYear);
    }

    public List<String> getAuthors() {
        return authors;
    }

    public List<String> getGenre() {
        return genre;
    }

    public List<String> getTranslator() {
        return translator;
    }

    public List<String> getPublishInfo() {
        return publishInfo;
    }

    public List<String> getCustomInfo() {
        return customInfo;
    }

    public List<String> getBookTitle() {
        return bookTitle;
    }

    public List<String> getBookName() {
        return bookName;
    }

    public List<String> getAnnotation() {
        return annotation;
    }

    public List<String> getBookYear() {
        return bookYear;
    }

}
