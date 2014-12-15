package ua.knure.fb2reader.Views.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import ua.knure.fb2reader.Book.Book;
import ua.knure.fb2reader.DataAccess.BookDAO;
import ua.knure.fb2reader.DataAccess.DAO;
import ua.knure.fb2reader.R;
import ua.knure.fb2reader.Utils.ViewUtils;
import ua.knure.fb2reader.Views.Fragments.BookInfoFragment;
import ua.knure.fb2reader.Views.Fragments.BookReadingFragment;
import ua.knure.fb2reader.Views.Fragments.BookShelfFragment;
import ua.knure.fb2reader.Views.Fragments.BookmarkAddDialogFragment;
import ua.knure.fb2reader.Views.Fragments.BookmarksListFragment;
import ua.knure.fb2reader.Views.Params;
import ua.knure.fb2reader.dropbox.SyncService;

public class MainActivity extends ActionBarActivity implements BookShelfFragment.OnBookSelectedInShelfListener,
        BookReadingFragment.OnInfoPageOpeningListener, BookInfoFragment.OnClosedBookInfoFragmentListener,
        BookReadingFragment.OnBookStatusChangedListener, BookmarkAddDialogFragment.OnBookmarkAddListener {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private Book lastOpenedBook;
    private boolean isLogined = true;
    private DAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTitle = mDrawerTitle = getTitle();
        Params.MENU_TITLES = getResources().getStringArray(R.array.screen_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, Params.MENU_TITLES));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(
                this, /* host Activity */
                mDrawerLayout, /* DrawerLayout object */
                R.drawable.ic_drawer, /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open, /* "open drawer" description */
                R.string.drawer_close /* "close drawer" description */
        ) {

            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                supportInvalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                supportInvalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            if (isLogined) {
                selectItem(Params.MENU_BOOK_SHELF);
            }
        }
        dao = DAO.getInstance(getBaseContext());
        new Thread(new Runnable() {
            @Override
            public void run() {
                startService(new Intent(getBaseContext(), SyncService.class));
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_settings:
                Toast.makeText(this, R.string.action_settings, Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, SyncService.class));
        super.onDestroy();
    }

    private void selectItem(int position) {
        Bundle arguments = new Bundle();
        Fragment fragment = null;

        switch (position) {
            case Params.MENU_BOOK_SHELF:
                fragment = BookShelfFragment.newInstance();
                arguments = fragment.getArguments();
                break;
            case Params.MENU_BOOK_INFO:
                if (lastOpenedBook != null) {
                    fragment = BookInfoFragment.newInstance(lastOpenedBook);
                    arguments = fragment.getArguments();
                } else {
                    Toast.makeText(getApplicationContext(), "OPEN BOOK BEFORE", Toast.LENGTH_LONG);
                }
                break;
            case Params.MENU_BOOK_ADD_BOOKMARK:
                addBookmarkDialog();
                break;
            case Params.MENU_BOOK_BOOKMARKS:
                fragment = BookmarksListFragment.newInstance(lastOpenedBook.getBookFullPathInStorage(), true);
                arguments = fragment.getArguments();
                break;
            default:
                break;
        }
        if (fragment != null) {
            openFragment(fragment, position, arguments);
        }
    }

    private void addBookmarkDialog() {
        if (lastOpenedBook != null) {
            BookmarkAddDialogFragment dialog = BookmarkAddDialogFragment.newInstance(lastOpenedBook);
            dialog.show(this.getSupportFragmentManager(), "");
        }
    }

    private void openFragment(Fragment fragment, int position, Bundle bundleArgs) {
        if (fragment != null) {
            bundleArgs.putStringArray("TITLES", Params.MENU_TITLES);
            fragment.setArguments(bundleArgs);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
            mDrawerList.setItemChecked(position, true);
            setTitle(Params.MENU_TITLES[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void OnBookSelectedInShelfEvent(String bookPath) {
        try {
            Fragment fragment = BookReadingFragment.newInstance(bookPath);
            if (fragment != null) {
                fragment.getArguments().putBoolean(Params.ARG_BOOK_INFO_WAS_OPENED, true);
                openFragment(fragment, Params.MENU_BOOK_SHELF, fragment.getArguments());
            }
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Cannot open the book by path:\n" + bookPath, Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onInfoPageOpeningEvent(boolean firstOpening, Book book) {
        Fragment fragment = BookInfoFragment.newInstance(book);
        if (fragment != null) {
            openFragment(fragment, Params.MENU_BOOK_SHELF, fragment.getArguments());
        }
    }

    @Override
    public void OnClosedBookInfoFragmentsEvent() {
        SharedPreferences sdPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor ed = sdPref.edit();
        String[] filePath = lastOpenedBook.getBookFullPathInStorage().split("/");
        ed.putString("currentBook", filePath[filePath.length - 1]);
        ed.commit();
        BookDAO bookDAO = ViewUtils.getBookFromDB(filePath[filePath.length - 1], getBaseContext());

        Fragment fragment = null;
        int numberOfChars = bookDAO == null ? 0 : bookDAO.getLastChar();
        if (numberOfChars > 0 && (numberOfChars / lastOpenedBook.getCharsPerLine() / lastOpenedBook.getLinesPerPage()) < lastOpenedBook.getBookPages().size()) {
            if (lastOpenedBook.getCharsToLastPage() < numberOfChars) {
                lastOpenedBook.setCharsToLastPage(numberOfChars);
            }
        }
        fragment = BookReadingFragment.newInstance(lastOpenedBook);
        if (fragment != null) {
            openFragment(fragment, Params.MENU_BOOK_SHELF, fragment.getArguments());
        }
    }

    @Override
    public void onBookStatusChangedEvent(Book book) {
        lastOpenedBook = book;
    }

    @Override
    public void addBookmarkEvent(int page, int charsCounter, String text, String name) {
        Toast.makeText(this.getApplicationContext(), text, Toast.LENGTH_LONG).show();
        lastOpenedBook.addBookmark(page, charsCounter, text, name);
        String email = PreferenceManager.getDefaultSharedPreferences(getBaseContext())
                .getString("email", "");
        String bookName = PreferenceManager.getDefaultSharedPreferences(getBaseContext())
                .getString("currentBook", "");
        DAO.addBookmark(email, name, text, page, charsCounter, bookName);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }
}