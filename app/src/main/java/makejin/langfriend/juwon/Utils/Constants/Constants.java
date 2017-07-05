package makejin.langfriend.juwon.Utils.Constants;

import android.util.Log;

import java.text.DecimalFormat;
import java.util.Calendar;

import makejin.langfriend.juwon.Model.User;

/**
 * Created by kksd0900 on 16. 9. 29..
 */
public class Constants {
    public static final String API_BASE_URL = "http://52.196.116.15:8888";
    public static final String IMAGE_BASE_URL = API_BASE_URL+"/images/";

    public static final String ERROR_MSG = "예상치 못한 에러가 발생했습니다 :(\n문제가 지속될 경우 앱을 재실행해주세요.";

    public static final int ACTIVITY_CODE_TAB2_REFRESH_REQUEST = 1111;
    public static final int ACTIVITY_CODE_TAB2_REFRESH_RESULT = 1112;
    public static final int ACTIVITY_CODE_TAB5_REFRESH_REQUEST = 1113;
    public static final int ACTIVITY_CODE_TAB5_REFRESH_RESULT = 1114;
    public static final int ACTIVITY_CODE_TAB5_PROFILE_DETAIL_REFRESH_REQUEST = 1115;
    public static final int ACTIVITY_CODE_TAB5_PROFILE_DETAIL_REFRESH_RESULT = 1116;




    public static double CalculationByDistance(User.LocationPoint StartP, User.LocationPoint EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.lat;
        double lat2 = EndP.lat;
        double lon1 = StartP.lon;
        double lon2 = EndP.lon;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        double distance = (Radius * c);
        return Math.round(distance*100d) / 100d;
    }

    public static String getAgeFromBirthday(String birthday){ //birthday 형식 : 1994-02-22
        if(birthday.equals(""))
            return "0";

        int year = Integer.parseInt(birthday.substring(0,4));
        int month = Integer.parseInt(birthday.substring(5,7));
        int day = Integer.parseInt(birthday.substring(8,10));

        Log.i("age", year + " " + month + " " + day);

        return getAge(year, month, day);
    }


    private static String getAge(int mYear, int mMonth, int mDay){
        int year;
        int month;
        int day = mDay;
        if(mMonth==1) {
            year = mYear - 1;
            month = 12;
        }
        else {
            year = mYear;
            month = mMonth - 1;
        }

        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);


        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        Log.i("asd", "today " + today.getTime().toString() + " / dob : " + dob.getTime().toString());
        Log.i("asd", "today " + today.get(Calendar.DAY_OF_YEAR) + " / dob : " + dob.get(Calendar.DAY_OF_YEAR));
        if (today.get(Calendar.DAY_OF_YEAR) >= dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }


//    public static final String mockMyFriendText(int index) {
//        int idx = index;
//        while (mockMyFriends.length <= idx) {
//            idx -= mockMyFriends.length;
//        }
//        return mockMyFriends[idx];
//    }
//    public static final String[] mockMyFriends = {
//            "Jung-Ho Seo, 홍미정", "", "권선복", "원응호", "양수길", "", "김태춘", "김태용",
//            "권민준", "Eun Ji Son", "이미정", "", "이선향", "정지윤", "박서준", "이기성", "사고몽킼",
//            "여효주", "", "이지민", "윤이나", "박진영", "이현일, 박수진", "", "Eun-ji Park",
//            "Hansu Kim", "박리세윤", "김이삭", "안성미", "신한재", "윤현웅", "홍주"};
}
