package ua.knure.fb2reader.Views.Fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import ua.knure.fb2reader.Book.Book;

public class BookPageFragmentPagerAdapter extends FragmentStatePagerAdapter {
    private final int numberOfDefaultPage = 1;
    private Book book;
    private int textSize = 0;
    private int textColor = 0;
    private int backgroundColor = 0;

    public BookPageFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public BookPageFragmentPagerAdapter(FragmentManager fm, Book book) {
        super(fm);
        this.book = book;
    }

    public BookPageFragmentPagerAdapter(FragmentManager fm, Book book, int textSize, int textColor, int backgroundColor) {
        super(fm);
        this.book = book;
        this.textColor = textColor;
        this.textSize = textSize;
        this.backgroundColor = backgroundColor;
    }

    @Override
    public Fragment getItem(int position) {
        if (book != null && textSize != 0 && textColor != 0 && backgroundColor != 0) {
            return ViewPageFragment.newInstance(position, book, textSize, textColor, backgroundColor);
        }
        if (book == null) {
            return ViewPageFragment.newInstance(position);
        }
        return ViewPageFragment.newInstance(position, book);
    }

    @Override
    public int getCount() {
        if (book != null) {
            return book.getBookPages().size();
        }
        return numberOfDefaultPage;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        position++;
        return " " + position;
    }
}
