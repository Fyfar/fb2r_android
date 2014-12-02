package ua.knure.fb2reader.Views.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
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

    private static final int PAGE_COUNT = 1;
    private static int charsPerLine;
    private static int linesPerScreen;
    private ViewPager viewPager;
    private PagerAdapter viewPagerAdapter;
    private Book book;
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
        Bundle bundle = new Bundle();
        bundle.putString(Params.ARGUMENT_BOOK_PATH, bookPath);
        bundle.putBoolean(Params.ARGUMENT_FIRST_TIME_BOOK_INFO_OPENING, true);
        fragment.setArguments(bundle);
        return fragment;
    }

    /* Метод-фабрика который будет создавать фрагмент с переданными в него
    *  параметрами (в данном случае мы кладем туда открытую книгу)
    * */
    public static BookReadingFragment newInstance(Book book) {
        BookReadingFragment fragment = new BookReadingFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Params.ARGUMENT_SERIALIZED_BOOK, book);
        fragment.setArguments(bundle);
        return fragment;
    }

    /* Метод-фабрика который будет создавать фрагмент с переданными в него
     * параметрами (в данном случае мы кладем туда открытую книгу и страницу
     * которая должна отобразится. Но пока что здесь нету нужной реализации)
     * */
    public static BookReadingFragment newInstance(Book book, int page) {
        BookReadingFragment fragment = new BookReadingFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Params.ARGUMENT_SERIALIZED_BOOK, book);
        fragment.setArguments(bundle);
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

        /*
        * Отложенный запуск книги для того что бы перед тем как начать открывать книгу
        * запустился активити с однотонным экраном для подсчета символов и строк что бы
        * потом с данными настройками открыть саму книгу
        * */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TextView view = (TextView) getView().findViewById(R.id.text_view_fragment);
                linesPerScreen = ViewUtils.getNumberOfLinesPerScreen(view);
                charsPerLine = ViewUtils.getNumberOfCharsPerLine(view);
                Bundle args = getArguments(); /* получения переданных аргументов с активити **/
                String bookPath = args.getString(Params.ARGUMENT_BOOK_PATH);
                boolean isOpeningInfo = args.getBoolean(Params.ARGUMENT_FIRST_TIME_BOOK_INFO_OPENING);

                book = (Book) args.getSerializable(Params.ARGUMENT_SERIALIZED_BOOK);
                if (book == null) {
                    book = DataAccess.openBook(DataAccess.openBookDocument(bookPath), charsPerLine, linesPerScreen);
                }

                book.setFullPath(bookPath);

                viewPagerAdapter = new BookPageFragmentPagerAdapter(getActivity().getSupportFragmentManager(), book);
                viewPagerAdapter.notifyDataSetChanged();
                viewPager.setAdapter(viewPagerAdapter);
                String name;
                try {
                    name = book.getBookInfo().getBookName().get(0);
                } catch (IndexOutOfBoundsException ex) {
                    name = "no name";
                }

                if (name != null) {
                    getActivity().getActionBar().setTitle(name);
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
        getActivity().getActionBar().setTitle("Opening..."); /*в экшенбаре устанавливаем нужный(текущий) заголовок активити*/
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

    /* адаптер для viewPager для отображения страниц книги*/
    private class BookPageFragmentPagerAdapter extends FragmentStatePagerAdapter {
        private Book book;

        public BookPageFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public BookPageFragmentPagerAdapter(FragmentManager fm, Book book) {
            super(fm);
            this.book = book;
        }

        @Override
        public Fragment getItem(int position) {
            if (book == null) {
                return ViewPageFragment.newInstance(position);
            }
            return ViewPageFragment.newInstance(position, book);
        }

        @Override
        public int getCount() {
            if (book != null) {
                return book.getPages().size();
            }
            return PAGE_COUNT;
        }

        /**
         * this method need for things like page number
         */
        @Override
        public CharSequence getPageTitle(int position) {
            position++;
            return " " + position;
        }
    }
}