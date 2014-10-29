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
    private int screenWidth;
    private int screenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_page);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
        characterHeight = 5;
        characterWidth = 5;

        TextView view = (TextView) findViewById(R.id.pageView);
        view.setTypeface(Typeface.MONOSPACE);
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
            Toast.makeText(this.getApplicationContext(), "int try", Toast.LENGTH_LONG);
            org.w3c.dom.Document doc = Parser.getParsedBook(currentBook);

            Book book = new Book(doc, 0);
            book.setMetrics(characterWidth, characterHeight, screenWidth, screenHeight);
            book.createPages();
            Toast.makeText(this.getApplicationContext(), "create page", Toast.LENGTH_LONG);
            //Log.d("Book", "create");

            view.setMovementMethod(new ScrollingMovementMethod());
            Collection<BookPage> pages = book.getPages();

            Toast.makeText(this.getApplicationContext(), "pages length " + pages.size(), Toast.LENGTH_LONG);

            //Log.d(pages.size()+": size", "pages");
            //view.setText(book.getAllText());
            Iterator<BookPage> iterator = pages.iterator();
            while (iterator.hasNext()) {
                String str[] = iterator.next().getLines().toArray(new String[iterator.next().getLines().size()]);
                Toast.makeText(this.getApplicationContext(), "while ", Toast.LENGTH_LONG).show();
                for (int i = 0; i < str.length; i++) {
                    view.append(str[i]);
                }

            }
            //Toast.makeText(this.getApplicationContext(), "Success ", Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(this.getApplicationContext(), "Exception: " + ex.getMessage(), Toast.LENGTH_LONG).show();

            System.out.println(ex.getMessage());
        }
        Toast.makeText(this.getApplicationContext(), Environment.getExternalStorageDirectory().toString(), Toast.LENGTH_LONG);
    }
}
