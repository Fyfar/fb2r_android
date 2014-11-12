package ua.knure.fb2reader.Book;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Александр on 29.10.2014.
 */
public class BookPageBuilder {
    private static final int WHITE_SPACE = 1;
    private final String WITHOUT_TITLE = null;
    private final int MAXIMUM_PAGES = 3000;
    private Collection<BookPage> bookPages;
    private Document book;
    private int linesAmount;
    private int linesLength;
    private BookPage lastTempPage;
    private int pageNumber;
    private SyllablesPartitionable syllables;

    public BookPageBuilder(Document book, int charactersPerLine, int linesPerPage, SyllablesPartitionable syllables) {
        this.book = book;
        bookPages = new ArrayList<>();
        linesAmount = linesPerPage;
        linesLength = charactersPerLine;// - charactersPerLine/4;
        pageNumber = 1;
        this.syllables = syllables;
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
        String[] title = current.getElementsByTagName("title").item(0).getTextContent().split("\n");
        Collection<String> col = new ArrayList<>();
        for (int i = 0; i < title.length; i++) {
            if (title[i] != null && title[i].length() > 0) {
                col.add(title[i]);
            }
        }
        //String tmp = current.getElementsByTagName("title").item(0).getTextContent();
        NodeList p = current.getElementsByTagName("title");
        Iterator<String> iterator = col.iterator();
        BookPage page = new BookPage(iterator.next(), linesAmount, pageNumber++);
        while (iterator.hasNext()) {
            page.addTextLine(iterator.next());
        }
        page.addTextLine(" ");
        lastTempPage = page;
        int numbersOfParagraphsInTitle = ((Element) p.item(0)).getElementsByTagName("p").getLength();
        buildPageByParagraphs(current.getElementsByTagName("p"), numbersOfParagraphsInTitle);
    }

    private void buildPageByParagraphs(NodeList p, int numberOfParagraphsInTitle) {
        int paragraphAmount = p.getLength();
        for (int i = numberOfParagraphsInTitle; i < paragraphAmount; i++) {
            createPagesForCurrentParagraph(p.item(i));
            if (pageNumber >= MAXIMUM_PAGES) break; //now its only for debug
        }
    }

    private void createPagesForCurrentParagraph(Node item) {
        BookPage currentPage;
        if (lastTempPage == null) {
            currentPage = new BookPage(WITHOUT_TITLE, linesAmount, pageNumber++);
            lastTempPage = currentPage;
        } else {
            currentPage = lastTempPage;
        }

        Element currentParagraph = (Element) item;
        String[] textInCurrentParagraph = currentParagraph.getTextContent().split(" ");
        StringBuilder tempLine;

        Queue<String> qu = new LinkedList<>();
        for (int i = 0; i < textInCurrentParagraph.length; i++) {

            String tempWord = textInCurrentParagraph[i];
            if (tempWord.length() + WHITE_SPACE >= linesLength) {
                /*StringBuilder strBuildFirstPart = new StringBuilder();
                StringBuilder strBuildSecondPart = new StringBuilder();
                for (int j = 0; j < tempWord.length() / 2; j++) {
                    strBuildFirstPart.append(tempWord.charAt(j));
                }
                for (int j = tempWord.length() / 2; j < tempWord.length(); j++) {
                    strBuildSecondPart.append(tempWord.charAt(j));
                }
                strBuildFirstPart.append("-");
                qu.add(strBuildFirstPart.toString());
                qu.add(strBuildSecondPart.toString());*/
                String[] parts = syllables.getWordSyllables(tempWord, linesLength);
                for (int j = 0; j < parts.length; j++) {
                    qu.add(parts[j]);
                }
            } else {
                qu.add(tempWord);
            }
        }
        while (!qu.isEmpty()) {
            tempLine = new StringBuilder();
            int len = tempLine.length() + qu.peek().length() + 1;
            while (qu.peek() != null && tempLine.length() + qu.peek().length() + 1 < linesLength) {
                tempLine.append(qu.poll() + " ");
            }
            if (currentPage.isNotFull()) {
                currentPage.addTextLine(tempLine.toString() + "|");// + "*" + tempLine.toString().length() + ":" + linesLength);//"**" - only for debug

            } else {
                bookPages.add(currentPage);
                currentPage = new BookPage(null, linesAmount, pageNumber++);
                currentPage.addTextLine(tempLine.toString() + "|");// + "*" + tempLine.toString().length() + ":" + linesLength); //"**" - only for debug
                lastTempPage = currentPage;
            }
            if (pageNumber >= MAXIMUM_PAGES) break; //now its only for debug
        }
    }
}
