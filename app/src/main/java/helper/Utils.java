package helper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/** Utility functions */

public class Utils {
    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String formattedDate = sdf.format(date.getTime());
        return "Due Date: " + formattedDate;
    }
}

