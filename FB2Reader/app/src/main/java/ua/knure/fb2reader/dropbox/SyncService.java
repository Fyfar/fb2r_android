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

import ua.knure.fb2reader.Utils.ViewUtils;
import ua.knure.fb2reader.DataAccess.BookDAO;
import ua.knure.fb2reader.DataAccess.DAO;
import ua.knure.fb2reader.Utils.PathListener;

public class SyncService extends Service {

    static final int REQUEST_LINK_TO_DBX = 0;
    private final String APP_KEY = "ygam033j049nurm";
    private final String APP_SECRET = "8jbe7gnyi4y9imt";
    private static final String URL = "https://fb2r-university.rhcloud.com/dropbox";

    private DbxFileSystem dbxFs;
    private DbxAccountManager mDbxAcctMgr;
    private DAO dao;

    private Timer tC;
    private TimerTask tt;

    private static String email;

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
        //startTimers();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(dao.dbIsOpen()) {
            dao.close();
        }
    }

    private void initialize() throws InvalidPathException, IOException {
        mDbxAcctMgr = DbxAccountManager.getInstance(getApplicationContext(),
                APP_KEY, APP_SECRET);
        dao = new DAO(getBaseContext());
        dao.open();
        try {
            Log.d("myLogs", "dbFx init");
            dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr.getLinkedAccount());
        } catch (Unauthorized e) {
            e.printStackTrace();
        }
        email = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext()).getString("email", null);
        PathListener.addBooksToDB(dao, email);
        tC = new Timer();
        startTimers();
        PathListener.downloadFiles(dbxFs, new DbxPath("/"));
        PathListener.uploadFiles(dbxFs);
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
                Log.d("myLogs", "tick");
                try {
                    PathListener.downloadFiles(dbxFs, new DbxPath("/"));
                    PathListener.uploadFiles(dbxFs);
                    new HttpAsyncTask().execute(URL);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };
    }

    public void postData(BookDAO book) {
        // Create a new HttpClient and Post Header
        InputStream inputStream = null;
        String result = "";
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(URL + "/" + ViewUtils.md5(email));
        Log.d("myLogs", "post Data");
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
            // receive response as inputStream
            inputStream = response.getEntity().getContent();

            // convert inputstream to string
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

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
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            //Log.d("myLogs", line);
        result += line;

        inputStream.close();
        return result;
    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            List<BookDAO> books = dao.getAllBooks(email);
            for(BookDAO book : books) {
                postData(book);
            }
            return GET(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(getBaseContext(), "Received!", Toast.LENGTH_SHORT).show();
            JSONObject json = null;
            try {
                json = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                JSONArray arr = json.getJSONArray("arr");
                for(int i = 0; i < arr.length(); i++) {
                    Log.d("myLogs", "book " + i + " = " + arr.get(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

