package makejin.langfriend.juwon.TabActivity.Tab5MyPage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.aigestudio.wheelpicker.WheelPicker;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.crystal.crystalrangeseekbar.widgets.CrystalSeekbar;
import com.facebook.login.LoginManager;
import com.google.firebase.messaging.FirebaseMessaging;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import makejin.langfriend.R;
import makejin.langfriend.juwon.Model.User;
import makejin.langfriend.juwon.Sign.SignActivity;
import makejin.langfriend.juwon.Utils.Connections.CSConnection;
import makejin.langfriend.juwon.Utils.Connections.ServiceGenerator;
import makejin.langfriend.juwon.Utils.Constants.Constants;
import makejin.langfriend.juwon.Utils.PopupNotCompleted;
import makejin.langfriend.juwon.Utils.PopupRequest;
import makejin.langfriend.juwon.Utils.SharedManager.PreferenceManager;
import makejin.langfriend.juwon.Utils.SharedManager.SharedManager;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static makejin.langfriend.juwon.TabActivity.Tab1Recommand.Tab1RecommandFragment.isConnRecommand;


public class Setting extends AppCompatActivity {
    public Setting activity;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    Button BT_X;
    Button BT_push_setting;
    Button BT_agreement;
    Button BT_privacy_rule;
    Button BT_logout;
    //Button BT_withdrawal;
    Button BT_add;
    Button BT_add2;

    Switch ST_push;


    WheelPicker wheelPicker;
    TagFlowLayout TFL_language;
    List<User.Language> language = new ArrayList<>();
    List<String> language_list = new ArrayList<String>();

    WheelPicker wheelPicker_level;
    List<User.Language> language_level= new ArrayList<>();
    List<String> language_level_list = new ArrayList<String>();

    WheelPicker wheelPicker2;
    TagFlowLayout TFL_country;
    List<String> country = new ArrayList<String>();
    List<String> country_list = new ArrayList<String>();

    CheckBox checkBox_female;
    CheckBox checkBox_male;
    TextView TV_distance;
    TextView TV_age;
    CheckBox checkBox_sameCountry;
    CrystalSeekbar SB_distance;
    CrystalRangeSeekbar SB_age;

    public static User user;


    public static boolean isWithdrawal = false;

