package ua.knure.fb2reader.Book;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by Александр on 29.10.2014.
 */
public class BookPageBuilder {
    private Collection<BookPage> pages;
    private Document book;

    private int linesAmount;
    private int linesLength;

    public BookPageBuilder(Document book, int characterWidth,
                           int characterHeight, int screenWidth, int screenHeight) {
        this.book = book;
        pages = new ArrayList<>();
        linesAmount = screenHeight / characterHeight;
        linesLength = screenWidth / characterWidth;
    }

    /*
    Parsing by this queue
        -bookinfo (has a parser)
        -section
            -title
            -section
                -title //now its working without recursion
                    -....same actions
            -p
        -section ... again in the cycle
    * */
    public void buildPages() {
        Element root = book.getDocumentElement();
        NodeList listOfTags = root.getElementsByTagName("section");
        int listOfTagsLength = listOfTags.getLength();
        for (int i = 0; i < listOfTagsLength; i++) {
            buildPagesBySection(listOfTags.item(i));
        }
    }

    private void buildPagesBySection(Node item) {
        Element current = (Element) item;
        String title = current.getElementsByTagName("title").item(0).getTextContent();
        BookPage page = new BookPage(title, linesAmount);
        pages.add(page);
        buildPageByParagraphs(current.getElementsByTagName("p"));
    }

    private void buildPageByParagraphs(NodeList p) {
        int paragraphAmount = p.getLength();
        for (int i = 1; i < paragraphAmount; i++) {
            createPagesForCurrentParagraph(p.item(i));
        }
    }

    private void createPagesForCurrentParagraph(Node item) {
        BookPage page = new BookPage(null, linesAmount);
        Element currentParagraph = (Element) item;
        String[] text = currentParagraph.getTextContent().split(" ");
        StringBuilder stringBuilder;
        StringBuilder temp;
        for (int i = 0; i < text.length; i++) {
            stringBuilder = new StringBuilder();
            temp = new StringBuilder();
            for (int j = i; j < text.length; j++) {
                temp.append(text[j] + " ");
                if (temp.length() < linesLength) {
                    stringBuilder.append(text[j] + " ");
                } else {
                    i = j;
                    break;
                }
            }
            if (page.hasPlaceForAddLine()) {
                try {
                    page.addTextLine(stringBuilder.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                pages.add(page);
                page = new BookPage(null, linesAmount);
            }

        }
    }

    public Collection<BookPage> getPages() {
        return pages;
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
