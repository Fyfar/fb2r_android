package ua.knure.fb2reader.Book;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Александр on 28.10.2014.
 */
public class BookInfo {
    private Collection<String> authors;
    private Collection<String> genre;
    private Collection<String> translator;
    private Collection<String> publishInfo;
    private Collection<String> customInfo;
    private Collection<String> bookTitle;
    private Collection<String> bookName;
    private Collection<String> annotation;
    private Collection<String> bookYear;

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

    public Collection<String> getAuthors() {
        return authors;
    }

    public Collection<String> getGenre() {
        return genre;
    }

    public Collection<String> getTranslator() {
        return translator;
    }

    public Collection<String> getPublishInfo() {
        return publishInfo;
    }

    public Collection<String> getCustomInfo() {
        return customInfo;
    }

    public Collection<String> getBookTitle() {
        return bookTitle;
    }

    public Collection<String> getBookName() {
        return bookName;
    }

    public Collection<String> getAnnotation() {
        return annotation;
    }

    public Collection<String> getBookYear() {
        return bookYear;
    }

}
