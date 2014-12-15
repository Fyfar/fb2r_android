package ua.knure.fb2reader.DataAccess;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class ImageUtils {

    public static Bitmap decodeStringToImage(String imageString) {
        Bitmap image = null;
        try {
            byte[] img = Base64.decode(imageString, Base64.DEFAULT);
            image = BitmapFactory.decodeByteArray(img, 0, img.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }
}
