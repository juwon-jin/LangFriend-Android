//package makejin.langfriend.juwon.TabActivity.Tab5MyPage;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.Toast;
//
//import com.facebook.login.LoginManager;
//import makejin.langfriend.juwon.Model.User;
//import makejin.langfriend.R;
//import makejin.langfriend.juwon.Sign.SignActivity;
//import makejin.langfriend.juwon.Utils.Connections.CSConnection;
//import makejin.langfriend.juwon.Utils.Connections.ServiceGenerator;
//import makejin.langfriend.juwon.Utils.Constants.Constants;
//import makejin.langfriend.juwon.Utils.SharedManager.SharedManager;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import rx.Subscriber;
//import rx.android.schedulers.AndroidSchedulers;
//import rx.schedulers.Schedulers;
//
//
//public class PopupWithdrawal extends Activity {
//
//    SharedPreferences prefs;
//    SharedPreferences.Editor editor;
//    Button BT_yes;
//    Button BT_no;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_popup_withdrawal);
//
//        prefs = getSharedPreferences("TodayFood", MODE_PRIVATE);
//        editor = prefs.edit();
//
//        BT_yes = (Button) findViewById(R.id.BT_yes);
//        BT_no = (Button) findViewById(R.id.BT_no);
//
//        BT_no.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//        BT_yes.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Setting.isWithdrawal = true;
//                withdrawal();
//                finish();
//            }
//        });
//    }
//
//
//    void withdrawal(){
//        Map field = new HashMap();
//        field.put("user_id", SharedManager.getInstance().getMe()._id);
//        connectWithdrawalUser(field);
//    }
//
//    void connectWithdrawalUser(final Map field) {
//        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
//        conn.withdrawalUser(field)
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<User>() {
//                    @Override
//                    public final void onCompleted() {
//                    }
//                    @Override
//                    public final void onError(Throwable e) {
//                        e.printStackTrace();
//                        Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
//                    }
//                    @Override
//                    public final void onNext(User response) {
//                        if (response != null) {
//                            editor.clear();
//                            editor.commit();
//                            LoginManager.getInstance().logOut();
//                            finish();
//                            startActivity(new Intent(getApplicationContext(), SignActivity.class));
//                        } else {
//                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }
//}
