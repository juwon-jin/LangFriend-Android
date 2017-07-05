package makejin.langfriend.juwon.Sign;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.iid.FirebaseInstanceId;

import makejin.langfriend.juwon.Model.User;
import makejin.langfriend.R;
import makejin.langfriend.juwon.Signup.SignupActivity;
import makejin.langfriend.juwon.Splash.SplashActivity;
import makejin.langfriend.juwon.TabActivity.TabActivity_;
import makejin.langfriend.juwon.Utils.Connections.CSConnection;
import makejin.langfriend.juwon.Utils.Connections.ServiceGenerator;
import makejin.langfriend.juwon.Utils.Constants.Constants;
import makejin.langfriend.juwon.Utils.Loadings.LoadingUtil;
import makejin.langfriend.juwon.Utils.SharedManager.PreferenceManager;
import makejin.langfriend.juwon.Utils.SharedManager.SharedManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.facebook.FacebookSdk.getApplicationContext;
import static makejin.langfriend.juwon.Sign.SignActivity.context;
import static makejin.langfriend.juwon.Sign.SignActivity.isSignupType;
import static makejin.langfriend.juwon.Splash.SplashActivity.cityName;
import static makejin.langfriend.juwon.Splash.SplashActivity.lat;
import static makejin.langfriend.juwon.Splash.SplashActivity.lon;

public class SignFragment extends Fragment {
    CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;
    ProfileTracker profileTracker;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    Button BT_facebook_no;

    User n_user;
    Map field = new HashMap();

    List<String> pic_list = new ArrayList<>();

    LinearLayout indicator;

    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(final LoginResult loginResult) {
            // 정보 받아오는 graph api
            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {

                            /*
                            field.put("social_id", object.optString("id"));
                            try{
                                JSONArray jsonArray = response.getJSONObject().getJSONObject("friends").getJSONArray("data");
                                int len = jsonArray.length();
                                List<User.Friends> list = new ArrayList<>();
                                for(int idx=0;idx<len;idx++) {
                                    list.add(0, n_user.newFriend(jsonArray.getJSONObject(idx).get("id").toString(), jsonArray.getJSONObject(idx).get("name").toString(), "http://graph.facebook.com/" + jsonArray.getJSONObject(idx).get("id").toString() +"/picture?type=small"));
                                }
                                field.put("friends", list);
                                User.LocationPoint locationPoint = new User.LocationPoint(lat, lon);
                                field.put("location_point", locationPoint);
                                field.put("location", cityName);
                                field.put("app_version", getAppVersion(getActivity()));
                                connectSigninUser(field);//이미 최초 로그인을 한 기록이 있어서 회원가입이 되있는 경우
                            }catch (Exception e){
                                Log.i("zxc", "1");
                                e.printStackTrace();
                            }
                            */

                            //오류 해결해야함!
                            //connectSigninUser하고 connecSignup 또 함
                            //SharedManager.getInstance().getMe()가 SharedManager.getInstance().setMe()보다 일찍불려서 그럼.

                            //최초 로그인 => 회원가입(DB에 없을때)
                            Log.i("zxccccc", "SharedManager.getInstance().getMe() : " + SharedManager.getInstance().getMe());
                            if(prefs.getString("social_id","").equals("")) {
                                n_user = new User();
                                n_user.social_id = object.optString("id");
                                n_user.social_type = "facebook";
                                n_user.device_type = "android";
                                n_user.app_version = getAppVersion(getActivity());
                                //pic_list.add("http://graph.facebook.com/"+n_user.social_id+"/picture?type=large");
                                n_user.pic_list = pic_list;
                                //n_user.pic_small = "http://graph.facebook.com/"+n_user.social_id+"/picture?type=small";
                                n_user.nickname = object.optString("name");
                                n_user.about_me = "";
                                //String tempBirthday = object.optString("birthday");
                                //tempBirthday = tempBirthday.substring(0,4) + "-" + tempBirthday.substring(5,7) + "-" +tempBirthday.substring(8,10);
                                //n_user.birthday = tempBirthday; //현재 facebook에서 birthday 받아올 수 없음.
                                if(object.optString("gender").equals("male"))
                                    n_user.gender = false;
                                else
                                    n_user.gender = true;

                                n_user.location = SplashActivity.cityName;
                                n_user.password = null;
                                n_user.languages = new ArrayList<>();
                                Locale locale = getResources().getConfiguration().locale;
                                n_user.lang_type = locale.getLanguage(); // kr , en ...
                                Log.i("zxc", "locale.getLanguage() : " + locale.getLanguage());
                                n_user.location_point = new User.LocationPoint(lat,lon);
                                List<User.Friends> list = new ArrayList<>();
                                try{
                                    JSONArray jsonArray = response.getJSONObject().getJSONArray("data");
                                    int len = jsonArray.length();
                                    for(int idx=0;idx<len;idx++) {
                                        list.add(0, n_user.newFriend(jsonArray.getJSONObject(idx).get("id").toString(), jsonArray.getJSONObject(idx).get("name").toString(), "http://graph.facebook.com/" + jsonArray.getJSONObject(idx).get("id").toString() +"/picture?type=small"));
                                    }
                                    n_user.friends = list;
                                }catch (Exception e){
                                    e.printStackTrace();
                                    n_user.friends = list;
                                    n_user.auth = true;
                                    //connectCreateUser(n_user);
                                    Intent intent = new Intent(getActivity(), SignupActivity.class);
                                    intent.putExtra("user", n_user);
                                    startActivity(intent);
                                    getActivity().finish();
                                }

                            }
                            //tempAge = object.optString("age_range");
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender,friends");
            request.setParameters(parameters);
            request.executeAsync();
        }


        @Override
        public void onCancel() {
        }

        @Override
        public void onError(FacebookException e) {
            e.printStackTrace();

        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FacebookSdk.sdkInitialize(context);
//        try{
//            PackageInfo info = getActivity().getPackageManager().getPackageInfo(
//                    "makejin.langfriend",
//                    PackageManager.GET_SIGNATURES);
//            for(Signature signature : info.signatures){
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        }catch(PackageManager.NameNotFoundException e) {
//        }catch(NoSuchAlgorithmException e){
//        }
        View view = inflater.inflate(R.layout.fragment_sign, container, false);
        initInstance(view);

        return view;
    }


    public void initInstance(View view){
        prefs = getActivity().getSharedPreferences("LangFriend", Context.MODE_PRIVATE);
        editor = prefs.edit();

        BT_facebook_no = (Button)view.findViewById(R.id.BT_facebook_no);

        callbackManager = CallbackManager.Factory.create();
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                // App code
                if(currentAccessToken == (null)){ //로그아웃된 상태
                }
            }

        };

