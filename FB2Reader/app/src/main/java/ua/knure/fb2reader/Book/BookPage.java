package ua.knure.fb2reader.Book;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Александр on 28.10.2014.
 */
public class BookPage {
    private Collection<String> lines;
    private boolean hasTitle;
    private String title;
    private int amountOfLines;

    public BookPage(String title, int amountOfLines) {
        this.amountOfLines = amountOfLines;
        lines = new ArrayList<>();
        if (title != null && title != "") {
            hasTitle = true;
            this.title = title;
            addTextLine(title + "\n");
            amountOfLines--;
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
}
