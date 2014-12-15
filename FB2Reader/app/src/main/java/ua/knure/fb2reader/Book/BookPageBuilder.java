package ua.knure.fb2reader.Book;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BookPageBuilder {
    private static final int WHITE_SPACE = 1;
    private final String WITHOUT_TITLE = null;
    private List<BookPage> bookPages;
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
        linesLength = charactersPerLine - 1;
        pageNumber = 1;
        this.syllables = syllables;
    }

    public List<BookPage> buildPages() {
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
                    if (currentTitleString.length() < linesLength) {
                        StringBuilder sb = new StringBuilder();
                        int center = (linesLength - currentTitleString.length()) / 2;
                        for (int k = 0; k < center; k++) {
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
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < linesLength / 2 - 3; i++) {
                builder.append(" ");
            }
            builder.append("***");
            for (int i = builder.length(); i < linesLength; i++) {
                builder.append(" ");
            }
            page = new BookPage(builder.toString(), linesAmount, bookPages.size());
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
            if (firstWord) {
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
                currentPage.addTextLine(align(tempLine.toString()));
            } else {
                bookPages.add(currentPage);
                currentPage = new BookPage(null, linesAmount, bookPages.size());
                currentPage.addTextLine(align(tempLine.toString()));
                lastPage = currentPage;
            }
        }
    }

    private String align(String str) {
        StringBuilder builder = new StringBuilder();
        int i = str.length() - 1;
        builder.append(str);
        if (linesLength - str.length() > linesLength / 4) {
            return str;
        }
        while (builder.length() != linesLength - 1) {
            if (i < str.length()) {
                i = builder.lastIndexOf(" ", i);
                if (i < 8) {
                    i = str.length() - 1;
                    i = builder.lastIndexOf(" ", i);
                }
                builder.insert(i--, " ");
            }
        }
        return builder.toString();
    }
}
