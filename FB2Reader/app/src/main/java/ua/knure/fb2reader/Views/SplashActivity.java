package ua.knure.fb2reader.Views;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.dropbox.sync.android.DbxAccountManager;

import ua.knure.fb2reader.R;
import ua.knure.fb2reader.Views.Activities.MainActivity;
import ua.knure.fb2reader.dropbox.DropboxAuth;

public class SplashActivity extends Activity {

    private final static String APP_KEY = "ygam033j049nurm";
    private final static String APP_SECRET = "8jbe7gnyi4y9imt";
    private static DbxAccountManager mDbxAcctMgr;
    private SharedPreferences sdPref;

    public static DbxAccountManager getmDbxAcctMgr() {
        return mDbxAcctMgr;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);


        mDbxAcctMgr = DbxAccountManager.getInstance(getApplicationContext(),
                APP_KEY, APP_SECRET);

        sdPref = PreferenceManager.getDefaultSharedPreferences(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!sdPref.getBoolean("isLinked", false)) {
                    startActivity(new Intent(getBaseContext(), DropboxAuth.class));
                    finish();
                } else {
                    startActivity(new Intent(getBaseContext(), MainActivity.class));
                    finish();
                }
            }
        }, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
