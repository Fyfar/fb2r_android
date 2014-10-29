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
    private Collection<String> publish_info;
    private Collection<String> custom_info;
    private Document document;

    public BookInfo(Document document) {
        if (document == null) {
            throw new IllegalArgumentException("Document is null");
        }
        authors = new ArrayList<>();
        genre = new ArrayList<>();
        translator = new ArrayList<>();
        publish_info = new ArrayList<>();
        custom_info = new ArrayList<>();
        this.document = document;
        //setAuthorsInfo();
        //setCustomInfo();
        //setGenre();
        //setPublishInfo();
        //setTranslator();
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
        setInfo("publish-info", publish_info);
    }

    private void setCustomInfo() {
        setInfo("custom-info", custom_info);
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
        return publish_info;
    }

    public Collection<String> getCustomInfo() {
        return custom_info;
    }

}
