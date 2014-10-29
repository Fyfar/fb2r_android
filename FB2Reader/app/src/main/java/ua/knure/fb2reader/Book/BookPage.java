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
    private int amount;

    public BookPage(String title, int amount) {
        this.amount = amount;
        lines = new ArrayList<>();
        if (title != null && title != "") {
            hasTitle = true;
            this.title = title;
            try {
                addTextLine(title + "\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
            amount--;
        }
    }

    public void addTextLine(String line) throws Exception {
        if (hasPlaceForAddLine()) {
            lines.add(line + "\n");
        } else {
            throw new Exception("Page is full");
        }
    }

    public boolean hasPlaceForAddLine() {
        return lines != null && lines.size() < amount;
    }

    public Collection<String> getLines() {
        return lines;
    }
}
