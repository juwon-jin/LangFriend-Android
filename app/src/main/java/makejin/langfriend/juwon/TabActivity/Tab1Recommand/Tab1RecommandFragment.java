package makejin.langfriend.juwon.TabActivity.Tab1Recommand;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import makejin.langfriend.R;
import makejin.langfriend.juwon.Model.GlobalResponse;
import makejin.langfriend.juwon.Model.User;

import makejin.langfriend.juwon.Sign.SignActivity;
import makejin.langfriend.juwon.TabActivity.ParentFragment.TabParentFragment;
import makejin.langfriend.juwon.TabActivity.TabActivity;
import makejin.langfriend.juwon.Utils.Connections.CSConnection;
import makejin.langfriend.juwon.Utils.Connections.ServiceGenerator;
import makejin.langfriend.juwon.Utils.Constants.Constants;
import makejin.langfriend.juwon.Utils.Loadings.LoadingUtil;
import makejin.langfriend.juwon.Utils.SharedManager.SharedManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.androidannotations.annotations.UiThread;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static makejin.langfriend.juwon.TabActivity.Tab1Recommand.RecommandLoadingActivity.recommandLoadingActivity;
import static makejin.langfriend.juwon.Utils.Constants.Constants.CalculationByDistance;
import static makejin.langfriend.juwon.Utils.Constants.Constants.getAgeFromBirthday;

/**
 * Created by kksd0900 on 16. 10. 11..
 */
public class Tab1RecommandFragment extends TabParentFragment {
    TabActivity activity;

    LinearLayout indicator;
    ImageView IV_user_pic;

    TextView TV_nickname;
    TextView TV_about_me;
    TextView TV_user_info;

    Button BT_report;
    Button BT_no;
    Button BT_yes;

    TextView TV_finish;
    RelativeLayout RL_user_desc;

    RelativeLayout RL_IV_yes_2;
    RelativeLayout RL_IV_no_2;

    TagFlowLayout TFL_language;


    List<User> recommandedUsers = new ArrayList<>();

    public int index = 0;

    public int page = 1;
    public boolean endOfPage = false;

    final int recommandOneTime_MAX = 5;

