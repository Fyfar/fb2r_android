package ua.knure.fb2reader.Views.Fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ua.knure.fb2reader.Book.Book;
import ua.knure.fb2reader.R;
import ua.knure.fb2reader.Views.Params;

public class BookInfoFragment extends Fragment {
    private Book book;
    private BookInfoListAdapter arrayAdapter;
    private List<String> headers;
    private List<String> texts;
    private OnClosedBookInfoFragmentListener onClosedBookInfoFragmentListener;

    public BookInfoFragment() {
    }

    public static BookInfoFragment newInstance(Book book) {
        BookInfoFragment fragment = new BookInfoFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(Params.ARG_SERIALIZED_BOOK, book);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.book_info_fragment, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        TextView myTextView = (TextView) view.findViewById(R.id.textView_book_info);
        Bundle arguments = getArguments();
        book = (Book) arguments.getSerializable(Params.ARG_SERIALIZED_BOOK);
        headers = new ArrayList<>();
        texts = new ArrayList<>();

        if (book != null && myTextView != null) {
            Bitmap bitmapDrawable = book.getBookCover();

            StringBuilder temp = new StringBuilder();

            Iterator<String> iterator = book.getBookInfo().getBookName().iterator();
            temp.append(getInfoFromIterator(iterator));
            if (temp.length() > 3) {
                headers.add("NAME");
                texts.add(temp.toString());
            }

            temp = new StringBuilder();
            iterator = book.getBookInfo().getAuthors().iterator();
            temp.append(getInfoFromIterator(iterator));
            if (temp.length() > 3) {
                headers.add("AUTHOR(S)");
                texts.add(temp.toString());
            }

            temp = new StringBuilder();
            iterator = book.getBookInfo().getPublishInfo().iterator();
            temp.append(getInfoFromIterator(iterator));
            if (temp.length() > 3) {
                headers.add("PUBLISH INFO");
                texts.add(temp.toString());
            }

            temp = new StringBuilder();
            iterator = book.getBookInfo().getGenre().iterator();
            temp.append(getInfoFromIterator(iterator));
            if (temp.length() > 3) {
                headers.add("GENRE");
                texts.add(temp.toString());
            }

            temp = new StringBuilder();
            iterator = book.getBookInfo().getTranslator().iterator();
            temp.append(getInfoFromIterator(iterator));
            if (temp.length() > 3) {
                headers.add("TRANSLATOR(S)");
                texts.add(temp.toString());
            }

            temp = new StringBuilder();
            iterator = book.getBookInfo().getAnnotation().iterator();
            temp.append(getInfoFromIterator(iterator));
            if (temp.length() > 3) {
                headers.add("ANNOTATION");
                texts.add(temp.toString());
            }

            BookInfoListAdapter adapter = new BookInfoListAdapter(getView().getContext(), headers, texts, bitmapDrawable);
            ListView listView = (ListView) view.findViewById(R.id.info_listView);
            listView.setAdapter(adapter);
        }

        Button readButton = (Button) view.findViewById(R.id.buttonRead);
        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClosedBookInfoFragmentListener.OnClosedBookInfoFragmentsEvent();
            }
        });
        getActivity().getActionBar().setTitle(Params.MENU_TITLES[Params.MENU_BOOK_INFO]);
    }

    private String getInfoFromIterator(Iterator<String> iterator) {
        StringBuilder builder = new StringBuilder();
        builder.append("");
        while (iterator.hasNext()) {
            builder.append(iterator.next());
        }
        String[] tempData = builder.toString().split("\n");
        builder = new StringBuilder();
        for (int i = 0; i < tempData.length; i++) {
            String[] line = tempData[i].split(" ");
            for (int j = 0; j < line.length; j++) {
                builder.append(line[j] + " ");
            }
            builder.append("\n");
        }
        if (builder.length() < 1) {
            builder.append("none");
        }
        return builder.toString();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onClosedBookInfoFragmentListener = (OnClosedBookInfoFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }

    public interface OnClosedBookInfoFragmentListener {
        public void OnClosedBookInfoFragmentsEvent();
    }
}