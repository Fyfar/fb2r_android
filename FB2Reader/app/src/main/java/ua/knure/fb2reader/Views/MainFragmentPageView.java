package ua.knure.fb2reader.Views;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import ua.knure.fb2reader.Book.Book;
import ua.knure.fb2reader.Book.BookPage;
import ua.knure.fb2reader.Book.Parser;
import ua.knure.fb2reader.Book.SimpleSyllables;
import ua.knure.fb2reader.DataAccess.DataAccess;
import ua.knure.fb2reader.R;

public class MainFragmentPageView extends FragmentActivity {

    static final String TAG = "myLogs";
    static final int PAGE_COUNT = 10;

    ViewPager viewPager;
    PagerAdapter viewPagerAdapter;

    private int charsPerLine;
    private int linesPerScreen;
    private org.w3c.dom.Document document;
    private String path;
    private StringBuilder builder;
    private Thread threadForOpenBook;

    public MainFragmentPageView(){
        super();
    }
    public MainFragmentPageView(int charsPerLine, int linesPerScreem, org.w3c.dom.Document doc) {
        this();
        this.charsPerLine = charsPerLine;
        this.linesPerScreen = linesPerScreem;
        this.document = doc;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slide_page_view_activity);
        path = getIntent().getStringExtra("path");
        document = openBookDocument(path);
        Book book = openBook(document, getIntent().getIntExtra("charsPerLine", 0)-5, getIntent().getIntExtra("linesPerScreen", 0)-5);
        viewPager = (ViewPager) findViewById(R.id.slide_page_view_pager);
        viewPagerAdapter = new BookPageFragmentPagerAdapter(getSupportFragmentManager(), (List<BookPage>) book.getPages());
        viewPager.setAdapter(viewPagerAdapter);


        /*viewPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });*/


    }

    private class BookPageFragmentPagerAdapter extends FragmentPagerAdapter {
        private List<BookPage> pages;

        public BookPageFragmentPagerAdapter(FragmentManager fm, List<BookPage> pages) {
            super(fm);
            this.pages = pages;
        }

        @Override
        public Fragment getItem(int position) {
            if (pages == null) {
                return PageFragment.newInstance(position);
            }
            return PageFragment.newInstance(position, pages);
        }

        @Override
        public int getCount() {
            if (pages != null) {
                return pages.size();
            }
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return " " + position;
        }

    }

    public Book openBook(org.w3c.dom.Document doc, int lineLength, int linesPerScreen) {
        try {
            Book book = new Book(doc, lineLength, linesPerScreen, 0, new SimpleSyllables());
            Toast.makeText(this.getApplicationContext(), "Book is loaded. Pages = " + book.getPages().size() + " pages", Toast.LENGTH_LONG).show();
            return book;
        } catch (Exception ex) {
            Toast.makeText(this.getApplicationContext(), "" + ex.getMessage() + "\n" + ex.getStackTrace().toString(), Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
        return null;
    }
    public org.w3c.dom.Document openBookDocument(String path) {
        TextView view = (TextView) findViewById(R.id.pageView);
        File currentBook = DataAccess.openBook(Environment.getExternalStorageDirectory() + path);
        //"/.fb2reader/sample.xml");//sample.xml //samplqe.xml //metro.fb2
        try {
            org.w3c.dom.Document doc = Parser.getParsedBook(currentBook);
            //Book book = new Book(doc, lineLength, linesPerScreen, 0, new SimpleSyllables());
            //Toast.makeText(this.getApplicationContext(), "Book is loaded. Pages = " + book.getPages().size() + " pages", Toast.LENGTH_LONG).show();
            return doc;
        } catch (Exception ex) {
            Toast.makeText(this.getApplicationContext(), "" + ex.getMessage() + "\n" + ex.getStackTrace().toString(), Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
        return null;
    }


}
