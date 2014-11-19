package ua.knure.fb2reader.dropbox;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxException.Unauthorized;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;
import com.dropbox.sync.android.DbxPath.InvalidPathException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

import ua.knure.fb2reader.util.PathListener;

public class SyncService extends Service {

    static final int REQUEST_LINK_TO_DBX = 0;
    private final String APP_KEY = "ygam033j049nurm";
    private final String APP_SECRET = "8jbe7gnyi4y9imt";
    private static final String URL = "https://fb2r-university.rhcloud.com/dropbox";

    private DbxFileSystem dbxFs;
    private DbxAccountManager mDbxAcctMgr;

    private Timer tC;
    private TimerTask tt;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            initialize();
        } catch (DbxException e) {
            e.printStackTrace();
        } catch (InvalidPathException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //startTimers();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initialize() throws InvalidPathException, IOException {
        mDbxAcctMgr = DbxAccountManager.getInstance(getApplicationContext(),
                APP_KEY, APP_SECRET);
        try {
            Log.d("myLogs", "dbFx init");
            dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr.getLinkedAccount());
        } catch (Unauthorized e) {
            e.printStackTrace();
        }
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
            tC.schedule(tt, 100, 10000);
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

    public static String GET(String url) {
        InputStream inputStream = null;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

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

            return GET(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(getBaseContext(), "Received!", Toast.LENGTH_SHORT).show();
            JSONArray json = null;
            try {
                json = new JSONArray(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                Log.d("myLogs", "result = " + json.getJSONObject(0));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //Toast.makeText(getBaseContext(), "Received!", Toast.LENGTH_LONG).show();
    }
}

