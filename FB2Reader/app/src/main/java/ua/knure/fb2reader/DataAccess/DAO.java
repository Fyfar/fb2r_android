package ua.knure.fb2reader.DataAccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ua.knure.fb2reader.Book.Book;
import ua.knure.fb2reader.Book.BookBookmark;

public class DAO {

    private static final String USER_TABLE = "users";
    private static final String EMAIL_COLUMN = "email";
    private static final String NAME_COLUMN = "user_name";
    public static final String BOOKS_TABLE = "books";
    private static final String BOOK_NAME = "book_name";
    private static final String LAST_CHAR = "lastChar";
    private static final String DATETIME = "updateTime";
    private static final String USER_ID = "userId";
    private static final String BOOKMARKS_TABLE = "bookmarks";
    private static final String BOOKMARK_NAME = "bookmark_name";
    private static final String BOOKMARK_TEXT = "bookmark_text";
    private static final String PAGE_NUMBER = "page_number";
    private static final String CHARS_COUNTER = "chars_counter";

    private static final String FIRST_QUERY = "create table users (id integer primary key autoincrement,"
            + "dropBox text, vk text, faceBook text, email text, user_name text);";
    private static final String SECOND_QUERY = "create table books (id integer primary key autoincrement,"
            + "book_name text, lastChar integer, updateTime text, userId integer,"
            + "foreign key (userId) references user (id));";
    private static final String THIRD_QUERY = "create table bookmarks (id integer primary key autoincrement,"
            + "bookId integer, userId integer, bookmark_name text, chars_counter integer,"
            + "bookmark_text text, page_number integer,"
            + "foreign key (userId) references user (id),"
            + "foreign key (bookId) references books (id));";
    private static final String DB_NAME = "library";
    private static final int DB_VERSION = 1;

    private Context ctx;
    private DBHelper mDBHelper;
    private static SQLiteDatabase db;

    public DAO(Context ctx) {
        this.ctx = ctx;
    }

    public void open() {
        mDBHelper = new DBHelper(ctx, DB_NAME, null, DB_VERSION, null);
        db = mDBHelper.getWritableDatabase();
    }

    public void close() {
        if (mDBHelper != null) {
            mDBHelper.close();
        }
    }

    public static Cursor getAllData(String table) {
        return db.query(table, null, null, null, null, null, null);
    }

    public static Cursor getUserByEmail(String email) {
        Cursor c = getAllData(USER_TABLE);
        if (c.moveToFirst()) {
            do {
                if (c.getString(c.getColumnIndex(EMAIL_COLUMN)).equals(email)) {
                    return c;
                }
            } while (c.moveToNext());
        }
        return c;
    }

    public static int getUserId(String email) {
        Cursor user = getUserByEmail(email);
        if (user.moveToFirst()) {
            return user.getInt(user.getColumnIndex("id"));
        }
        return -1;
    }

    public boolean dbIsOpen() {
        return db.isOpen();
    }

    public void clearTable(String table) {
        db.delete(table, null, null);
    }

    public void addUser(String email, String userName, List<Book> books) {
        if (checkRec(email, USER_TABLE, EMAIL_COLUMN)) {
            return;
        }
        //db.beginTransaction();
        ContentValues cv = new ContentValues();
        cv.put(EMAIL_COLUMN, email);
        cv.put(NAME_COLUMN, userName);
        db.insert(USER_TABLE, null, cv);
        //db.endTransaction();
    }

    public void addBooks(List<String> books, String email) {
        ContentValues cv = new ContentValues();
        int userId = getUserId(email);
        for (String book : books) {
            if (checkRec(book, BOOKS_TABLE, BOOK_NAME)) {
                continue;
            }
            cv.put(BOOK_NAME, book);
            cv.put(USER_ID, userId);
            cv.put(DATETIME, 0);
            cv.put(LAST_CHAR, 0);
            db.insert(BOOKS_TABLE, null, cv);
        }
    }

