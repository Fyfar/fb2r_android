package ua.knure.fb2reader.Book;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BookInfo {
    private List<String> authors;
    private List<String> genre;
    private List<String> translator;
    private List<String> publishInfo;
    private List<String> bookName;
    private List<String> annotation;

    private Document document;

    public BookInfo(Document document) {
        if (document == null) {
            throw new IllegalArgumentException("Document is null");
        }
        authors = new ArrayList<>();
        genre = new ArrayList<>();
        translator = new ArrayList<>();
        publishInfo = new ArrayList<>();
        bookName = new ArrayList<>();
        annotation = new ArrayList<>();
        this.document = document;
        setAuthorsInfo();
        setGenre();
        setPublishInfo();
        setTranslator();
        setBookName();
        setAnnotation();
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
        Element root = document.getDocumentElement();
        int count = root.getElementsByTagName("author").getLength();
        for (int i = 0; i < count; i++) {
            Element message = (Element) root.getElementsByTagName("author").item(i);
            if (((Element) message.getParentNode()).getTagName().equals("title-info")) {
                Element firstName = (Element) message.getElementsByTagName("first-name").item(i);
                Element lastName = (Element) message.getElementsByTagName("last-name").item(i);
                Element middleName = (Element) message.getElementsByTagName("middle-name").item(i);
                String first, last, middle;
                try {
                    first = firstName.getTextContent();
                } catch (NullPointerException ex) {
                    first = " ";
                }
                try {
                    middle = middleName.getTextContent();
                } catch (NullPointerException ex) {
                    middle = " ";
                }
                try {
                    last = lastName.getTextContent();
                } catch (NullPointerException ex) {
                    last = " ";
                }
                authors.add(first + " " + middle + " " + last);
            }
        }
    }

    private void setGenre() {
        setInfo("genre", genre);
    }

    private void setTranslator() {
        Element root = document.getDocumentElement();
        int count = root.getElementsByTagName("translator").getLength();
        for (int i = 0; i < count; i++) {
            Element message = (Element) root.getElementsByTagName("translator").item(i);
            if (((Element) message.getParentNode()).getTagName().equals("title-info")) {
                Element firstName = (Element) message.getElementsByTagName("first-name").item(i);
                Element lastName = (Element) message.getElementsByTagName("last-name").item(i);
                Element middleName = (Element) message.getElementsByTagName("middle-name").item(i);
                String first, last, middle;
                try {
                    first = firstName.getTextContent();
                } catch (NullPointerException ex) {
                    first = " ";
                }
                try {
                    middle = middleName.getTextContent();
                } catch (NullPointerException ex) {
                    middle = " ";
                }
                try {
                    last = lastName.getTextContent();
                } catch (NullPointerException ex) {
                    last = " ";
                }
                translator.add(first + " " + middle + " " + last);
            }
        }
    }

    private void setPublishInfo() {
        Element root = document.getDocumentElement();
        int count = root.getElementsByTagName("publish-info").getLength();
        for (int i = 0; i < count; i++) {
            Element message = (Element) root.getElementsByTagName("publish-info").item(i);
            if (((Element) message.getParentNode()).getTagName().equals("description")) {

                Element bookName = (Element) message.getElementsByTagName("book-name").item(i);
                Element publisherName = (Element) message.getElementsByTagName("publisher").item(i);
                Element bookYear = (Element) message.getElementsByTagName("year").item(i);
                String book_name, publisher_name, year;
                try {
                    book_name = bookName.getTextContent();
                } catch (NullPointerException ex) {
                    book_name = " ";
                }
                try {
                    year = bookYear.getTextContent();
                } catch (NullPointerException ex) {
                    year = " ";
                }
                try {
                    publisher_name = publisherName.getTextContent();
                } catch (NullPointerException ex) {
                    publisher_name = " ";
                }
                publishInfo.add(book_name + "\n" + publisher_name + "\n" + year);
            }
        }
    }

    private void setBookName() {
        setInfo("book-name", bookName);
    }

    private void setAnnotation() {
        setInfo("annotation", annotation);
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

    public List<String> getBookName() {
        return bookName;
    }

    public List<String> getAnnotation() {
        return annotation;
    }
}
