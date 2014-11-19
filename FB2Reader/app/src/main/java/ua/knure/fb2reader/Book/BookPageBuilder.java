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
    private Collection<BookPage> bookPages;
    private Document book;
    private int linesAmount;
    private int linesLength;
    private BookPage lastPage;
    private int pageNumber;
    private SyllablesPartitionable syllables;

    public BookPageBuilder(Document book, int charactersPerLine, int linesPerPage, SyllablesPartitionable syllables) {
        this.book = book;
        bookPages = new ArrayList<>();
        linesAmount = linesPerPage;
        linesLength = charactersPerLine;
        pageNumber = 1;
        this.syllables = syllables;
    }

    public Collection<BookPage> buildPages() {
        Element root = book.getDocumentElement();
        NodeList listOfTags = root.getElementsByTagName("section");
        int listOfTagsLength = listOfTags.getLength();
        for (int i = 0; i < listOfTagsLength; i++) {
            Element listElement = (Element) listOfTags.item(i);
            if (((Element) listElement.getParentNode()).getAttribute("name").equals("notes")) {
                continue;
            }
            String name = listElement.getAttribute("name");
            if (!name.equals("notes")) {
                buildPagesBySection(listOfTags.item(i));
                if (!bookPages.contains(lastPage)) {
                    bookPages.add(lastPage);
                }
            }
        }
        return bookPages;
    }

    private void buildPagesBySection(Node item) {
        Element current = (Element) item;

        BookPage page;
        NodeList p = current.getElementsByTagName("title");
        boolean hasTitle = true;
        try {
            hasTitle = p.item(0).getTextContent().length() > 0;
        } catch (NullPointerException ex) {
            hasTitle = false;
        }
        if (hasTitle) {
            String[] title = p.item(0).getTextContent().split("\n");
            Collection<String> titleText = new ArrayList<>();
            for (int i = 0; i < title.length; i++) {
                if (title[i] != null && title[i].length() > 0) {
                    String currentTitleString = title[i];
                    if (currentTitleString.length() < linesLength){
                        StringBuilder sb = new StringBuilder();
                        for (int k=0; k<(linesLength-currentTitleString.length())/2; k++){
                            sb.append(" ");
                        }
                        currentTitleString = sb.toString() + currentTitleString;
                    }
                    titleText.add(currentTitleString);
                }
            }
            Iterator<String> titleTextIterator = titleText.iterator();
            page = new BookPage(titleTextIterator.next(), linesAmount, bookPages.size());
            while (titleTextIterator.hasNext()) {
                page.addTextLine(titleTextIterator.next());
            }
            page.addTextLine(" ");
            lastPage = page;
            int numbersOfParagraphsInTitle = ((Element) p.item(0)).getElementsByTagName("p").getLength();
            buildPageByParagraphs(current.getElementsByTagName("p"), numbersOfParagraphsInTitle);
        } else {
            page = new BookPage("***", linesAmount, bookPages.size());
            page.addTextLine(" ");
            lastPage = page;
            int numbersOfParagraphsInTitle = 0;
            buildPageByParagraphs(current.getElementsByTagName("p"), numbersOfParagraphsInTitle);
        }
    }

    private void buildPageByParagraphs(NodeList p, int numberOfParagraphsInTitle) {
        int paragraphAmount = p.getLength();
        for (int i = numberOfParagraphsInTitle; i < paragraphAmount; i++) {
            createPagesForCurrentParagraph(p.item(i));
        }
    }

    private void createPagesForCurrentParagraph(Node item) {
        BookPage currentPage;
        if (lastPage == null) {
            currentPage = new BookPage(WITHOUT_TITLE, linesAmount, bookPages.size());
            lastPage = currentPage;
        } else {
            currentPage = lastPage;
        }
        Element currentParagraph = (Element) item;
        String[] textInCurrentParagraph = currentParagraph.getTextContent().split(" ");

        StringBuilder tempLine;
        Queue<String> queueOfWords = new LinkedList<>();
        boolean firstWord = true;
        for (int i = 0; i < textInCurrentParagraph.length; i++) {
            String tempWord = textInCurrentParagraph[i];
            if (firstWord){
                tempWord = "   " + tempWord;
                firstWord = false;
            }
            if (tempWord.length() + WHITE_SPACE >= linesLength) {
                String[] wordSyllables = syllables.getWordSyllables(tempWord, linesLength);
                for (int j = 0; j < wordSyllables.length; j++) {
                    queueOfWords.add(wordSyllables[j]);
                }
            } else {
                queueOfWords.add(tempWord);
            }
        }
        while (!queueOfWords.isEmpty()) {
            tempLine = new StringBuilder();
            while (queueOfWords.peek() != null && tempLine.length() + queueOfWords.peek().length() + 1 < linesLength) {
                tempLine.append(" " + queueOfWords.poll());
            }
            if (currentPage.isNotFull()) {
                currentPage.addTextLine(tempLine.toString());
            } else {
                bookPages.add(currentPage);
                currentPage = new BookPage(null, linesAmount, bookPages.size());
                currentPage.addTextLine(tempLine.toString());
                lastPage = currentPage;
            }
        }
    }
}
