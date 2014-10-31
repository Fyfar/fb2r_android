package ua.knure.fb2reader;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import ua.knure.fb2reader.Book.Book;
import ua.knure.fb2reader.Book.BookPage;
import ua.knure.fb2reader.Book.Parser;
import ua.knure.fb2reader.DataAccess.DataAccess;

public class BookPageView extends Activity {

    private int characterWidth;
    private int characterHeight;
    private int linesPerScreen;
    private int lineLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_page);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        characterHeight = 20; // i don't know what a size of character, so i use random value
        characterWidth = 20;
        linesPerScreen = metrics.heightPixels / characterHeight;
        lineLength = metrics.widthPixels / characterWidth;

        TextView view = (TextView) findViewById(R.id.pageView);
        view.setTypeface(Typeface.MONOSPACE);
        view.setMaxLines(Integer.MAX_VALUE);
        //view.setTextScaleX(1.6f); // something like justify, but isn't good

        openBook();
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
        File currentBook = DataAccess.openBook(Environment.getExternalStorageDirectory() + "/.fb2reader/sample.xml");
        try {
            //Toast.makeText(this.getApplicationContext(), "method::openBook", Toast.LENGTH_SHORT);//for debug
            org.w3c.dom.Document doc = Parser.getParsedBook(currentBook);
            Book book = new Book(doc, lineLength, linesPerScreen, 0);
            //Toast.makeText(this.getApplicationContext(), "Book is created and parsed", Toast.LENGTH_SHORT);//for debug
            view.setMovementMethod(new ScrollingMovementMethod());
            Collection<BookPage> pages = book.getPages();
            //Toast.makeText(this.getApplicationContext(), "pages amount:: " + pages.size(), Toast.LENGTH_LONG);//for debug
            Iterator<BookPage> iterator = pages.iterator();
            //int counter = 0;//for debug

            while (iterator.hasNext()) {
                Collection<String> temp = iterator.next().getLines();
                String str[] = temp.toArray(new String[temp.size()]);

                for (int i = 0; i < str.length; i++) {
                    view.append(str[i]);
                }
                //Toast.makeText(this.getApplicationContext(), "while " + counter++, Toast.LENGTH_LONG).show();//for debug
            }
            Toast.makeText(this.getApplicationContext(), "Success downloading (parsing) book", Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(this.getApplicationContext(), "" + ex.getMessage() + "\n" + ex.getStackTrace().toString(), Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }
}
