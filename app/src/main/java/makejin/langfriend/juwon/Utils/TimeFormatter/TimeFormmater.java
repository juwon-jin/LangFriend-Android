package makejin.langfriend.juwon.Utils.TimeFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by kksd0900 on 16. 10. 16..
 */
public class TimeFormmater {
    public static String getCurrentTime_UTC() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date());
    }
    public static String getTimeFromUTCToLocal(String time) {
        String formatted = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf2.setTimeZone(TimeZone.getDefault());
        try {
            Date date = sdf.parse(time);
            formatted = sdf2.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return formatted;
    }
}

