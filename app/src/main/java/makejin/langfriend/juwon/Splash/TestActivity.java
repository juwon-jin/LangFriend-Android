package makejin.langfriend.juwon.Splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import makejin.langfriend.R;
import makejin.langfriend.juwon.Model.User;
import makejin.langfriend.juwon.Sign.SignActivity;
import makejin.langfriend.juwon.Utils.Connections.CSConnection;
import makejin.langfriend.juwon.Utils.Connections.ServiceGenerator;
import makejin.langfriend.juwon.Utils.Constants.Constants;
import makejin.langfriend.juwon.Utils.Loadings.LoadingUtil;
import makejin.langfriend.juwon.Utils.SharedManager.SharedManager;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.facebook.FacebookSdk.getApplicationContext;
import static makejin.langfriend.juwon.Sign.SignActivity.isSignup;

public class TestActivity extends AppCompatActivity {

    Button BT_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        BT_button = (Button) findViewById(R.id.BT_button);

        BT_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User();
                user.social_id = "makejin0222@gmail.com";
                user._id="";
                user.update_date="";
                user.create_date="";
                user.social_type="";//normal or facebook
                user.social_id="";//**email
                user.session_key="";
                user.session_expire_date="";
                user.push_token="";
                user.push_use=true;//회원가입 후, 팝업으로 동의구하기
                user.device_type="";
                user.app_version="";
                user.access_ip="";//타입 Date?
                user.access_last_date="";
                user.login_last_date="";
                user.access_cnt=0;
                user.login_cnt=0;
                user.report_cnt=0;
                user.pic_list= new ArrayList<>();
                user.pic_small="";
                user.nickname="";
                user.about_me="";
                user.birthday="";
                user.gender=false;
                user.location="";
                user.language=new ArrayList<>();
                user.activity="";
                user.password="";
                user.friends=new ArrayList<>();
                user.friends_NonFacebook=new ArrayList<>();
                user.friends_NonFacebook_Waiting=new ArrayList<>();
                user.friends_NonFacebook_Rejected=new ArrayList<>();
                user.friends_NonFacebook_Requested=new ArrayList<>();
                user.choice_cnt=0;
                user.choice_last_date="";
                user.country="";
                user.languages = new ArrayList<>(); //내가 관심있는 언어
                user.lang_type=""; //사용자가 앱에서 쓸 언어 선택. 일단은 한국어랑 영어만
                user.location_point=new User.LocationPoint(0,0);
                user.auth=false;


                //connectCreateUser(user);
            }
        });
    }
//    void connectCreateUser(User user) {
//        LoadingUtil.startLoading(SignActivity.indicator);
//        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
//        Log.i("zxc", "user.social_id : " + user.social_id);
//        Log.i("zxc", "connectCreateUser : " + user);
//        user.social_id = "makejin0222@gmail.com";
//        Log.i("zxc", "user.social_id2 : " + user.social_id);
//        conn.signupUser(user)
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<User>() {
//                    @Override
//                    public final void onCompleted() {
//                        LoadingUtil.startLoading(SignActivity.indicator);
//                    }
//                    @Override
//                    public final void onError(Throwable e) {
//                        e.printStackTrace();
//                    }
//                    @Override
//                    public final void onNext(User response) {
//                        if (response != null) {
//                            SharedManager.getInstance().setMe(response);
//
////                            for(int i=0;i<response.pic_list.size();i++) {
////                                //Log.i("makejin", "response.pic_list.get(" +i +") : " + response.pic_list.get(i));
////                                //uploadFile1(response, i);
////                            }
//
//                        } else {
//                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }

}

