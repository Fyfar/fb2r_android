/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fb2parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Александр
 */
public class BookInfo {

    private Collection<String> authors;
    private Collection<String> genre;
    private Collection<String> translator;
    private Collection<String> publish_info;
    private Collection<String> custom_info;
    private Document document;

    public BookInfo(Document document) throws RuntimeException {
        authors = new ArrayList<>();
        genre = new ArrayList<>();
        translator = new ArrayList<>();
        publish_info = new ArrayList<>();
        custom_info = new ArrayList<>();
        this.document = document;
        setAuthorsInfo();
        setCustomInfo();
        setGenre();
        setPublishInfo();
        setTranslator();
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

    public Collection getAuthors() {
        return authors;
    }

    public Collection getGenre() {
        return genre;
    }

    public Collection getTranslator() {
        return translator;
    }

    public Collection getPublishInfo() {
        return publish_info;
    }

    public Collection getCustomInfo() {
        return custom_info;
    }

    public void printAllInfoAboutBook() {
        Iterator<String> iter = authors.iterator();
        System.out.println("authors:");
        while (iter.hasNext()){
            System.out.println("\t-" + iter.next());
        }
        System.out.println("genre");
        iter = genre.iterator();
        while (iter.hasNext()){
            System.out.println("\t-" + iter.next());
        }
        System.out.println("translator");
        iter = translator.iterator();
        while (iter.hasNext()){
            System.out.println("\t-" + iter.next());
        }
        System.out.println("publish_info");
        iter = publish_info.iterator();
        while (iter.hasNext()){
            System.out.println("\t-" + iter.next());
        }
        System.out.println("custom_info");
        iter = custom_info.iterator();
        while (iter.hasNext()){
            System.out.println("\t-" + iter.next());
        }
    }
}
