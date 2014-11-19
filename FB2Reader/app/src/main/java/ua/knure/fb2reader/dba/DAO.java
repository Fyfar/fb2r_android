package ua.knure.fb2reader.dba;

import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import ua.knure.fb2reader.Book.Book;

public class DAO {

	private static final String USER_TABLE = "users";
	private static final String EMAIL_COLUMN = "email";
	private static final String NAME_COLUMN = "user_name";
	private static final String BOOKS_TABLE = "books";
    private static final String BOOK_NAME = "book_name";
    private static final String LAST_CHAR = "lastChar";
    private static final String DATETIME = "updateTime";
    private static final String USER_ID = "userId";
	private static final String BOOKMARKS_TABLE = "bookmarks";

	private static final String FIRST_QUERY = "create table users (id integer primary key autoincrement,"
			+ "dropBox text, vk text, faceBook text, email text, user_name text);";
	private static final String SECOND_QUERY = "create table books (id integer primary key autoincrement,"
			+ "book_name text, lastChar integer, updateTime text, userId integer,"
			+ "foreign key (userId) references user (id));";
	private static final String THIRD_QUERY = "create table bookmarks (id integer primary key autoincrement,"
			+ "bookId integer, userId integer, page integer,"
			+ "foreign key (userId) references user (id),"
			+ "foreign key (bookId) references books (id));";
	private static final String DB_NAME = "library";
	private static final int DB_VERSION = 1;

	private Context ctx;
	private DBHelper mDBHelper;
	private SQLiteDatabase db;

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

	public Cursor getAllData(String table) {
		return db.query(table, null, null, null, null, null, null);
	}

    public Cursor getUserByEmail(String email) {
        Cursor c = getAllData(USER_TABLE);
        if(c.moveToFirst()) {
            do {
                if(c.getString(c.getColumnIndex(EMAIL_COLUMN)).equals(email)) {
                    return c;
                }
            }while(c.moveToNext());
        }
        return c;
    }

	public boolean dbIsOpen() {
		return db.isOpen();
	}

	public void clearTable(String table) {
		db.delete(table, null, null);
	}
	
	public void addUser(String email, String userName, List<Book> books) {
		if(checkRec(email, USER_TABLE, EMAIL_COLUMN)) {
			return;
		}
		db.beginTransaction();
		ContentValues cv = new ContentValues();
		cv.put(EMAIL_COLUMN, email);
		cv.put(NAME_COLUMN, userName);
		db.insert(USER_TABLE, null, cv);
		db.endTransaction();
	}
	
	public void addBooks(List<String> books, String email) {
        Cursor c = getUserByEmail(email);
        if(c.moveToFirst()) {
            db.beginTransaction();
            ContentValues cv = new ContentValues();
            int id = c.getInt(c.getColumnIndex(EMAIL_COLUMN));
            for(String book : books) {
                if(checkRec(book, BOOKS_TABLE, BOOK_NAME)) {
                    continue;
                }
                cv.put(BOOK_NAME, book);
                cv.put(USER_ID, id);
                cv.put(DATETIME, 0);
                cv.put(LAST_CHAR, 0);
                db.insert(BOOKMARKS_TABLE, null, cv);
            }
            db.endTransaction();
        }
	}
	
	public boolean checkRec(String checking, String table, String column) {
		Cursor c = getAllData(table);
		if(c.moveToFirst()) {
			do {
				if(c.getString(c.getColumnIndex(column)).equals(checking)) {
					c.close();
					return true;
				}
			}while(c.moveToNext());
		}
		c.close();
		return false;
	}

	public void updateBook(String table, Date updateTime, int lastChar, String book, String email) {
        ContentValues cv = new ContentValues();
        cv.put(DATETIME, updateTime.toString());
        cv.put(LAST_CHAR, lastChar);
        Cursor c = getUserByEmail(email);
        int id = c.getInt(c.getColumnIndex("id"));
        String where = "userId = ? and book_name = ?";
        db.update(table, cv, where, new String[] {String.valueOf(id), book});
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

		}

	}
}