    public static boolean addBookMark(String email, String bookmarkName, String bookmarkText
            , int pageNumber, int charsCount) {
        ContentValues cv = new ContentValues();
        int userId = getUserId(email);
        if(userId == -1) {
            return false;
        }
        cv.put(USER_ID, userId);
        cv.put(BOOKMARK_NAME, bookmarkName);
        cv.put(BOOKMARK_TEXT, bookmarkText);
        cv.put(PAGE_NUMBER, pageNumber);
        cv.put(CHARS_COUNTER, charsCount);
        db.insert(BOOKMARKS_TABLE, null, cv);
        return true;
    }

    public static boolean checkRec(String checking, String table, String column) {
        Cursor c = getAllData(table);
        if (c.moveToFirst()) {
            do {
                if (c.getString(c.getColumnIndex(column)).equals(checking)) {
                    c.close();
                    return true;
                }
            } while (c.moveToNext());
        }
        c.close();
        return false;
    }

    public static boolean updateBook(String table, Date updateTime, int lastChar, String book, String email) {
        if(!checkRec(book, BOOKS_TABLE, BOOK_NAME)) {
            return false;
        }
        ContentValues cv = new ContentValues();
        cv.put(DATETIME, updateTime.toString());
        cv.put(LAST_CHAR, lastChar);
        Cursor c = getUserByEmail(email);
        int id = c.getInt(c.getColumnIndex("id"));
        String where = "userId=" + id + " and book_name=\"" + book + "\"";
        return db.update(table, cv, where, null) > 0;
    }

    public void updateBooks(JSONArray arr, String email) {
        try {
            for (int i = 0; i < arr.length(); i++) {
                updateBook(BOOKS_TABLE, new Date()
                        , (Integer) ((JSONObject) arr.get(i)).get("lastChar")
                        , String.valueOf(((JSONObject) arr.get(i)).get("name")), email);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static BookDAO getBook(String email, String bookName) {
        BookDAO book = new BookDAO();
        int userId = getUserId(email);
        Cursor all = getAllData(BOOKS_TABLE);
        if(all.moveToFirst()) {
            do {
                if(all.getInt(all.getColumnIndex(USER_ID)) == userId
                        && bookName.equals(all.getString(all.getColumnIndex(BOOK_NAME)))) {
                    book.setBookName(bookName);
                    book.setLastChar(all.getInt(all.getColumnIndex(LAST_CHAR)));
                    all.close();
                    return book;
                }
            } while(all.moveToNext());
        }
        all.close();
        return null;
    }

    public List<BookDAO> getAllBooks(String email) {
        int userId = getUserId(email);
        List<BookDAO> books = new ArrayList<>();
        Cursor cursor = getAllData(BOOKS_TABLE);
        if (cursor.moveToFirst()) {
            BookDAO book;
            do {
                if (cursor.getInt(cursor.getColumnIndex(USER_ID)) == userId) {
                    book = new BookDAO();
                    book.setLastChar(cursor.getInt(cursor.getColumnIndex(LAST_CHAR)));
                    book.setBookName(cursor.getString(cursor.getColumnIndex(BOOK_NAME)));
                    books.add(book);
                }
            } while (cursor.moveToNext());
        }
        return books;
    }

    public static List<BookBookmark> getAllBookmarks(String email) {
        Cursor all = getAllData(BOOKMARKS_TABLE);
        List<BookBookmark> bookmarks = new ArrayList<>();
        if(all.moveToFirst()) {
            int userId = getUserId(email);
            do {
                if(all.getInt(all.getColumnIndex(USER_ID)) == userId) {
                    bookmarks.add(new BookBookmark(all.getInt(all.getColumnIndex(PAGE_NUMBER))
                            , all.getInt(all.getColumnIndex(CHARS_COUNTER))
                            , all.getString(all.getColumnIndex(BOOKMARK_TEXT))
                            , all.getString(all.getColumnIndex(BOOK_NAME))));
                }
            } while(all.moveToNext());
        }
        return bookmarks;
    }

    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, CursorFactory factory,
                        int version, DatabaseErrorHandler errorHandler) {
            super(context, name, factory, version, errorHandler);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(FIRST_QUERY);
            db.execSQL(SECOND_QUERY);
            db.execSQL(THIRD_QUERY);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //Actions on Upgrade
        }

    }
}
