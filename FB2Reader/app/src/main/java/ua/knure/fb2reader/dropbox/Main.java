package ua.knure.fb2reader.dropbox;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.dropbox.sync.android.DbxAccountInfo;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException.Unauthorized;
import com.dropbox.sync.android.DbxFileSystem;

import org.json.JSONException;

import ua.knure.fb2reader.R;
import ua.knure.fb2reader.Views.GetBookSettingsActivity;
import ua.knure.fb2reader.dba.DAO;
import ua.knure.fb2reader.util.AccountInfo;

public class Main extends Activity {
    static final int REQUEST_LINK_TO_DBX = 1;
    private final static String APP_KEY = "ygam033j049nurm";
    private final static String APP_SECRET = "8jbe7gnyi4y9imt";

    private DbxFileSystem dbxFs;

    private DbxAccountManager mDbxAcctMgr;

    private DAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        dao = new DAO(this);
        dao.open();
        mDbxAcctMgr = DbxAccountManager.getInstance(getApplicationContext(),
                APP_KEY, APP_SECRET);
        /*Intent intent = new Intent(this, SyncService.class);
		if (mDbxAcctMgr.hasLinkedAccount()) {
			startService(intent);
		}*/
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
                try {
                    Log.d("myLogs",
                            Boolean.toString(mDbxAcctMgr.hasLinkedAccount()));
                    dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr
                            .getLinkedAccount());
                    Intent intent = new Intent(this, GetBookSettingsActivity.class);
                    startActivity(intent);
                    DbxAccountInfo info = mDbxAcctMgr.getLinkedAccount().getAccountInfo();
                    dao.addUser(AccountInfo.getJson(info).getString("email"), AccountInfo.getJson(info).getString("user_name"), null);
                    finish();
                    System.exit(0);
                    //setContentView(R.layout.activity_main);
                } catch (Unauthorized | JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, SyncService.class));
        dao.close();
    }

    public void onClickLinkToDropbox(View view) {
        if (!mDbxAcctMgr.hasLinkedAccount()) {
            mDbxAcctMgr.startLink((Activity) this, REQUEST_LINK_TO_DBX);
        }
        Intent intent = new Intent(this, SyncService.class);
        startService(intent);
        intent = new Intent(this, GetBookSettingsActivity.class);
        startActivity(intent);
        finish();
        System.exit(0);
        //setContentView(R.layout.activity_main);
    }

    public void downloadFiles(View v) {
        DbxAccountInfo info = mDbxAcctMgr.getLinkedAccount().getAccountInfo();
        try {
            Log.d("myLogs", AccountInfo.getJson(info).getString("email"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
