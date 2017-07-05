package makejin.langfriend.juwon.Sign;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import makejin.langfriend.R;
import makejin.langfriend.juwon.Model.User;
import makejin.langfriend.juwon.Splash.SplashActivity;
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
import static makejin.langfriend.juwon.Splash.SplashActivity.cityName;
import static makejin.langfriend.juwon.Splash.SplashActivity.lat;
import static makejin.langfriend.juwon.Splash.SplashActivity.lon;


public class SignNonFacebook1Fragment extends Fragment {
    EditText ET_email, ET_pw;
    Button BT_signin, BT_signup;

    Map field = new HashMap();


    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    LinearLayout indicator;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_non_facebook1, container, false);

        prefs = getActivity().getSharedPreferences("LangFriend", Context.MODE_PRIVATE);
        editor = prefs.edit();

        ET_email = (EditText)view.findViewById(R.id.ET_email);
        ET_pw = (EditText)view.findViewById(R.id.ET_pw);

        BT_signin = (Button) view.findViewById(R.id.BT_signin);
        BT_signup = (Button) view.findViewById(R.id.BT_signup);

        indicator = (LinearLayout) view.findViewById(R.id.indicator);

        BT_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment reg = new SignupNonFacebookFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.activity_sign, reg);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();
            }
        });


        BT_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tempEmail = ET_email.getText().toString();
                String tempPassword = ET_pw.getText().toString();

                if(isEmpty(tempEmail)|| isEmpty(tempPassword)){
                    Toast toast = Toast.makeText(getActivity(), "Write your E-mail and password", Toast.LENGTH_SHORT);
                    int offsetX = 0;
                    int offsetY = 0;
                    toast.setGravity(Gravity.CENTER, offsetX, offsetY);
                    toast.show();
                    return;
                }



                field = new HashMap();
                field.put("social_id", tempEmail);
                field.put("password", tempPassword);
                User.LocationPoint locationPoint = new User.LocationPoint(lat, lon);
                field.put("location_point", locationPoint);
                field.put("location", cityName);
                field.put("app_version", getAppVersion(getActivity()));
                connectSigninUser_NonFacebook(field);
            }
        });

        return view;
    }

    // 아이디 비어있는지 검사
    boolean isEmpty(String temp)
    {
        // 공백문자 제거
        String str = temp.replaceAll("\\p{Z}", "");
        return str.equals("");
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

    void connectSigninUser_NonFacebook(final Map field) {
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
                                Toast.makeText(getActivity(), "ID 혹은 PW를 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if(!response.auth) {
                                Toast.makeText(getActivity(), "등록하신 이메일로 가서, 본인 인증을 먼저 완료해주세요!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            SharedManager.getInstance().setMe(response);

                            editor.putString("social_id", response.social_id);
                            editor.putString("password", response.password);
                            editor.putString("_id", response._id);
                            editor.commit();
                            Intent intent = new Intent(getActivity(), TabActivity_.class);
                            startActivity(intent);
                            getActivity().finish();
                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}