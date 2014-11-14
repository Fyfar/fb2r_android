package ua.knure.fb2reader.Book;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Александр on 28.10.2014.
 */
public class BookPage {
    private Collection<String> lines;
    private int amountOfLines;
    private int pageNumber;

    public BookPage(String title, int amountOfLines, int pageNumber) {
        this.amountOfLines = amountOfLines;
        lines = new ArrayList<>();
        this.pageNumber = pageNumber;
        if (title != null && title != "") {
            addTextLine(title);
        }
    }

    public void addTextLine(String line) {
        if (isNotFull()) {
            lines.add(line + "\n");
        } else {
            throw new RuntimeException("Page is full");
        }
    }

    public boolean isNotFull() {
        return lines.size() < amountOfLines;
    }

    public Collection<String> getLines() {
        return lines;
    }

    public int getPageNumber() {
        return pageNumber;
    }
}