    public String you_id = "";
    public static boolean isConnRecommand = false; //false : connRecommand 가능  //true : connRecommand 불가능
    /**
     * Create a new instance of the fragment
     */
    public static Tab1RecommandFragment newInstance(int index) {
        Tab1RecommandFragment fragment = new Tab1RecommandFragment();
        Bundle b = new Bundle();
        b.putInt("index", index);
        fragment.setArguments(b);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommand, container, false);
        initViewSetting(view);
        return view;
    }

    private void initViewSetting(View view) {
        final TabActivity tabActivity = (TabActivity) getActivity();
        this.activity = tabActivity;

        registerView(view);

        Toolbar cs_toolbar = (Toolbar)view.findViewById(R.id.cs_toolbar);
        activity.setSupportActionBar(cs_toolbar);
        activity.getSupportActionBar().setTitle("");

        indicator = (LinearLayout)view.findViewById(R.id.indicator);

        isConnRecommand = false;
        connRecommand(1);
        //nextUser(index);
    }
    @Override
    public void reload() {
        //nextUser(index);
        connRecommand(1);
    }

    @Override
    public void refresh() {connRecommand(1);}

    private void registerView(View v){
        BT_report = (Button) v.findViewById(R.id.BT_report);
        BT_no = (Button) v.findViewById(R.id.BT_no);
        BT_yes = (Button) v.findViewById(R.id.BT_yes);
        IV_user_pic = (ImageView) v.findViewById(R.id.IV_user_pic);
        TV_nickname = (TextView) v.findViewById(R.id.TV_nickname);
        //TV_language = (TextView) v.findViewById(R.id.TV_language);
        TV_about_me = (TextView) v.findViewById(R.id.TV_about_me);
        TV_finish = (TextView) v.findViewById(R.id.TV_finish);
        TV_user_info = (TextView) v.findViewById(R.id.TV_user_info);

        RL_user_desc = (RelativeLayout) v.findViewById(R.id.RL_user_desc);

        BT_report.setOnClickListener(mBtnListener);
        BT_no.setOnClickListener(mBtnListener);
        BT_yes.setOnClickListener(mBtnListener);
        IV_user_pic.setOnClickListener(mBtnListener);

        RL_IV_yes_2 = (RelativeLayout) v.findViewById(R.id.RL_IV_yes_2);
        RL_IV_no_2 = (RelativeLayout) v.findViewById(R.id.RL_IV_no_2);

        RL_IV_yes_2.setVisibility(View.INVISIBLE);
        RL_IV_no_2.setVisibility(View.INVISIBLE);


        TFL_language = (TagFlowLayout) v.findViewById(R.id.TFL_language);


    }

    private View.OnClickListener mBtnListener = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            switch (v.getId()){
                case R.id.BT_report:
                    report_user(you_id);
                    break;

                case R.id.BT_no:
                    if(index<recommandOneTime_MAX)
                        try {

                            RL_IV_no_2.setVisibility(View.VISIBLE);
                            user_reject_tab1(recommandedUsers.get(index));
                            new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
                                @Override
                                public void run() {
                                    RL_IV_no_2.setVisibility(View.INVISIBLE);
                                }
                            }, 1000);

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    break;

                case R.id.BT_yes:
                    //좋아요한 목록(배열)에 해당 유저추가하기
                    if(index<recommandOneTime_MAX)
                        try {
                            RL_IV_yes_2.setVisibility(View.VISIBLE);
                            user_accept_tab1(recommandedUsers.get(index));
                            new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
                                @Override
                                public void run() {
                                    RL_IV_yes_2.setVisibility(View.INVISIBLE);
                                }
                            }, 1000);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    break;

                case R.id.IV_user_pic:
                    try{
                        user_detail(recommandedUsers.get(index));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
            }
        }
    };

    private void user_detail(User user){
        Intent intent = new Intent(getActivity(), DetailUserActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    private void endOfPage(){
        isConnRecommand = false;
        endOfPage = true;
        //이미지 변경
        Glide.with(getActivity()).
                load("").
                into(IV_user_pic);

        RL_user_desc.setVisibility(View.INVISIBLE);
        TV_finish.setVisibility(View.VISIBLE);
    }

    private void endOfPage_not(){
        endOfPage=false;
        RL_user_desc.setVisibility(View.VISIBLE);
        TV_finish.setVisibility(View.INVISIBLE);
    }


    private void nextUser(int index){
        if(endOfPage)
            return;

        Log.i("zxc","i" + index);


        //server에서 먼저 is
        if(index>=recommandOneTime_MAX){
            //endOfPage();
            isConnRecommand = false; //connRecommand가능
            connRecommand(1);
            return;
        }
        User mUser;
        try {
            mUser = recommandedUsers.get(index);
            you_id = mUser._id;
        }catch (Exception e){
            endOfPage();
            return;
        }
        ChangeUser(mUser.getPic(0), mUser.nickname, mUser.languages, mUser.about_me, mUser.birthday, mUser.location_point);
    }

    void ChangeUser(String image_url, String nickname, List<User.Language> languages, String about_me, String birthday, User.LocationPoint location_point){

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
        Glide.with(getActivity()).
                load(image_url).skipMemoryCache(true).
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
        if(location_point.lat!=0 && location_point.lon!=0) {
            distance = String.valueOf(CalculationByDistance(SharedManager.getInstance().getMe().location_point, location_point));
            distance += "km";
        }
        else
            distance = "";


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

    void connRecommand(int page) {
        Log.i("zxc", "isConnRecommand : " + isConnRecommand);
        if(isConnRecommand) {//검색 옵션을 바꾼 경우에만 connRecommand 아래 소스들이 실행되도록
            return;
        }
        isConnRecommand = true;
        //LoadingUtil.startLoading(indicator);
        //Splash처럼 로딩하는 애니메이션 화면 시작
        startActivity(new Intent(getActivity(),RecommandLoadingActivity.class));
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.getRecommandUsers(SharedManager.getInstance().getMe()._id, page)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<User>>() {
                    @Override
                    public final void onCompleted() {
                        //LoadingUtil.stopLoading(indicator);
                        //Splash처럼 로딩하는 애니메이션 화면 종료

//                        new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
//                            @Override
//                            public void run() {
//                                if(recommandLoadingActivity != null)
//                                    recommandLoadingActivity.finish();
//                            }
//                        }, 5000);

                        if(recommandLoadingActivity != null)
                            recommandLoadingActivity.finish();
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity().getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(final List<User> response) {
                        if (response != null && response.size()>0) {
                            if(endOfPage)
                                endOfPage_not();

                            recommandedUsers.clear();
                            recommandedUsers = response;
                            index = 0;
                            nextUser(index);
                        } else {
                            endOfPage();
                        }
                    }
                });
    }

    /*
    @UiThread
    void uiThread3() {


//        Timer timer = new Timer();
//        timer.schedule(this.spashScreenFinished, 12000);

//
//        Timer timer = new Timer();
//        timer.schedule(this.spashScreenFinished, 0);


    }
*/


    public void user_accept_tab1(User you) {
        LoadingUtil.startLoading(indicator);
        //RL_IV_yes_2.setVisibility(View.VISIBLE);
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.acceptYou_TAB1(you, SharedManager.getInstance().getMe()._id, you._id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public final void onCompleted() {
                        LoadingUtil.stopLoading(indicator);
                        //RL_IV_yes_2.setVisibility(View.INVISIBLE);
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(User response) {
                        Log.i("zxc", "response : " + response);
                        if (response != null) {
                            if(response._id == null) {
                                index += 1;
                                nextUser(index);
                            }
                            else if(response._id.equals(SharedManager.getInstance().getMe()._id)) { //내 아이디를 반환하면 아직 유효시간 안지난걸로.
                                startActivity(new Intent(getActivity(), PopupNoMore.class));
                                return;
                            }
                        } else {
                            Toast.makeText(getActivity(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void user_reject_tab1(User you) {
        LoadingUtil.startLoading(indicator);
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.rejectYou_TAB1(you, SharedManager.getInstance().getMe()._id, you._id)
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
                        Toast.makeText(getActivity(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(User response) {
                        if (response != null) {
                            if(response._id == null) {
                                index += 1;
                                nextUser(index);
                            }
                            else if(response._id.equals(SharedManager.getInstance().getMe()._id)) { //내 아이디를 반환하면 아직 유효시간 안지난걸로.
                                startActivity(new Intent(getActivity(), PopupNoMore.class));
                                return;
                            }
                        } else {
                            Toast.makeText(getActivity(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void report_user(String you_id) {
        LoadingUtil.startLoading(indicator);
        final CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.reportUser(SharedManager.getInstance().getMe()._id, you_id)
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
                        Toast.makeText(getActivity(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(GlobalResponse response) {
                        if (response.code == 0) {
                            Toast.makeText(getActivity(), "신고가 접수되었습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        if(!endOfPage){
//            nextUser(index);
//        }
//    }
}
