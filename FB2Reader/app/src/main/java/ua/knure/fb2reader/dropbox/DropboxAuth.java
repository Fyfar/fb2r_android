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

import ua.knure.fb2reader.R;
import ua.knure.fb2reader.Views.Activities.MainActivity;
import ua.knure.fb2reader.DataAccess.DAO;
import ua.knure.fb2reader.Utils.AccountInfo;

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
        dao = new DAO(this);
        dao.open();
        mDbxAcctMgr = DbxAccountManager.getInstance(getApplicationContext(),
                APP_KEY, APP_SECRET);
        /*if (mDbxAcctMgr.hasLinkedAccount()) {

            DbxAccountInfo info = mDbxAcctMgr.getLinkedAccount().getAccountInfo();
            try {
                setResult(1, new Intent().putExtra("email",
                        AccountInfo.getJson(info).getString("email")));
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }*/
        // if (!mDbxAcctMgr.hasLinkedAccount()) {
        //  Log.d("myLogs", "linked account = false");
        //mDbxAcctMgr.unlink();
        //  mDbxAcctMgr.startLink(this, REQUEST_LINK_TO_DBX);
        // Log.d("myLogs", mDbxAcctMgr.getLinkedAccount().getAccountInfo().toString());
        //}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_get_book_settings, menu);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LINK_TO_DBX) {
            if (resultCode == Activity.RESULT_OK) {
                // ... Start using Dropbox files.
                SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(this).edit();
                ed.putBoolean("isLinked", true);
                ed.commit();
                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DbxAccount account = mDbxAcctMgr.getLinkedAccount();
        //Log.d("myLogs", account.getUserId());
        DbxAccountInfo info = account.getAccountInfo();

        try {
            String email = AccountInfo.getJson(info).getString("email");
            dao.addUser(email,
                    AccountInfo.getJson(info).getString("user_name"), null);
            SharedPreferences.Editor ed = PreferenceManager
                    .getDefaultSharedPreferences(getBaseContext()).edit();
            ed.putString("email", email);
            ed.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        startActivity(new Intent(getBaseContext(), MainActivity.class));
        dao.close();
    }

    public void onClickLinkToDropbox(View view) {
        if (!mDbxAcctMgr.hasLinkedAccount()) {
            mDbxAcctMgr.startLink(this, REQUEST_LINK_TO_DBX);
        }
    }

  /*  public static boolean isLinked(Context context) {
        mDbxAcctMgr = DbxAccountManager.getInstance(this,
                APP_KEY, APP_SECRET);
        if (mDbxAcctMgr.hasLinkedAccount()) {
            return true;
        }
        return false;
    }*/

}
