package ua.knure.fb2reader.Views;

import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import ua.knure.fb2reader.Book.Book;
import ua.knure.fb2reader.Book.Parser;
import ua.knure.fb2reader.Book.SimpleSyllables;
import ua.knure.fb2reader.DataAccess.DataAccess;
import ua.knure.fb2reader.R;

public class GetBookSettingsActivity extends FragmentActivity {

    static final int PAGE_COUNT = 1;
    static int charsPerLine;
    static int linesPerScreen;

    private ViewPager viewPager;
    private PagerAdapter viewPagerAdapter;

    public static int getNumberOfCharsPerLine(TextView view) {
        if (view == null) {
            return 0;
        }
        String text = "This string is using for calculate line width value in text view";
        int textViewWidth = view.getWidth();
        int charCount;

        Paint paint = view.getPaint();
        for (charCount = 1; charCount <= text.length(); ++charCount) {
            if (paint.measureText(text, 0, charCount) > textViewWidth) {
                break;
            }
        }
        return charCount;
    }

    public static int getNumberOfLinesPerScreen(TextView view) {
        if (view == null) {
            return 0;
        }
        int linesPerScreen = view.getHeight() / (view.getLineHeight() + (int) view.getLineSpacingExtra());
        return linesPerScreen;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slide_page_view_activity);
        viewPager = (ViewPager) findViewById(R.id.slide_page_view_pager);
        viewPagerAdapter = new BookPageFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TextView view = (TextView) findViewById(R.id.text_view_in_fragment_page);

                linesPerScreen = getNumberOfLinesPerScreen(view);
                charsPerLine = getNumberOfCharsPerLine(view);

                //"/.fb2reader/sample.xml");//sample.xml //samplqe.xml //metro.fb2
                Book book = openBook(openBookDocument("/.fb2reader/sample.xml"), charsPerLine, linesPerScreen);

                viewPagerAdapter = new BookPageFragmentPagerAdapter(getSupportFragmentManager(), book);
                viewPagerAdapter.notifyDataSetChanged();
                viewPager.setAdapter(viewPagerAdapter);
            }
        }, 1000);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_get_book_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public org.w3c.dom.Document openBookDocument(String path) {
        File currentBook = DataAccess.openBook(Environment.getExternalStorageDirectory() + path);
        try {
            org.w3c.dom.Document doc = Parser.getParsedBook(currentBook);
            return doc;
        } catch (Exception ex) {
            Toast.makeText(this.getApplicationContext(), "" + ex.getMessage() + "\n" + ex.getStackTrace().toString(), Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
        return null;
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
                return PageFragment.newInstance(position);
            }
            return PageFragment.newInstance(position, book);
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
            return " " + position;
        }
    }
}
