package ua.knure.fb2reader.Views;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ua.knure.fb2reader.Book.Book;
import ua.knure.fb2reader.Book.BookPage;
import ua.knure.fb2reader.R;

/**
 * Created by Александр on 15.11.2014.
 */

public class PageFragment extends Fragment {

    static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";
    static final String ARGUMENT_PAGE_TEXT = "arg_page_text";
    static final String ARGUMENT_TEXT_SIZE = "arg_text_size";
    static final String ARGUMENT_TEXT_COLOR = "arg_text_color";
    static final String ARGUMENT_BACKGROUND_COLOR = "arg_background_color";

    static List<String> BOOK_PAGES;
    static int PAGE_NUMBER;
    static int TEXT_SIZE;
    static int TEXT_COLOR;
    static int BACKGROUND_COLOR;

    public PageFragment() {
        super();
    }

    static PageFragment newInstance(int page) {
        PageFragment pageFragment = new PageFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    public static Fragment newInstance(int position, Book book) {
        PageFragment pageFragment = new PageFragment();
        List<BookPage> pages = (List<BookPage>) book.getPages();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, position);
        arguments.putStringArrayList(ARGUMENT_PAGE_TEXT, (ArrayList<String>) pages.get(position).getLines());
        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    public static Fragment newInstance(int position, Book book, int textSize, int textColor, int backgroundColor) {
        PageFragment pageFragment = new PageFragment();
        List<BookPage> pages = (List<BookPage>) book.getPages();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, position);
        arguments.putStringArrayList(ARGUMENT_PAGE_TEXT, (ArrayList<String>) pages.get(position).getLines());
        arguments.putInt(ARGUMENT_TEXT_SIZE, textSize);
        arguments.putInt(ARGUMENT_TEXT_COLOR, textColor);
        arguments.putInt(ARGUMENT_BACKGROUND_COLOR, backgroundColor);
        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PAGE_NUMBER = getArguments().getInt(ARGUMENT_PAGE_NUMBER);
        BOOK_PAGES = getArguments().getStringArrayList(ARGUMENT_PAGE_TEXT);
        TEXT_COLOR = getArguments().getInt(ARGUMENT_TEXT_COLOR);
        BACKGROUND_COLOR = getArguments().getInt(ARGUMENT_BACKGROUND_COLOR);
        TEXT_SIZE = getArguments().getInt(ARGUMENT_TEXT_SIZE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        PAGE_NUMBER = getArguments().getInt(ARGUMENT_PAGE_NUMBER);
        BOOK_PAGES = getArguments().getStringArrayList(ARGUMENT_PAGE_TEXT);
        TEXT_COLOR = getArguments().getInt(ARGUMENT_TEXT_COLOR);
        BACKGROUND_COLOR = getArguments().getInt(ARGUMENT_BACKGROUND_COLOR);
        TEXT_SIZE = getArguments().getInt(ARGUMENT_TEXT_SIZE);


        View view = inflater.inflate(R.layout.fragment, null);
        TextView textView = (TextView) view.findViewById(R.id.text_view_in_fragment_page);

        //this operations like lower that will be need to add some properties to textview like text size, color, etc...
        //textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);

        if (BOOK_PAGES != null && BOOK_PAGES.size() > 0) {
            textView.setBackgroundColor(Color.WHITE);
            Iterator<String> iterator = BOOK_PAGES.iterator();
            while (iterator.hasNext()) {
                textView.append(iterator.next().toString());
            }
            return view;
        }
        textView.setBackgroundColor(Color.BLACK);
        textView.setText("...  Please   wait,   book   loading   all   pages   data  ..." +
                "...  Please   wait,   book   loading   all   pages   data  ..." +
                "...  Please   wait,   book   loading   all   pages   data  ..." +
                "...  Please   wait,   book   loading   all   pages   data  ..." +
                "...  Please   wait,   book   loading   all   pages   data  ..." +
                "...  Please   wait,   book   loading   all   pages   data  ..." +
                "...  Please   wait,   book   loading   all   pages   data  ..." +
                "...  Please   wait,   book   loading   all   pages   data  ..." +
                "...  Please   wait,   book   loading   all   pages   data  ..." +
                "...  Please   wait,   book   loading   all   pages   data  ..." +
                "...  Please   wait,   book   loading   all   pages   data  ..." +
                "...  Please   wait,   book   loading   all   pages   data  ..." +
                "...  Please   wait,   book   loading   all   pages   data  ..." +
                "...  Please   wait,   book   loading   all   pages   data  ...");
        return view;
    }
}
