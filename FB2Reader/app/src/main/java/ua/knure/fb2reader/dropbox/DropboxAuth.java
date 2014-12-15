package ua.knure.fb2reader.dropbox;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;

import com.dropbox.sync.android.DbxAccount;
import com.dropbox.sync.android.DbxAccountInfo;
import com.dropbox.sync.android.DbxAccountManager;

import org.json.JSONException;

import ua.knure.fb2reader.DataAccess.DAO;
import ua.knure.fb2reader.R;
import ua.knure.fb2reader.Utils.ViewUtils;
import ua.knure.fb2reader.Views.Activities.MainActivity;
import ua.knure.fb2reader.Views.SplashActivity;

public class DropboxAuth extends Activity {
    static final int REQUEST_LINK_TO_DBX = 1;
    private final static String APP_KEY = "ygam033j049nurm";
    private final static String APP_SECRET = "8jbe7gnyi4y9imt";

    private static DbxAccountManager mDbxAcctMgr;

    private DAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        dao = DAO.getInstance(this);
        dao.open();
        mDbxAcctMgr = SplashActivity.getmDbxAcctMgr();
        if (!mDbxAcctMgr.hasLinkedAccount()) {
            mDbxAcctMgr.startLink(this, REQUEST_LINK_TO_DBX);
        } else {
            startActivity(new Intent(getBaseContext(), MainActivity.class));
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_get_book_settings, menu);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (requestCode == REQUEST_LINK_TO_DBX) {
            if (resultCode == Activity.RESULT_OK) {
                SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(this).edit();
                ed.putBoolean("isLinked", true);
                ed.commit();
                DbxAccount account = mDbxAcctMgr.getLinkedAccount();
                DbxAccountInfo info = account.getAccountInfo();
                try {
                    String email = ViewUtils.getJson(info).getString("email");
                    dao.addUser(email,
                            ViewUtils.getJson(info).getString("user_name"), null);
                    ed = PreferenceManager
                            .getDefaultSharedPreferences(getBaseContext()).edit();
                    ed.putString("email", email);
                    ed.commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(new Intent(getBaseContext(), MainActivity.class));
                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onClickLinkToDropbox(View view) {
        if (!mDbxAcctMgr.hasLinkedAccount()) {
            mDbxAcctMgr.startLink(this, REQUEST_LINK_TO_DBX);
        }
    }
}
