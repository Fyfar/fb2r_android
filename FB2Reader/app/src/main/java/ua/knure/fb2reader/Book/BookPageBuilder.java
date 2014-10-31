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
    private Collection<BookPage> pages;
    private Document book;
    private int linesAmount;
    private int linesLength;

    public BookPageBuilder(Document book, int charactersPerLine, int linesPerPage) {
        this.book = book;
        pages = new ArrayList<>();
        linesAmount = linesPerPage;
        linesLength = charactersPerLine;
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
    public Collection<BookPage> buildPages() {
        Element root = book.getDocumentElement();
        NodeList listOfTags = root.getElementsByTagName("section");
        int listOfTagsLength = listOfTags.getLength();
        for (int i = 0; i < listOfTagsLength; i++) {
            buildPagesBySection(listOfTags.item(i));
        }
        return pages;
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
        for (int i = 1; i < paragraphAmount; i++) {// start from 1 because element 0 is a title
            createPagesForCurrentParagraph(p.item(i));
        }
    }

    private void createPagesForCurrentParagraph(Node item) {
        BookPage page = new BookPage(WITHOUT_TITLE, linesAmount);

        Element currentParagraph = (Element) item;
        String[] text = currentParagraph.getTextContent().split(" ");

        StringBuilder currentLine;
        StringBuilder tempLine;

        int numberOfWord = 0; //usually it is a last word in the line

        //int numberOfCurrentLine = 0;//only for debug


        for (int i = 0; i < text.length; i++) {

            currentLine = new StringBuilder();
            tempLine = new StringBuilder();

            String tempWord = "";
            while (tempLine.length() < linesLength && numberOfWord < text.length) {
                if (!tempLine.toString().isEmpty()) {
                    currentLine.append(tempWord);
                }
                tempWord = text[numberOfWord++] + " ";
                tempLine.append(tempWord);
            }
            i = numberOfWord;

            if (page.isNotFull() && numberOfWord < text.length) {
                //numberOfCurrentLine++;
                page.addTextLine(currentLine.toString());
            } else {
                pages.add(page);
                //numberOfCurrentLine = 0;
                page = new BookPage(null, linesAmount);
                page.addTextLine(currentLine.toString());
            }
            //System.out.println(" -- count of lines :" + numberOfCurrentLine);
        }
    }
}
