package makejin.langfriend.juwon.FCM;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import makejin.langfriend.R;
import makejin.langfriend.juwon.Splash.SplashActivity;

/**
 * Created by mijeong on 2016. 11. 15..
 */
public class MyFcmListenerService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage message) {


        RemoteMessage.Notification dd = message.getNotification();
        String title = dd.getTitle();
        String msg = dd.getBody();

//        Map<String, String> data = message.getData();
//        if (data.containsKey("click_action")) {
//            Log.i("zxc", "data.get(\"click_action\") : " + data.get("click_action"));
//            Log.i("zxc", "context : " + this);
//
//            ClickActionHelper.startActivity(data.get("click_action"), null, this);
//        }

//        String from = message.getFrom();
//        Map<String, String> data = message.getData();
//        String title = data.get("title");
//        String msg = data.get("message");
//        if (data.containsKey("click_action")) {
//            ClickActionHelper.startActivity(data.get("click_action"), null, this);
//        }

//        Map<String, String> data = message.getData();
//        String title = data.get("title");
//        String msg = data.get("message");
//        if (data.containsKey("click_action")) {
//            ClickActionHelper.startActivity(data.get("click_action"), null, this);
//        }

//        RemoteMessage.Notification dd = message.getNotification();
//        Log.i("zxc","dd : " + dd.toString());

//        String title = dd.getTitle();
//        String msg = dd.getBody();

        sendNotification(title, msg);

    }

    /**
     * 실제 디바에스에 GCM으로부터 받은 메세지를 알려주는 함수이다. 디바이스 Notification Center에 나타난다.
     * @param title
     * @param message
     */
    private void sendNotification(String title, String message) {
        Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
/*
public class MyFcmListenerService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        // 이 token을 서버에 전달 한다.
    }
}
*/