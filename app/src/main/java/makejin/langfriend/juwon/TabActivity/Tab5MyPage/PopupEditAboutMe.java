package makejin.langfriend.juwon.TabActivity.Tab5MyPage;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import makejin.langfriend.juwon.Model.User;
import makejin.langfriend.R;
import makejin.langfriend.juwon.Utils.Connections.CSConnection;
import makejin.langfriend.juwon.Utils.Connections.ServiceGenerator;
import makejin.langfriend.juwon.Utils.Constants.Constants;
import makejin.langfriend.juwon.Utils.SharedManager.SharedManager;

import java.util.HashMap;
import java.util.Map;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PopupEditAboutMe extends Activity {

    Button BT_yes;
    Button BT_no;
    EditText ET_about_me;

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_edit_about_me);

        prefs = getSharedPreferences("LangFriend", MODE_PRIVATE);

        BT_yes = (Button) findViewById(R.id.BT_yes);
        BT_no = (Button) findViewById(R.id.BT_no);
        ET_about_me = (EditText) findViewById(R.id.ET_about_me);
        BT_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        BT_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ET_about_me.getText()
                Map field = new HashMap();
                field.put("about_me", ET_about_me.getText().toString());
                connectUpdateUserAboutme(SharedManager.getInstance().getMe()._id, field);
            }
        });
    }

    void connectUpdateUserAboutme(final String user_id, final Map field) {
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.updateAboutme(user_id, field)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public final void onCompleted() {
                        Tab5MyPageFragment.TV_about_me.setText((String)field.get("about_me"));
                        finish();
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(User response) {
                        if (response != null) {
                            Log.i("makejin", "response "+response);
                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}

