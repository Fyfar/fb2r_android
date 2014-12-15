package ua.knure.fb2reader.Views.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.util.Iterator;
import java.util.List;

import ua.knure.fb2reader.Book.Book;
import ua.knure.fb2reader.R;
import ua.knure.fb2reader.Views.Params;

public class BookmarkAddDialogFragment extends DialogFragment {
    private static final String ARG_PAGE = "arg_page";
    private static final String ARG_CHARS = "arg_chars";
    EditText textBookmark;
    EditText nameBookmark;

    Activity currentActivity;
    Dialog dialog;
    int page, charsCounter;
    String text, name;
    OnBookmarkAddListener listener;

    public BookmarkAddDialogFragment() {
    }

    public static BookmarkAddDialogFragment newInstance(Book book) {
        BookmarkAddDialogFragment fragment = new BookmarkAddDialogFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(Params.ARG_SERIALIZED_BOOK, book);
        int page = book.getNumberOfLastPage();
        int charsCounter = book.getCharsToLastPage();
        arguments.putInt(ARG_PAGE, page);
        arguments.putInt(ARG_CHARS, charsCounter);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.currentActivity = activity;
        try {
            listener = (OnBookmarkAddListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.bookmark_dialog_layout, null);
        Bundle arguments = getArguments();

        Book book = (Book) arguments.getSerializable(Params.ARG_SERIALIZED_BOOK);
        page = book.getNumberOfLastPage();
        charsCounter = book.getCharsToLastPage();
        StringBuilder textbuilder = new StringBuilder();
        List<String> lines = book.getBookPages().get(page).getLinesOnThePage();
        Iterator<String> iterator = lines.iterator();
        while (iterator.hasNext()) {
            textbuilder.append(iterator.next());
        }
        text = textbuilder.toString();
        name = null;

        textBookmark = (EditText) v.findViewById(R.id.book_page_text_for_edittext);
        nameBookmark = (EditText) v.findViewById(R.id.bookmark_name_edittext);

        textBookmark.append(text);

        builder.setView(v)
                .setTitle("Bookmark")
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        text = textBookmark.getText().toString();
                        name = nameBookmark.getText().toString();
                        if (name == null || text == null) {
                            BookmarkAddDialogFragment.this.getDialog().cancel();
                        } else {
                            listener.addBookmarkEvent(page, charsCounter, text, name);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        BookmarkAddDialogFragment.this.getDialog().cancel();
                    }
                });


        dialog = builder.create();
        return dialog;
    }

    public interface OnBookmarkAddListener {
        public void addBookmarkEvent(int page, int charsCounter, String text, String name);
    }
}
