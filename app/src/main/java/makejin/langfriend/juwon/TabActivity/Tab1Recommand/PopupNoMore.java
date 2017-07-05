package makejin.langfriend.juwon.TabActivity.Tab1Recommand;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.sql.Date;

import makejin.langfriend.R;
import makejin.langfriend.juwon.Model.GlobalResponse;
import makejin.langfriend.juwon.Model.User;
import makejin.langfriend.juwon.Signup.SignupActivity;
import makejin.langfriend.juwon.Utils.Connections.CSConnection;
import makejin.langfriend.juwon.Utils.Connections.ServiceGenerator;
import makejin.langfriend.juwon.Utils.Constants.Constants;
import makejin.langfriend.juwon.Utils.SharedManager.PreferenceManager;
import makejin.langfriend.juwon.Utils.SharedManager.SharedManager;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.facebook.FacebookSdk.getApplicationContext;


public class PopupNoMore extends Activity {

    Button BT_yes;
    TextView TV_finish_2;
    Button BT_push;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_no_more);

        BT_yes = (Button) findViewById(R.id.BT_yes);
        TV_finish_2 = (TextView) findViewById(R.id.TV_finish_2);
        BT_push = (Button) findViewById(R.id.BT_push);

        connCheckMatchingTime(SharedManager.getInstance().getMe()._id);

        BT_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (!PreferenceManager.getInstance(getApplicationContext()).getPush()) {
            BT_push.setVisibility(View.VISIBLE);
            BT_push.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseMessaging.getInstance().subscribeToTopic("push_on");
                    PreferenceManager.getInstance(getApplicationContext()).setPush(true);
                    Toast.makeText(getApplicationContext(), "좋아요 남은 시간이 다 지나면 알려드릴게요!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }


    //checkMatchingTime
    void connCheckMatchingTime(String user_id) {
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.checkMatchingTime(user_id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GlobalResponse>() {
                    @Override
                    public final void onCompleted() {

                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(GlobalResponse response) {
                        if (response != null) {
                            String finalTime = response.message;
                            TV_finish_2.setText(finalTime);
                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