        accessTokenTracker.startTracking();

        indicator = (LinearLayout) view.findViewById(R.id.indicator);


        if(isLoggedIn()){ //facebook 로그인되있었던 경우
            field.put("social_id", AccessToken.getCurrentAccessToken().getUserId());
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/me/friends",
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            try{

                                JSONArray jsonArray = response.getJSONObject().getJSONArray("data");
                                int len = jsonArray.length();
                                //List<String> list = new ArrayList<>();
                                List<User.Friends> list = new ArrayList<>();
                                for(int idx=0;idx<len;idx++) {
                                    //list.add(0,jsonArray.getJSONObject(idx).get("id").toString());
                                    list.add(0, n_user.newFriend(jsonArray.getJSONObject(idx).get("id").toString(), jsonArray.getJSONObject(idx).get("name").toString(), "http://graph.facebook.com/" + jsonArray.getJSONObject(idx).get("id").toString() +"/picture?type=small"));
                                }
                                field.put("friends", list);

                                User.LocationPoint locationPoint = new User.LocationPoint(lat, lon);
                                field.put("location_point", locationPoint);
                                field.put("location", cityName);
                                field.put("app_version", getAppVersion(getActivity()));
                                connectSigninUser(field);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
            ).executeAsync();
        } else if(!prefs.getString("social_id","").equals("") || !prefs.getString("password","").equals("") ){
            field.put("social_id", prefs.getString("social_id",""));
            field.put("password", prefs.getString("password",""));

            User.LocationPoint locationPoint = new User.LocationPoint(lat, lon);
            field.put("location_point", locationPoint);
            field.put("location", cityName);
            field.put("app_version", getAppVersion(getActivity()));
            connectSigninUser_NonFacebook(field);
        }


        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {


                // Set the access token using
                // currentAccessToken when it's loaded or set.
            }
        };
        // If the access token is available already assign it.
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(
                    Profile oldProfile,
                    Profile currentProfile) {
                // App code
            }

        };


        BT_facebook_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment reg = new SignNonFacebook1Fragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.activity_sign, reg);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LoginButton loginButton = (LoginButton)view.findViewById(R.id.BT_facebook);
        loginButton.setReadPermissions("public_profile", "user_friends","email", "user_birthday");
        loginButton.setFragment(this);
        loginButton.registerCallback(callbackManager, callback);
    }


    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
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


    void connectSigninUser(final Map field) {
        LoadingUtil.startLoading(indicator);
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        Log.i("zxc", "connectSigninUser : " + field);
        conn.signinUser(field)
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
                            try{
                                if(response.nickname == null) {
                                    Log.i("zxc", "poio23 : ");
                                    //connectCreateUser(n_user); //아에이오우
                                    return;
                                }

                                PreferenceManager.getInstance(getActivity()).set_id(response._id);
                                Long current_time = System.currentTimeMillis();
                                PreferenceManager.getInstance(getApplicationContext()).setExpireDate(current_time+86400);

                                SharedManager.getInstance().setMe(response);
                                editor.putString("social_id", response.social_id);
                                editor.putString("password", response.password);
                                editor.putString("_id", response._id);
                                editor.commit();
                                Intent intent = new Intent(getActivity(), TabActivity_.class);
                                startActivity(intent);
                                getActivity().finish();
                            }catch(Exception e){
                                e.printStackTrace();
                                //connectCreateUser(n_user);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
                            Log.i("zxcauth", "auth : "+ response.auth);
                            if(!response.auth) {
                                Toast.makeText(getActivity(), "등록하신 이메일로 가서, 본인 인증을 먼저 완료해주세요!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            PreferenceManager.getInstance(getActivity()).set_id(response._id);
                            Long current_time = System.currentTimeMillis();
                            PreferenceManager.getInstance(getApplicationContext()).setExpireDate(current_time+86400);

                            SharedManager.getInstance().setMe(response);

                            editor.putString("social_id", response.social_id);
                            editor.putString("password", response.password);
                            editor.putString("_id", response._id);
                            editor.commit();

                            Intent intent = new Intent(context, TabActivity_.class);
                            startActivity(intent);
                            getActivity().finish();
                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
