package ua.knure.fb2reader.dropbox;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException.Unauthorized;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;
import com.dropbox.sync.android.DbxPath.InvalidPathException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ua.knure.fb2reader.Book.BookBookmark;
import ua.knure.fb2reader.DataAccess.BookDAO;
import ua.knure.fb2reader.DataAccess.DAO;
import ua.knure.fb2reader.Utils.PathListener;
import ua.knure.fb2reader.Utils.ViewUtils;

public class SyncService extends Service {

    static final int REQUEST_LINK_TO_DBX = 0;
    private static final String URL = "https://fb2r-university.rhcloud.com/dropbox";
    private static String email;
    private final String APP_KEY = "ygam033j049nurm";
    private final String APP_SECRET = "8jbe7gnyi4y9imt";
    private DbxFileSystem dbxFs;
    private DbxAccountManager mDbxAcctMgr;
    private DAO dao;
    private Timer tC;
    private TimerTask tt;
    private HttpAsyncTask task;

    public static String GET(String url) {
        InputStream inputStream = null;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url + "/" + ViewUtils.md5(email)));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private static void addBooksToDB(String result) {
        JSONObject json = null;
        try {
            json = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            JSONArray arr = json.getJSONArray("books");
            for (int i = 0; i < arr.length(); i++) {
                if(DAO.dbIsOpen()) {
                    DAO.updateBooks(arr, email);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void addBookMarksToDB(String result) {
        JSONObject json = null;
        try {
            json = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            JSONArray arr = json.getJSONArray("bookmarks");
            for (int i = 0; i < arr.length(); i++) {
                if(DAO.dbIsOpen()) {
                    DAO.updateBookmarks(arr, email);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            initialize();
        } catch (InvalidPathException | IOException e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (DAO.dbIsOpen()) {
            dao.close();
        }
        tt.cancel();
        tC.cancel();
    }

    private void initialize() throws InvalidPathException, IOException {
        mDbxAcctMgr = DbxAccountManager.getInstance(getApplicationContext(),
                APP_KEY, APP_SECRET);
        dao = DAO.getInstance(getBaseContext());
        dao.open();
        try {
            dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr.getLinkedAccount());
        } catch (Unauthorized e) {
            e.printStackTrace();
        }
        email = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext()).getString("email", null);
        PathListener.addBooksToDB(dao, email);
        PathListener.downloadFiles(dbxFs, new DbxPath("/"));
        PathListener.uploadFiles(dbxFs);
        tC = new Timer();
        startTimers();
        dbxFs.addPathListener(new PathListener(), new DbxPath("/"),
                DbxFileSystem.PathListener.Mode.PATH_OR_DESCENDANT);
    }

    private void startTimers() {
        try {
            newTaskControl();
            tC.schedule(tt, 100, 20000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void newTaskControl() {
        if (tt != null) {
            tt.cancel();
        }
        tt = new TimerTask() {

            @Override
            public void run() {
                try {
                    if(isConnected()) {
                        PathListener.downloadFiles(dbxFs, new DbxPath("/"));
                        PathListener.uploadFiles(dbxFs);
                        task = new HttpAsyncTask();
                        task.execute(URL);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };
    }

    public void postBooks(BookDAO book) {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(URL + "/" + ViewUtils.md5(email));
        List<NameValuePair> nameValuePairs = new ArrayList<>(2);
        nameValuePairs.add(new BasicNameValuePair("lastChar", String.valueOf(book.getLastChar())));
        nameValuePairs.add(new BasicNameValuePair("bookName", book.getBookName()));
        try {
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // Execute HTTP Post Request
        try {
            HttpResponse response = httpclient.execute(httppost);
            String responseBody = EntityUtils.toString(response.getEntity());

            if (responseBody != null) {
                JSONObject json = new JSONObject(responseBody);
                JSONArray arr = json.getJSONArray("books");
                if(DAO.dbIsOpen()) {
                    DAO.updateBooks(arr, email);
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

    }


    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    public void postBookMarks(BookBookmark bookmark) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(URL + "/bookmark/" + ViewUtils.md5(email));
        List<NameValuePair> nameValuePairs = new ArrayList<>(5);
        try {
            byte[] b = bookmark.getBookName().getBytes("UTF-8");
            nameValuePairs.add(new BasicNameValuePair("lastChar", String.valueOf(bookmark.getCharsCounter())));
            nameValuePairs.add(new BasicNameValuePair("bookName", new String(b, "UTF-8")));
            b = bookmark.getBookmarkName().getBytes("UTF-8");
            nameValuePairs.add(new BasicNameValuePair("bookmarkName", new String(b, "UTF-8")));
            b = bookmark.getText().getBytes("UTF-8");
            nameValuePairs.add(new BasicNameValuePair("text", new String(b, "UTF-8")));
            nameValuePairs.add(new BasicNameValuePair("pageNumber", String.valueOf(bookmark.getPageNumber())));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // Execute HTTP Post Request
        try {
            HttpResponse response = httpclient.execute(httppost);
            String responseBody = EntityUtils.toString(response.getEntity());

            if (responseBody != null) {
                JSONObject json = new JSONObject(responseBody);
                Log.d("myLogs", responseBody);
                JSONArray arr = json.getJSONArray("bookmarks");
                if(DAO.dbIsOpen()) {
                    DAO.updateBookmarks(arr, email);
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            List<BookDAO> books = dao.getAllBooks(email);
            for (BookDAO book : books) {
                postBooks(book);

            }
            List<BookBookmark> bookmarks = DAO.getAllBookmarks(email);
            for (BookBookmark bookmark : bookmarks) {
                postBookMarks(bookmark);
            }
            String res = GET(URL + "/bookmark");
            addBooksToDB(res);
            addBookMarksToDB(res);
            return "";
        }
    }
}

