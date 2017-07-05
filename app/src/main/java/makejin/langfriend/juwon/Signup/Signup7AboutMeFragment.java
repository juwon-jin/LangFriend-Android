package makejin.langfriend.juwon.Signup;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import makejin.langfriend.R;
import makejin.langfriend.juwon.Model.User;
import makejin.langfriend.juwon.Sign.SignActivity;
import makejin.langfriend.juwon.Utils.Connections.CSConnection;
import makejin.langfriend.juwon.Utils.Connections.ServiceGenerator;
import makejin.langfriend.juwon.Utils.Constants.Constants;
import makejin.langfriend.juwon.Utils.Loadings.LoadingUtil;
import makejin.langfriend.juwon.Utils.SharedManager.PreferenceManager;
import makejin.langfriend.juwon.Utils.SharedManager.SharedManager;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.facebook.FacebookSdk.getCacheDir;
import static makejin.langfriend.juwon.Sign.SignActivity.isSignup;
import static makejin.langfriend.juwon.Signup.Signup6AddImageFragment.imagepath;
import static makejin.langfriend.juwon.Signup.SignupActivity.activity;
public class Signup7AboutMeFragment extends Fragment {
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    TextView title;
    Button BT_yes;

    Toolbar cs_toolbar;
    EditText ET_about_me;

    public String image_url = null;

    LinearLayout indicator;

    public static Signup7AboutMeFragment newInstance(int index) {
        Signup7AboutMeFragment fragment = new Signup7AboutMeFragment();
        Bundle b = new Bundle();
        b.putInt("index", index);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signup7_about_me, container, false);

        prefs = getActivity().getSharedPreferences("LangFriend", Context.MODE_PRIVATE);
        editor = prefs.edit();


        cs_toolbar = (Toolbar) view.findViewById(R.id.cs_toolbar);
        activity.setSupportActionBar(cs_toolbar);
        activity.getSupportActionBar().setTitle("회원가입");
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setHomeButtonEnabled(true);

        BT_yes = (Button) view.findViewById(R.id.BT_yes);
        title = (TextView) view.findViewById(R.id.title);
        ET_about_me = (EditText) view.findViewById(R.id.ET_about_me);

        indicator = (LinearLayout) view.findViewById(R.id.indicator);

        BT_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ET_about_me.getText().toString().equals("")){
                    Toast.makeText(activity, "자기소개를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                activity.user.about_me = ET_about_me.getText().toString();
                Log.i("zxc", "user : " + activity.user);

                if(activity.user.gender==false) {//남자
                    activity.user.pref_gender = 1; //여자
                }else{
                    activity.user.pref_gender = 2;
                }

                activity.user.distance_max = 100; //단위는 km
                activity.user.age_min = 18;
                activity.user.age_max = 32;
                activity.user.age_min = 18;
                activity.user.country_in_search.add("모두");
                activity.user.push_token = FirebaseInstanceId.getInstance().getToken();

                connectCreateUser(activity.user);
            }
        });


        return view;

    }


    void connectCreateUser(User user) {
        LoadingUtil.startLoading(indicator);
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        Log.i("zxc", "connectCreateUser : " + user);

        conn.signupUser(user)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public final void onCompleted() {
                        LoadingUtil.startLoading(indicator);
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                    }
                    @Override
                    public final void onNext(User response) {
                        if (response != null) {
                            //회원가입 시 push_on
                            FirebaseMessaging.getInstance().subscribeToTopic("push_on");
                            PreferenceManager.getInstance(getApplicationContext()).setPush(true);
                            PreferenceManager.getInstance(getActivity()).set_id(response._id);

                            SharedManager.getInstance().setMe(response);
                            editor.putString("social_id", response.social_id);
                            editor.putString("password", response.password);
                            editor.putString("_id", response._id);
                            editor.commit();

                            //for(int i=0;i<response.pic_list.size();i++) {
                            for(int i=0;i<imagepath.size();i++) {
                                if(imagepath.get(i) == null)
                                    continue;
                                Log.i("makejin", "response.pic_list.get(" +i +") : " + response.pic_list.get(i));
                                if(imagepath.get(i).contains("facebook")) {
                                    continue;
                                }
                                uploadFile1(response, i);
                            }
                            if(response.social_type.equals("normal"))
                                isSignup = true;
                            else
                                isSignup = false;
                            Intent intent = new Intent(getActivity(), SignActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void uploadFile1(final User user, final int index) {
        String tempIndex = String.valueOf(index);

        File file;
        if(imagepath.get(index).contains("/makejin.langfriend/cache/cropped")){
            file = new File(getCacheDir(),imagepath.get(index).substring(imagepath.get(index).lastIndexOf("cache/")+6,imagepath.get(index).length()));
        }else{
            file = new File(imagepath.get(index));
        }
        RequestBody ubody = RequestBody.create(MediaType.parse("image/*"), saveBitmapToFile(file));

        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.fileUploadWrite_User(user._id, tempIndex, ubody)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public final void onCompleted() {

                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(User response) {
                        if (response != null) {
                            SharedManager.getInstance().setMe(response);
                            image_url = response.pic_list.get(index);
                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public File saveBitmapToFile(File file){
        try {

            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE=75;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 80 , outputStream);

            return file;
        } catch (Exception e) {
            return null;
        }
    }
}
