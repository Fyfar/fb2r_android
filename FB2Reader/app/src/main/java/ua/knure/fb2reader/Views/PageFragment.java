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
import java.util.Random;

import ua.knure.fb2reader.Book.BookPage;
import ua.knure.fb2reader.R;

/**
 * Created by Александр on 15.11.2014.
 */


public class PageFragment extends Fragment {

    static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";
    static final String ARGUMENT_PAGE_TEXT = "arg_page_text";

    int pageNumber;
    int backColor;

    static List<String> pagesCurrent;
    static int LastPosition;

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

    public static Fragment newInstance(int position, List<BookPage> pages) {
        //pagesCurrent = pages;
        //LastPosition = position;
        PageFragment pageFragment = new PageFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, position);
        arguments.putStringArrayList(ARGUMENT_PAGE_TEXT, (ArrayList<String>) pages.get(position).getLines());
        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);
        pagesCurrent = getArguments().getStringArrayList(ARGUMENT_PAGE_TEXT);
        Random rnd = new Random();
        backColor = Color.argb(40, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);
        pagesCurrent = getArguments().getStringArrayList(ARGUMENT_PAGE_TEXT);

        if (pagesCurrent != null && pagesCurrent.size() > 0) {
            View view = inflater.inflate(R.layout.fragment, null);
            TextView textView = (TextView) view.findViewById(R.id.text_view_in_fragment_page);
            Iterator<String> iter = pagesCurrent.iterator();
            while (iter.hasNext()) {
                textView.append(iter.next().toString());
            }
            return view;
        }
        //textView.setText("...Please wait, book loading all pages data...");

        View view = inflater.inflate(R.layout.fragment, null);
        TextView textView = (TextView) view.findViewById(R.id.text_view_in_fragment_page);
        textView.setText("...Please wait, book loading all pages data...");

        textView.setBackgroundColor(backColor);
        //textView.setText("Page " + pageNumber);
        return view;
    }


}
