package makejin.langfriend.juwon.TabActivity.Tab5MyPage;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.theartofdev.edmodo.cropper.CropImage;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.List;

import makejin.langfriend.R;
import makejin.langfriend.juwon.Model.User;
import makejin.langfriend.juwon.TabActivity.Tab1Recommand.DetailUserActivity;
import makejin.langfriend.juwon.Utils.Connections.CSConnection;
import makejin.langfriend.juwon.Utils.Connections.ServiceGenerator;
import makejin.langfriend.juwon.Utils.Constants.Constants;
import makejin.langfriend.juwon.Utils.Loadings.LoadingUtil;
import makejin.langfriend.juwon.Utils.SharedManager.SharedManager;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static makejin.langfriend.juwon.Utils.Constants.Constants.CalculationByDistance;
import static makejin.langfriend.juwon.Utils.Constants.Constants.getAgeFromBirthday;

public class ProfileDetailActivity extends AppCompatActivity {
    public static ProfileDetailActivity activity;
    LinearLayout indicator;
    ImageView IV_user_pic;

    TextView TV_nickname;
    TextView TV_about_me;

    TextView TV_finish;
    TextView TV_user_info;

    TagFlowLayout TFL_language;

    User mUser;
    Button BT_edit;

    String user_id;
    public static boolean edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_detail);
        this.activity = this;

        user_id = getIntent().getStringExtra("_id");
        edit = getIntent().getBooleanExtra("edit",false);


        IV_user_pic = (ImageView) findViewById(R.id.IV_user_pic);

        indicator = (LinearLayout)findViewById(R.id.indicator);
        registerView();

        if(user_id == null) {//me
            if(edit){//프로필 수정을 원하면
                BT_edit.callOnClick();
            }
            mUser = SharedManager.getInstance().getMe();
            ChangeUser(mUser.getPic(0), mUser.nickname, mUser.languages, mUser.about_me, mUser.birthday,  mUser.location_point);
        }else{
            BT_edit.setVisibility(View.INVISIBLE);
            connGetOneUser(user_id);
        }



    }


    private void registerView(){

        TV_nickname = (TextView) findViewById(R.id.TV_nickname);
        TV_user_info = (TextView) findViewById(R.id.TV_user_info);
        TV_about_me = (TextView) findViewById(R.id.TV_about_me);
        TV_finish = (TextView) findViewById(R.id.TV_finish);
        BT_edit = (Button) findViewById(R.id.BT_edit);

        TFL_language = (TagFlowLayout) findViewById(R.id.TFL_language);

        IV_user_pic.setOnClickListener(mBtnListener);
        BT_edit.setOnClickListener(mBtnListener);



    }

    private View.OnClickListener mBtnListener = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            switch (v.getId()){
                case R.id.IV_user_pic:
                    try{
                        user_detail(mUser);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case R.id.BT_edit:
                    Log.i("zxc", "BT_X");
                    Intent intent = new Intent(getApplicationContext(), ProfileEditActivity.class);
                    startActivityForResult(intent, Constants.ACTIVITY_CODE_TAB5_PROFILE_DETAIL_REFRESH_REQUEST);

//                    Fragment fragment = new ProfileEditFragment();
//                    FragmentTransaction ft = getFragmentManager().beginTransaction();
//                    ft.replace(R.id.activity_profile_detail, fragment);
//                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//                    ft.addToBackStack(null);
//                    ft.commit();
                    break;

            }
        }
    };

    private void user_detail(User user){
        Intent intent = new Intent(getApplicationContext(), DetailUserActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    void ChangeUser(String image_url, String nickname, List<User.Language> languages, String about_me, String birthday, User.LocationPoint location_point){
        Log.i("asd", birthday);
        //이미지 변경
        try {
            if (image_url.contains("facebook"))
                image_url = image_url.replace("type=large", "width=" + IV_user_pic.getWidth() + "&height=" + IV_user_pic.getHeight());
        }catch (NullPointerException e){
            e.printStackTrace();
            image_url = "";
        }


        Log.i("asd", image_url);

        LoadingUtil.startLoading(indicator);
        Glide.with(getApplicationContext()).
                load(image_url).
                listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        LoadingUtil.stopLoading(indicator);
                        return false;
                    }
                }).
                into(IV_user_pic);


        //이름 변경
        TV_nickname.setText(nickname);


        Log.i("zxc", "birthday : " + birthday);
        //user_info -> 나이 / 거리
        String age = getAgeFromBirthday(birthday);


        String distance;
        if(user_id == null){ //me
            distance = "0 km";
        }else{
            distance = String.valueOf(CalculationByDistance(SharedManager.getInstance().getMe().location_point, location_point));
            distance += "km";
        }


        TV_user_info.setText(age + " / " + distance);


        addFlowChart(TFL_language, languages.toArray(new User.Language[languages.size()]));

        //소개 변경
        TV_about_me.setText(about_me);
    }

    private void addFlowChart(final TagFlowLayout mFlowLayout, User.Language[] array) {
        final LayoutInflater mInflater = LayoutInflater.from(activity);

        mFlowLayout.setAdapter(new TagAdapter<User.Language>(array){
            @Override
            public View getView(final FlowLayout parent, final int position, User.Language s) {
                final TextView tv = (TextView) mInflater.inflate(R.layout.tag_result_language_tab1, mFlowLayout, false);
                tv.setText(s.name + " - " + s.getLevel());

                return tv;
            }

            @Override
            public boolean setSelected(int position, User.Language s) {
                return s.equals("Android");
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.ACTIVITY_CODE_TAB5_PROFILE_DETAIL_REFRESH_REQUEST) {
            if (resultCode == Constants.ACTIVITY_CODE_TAB5_PROFILE_DETAIL_REFRESH_RESULT) {
                Log.d("hansjin", "refresh");
                mUser = SharedManager.getInstance().getMe();
                ChangeUser(mUser.getPic(0), mUser.nickname, mUser.languages, mUser.about_me, mUser.birthday,  mUser.location_point);
            }
        }
    }

    //getOneUser
    void connGetOneUser(String user_id) {
        LoadingUtil.startLoading(indicator);
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.getOneUser(user_id)
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
                        Toast.makeText(activity, Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(User response) {
                        if (response != null) {
                            mUser = response;
                            ChangeUser(mUser.getPic(0), mUser.nickname, mUser.languages, mUser.about_me, mUser.birthday,  mUser.location_point);
                        } else {
                            Toast.makeText(activity, Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
