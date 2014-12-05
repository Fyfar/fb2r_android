package ua.knure.fb2reader.Utils;

import java.lang.reflect.Field;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.dropbox.sync.android.DbxAccountInfo;

public class AccountInfo {
    public static JSONObject getJson(DbxAccountInfo info) {
        Class<?> accountClass;
        Field rawJson = null;
        try {
            accountClass = Class.forName(info.getClass().getName());
            rawJson = accountClass.getDeclaredField("rawJson");
        } catch (ClassNotFoundException e1) {
            Log.d("myLogs", e1.getMessage() + " exception");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        rawJson.setAccessible(true);
        JSONObject json = null;
        try {
            json = new JSONObject((String)rawJson.get(info));
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
        return json;
    }
}