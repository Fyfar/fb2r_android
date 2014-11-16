package ua.knure.fb2reader.Views;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import ua.knure.fb2reader.R;

public class GetBookSettingsActivity extends FragmentActivity {

    static final int PAGE_COUNT = 1;
    static int charsPerLine;
    static int linesPerScreen;

    private ViewPager viewPager;
    private PagerAdapter viewPagerAdapter;
    private org.w3c.dom.Document document;

    public static int getNumberOfCharsPerLine(TextView view) {
        if (view == null) return 0;
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
        if (view == null) return 0;
        int linesPerScreen = view.getHeight() / (view.getLineHeight() + (int) view.getLineSpacingExtra());
        return linesPerScreen;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slide_page_view_activity);
        //document = openBookDocument("/.fb2reader/sample.xml");
        viewPager = (ViewPager) findViewById(R.id.slide_page_view_pager);
        viewPagerAdapter = new BookPageFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final TextView view = (TextView) findViewById(R.id.text_view_in_fragment_page);
                linesPerScreen = getNumberOfLinesPerScreen(view);
                charsPerLine = getNumberOfCharsPerLine(view);

                Intent intent = new Intent(GetBookSettingsActivity.this, MainFragmentPageView.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.putExtra("charsPerLine", charsPerLine);
                intent.putExtra("linesPerScreen", linesPerScreen);
                intent.putExtra("path","/.fb2reader/sample.xml");
                startActivity(intent);
                finish();
                System.exit(0);
            }
        }, 2000);
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

    private class BookPageFragmentPagerAdapter extends FragmentPagerAdapter {
        public BookPageFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PageFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return " " + position;
        }

    }


}
