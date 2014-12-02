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
    static List<String> BOOK_PAGES;
    static int PAGE_NUMBER;
    static int TEXT_SIZE;
    static int TEXT_COLOR;
    static int BACKGROUND_COLOR;

    public ViewPageFragment() {
        super();
    }

    public static ViewPageFragment newInstance(int page) {
        ViewPageFragment viewPageFragment = new ViewPageFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(Params.ARGUMENT_PAGE_NUMBER, page);
        viewPageFragment.setArguments(arguments);
        return viewPageFragment;
    }

    public static Fragment newInstance(int position, Book book) {
        ViewPageFragment viewPageFragment = new ViewPageFragment();
        List<BookPage> pages = (List<BookPage>) book.getPages();
        Bundle arguments = new Bundle();
        arguments.putInt(Params.ARGUMENT_PAGE_NUMBER, position);
        arguments.putStringArrayList(Params.ARGUMENT_PAGE_TEXT, (ArrayList<String>) pages.get(position).getLines());
        arguments.putInt(Params.ARGUMENT_TEXT_SIZE, book.getCharsPerLine());
        viewPageFragment.setArguments(arguments);
        return viewPageFragment;
    }

    public static Fragment newInstance(int position, Book book, int textSize, int textColor, int backgroundColor) {
        ViewPageFragment viewPageFragment = new ViewPageFragment();
        List<BookPage> pages = (List<BookPage>) book.getPages();
        Bundle arguments = new Bundle();
        arguments.putInt(Params.ARGUMENT_PAGE_NUMBER, position);
        arguments.putStringArrayList(Params.ARGUMENT_PAGE_TEXT, (ArrayList<String>) pages.get(position).getLines());
        arguments.putInt(Params.ARGUMENT_TEXT_SIZE, textSize);
        arguments.putInt(Params.ARGUMENT_TEXT_COLOR, textColor);
        arguments.putInt(Params.ARGUMENT_BACKGROUND_COLOR, backgroundColor);
        viewPageFragment.setArguments(arguments);
        return viewPageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PAGE_NUMBER = getArguments().getInt(Params.ARGUMENT_PAGE_NUMBER);
        BOOK_PAGES = getArguments().getStringArrayList(Params.ARGUMENT_PAGE_TEXT);
        TEXT_COLOR = getArguments().getInt(Params.ARGUMENT_TEXT_COLOR);
        BACKGROUND_COLOR = getArguments().getInt(Params.ARGUMENT_BACKGROUND_COLOR);
        TEXT_SIZE = getArguments().getInt(Params.ARGUMENT_TEXT_SIZE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        PAGE_NUMBER = getArguments().getInt(Params.ARGUMENT_PAGE_NUMBER);
        BOOK_PAGES = getArguments().getStringArrayList(Params.ARGUMENT_PAGE_TEXT);
        TEXT_COLOR = getArguments().getInt(Params.ARGUMENT_TEXT_COLOR);
        BACKGROUND_COLOR = getArguments().getInt(Params.ARGUMENT_BACKGROUND_COLOR);
        TEXT_SIZE = getArguments().getInt(Params.ARGUMENT_TEXT_SIZE);


        View view = inflater.inflate(R.layout.view_page_fragment, null);
        TextView textView = (TextView) view.findViewById(R.id.text_view_fragment);

        //this operations like lower that will be need to add some properties to textview like text size, color, etc...
        //textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
        StringBuilder builder = new StringBuilder();


        if (BOOK_PAGES != null && BOOK_PAGES.size() > 0) {
            textView.setBackgroundColor(Color.WHITE);
            textView.setTextColor(Color.BLACK);
            Iterator<String> iterator = BOOK_PAGES.iterator();

            while (iterator.hasNext()) {
                //textView.append(iterator.next().toString());
                builder.append(iterator.next().toString());
            }
            textView.setText(builder.toString());
            //textView.setText(builder.toString(), true);
            //textView.setHyphenate(true, " ");
            return view;
        }
        textView.setBackgroundColor(Color.BLACK);
        textView.setTextColor(Color.BLACK);
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
        /*
        * Этот длинный текст нужен для того что бы заполнить от левого до правого края текствью
        * что бы правильно посчитать максимальное число символов в строке.
        * Текст длинный для того что бы перестраховаться и на разных экранах он заполнил
        * всю строку
        * */
        return view;
    }
}
