package makejin.langfriend.juwon.Sign;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Locale;

import makejin.langfriend.juwon.Model.GlobalResponse;
import makejin.langfriend.juwon.Model.User;
import makejin.langfriend.R;
import makejin.langfriend.juwon.Signup.SignupActivity;
import makejin.langfriend.juwon.Splash.SplashActivity;
import makejin.langfriend.juwon.TabActivity.TabActivity_;
import makejin.langfriend.juwon.Utils.Connections.CSConnection;
import makejin.langfriend.juwon.Utils.Connections.ServiceGenerator;
import makejin.langfriend.juwon.Utils.SharedManager.SharedManager;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.facebook.FacebookSdk.getApplicationContext;
import static makejin.langfriend.juwon.Splash.SplashActivity.lat;
import static makejin.langfriend.juwon.Splash.SplashActivity.lon;


public class SignupNonFacebookFragment extends Fragment {
    SharedPreferences prefs;
    EditText ET_email, ET_name, ET_pw, ET_pw2;
    Button BT_signup;
    private RadioButton RB_gender_male;
    private RadioButton RB_gender_female;
    String email;

    public User n_user = new User();


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_non_facebook, container, false);

        ET_email = (EditText)view.findViewById(R.id.ET_email);
        ET_name = (EditText)view.findViewById(R.id.ET_name);
        ET_pw = (EditText)view.findViewById(R.id.ET_pw);
        ET_pw2 = (EditText)view.findViewById(R.id.ET_pw2);

        BT_signup = (Button)view.findViewById(R.id.BT_signup);

        RB_gender_male = (RadioButton) view.findViewById(R.id.RB_gender_male);
        RB_gender_female = (RadioButton) view.findViewById(R.id.RB_gender_female);

        BT_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tempEmail = ET_email.getText().toString();
                String tempName = ET_name.getText().toString();
                String tempPw = ET_pw.getText().toString();
                String tempPw2 = ET_pw2.getText().toString();
                boolean tempGender = false;

                if(!isEmpty(ET_email) && ET_email.getText().toString().contains("@")){
                    if(tempName.equals("") || tempPw.equals("") || tempPw2.equals("") || tempName.equals("") || (!RB_gender_male.isChecked() && !RB_gender_female.isChecked()) ){
                        Toast toast = Toast.makeText(getActivity(), "Write your information all", Toast.LENGTH_SHORT);
                        int offsetX = 0;
                        int offsetY = 0;
                        toast.setGravity(Gravity.CENTER, offsetX, 300);
                        toast.show();
                        return;
                    }

                    if(tempPw.equals(tempPw2)) {
                        n_user.social_id = tempEmail;
                        n_user.password = tempPw;
                        n_user.app_version = getAppVersion(getActivity());
                        n_user.nickname = tempName;
                        n_user.location_point = new User.LocationPoint(lat,lon);
                        n_user.location = SplashActivity.cityName;
                        if(RB_gender_female.isChecked()){
                            tempGender = true;
                        }
                        n_user.gender = tempGender;
                        n_user.social_type = "normal";
                        n_user.friends = null;
                        n_user.device_type = "android";
                        Locale locale = getResources().getConfiguration().locale;
                        n_user.lang_type = locale.getLanguage(); // kr , en ...
                        n_user.auth = false;
                        conncheckSocialID(tempEmail);

//                        n_user.about_me = "자기소개 글을 입력해주세요";
//                        n_user.birthday = "";
//                        n_user.languages = new ArrayList<>();
//
//                        n_user.push_token = "random";

                        //connectCreateUser(n_user);
                    }else{
                        Toast toast = Toast.makeText(getActivity(), "incorrect 1st PW and 2nd PW", Toast.LENGTH_SHORT);
                        int offsetX = 0;
                        int offsetY = 0;
                        toast.setGravity(Gravity.CENTER, offsetX, 300);
                        toast.show();
                        return;
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "올바른 메일 주소를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    // 아이디 비어있는지 검사
    boolean isEmpty(EditText ET)
    {
        // 공백문자 제거
        String str =ET.getText().toString().replaceAll("\\p{Z}", "");
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
    void conncheckSocialID(String social_id) {
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.checkSocialID(social_id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GlobalResponse>() {
                    @Override
                    public final void onCompleted() {
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "이미 등록된 ID(Email)입니다.", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(GlobalResponse response) {
                        if (response.code == 0) {
                            Intent intent = new Intent(getActivity(), SignupActivity.class);
                            intent.putExtra("user", n_user);
                            startActivity(intent);
                            getActivity().finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "이미 등록된 ID(Email)입니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}


