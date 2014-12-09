package ua.knure.fb2reader.Views.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ua.knure.fb2reader.Book.Book;
import ua.knure.fb2reader.DataAccess.DataAccess;
import ua.knure.fb2reader.R;
import ua.knure.fb2reader.Utils.ViewUtils;
import ua.knure.fb2reader.Views.Params;

/*
* В этом фрагменте отображается открытая книжка
* */
public class BookReadingFragment extends Fragment {


    private int numberOfCharsPerLine;
    private int numberOfLinesPerScreen;
    private ViewPager viewPager;
    private PagerAdapter viewPagerAdapter;
    private Book book;

    /* Интерфейсы которые должны быть реализованы в предке(того кто содержит, а не того кого наследует)
     * данного фрагмента (главное активити)
     * */
    private OnInfoPageOpeningListener onInfoPageOpeningListener;
    private OnBookOpenedListener onBookOpenedListener;
    private OnSearchListener onSearchListener;

    /*
    * Конструктор должен быть обязательно пустой для правильной
    * инициализации фрагмента при каждом его создании
    * */
    public BookReadingFragment() {
    }

    /* Метод-фабрика который будет создавать фрагмент с переданными в него
    *  параметрами (в данном случае мы кладем туда путь к книге)
    * */
    public static BookReadingFragment newInstance(String bookPath) {
        BookReadingFragment fragment = new BookReadingFragment();
        Bundle arguments = new Bundle();
        arguments.putString(Params.ARG_BOOK_PATH, bookPath);
        arguments.putBoolean(Params.ARG_BOOK_INFO_WAS_OPENED, true);
        fragment.setArguments(arguments);
        return fragment;
    }

    /* Метод-фабрика который будет создавать фрагмент с переданными в него
    *  параметрами (в данном случае мы кладем туда открытую книгу)
    * */
    public static BookReadingFragment newInstance(Book book) {
        BookReadingFragment fragment = new BookReadingFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(Params.ARG_SERIALIZED_BOOK, book);
        fragment.setArguments(arguments);
        return fragment;
    }

    /* Метод-фабрика который будет создавать фрагмент с переданными в него
     * параметрами (в данном случае мы кладем туда открытую книгу и страницу
     * которая должна отобразится. Но пока что здесь нету нужной реализации)
     * */
    public static BookReadingFragment newInstance(Book book, int lastPage) {
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
        viewPager = (ViewPager) view.findViewById(R.id.ViewPagerID_Screen_One);
        viewPagerAdapter = new BookPageFragmentPagerAdapter(getActivity().getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        getActivity().getActionBar().setTitle("Opening..."); /*в экшенбаре устанавливаем нужный(текущий) заголовок активити*/

        /*
        * Отложенный запуск книги для того что бы перед тем как начать открывать книгу
        * запустился активити с однотонным экраном для подсчета символов и строк что бы
        * потом с данными настройками открыть саму книгу
        * */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TextView view = (TextView) getView().findViewById(R.id.text_view_fragment);
                numberOfCharsPerLine = ViewUtils.getNumberOfCharsPerLine(view);
                numberOfLinesPerScreen = ViewUtils.getNumberOfLinesPerScreen(view);
                Bundle arguments = getArguments(); /* получения переданных аргументов с активити **/
                String bookPath = arguments.getString(Params.ARG_BOOK_PATH);
                boolean isOpeningInfo = arguments.getBoolean(Params.ARG_BOOK_INFO_WAS_OPENED);
                book = (Book) arguments.getSerializable(Params.ARG_SERIALIZED_BOOK);

                if (book == null) {
                    book = DataAccess.openBookFromDocument(DataAccess.openBookDocumentFromFile(bookPath), numberOfCharsPerLine, numberOfLinesPerScreen);
                }
                book.setBookFullPathInStorage(bookPath);

                viewPagerAdapter = new BookPageFragmentPagerAdapter(getActivity().getSupportFragmentManager(), book);
                viewPagerAdapter.notifyDataSetChanged();
                viewPager.setAdapter(viewPagerAdapter);
                String bookNameInActionBar;
                try {
                    bookNameInActionBar = book.getBookInfo().getBookName().get(0);
                } catch (IndexOutOfBoundsException ex) {
                    bookNameInActionBar = "...";
                }
                if (bookNameInActionBar != null) {
                    getActivity().getActionBar().setTitle(bookNameInActionBar);
                }

                onBookOpenedListener.onBookOpenedEvent(book);/* оповещаем главный активити о окрытой книге и передаем ему ссылку на нее*/
                if (isOpeningInfo) {
                    onInfoPageOpeningListener.onInfoPageOpeningEvent(isOpeningInfo, book);
                    /*проверяем было ли открыто окно с информацией для данной книги если да то говорим
                    * активити что нужно его открыть
                    * */
                }
            }
        }, 1000);
    }

    /*
    * данный метод нужен для того что бы привязать главное активити (создание слушателя)
    * для последующей обработки события в главном активити
    * */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onInfoPageOpeningListener = (OnInfoPageOpeningListener) activity;
            onBookOpenedListener = (OnBookOpenedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }

    /*
    * см. описания в главном активити
    * */
    public interface OnInfoPageOpeningListener {
        public void onInfoPageOpeningEvent(boolean firstOpening, Book book);
    }

    public interface OnBookOpenedListener {
        public void onBookOpenedEvent(Book book);
    }

    public interface OnSearchListener {
        public int onSearchEvent(String pattern);
        /*нужен будет для создания поиска по книге*/
    }
}