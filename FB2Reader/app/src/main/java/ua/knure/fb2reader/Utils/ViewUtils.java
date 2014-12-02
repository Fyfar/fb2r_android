package ua.knure.fb2reader.Utils;

import android.graphics.Paint;
import android.widget.TextView;

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
}
