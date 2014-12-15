package ua.knure.fb2reader.Views.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import ua.knure.fb2reader.Book.BookBookmark;
import ua.knure.fb2reader.DataAccess.DAO;
import ua.knure.fb2reader.R;
import ua.knure.fb2reader.Views.Params;

public class BookmarksListFragment extends Fragment {
    private static final String ARG_FOR_ALL_BOOKMARKS = "arg_for_all_bookmarks";
    private BookInfoListAdapter arrayAdapter;
    private List<String> headers;
    private List<String> texts;
    private boolean forAllBookmarks;
    private BookInfoFragment.OnClosedBookInfoFragmentListener onClosedBookInfoFragmentListener;

    public BookmarksListFragment() {
    }

    public static BookmarksListFragment newInstance(String bookPath, boolean forAllBook) {
        BookmarksListFragment fragment = new BookmarksListFragment();
        Bundle arguments = new Bundle();

        arguments.putString(Params.ARG_BOOK_PATH, bookPath);
        arguments.putBoolean(ARG_FOR_ALL_BOOKMARKS, forAllBook);

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

        forAllBookmarks = arguments.getBoolean(ARG_FOR_ALL_BOOKMARKS);
        headers = new ArrayList<>();
        texts = new ArrayList<>();
        String path = arguments.getString(Params.ARG_BOOK_PATH);
        if (path != null) {
            String email = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext())
                    .getString("email", "");
            Iterator<BookBookmark> iterator = DAO.getAllBookmarks(email).iterator();

            while (iterator.hasNext() && forAllBookmarks) {
                BookBookmark bookBookmark = iterator.next();
                headers.add(bookBookmark.getBookmarkName());
                texts.add(bookBookmark.getText() + "\nPage: " + bookBookmark.getPageNumber());
            }
            String[] filePath = path.split("/");
            while (iterator.hasNext() && !forAllBookmarks) {
                BookBookmark bookBookmark = iterator.next();
                if (bookBookmark.getBookmarkName().equals(filePath[filePath.length - 1])) {
                    headers.add(bookBookmark.getBookmarkName());
                    texts.add(bookBookmark.getText() + "\nPage: " + bookBookmark.getPageNumber());
                }
            }
            BookInfoListAdapter adapter = new BookInfoListAdapter(getView().getContext(), headers, texts, null);
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onClosedBookInfoFragmentListener = (BookInfoFragment.OnClosedBookInfoFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }
}