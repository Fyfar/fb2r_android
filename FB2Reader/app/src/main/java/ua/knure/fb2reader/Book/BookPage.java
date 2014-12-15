package ua.knure.fb2reader.Book;

import java.util.ArrayList;
import java.util.List;

public class BookPage {
    private List<String> linesOnThePage;
    private int numberOfLines;
    private int numberOfThePage;

    public BookPage(String title, int numberOfLines, int numberOfThePage) {
        this.numberOfLines = numberOfLines;
        this.numberOfThePage = numberOfThePage;
        linesOnThePage = new ArrayList<>();
        if (title != null && title != "") {
            addTextLine(title);
        }
    }

    public void addTextLine(String line) {
        if (isNotFull()) {
            linesOnThePage.add(line + "\n");
        } else {
            throw new RuntimeException("Page is full");
        }
    }

    public boolean isNotFull() {
        return linesOnThePage.size() < numberOfLines;
    }

    public List<String> getLinesOnThePage() {
        return linesOnThePage;
    }

    public int getNumberOfThePage() {
        return numberOfThePage;
    }
}
