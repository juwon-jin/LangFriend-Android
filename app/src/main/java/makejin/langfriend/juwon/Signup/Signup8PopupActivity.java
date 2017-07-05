package makejin.langfriend.juwon.Signup;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import makejin.langfriend.R;
import makejin.langfriend.juwon.Model.GlobalResponse;
import makejin.langfriend.juwon.Model.User;
import makejin.langfriend.juwon.Sign.SignActivity;
import makejin.langfriend.juwon.TabActivity.TabActivity_;
import makejin.langfriend.juwon.Utils.Connections.CSConnection;
import makejin.langfriend.juwon.Utils.Connections.ServiceGenerator;
import makejin.langfriend.juwon.Utils.Constants.Constants;
import makejin.langfriend.juwon.Utils.Loadings.LoadingUtil;
import makejin.langfriend.juwon.Utils.SharedManager.SharedManager;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.facebook.FacebookSdk.getApplicationContext;
import static makejin.langfriend.juwon.Sign.SignActivity.context;
import static makejin.langfriend.juwon.Sign.SignActivity.isSignup;
import static makejin.langfriend.juwon.Splash.SplashActivity.cityName;
import static makejin.langfriend.juwon.Splash.SplashActivity.lat;
import static makejin.langfriend.juwon.Splash.SplashActivity.lon;


public class Signup8PopupActivity extends Activity {
    private Typeface yunGothicFont;
    private Button BT_resend;
    private Button BT_close;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    LinearLayout indicator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup8_popup);

        prefs = getApplicationContext().getSharedPreferences("LangFriend", Context.MODE_PRIVATE);
        editor = prefs.edit();

        yunGothicFont = Typeface.createFromAsset(getAssets(), "fonts/yungothic330.ttf");

        BT_resend = (Button) findViewById(R.id.BT_resend);
        BT_close = (Button) findViewById(R.id.BT_close);

        indicator = (LinearLayout) findViewById(R.id.indicator);

        BT_resend.setTypeface(yunGothicFont);
        BT_close.setTypeface(yunGothicFont);

        //final Intent intent = new Intent(getApplicationContext(), SignActivity.class);

        BT_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //이메일 다시 보내도록 요청 후(토스트메시지), 팝업 재출력.
                Log.i("zxc", prefs.getString("social_id",""));

                Map field = new HashMap();
                field.put("social_id",prefs.getString("social_id",""));
                connRequestVerification(field);


            }
        });

//        BT_success.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Map field = new HashMap();
//                field.put("social_id", prefs.getString("social_id",""));
//                field.put("password", prefs.getString("password",""));
//                Log.i("Zxc", "social_id : " + prefs.getString("social_id","") + " password : " + prefs.getString("password",""));
//                User.LocationPoint locationPoint = new User.LocationPoint(lat, lon);
//                field.put("location_point", locationPoint);
//                field.put("location", cityName);
//                field.put("app_version", getAppVersion(getApplicationContext()));
//                connectSigninUser_NonFacebook2(field);
//            }
//        });

        BT_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //로그인 화면으로 이동
                //startActivity(intent);
                finish();
            }
        });


    }



    void connRequestVerification(Map field) {
        Toast.makeText(getApplicationContext(), "인증 이메일을 다시 보내고있습니다. 잠시만 기다려주세요.", Toast.LENGTH_SHORT).show();
        LoadingUtil.startLoading(indicator);
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.requestVerification(field)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GlobalResponse>() {
                    @Override
                    public final void onCompleted() {
                        LoadingUtil.stopLoading(indicator);
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(GlobalResponse response) {
                        if (response.code == 0) {
                            Toast.makeText(getApplicationContext(), "인증 이메일 재전송에 성공했습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "인증 이메일 재전송에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public String getAppVersion(Context context) {

        // application version
        String versionName = "";
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {

        }

        return versionName;
    }

    void connectSigninUser_NonFacebook2(final Map field) {
        LoadingUtil.startLoading(indicator);
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.signinUser_NonFacebook(field)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public final void onCompleted() {
                        LoadingUtil.stopLoading(indicator);
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(User response) {
                        if (response != null) {
                            if(response.social_id == null){
                                Toast.makeText(getApplicationContext(), "ID 혹은 PW를 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if(!response.auth) {
                                Toast.makeText(getApplicationContext(), "등록하신 이메일로 가서, 본인 인증을 먼저 완료해주세요!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            Log.i("Zxc","Zxc");


                            Toast.makeText(getApplicationContext(), "이메일 인증이 성공적으로 완료되었습니다.", Toast.LENGTH_SHORT).show();
                            SharedManager.getInstance().setMe(response);

                            editor.putString("social_id", response.social_id);
                            editor.putString("password", response.password);
                            editor.commit();

                            finish();

                            Intent intent = new Intent(context, TabActivity_.class);
                            startActivity(intent);

                        } else {
                            Log.i("Zxc","2");
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
