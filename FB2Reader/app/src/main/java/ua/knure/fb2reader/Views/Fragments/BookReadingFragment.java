package ua.knure.fb2reader.Views.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import ua.knure.fb2reader.Book.Book;
import ua.knure.fb2reader.DataAccess.BookDAO;
import ua.knure.fb2reader.DataAccess.DAO;
import ua.knure.fb2reader.DataAccess.DataAccess;
import ua.knure.fb2reader.R;
import ua.knure.fb2reader.Utils.ViewUtils;
import ua.knure.fb2reader.Views.Params;

public class BookReadingFragment extends Fragment {
    private int numberOfCharsPerLine;
    private int numberOfLinesPerScreen;
    private ViewPager viewPager;
    private PagerAdapter viewPagerAdapter;
    private Book book;
    private Context ctx;


    private OnInfoPageOpeningListener onInfoPageOpeningListener;
    private OnBookStatusChangedListener onBookStatusChangedListener;

    public BookReadingFragment() {
    }

    public static BookReadingFragment newInstance(String bookPath) {
        BookReadingFragment fragment = new BookReadingFragment();
        Bundle arguments = new Bundle();
        arguments.putString(Params.ARG_BOOK_PATH, bookPath);
        arguments.putBoolean(Params.ARG_BOOK_INFO_WAS_OPENED, true);
        fragment.setArguments(arguments);
        return fragment;
    }

    public static BookReadingFragment newInstance(Book book) {
        BookReadingFragment fragment = new BookReadingFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(Params.ARG_SERIALIZED_BOOK, book);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.book_reading_fragment, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ctx = getActivity().getBaseContext();
        viewPager = (ViewPager) view.findViewById(R.id.ViewPagerID_Screen_One);
        viewPagerAdapter = new BookPageFragmentPagerAdapter(getActivity().getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        getActivity().getActionBar().setTitle("Opening..."); /*в экшенбаре устанавливаем нужный(текущий) заголовок активити*/

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                if (book != null) {
                    book.setCharsToLastPage((i + 1) * book.getCharsPerLine() * book.getLinesPerPage());

                    String email = PreferenceManager
                            .getDefaultSharedPreferences(ctx).getString("email", "");
                    String bookPath = PreferenceManager
                            .getDefaultSharedPreferences(ctx).getString("currentBook", "");

                    SharedPreferences sdPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
                    SharedPreferences.Editor ed = sdPref.edit();
                    String[] filePath = book.getBookFullPathInStorage().split("/");
                    ed.putString("currentBook", filePath[filePath.length - 1]);
                    ed.commit();
                    BookDAO bookDAO = ViewUtils.getBookFromDB(filePath[filePath.length - 1], getActivity().getBaseContext());
                    if (bookDAO != null && bookDAO.getLastChar() < book.getCharsToLastPage()) {
                        bookDAO.setLastChar(book.getCharsToLastPage());
                    }
                    DAO.updateBook(DAO.BOOKS_TABLE, new Date(), book.getCharsToLastPage(), bookPath, email);
                    onBookStatusChangedListener.onBookStatusChangedEvent(book);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }

        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TextView view = (TextView) getView().findViewById(R.id.text_view_fragment);
                numberOfCharsPerLine = ViewUtils.getNumberOfCharsPerLine(view);
                numberOfLinesPerScreen = ViewUtils.getNumberOfLinesPerScreen(view);
                Bundle arguments = getArguments();

                String bookPath = arguments.getString(Params.ARG_BOOK_PATH);
                boolean isOpeningInfo = arguments.getBoolean(Params.ARG_BOOK_INFO_WAS_OPENED);
                book = (Book) arguments.getSerializable(Params.ARG_SERIALIZED_BOOK);

                if (book == null) {
                    book = DataAccess.openBookFromDocument(DataAccess.openBookDocumentFromFile(bookPath), numberOfCharsPerLine, numberOfLinesPerScreen, bookPath);
                }
                if (book != null) {

                    viewPagerAdapter = new BookPageFragmentPagerAdapter(getActivity().getSupportFragmentManager(), book);
                    viewPagerAdapter.notifyDataSetChanged();

                    int chars = book.getCharsToLastPage();
                    viewPager.setAdapter(viewPagerAdapter);
                    book.setCharsToLastPage(chars);

                    String bookNameInActionBar;
                    try {
                        bookNameInActionBar = book.getBookInfo().getBookName().get(0);
                    } catch (IndexOutOfBoundsException ex) {
                        bookNameInActionBar = "...";
                    }
                    if (bookNameInActionBar != null) {
                        getActivity().getActionBar().setTitle(bookNameInActionBar);
                    }

                    if (book.getNumberOfLastPage() > 0 && book.getNumberOfLastPage() < book.getBookPages().size()) {
                        viewPager.setCurrentItem(book.getNumberOfLastPage());
                    }

                    onBookStatusChangedListener.onBookStatusChangedEvent(book);/* оповещаем главный активити о окрытой книге и передаем ему ссылку на нее*/

                    if (isOpeningInfo) {
                        onInfoPageOpeningListener.onInfoPageOpeningEvent(isOpeningInfo, book);
                    }
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Error with opening a book");
                    builder.setMessage("Book by path : " + bookPath + " cannot be opened");
                    builder.setCancelable(true);
                    builder.create().setCanceledOnTouchOutside(true);
                    Toast.makeText(getActivity().getApplicationContext(), "Cannot open the book by path:\n" + bookPath, Toast.LENGTH_LONG);
                }
            }
        }, 1000);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onInfoPageOpeningListener = (OnInfoPageOpeningListener) activity;
            onBookStatusChangedListener = (OnBookStatusChangedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }

    public interface OnInfoPageOpeningListener {
        public void onInfoPageOpeningEvent(boolean firstOpening, Book book);
    }

    public interface OnBookStatusChangedListener {
        public void onBookStatusChangedEvent(Book book);
    }
}