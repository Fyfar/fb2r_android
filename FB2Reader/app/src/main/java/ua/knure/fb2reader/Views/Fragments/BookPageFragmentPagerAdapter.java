package ua.knure.fb2reader.Views.Fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.Date;

import ua.knure.fb2reader.Book.Book;
import ua.knure.fb2reader.Book.BookPage;
import ua.knure.fb2reader.DataAccess.DAO;

/**
 * Created by Александр on 09.12.2014.
 */
public class BookPageFragmentPagerAdapter extends FragmentStatePagerAdapter {
    private final int numberOfDefaultPage = 1;
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
        Log.d("myLogs", "lastChar = " + getLastChar(position) + "  " + book.getBookFullPathInStorage());
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

    private int getLastChar(int pos) {
        int lastChar = 0;
        for(int i = 0; i < pos; i++) {
            for(String line : book.getBookPages().get(i).getLinesOnThePage()) {
                lastChar += line.length();
            }
        }
        return lastChar + 1;
    }
}
