package sample.util;

import java.text.SimpleDateFormat;

public class DateFormatUtil {

    public static String format(long timestamp, String pattern){
        return new SimpleDateFormat(pattern).format(timestamp);
    }
}
