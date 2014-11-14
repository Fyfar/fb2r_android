package ua.knure.fb2reader;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import ua.knure.fb2reader.Book.Book;
import ua.knure.fb2reader.Book.BookPage;
import ua.knure.fb2reader.Book.Parser;
import ua.knure.fb2reader.Book.SimpleSyllables;
import ua.knure.fb2reader.DataAccess.DataAccess;

public class BookPageView extends Activity {

    private int linesPerScreen;
    private int lineLength;
    private StringBuilder builder;
    private Thread threadForOpenBook;

    private int getNumberOfCharsPerLine() {
        TextView view = (TextView) findViewById(R.id.pageView);
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

    private int getNumberOfLinesPerScreen() {
        TextView view = (TextView) findViewById(R.id.pageView);
        int linesPerScreen = view.getHeight() / (view.getLineHeight() + (int) view.getLineSpacingExtra());
        return linesPerScreen;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_page);
        builder = new StringBuilder();

        final TextView view = (TextView) findViewById(R.id.pageView);
        view.setMaxLines(Integer.MAX_VALUE);
        view.setMovementMethod(new ScrollingMovementMethod());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                linesPerScreen = getNumberOfLinesPerScreen();
                lineLength = getNumberOfCharsPerLine();
                openBook();
                SeekBar bar = (SeekBar) findViewById(R.id.seekBar);
                bar.setMax(view.length());
                view.getScrollBarSize();
                bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        view.scrollTo(0, progress);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });
            }
        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.book_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openBook() {
        TextView view = (TextView) findViewById(R.id.pageView);
        File currentBook = DataAccess.openBook(Environment.getExternalStorageDirectory() + "/.fb2reader/sample.xml");//sample.xml //samplqe.xml //metro.fb2
        try {
            org.w3c.dom.Document doc = Parser.getParsedBook(currentBook);
            Book book = new Book(doc, lineLength, linesPerScreen, 0, new SimpleSyllables());
            Collection<BookPage> pages = book.getPages();
            Iterator<BookPage> iterator = pages.iterator();

            while (iterator.hasNext()) {
                BookPage page = iterator.next();
                Collection<String> temp = page.getLines();
                String str[] = temp.toArray(new String[temp.size()]);
                for (int i = 0; i < str.length; i++) {
                    builder.append(str[i]);
                }
                builder.append("\n***Page : " + page.getPageNumber() + " ***\n\n");
            }
            view.setText(builder.toString());
            Toast.makeText(this.getApplicationContext(), "Book is loaded. Pages = " + book.getPages().size() + " pages", Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(this.getApplicationContext(), "" + ex.getMessage() + "\n" + ex.getStackTrace().toString(), Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }
}
