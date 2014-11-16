package ua.knure.fb2reader.DataAccess;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

/**
 * Created by Александр on 16.11.2014.
 */
public class ImageUtils {

    public static Bitmap decodeToImage(byte[] imageString) {

        Bitmap image = null;

        try {
            byte[] img = Base64.decode(imageString, 0, imageString.length, Base64.DEFAULT);
            image = BitmapFactory.decodeByteArray(img, 0, img.length);
            //ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            //image = ImageIO.read(bis);
            //bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }
}
