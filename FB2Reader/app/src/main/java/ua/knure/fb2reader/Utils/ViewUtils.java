package ua.knure.fb2reader.Utils;

import android.graphics.Paint;
import android.util.Log;
import android.widget.TextView;

import com.dropbox.sync.android.DbxAccountInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Александр on 30.11.2014.
 */
public class ViewUtils {
    /*
    * Метод нужен для того что бы в октрытом! текствью получить количество
    * символов в строчке
    * */
    public static int getNumberOfCharsPerLine(TextView view) {
        if (view == null) {
            return 0;
        }
        String text = "This string is using for calculate line width value in text view";
        int textViewWidth = view.getWidth();
        int charCount;

        Paint paint = view.getPaint();
        for (charCount = 1; charCount <= text.length(); ++charCount) {
            if (paint.measureText(text, 0, charCount) > textViewWidth) {
                break;
            }
        }
        return charCount;
    }

    /*
    * Метод нужен для того что бы в октрытом! текствью получить количество
    * строк в экране
    * */
    public static int getNumberOfLinesPerScreen(TextView view) {
        if (view == null) {
            return 0;
        }
        int linesPerScreen = view.getHeight() / (view.getLineHeight() + (int) view.getLineSpacingExtra());
        return linesPerScreen;
    }

    public static String md5 (String input) {
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        m.reset();
        m.update(input.getBytes());
        byte[] digest = m.digest();
        BigInteger bigInt = new BigInteger(1,digest);
        String hashtext = bigInt.toString(16);
        while(hashtext.length() < 32 ){
            hashtext = "0"+hashtext;
        }
        return hashtext;
    }

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
