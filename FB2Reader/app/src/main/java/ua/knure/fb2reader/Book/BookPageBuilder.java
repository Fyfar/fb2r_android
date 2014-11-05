package ua.knure.fb2reader.Book;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Александр on 29.10.2014.
 */
public class BookPageBuilder {
    private final String WITHOUT_TITLE = null;
    private Collection<BookPage> bookPages;
    private Document book;
    private int linesAmount;
    private int linesLength;
    private BookPage lastTempPage;

    public BookPageBuilder(Document book, int charactersPerLine, int linesPerPage) {
        this.book = book;
        bookPages = new ArrayList<>();
        linesAmount = linesPerPage;
        linesLength = charactersPerLine;
    }

    /*
    Parsing by this queue
        -bookinfo (has a parser)
        -section
            -title
            -section
                -title
                    -....same actions
            -p
        -section ... again in the cycle
    * */
    public Collection<BookPage> buildPages() {
        Element root = book.getDocumentElement();
        NodeList listOfTags = root.getElementsByTagName("section");
        int listOfTagsLength = listOfTags.getLength();
        for (int i = 0; i < listOfTagsLength; i++) {
            buildPagesBySection(listOfTags.item(i));
        }
        return bookPages;
    }

    private void buildPagesBySection(Node item) {
        Element current = (Element) item;
        String title = current.getElementsByTagName("title").item(0).getTextContent();
        BookPage page = new BookPage(title, linesAmount);
        lastTempPage = page;
        buildPageByParagraphs(current.getElementsByTagName("p"));
    }

    private void buildPageByParagraphs(NodeList p) {
        int paragraphAmount = p.getLength();
        for (int i = 1; i < paragraphAmount; i++) {// start from 1 because element 0 is a paragraph in the title
            createPagesForCurrentParagraph(p.item(i));
        }
    }

    private void createPagesForCurrentParagraph(Node item) {
        BookPage currentPage;
        if (lastTempPage == null) {
            currentPage = new BookPage(WITHOUT_TITLE, linesAmount);
            lastTempPage = currentPage;
        } else {
            currentPage = lastTempPage;
        }
        Element currentParagraph = (Element) item;
        String[] textInCurrentParagraph = currentParagraph.getTextContent().split(" ");
        StringBuilder currentLine;
        StringBuilder tempLine;
        int numberOfCurrentWord = 0; //usually it is a last word in the line
        while (numberOfCurrentWord < textInCurrentParagraph.length - 1) {
            currentLine = new StringBuilder();
            tempLine = new StringBuilder();
            int lastWord = numberOfCurrentWord;
            while (tempLine.length() < linesLength && numberOfCurrentWord < textInCurrentParagraph.length - 1) {
                tempLine.append(textInCurrentParagraph[numberOfCurrentWord++] + " ");
            }
            while (lastWord <= numberOfCurrentWord) {
                currentLine.append(textInCurrentParagraph[lastWord++] + " ");
            }
            if (currentPage.isNotFull() && numberOfCurrentWord < textInCurrentParagraph.length) {
                currentPage.addTextLine(currentLine.toString() + "**");//"**" - only for debug
            } else {
                bookPages.add(currentPage);
                currentPage = new BookPage(null, linesAmount);
                currentPage.addTextLine(currentLine.toString() + "**"); //"**" - only for debug
                lastTempPage = currentPage; // add for new algorithm
            }
            numberOfCurrentWord++; // we take next word for new iteration
        }
    }
}
