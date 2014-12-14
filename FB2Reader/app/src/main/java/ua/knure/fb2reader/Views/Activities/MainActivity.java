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

/*
*  Этот активити имплементирует интерфейсы некоторых фрагментов для того что бы
*  потом можно было обрабатывать события фрагментов в данном активити, например
*  для передачи данных с одного фрагмента во второй
* **/
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

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, Params.MENU_TITLES));
        // Set the list's click listener
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

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // Initialize the first fragment when the application first loads.
        if (savedInstanceState == null) {
            if (isLogined) {
                selectItem(Params.MENU_BOOK_SHELF);
            } else {
                //selectItem(Params.MENU_APP_LOGIN);
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
        // Inflate the menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch (item.getItemId()) {
            case R.id.action_settings:
                // Show toast about click.
                Toast.makeText(this, R.string.action_settings, Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, SyncService.class));
        if (dao.dbIsOpen()) {
            dao.close();
        }
        super.onDestroy();
    }

    /**
     * Swaps fragments in the main content view
     */
    private void selectItem(int position) {
        // Update the main content by replacing fragments
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
                break;/*
            case Params.MENU_APP_SETTINGS:
                //fragment = SettingsFragment.newInstance("","");
                break;
            case Params.MENU_APP_STATISTICS:
                //fragment = StatisticsFragment.newInstance("","");
                break;
            case Params.MENU_APP_LOGIN:
                //fragment = LoginFragment.newInstance("","");
                break;*/
            default:
                break;
        }
        if (fragment != null) {
            openFragment(fragment, position, arguments);
        }
        // Insert the fragment by replacing any existing fragment
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

            // Highlight the selected item, update the title, and close the drawer
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

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     * */




    /*
    * Ниже представлены реализации интерфейсов некоторых
    * фрагментов для того что бы они могли в данное активити передавать
    * какие-либо нужные данные при совершении некоторых событий
    * тоесть здесь описаны обработчики событий во фрагментах
    * */
    @Override
    public void OnBookSelectedInShelfEvent(String bookPath) {
        /*
        * здесь реализован интерфейс из фрагмента bookShelfFragment
        * данный код срабатывает когда мы во фрагменте выбираем файл(книжку)
        * */
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
        int numberOfChars = bookDAO.getLastChar();
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
        DAO.addBookMark(email, name, text, page, charsCounter);
        //Log.d("myLogs", "[bookmarkName = " + name + " charsCounter = "
        //        + charsCounter + " pageNumber = " + page + " text = " + text + "]");
    }

    /*
    *
    *
    *
    *
    *
    *
    *
    *
    *
    *
    *
    * */
    /* The click listener for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }
}