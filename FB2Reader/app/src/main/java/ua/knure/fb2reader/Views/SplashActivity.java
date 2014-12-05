package ua.knure.fb2reader.Views;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import ua.knure.fb2reader.R;
import ua.knure.fb2reader.Views.Activities.MainActivity;
import ua.knure.fb2reader.dropbox.DropboxAuth;

/**
 * Created by evilcorp on 20.11.14.
 */
public class SplashActivity extends Activity {

    private SharedPreferences sdPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);

        sdPref = PreferenceManager.getDefaultSharedPreferences(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!sdPref.getBoolean("isLinked", false)) {
                    startActivity(new Intent(getBaseContext(), DropboxAuth.class));
                    //startActivity(new Intent(getBaseContext(), GetBookSettingsActivity.class));
                    //finish();
                } else {
                    startActivity(new Intent(getBaseContext(), MainActivity.class));
                    finish();
                }
            }
        }, 2000);
    }
}