    public int tempDistance;
    public int tempAgeMin;
    public int tempAgeMax;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initViewSetting();

    }

    private void initViewSetting() {
        Toolbar cs_toolbar = (Toolbar)findViewById(R.id.cs_toolbar);
        setSupportActionBar(cs_toolbar);
        getSupportActionBar().setTitle("설정");

        this.activity = this;
        prefs = getSharedPreferences("LangFriend", MODE_PRIVATE);
        editor = prefs.edit();
        connGetEditMatchingOption(prefs.getString("_id",""));


        BT_X = (Button) findViewById(R.id.BT_X);
        BT_push_setting = (Button) findViewById(R.id.BT_push_setting);
        BT_agreement = (Button) findViewById(R.id.BT_agreement);
        BT_privacy_rule = (Button) findViewById(R.id.BT_privacy_rule);
        BT_logout = (Button) findViewById(R.id.BT_logout);
        //BT_withdrawal = (Button) findViewById(R.id.BT_withdrawal);

        BT_X.setText("완료");

        wheelPicker = (WheelPicker) findViewById(R.id.wheelPicker);
        wheelPicker_level = (WheelPicker) findViewById(R.id.wheelPicker_level);
        BT_add = (Button) findViewById(R.id.BT_add);
        TFL_language = (TagFlowLayout) findViewById(R.id.TFL_language);

        wheelPicker2 = (WheelPicker) findViewById(R.id.wheelPicker2);
        BT_add2 = (Button) findViewById(R.id.BT_add2);
        TFL_country = (TagFlowLayout) findViewById(R.id.TFL_country);



        TV_distance = (TextView) findViewById(R.id.TV_distance);
        TV_age = (TextView) findViewById(R.id.TV_age);
        checkBox_female = (CheckBox) findViewById(R.id.checkBox_female);
        checkBox_male = (CheckBox) findViewById(R.id.checkBox_male);
        checkBox_sameCountry = (CheckBox) findViewById(R.id.checkBox_sameCountry);
        SB_distance = (CrystalSeekbar) findViewById(R.id.SB_distance);
        SB_age = (CrystalRangeSeekbar) findViewById(R.id.SB_age);

        ST_push = (Switch) findViewById(R.id.push_switch);

        language_list.add("한국어");
        language_list.add("영어");
        language_list.add("중국어");
        language_list.add("일본어");
        language_list.add("기타");
        wheelPicker.setData(language_list);

        language_level_list.add("입문");
        language_level_list.add("초급");
        language_level_list.add("중급");
        language_level_list.add("고급");
        language_level_list.add("네이티브");
        wheelPicker_level.setData(language_level_list);

        country_list.add("(모두)");
        country_list.add("한국");
        country_list.add("미국");
        country_list.add("중국");
        country_list.add("일본");
        country_list.add("기타");
        wheelPicker2.setData(country_list);


        SB_distance.setOnSeekbarChangeListener(new OnSeekbarChangeListener() {
            @Override
            public void valueChanged(Number value) {
                tempDistance = value.intValue();
                if(tempDistance == 200){
                    TV_distance.setText(("무제한"));
                }else {
                    TV_distance.setText(tempDistance + " km");
                }
            }
        });

        SB_age.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                tempAgeMin = minValue.intValue();
                tempAgeMax = maxValue.intValue();
                TV_age.setText(minValue + " ~ " + maxValue);
            }
        });


        BT_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(language.size()>6) {
                    Toast.makeText(activity, "최대 선택 가능한 언어는 6가지입니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String tempLanguage = language_list.get(wheelPicker.getCurrentItemPosition());
                int tempLanguageLevel = wheelPicker_level.getCurrentItemPosition();


                for(int i=0;i<language.size();i++)
                    if (language.get(i).name.equals(tempLanguage)) {
                        Toast.makeText(activity, "이미 선택하신 언어입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                if(tempLanguage.equals(user.languages.get(0).name)) {
                    Toast.makeText(activity, "모국어 이외의 관심언어를 선택해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i("zxc", "l :" + tempLanguage + " lv : " + tempLanguageLevel);
                //user.language.add(s.getSelectedItem().toString());
                User.Language temp = new User.Language();

                temp._id = language.size()+1;
                temp.name = tempLanguage;
                temp.level = tempLanguageLevel+1; //1부터 시작하는걸로. ex. 1 -> 입문

                language.add(temp);
                addFlowChart(TFL_language, language.toArray(new User.Language[language.size()]));
            }
        });

        BT_add2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tempCountry = country_list.get(wheelPicker2.getCurrentItemPosition());
                if(country.contains(country_list.get(0))){
                    Toast.makeText(activity, "특정 국가를 선택하시려면, 먼저 (모두)를 선택해제 해주세요.",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(country.size()>6) {
                    Toast.makeText(activity, "최대 선택 가능한 국가는 6가지입니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                for(int i=0;i<country.size();i++)
                    if (country.get(i).equals(tempCountry)) {
                        Toast.makeText(activity, "이미 선택하신 국가입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                if(tempCountry.equals("(모두)")) {
                    country.removeAll(country);
                    country.add(tempCountry);
                }else {
                    country.add(tempCountry);
                }

                addFlowChart2(TFL_country, country.toArray(new String[country.size()]));
            }
        });

//        BT_push_setting.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getApplicationContext(), PopupNotCompleted.class));
//            }
//        });

        BT_agreement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Agreement.class));
            }
        });

        BT_privacy_rule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PrivacyRule.class));
            }
        });

        BT_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                PreferenceManager.getInstance(getApplicationContext()).setLogout();
                SharedManager.getInstance().setLogout();

                Intent intent = new Intent(getApplicationContext(), SignActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

//        BT_withdrawal.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getApplicationContext(), PopupRequest.class));
//                //탈퇴 기능 완성했지만 쓰지않는걸로(탈퇴를 완벽하게 구현하려면 서버단에서 data 조인해서 지워야할 것이 무진장 많음)
//                //startActivity(new Intent(getApplicationContext(), PopupWithdrawal.class));
//            }
//        });


        if(PreferenceManager.getInstance(getApplicationContext()).getPush())
            ST_push.setChecked(true);
        else
            ST_push.setChecked(false);

        ST_push.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == true){
                    FirebaseMessaging.getInstance().subscribeToTopic("push_on");
                    PreferenceManager.getInstance(getApplicationContext()).setPush(true);
                }else{
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("push_on");
                    PreferenceManager.getInstance(getApplicationContext()).setPush(false);
                }
            }
        });


        BT_X.setOnClickListener(new View.OnClickListener() { //앱 상단 바의 완료 버튼
            @Override
            public void onClick(View v) {

                int temp_pref_gender;

                if(checkBox_female.isChecked() && checkBox_male.isChecked()){
                    temp_pref_gender = 0;
                }else if(checkBox_female.isChecked()){
                    temp_pref_gender = 1;
                }else if(checkBox_male.isChecked()){
                    temp_pref_gender = 2;
                }else{
                    Toast.makeText(activity, "성별에 하나 이상 체크해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(language.size()==0){
                    Toast.makeText(activity, "관심 언어에 하나 이상 체크해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(country.size()==0){
                    Toast.makeText(activity, "관심 국적에 하나 이상 체크해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map field = new HashMap();
                field.put("pref_gender", temp_pref_gender);
                field.put("distance_max", tempDistance);
                field.put("age_min", tempAgeMin);
                field.put("age_max", tempAgeMax);

                language.add(0,user.languages.get(0));
                field.put("languages", language);
                field.put("country_in_search", country);
                field.put("want_same_country", !checkBox_sameCountry.isChecked());

                connEditMatchingOption(user._id, field);
            }
        });
    }

    public void initView(User user){
        switch (user.pref_gender){
            case 0:
                checkBox_female.setChecked(true);
                checkBox_male.setChecked(true);
                break;
            case 1: //여자
                checkBox_female.setChecked(true);
                checkBox_male.setChecked(false);
                break;
            case 2: //남자
                checkBox_female.setChecked(false);
                checkBox_male.setChecked(true);
                break;
            default:
                break;
        }


        Log.i("zxc", "user.distance_max) : " + user.distance_max);

        if(user.want_same_country)
            checkBox_sameCountry.setChecked(false);
        else
            checkBox_sameCountry.setChecked(true);

        for(int i=1;i<user.languages.size();i++) {
            language.add(user.languages.get(i));
        }
        addFlowChart(TFL_language, language.toArray(new User.Language[language.size()]));

        for(int i=0;i<user.country_in_search.size();i++) {
            country.add(user.country_in_search.get(i));
        }
        addFlowChart2(TFL_country, country.toArray(new String[country.size()]));

        TV_distance.setText(SB_distance.getPosition()+"km");
        TV_age.setText(SB_age.getLeft() + " ~ " +SB_age.getRight());


        Log.i("zxc", "D : " + user.distance_max);
        Log.i("zxc", "A : " + user.age_min);

        SB_distance.setMinStartValue(user.distance_max).apply();
        SB_age.setMinStartValue(user.age_min).setMaxStartValue(user.age_max).apply();

    }

    private void addFlowChart(final TagFlowLayout mFlowLayout, User.Language[] array) {
        final LayoutInflater mInflater = LayoutInflater.from(activity);

        mFlowLayout.setAdapter(new TagAdapter<User.Language>(array){
            @Override
            public View getView(final FlowLayout parent, final int position, User.Language s) {
                final TextView tv = (TextView) mInflater.inflate(R.layout.tag_result_language, mFlowLayout, false);
                tv.setText(s.name + " - " + s.getLevel() + "   X");

                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        language.remove(position);
                        mFlowLayout.removeViewAt(position);
                        addFlowChart(TFL_language, language.toArray(new User.Language[language.size()]));
                    }
                });
                return tv;
            }

            @Override
            public boolean setSelected(int position, User.Language s) {
                return s.equals("Android");
            }
        });

    }

    private void addFlowChart2(final TagFlowLayout mFlowLayout, String[] array) {
        final LayoutInflater mInflater = LayoutInflater.from(getApplication());

        mFlowLayout.setAdapter(new TagAdapter<String>(array) {
            @Override
            public View getView(final FlowLayout parent, final int position, String s) {
                final TextView tv = (TextView) mInflater.inflate(R.layout.tag_result, mFlowLayout, false);
                tv.setText(s + " X");

                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        country.remove(position);
                        mFlowLayout.removeViewAt(position);
                        addFlowChart2(TFL_country, country.toArray(new String[country.size()]));
                    }
                });

                return tv;
            }

            @Override
            public boolean setSelected(int position, String s) {
                return s.equals("Android");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }



    void connEditMatchingOption(final String user_id, final Map field) {
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.editMatchingOption(user_id, field)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public final void onCompleted() {
                        isConnRecommand = false;
                        Toast.makeText(activity, "정상적으로 수정되었습니다.", Toast.LENGTH_SHORT).show();
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
                            if(response.nickname==null)
                                return;

                            SharedManager.getInstance().setMe(response);
                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    void connGetEditMatchingOption(final String user_id) {
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.getEditMatchingOption(user_id)
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
                            user = response;
                            initView(user);
                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}



