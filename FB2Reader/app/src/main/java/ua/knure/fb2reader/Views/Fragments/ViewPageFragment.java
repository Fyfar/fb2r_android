package ua.knure.fb2reader.Views.Fragments;

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
import ua.knure.fb2reader.Views.Params;

/**
 * Created by Александр on 15.11.2014.
 */
/*
* Этот фрагмент отвечает за отображение страниц во viewPager'е. Здесь
* описана логика создания страниц самой книги в TextView
* */
public class ViewPageFragment extends Fragment {
    private List<String> bookPages;
    private int pageNumber;
    private int textSize;
    private int textColor;
    private int backgroundColor;

    public ViewPageFragment() {
        super();
    }

    public static ViewPageFragment newInstance(int page) {
        ViewPageFragment viewPageFragment = new ViewPageFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(Params.ARG_PAGE_NUMBER, page);
        viewPageFragment.setArguments(arguments);
        return viewPageFragment;
    }

    public static Fragment newInstance(int position, Book book) {
        ViewPageFragment viewPageFragment = new ViewPageFragment();
        List<BookPage> pages = book.getBookPages();
        Bundle arguments = new Bundle();
        arguments.putInt(Params.ARG_PAGE_NUMBER, position);
        arguments.putStringArrayList(Params.ARG_PAGE_TEXT, (ArrayList<String>) pages.get(position).getLinesOnThePage());
        arguments.putInt(Params.ARG_TEXT_SIZE, book.getCharsPerLine());
        viewPageFragment.setArguments(arguments);
        return viewPageFragment;
    }

    public static Fragment newInstance(int position, Book book, int textSize, int textColor, int backgroundColor) {
        ViewPageFragment viewPageFragment = new ViewPageFragment();
        List<BookPage> pages = book.getBookPages();
        Bundle arguments = new Bundle();
        arguments.putInt(Params.ARG_PAGE_NUMBER, position);
        arguments.putStringArrayList(Params.ARG_PAGE_TEXT, (ArrayList<String>) pages.get(position).getLinesOnThePage());
        arguments.putInt(Params.ARG_TEXT_SIZE, textSize);
        arguments.putInt(Params.ARG_TEXT_COLOR, textColor);
        arguments.putInt(Params.ARG_BACKGROUND_COLOR, backgroundColor);
        viewPageFragment.setArguments(arguments);
        return viewPageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments().getInt(Params.ARG_PAGE_NUMBER);
        bookPages = getArguments().getStringArrayList(Params.ARG_PAGE_TEXT);
        textColor = getArguments().getInt(Params.ARG_TEXT_COLOR);
        backgroundColor = getArguments().getInt(Params.ARG_BACKGROUND_COLOR);
        textSize = getArguments().getInt(Params.ARG_TEXT_SIZE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_page_fragment, null);
        TextView textView = (TextView) view.findViewById(R.id.text_view_fragment);

        StringBuilder builder = new StringBuilder();
        if (bookPages != null && bookPages.size() > 0) {
            textView.setBackgroundColor(Color.WHITE);
            textView.setTextColor(Color.BLACK);
            Iterator<String> iterator = bookPages.iterator();
            while (iterator.hasNext()) {
                builder.append(iterator.next().toString());
            }
            textView.setText(builder.toString());
            return view;
        }
        textView.setBackgroundColor(Color.BLACK);
        textView.setTextColor(Color.BLACK);
        int NumberOfCharsInPage = 2000;
        for (int i = 0; i < NumberOfCharsInPage; i++){
            textView.append("W");
        }
        return view;
    }
}