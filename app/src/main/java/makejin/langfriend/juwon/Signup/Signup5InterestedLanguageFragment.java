package makejin.langfriend.juwon.Signup;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aigestudio.wheelpicker.WheelPicker;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;

import makejin.langfriend.R;
import makejin.langfriend.juwon.Model.User;

import static makejin.langfriend.juwon.Signup.SignupActivity.activity;

public class Signup5InterestedLanguageFragment extends Fragment {
    TextView title;
    Button BT_yes;
    Button BT_add;

    Toolbar cs_toolbar;
    public String tempLanguage;
    public int tempLanguageLevel;
    TagFlowLayout TFL_interested_language;

    List<User.Language> language = new ArrayList<>();

    public static Signup5InterestedLanguageFragment newInstance(int index) {
        Signup5InterestedLanguageFragment fragment = new Signup5InterestedLanguageFragment();
        Bundle b = new Bundle();
        b.putInt("index", index);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signup5_interested_language, container, false);


        cs_toolbar = (Toolbar) view.findViewById(R.id.cs_toolbar);
        activity.setSupportActionBar(cs_toolbar);
        activity.getSupportActionBar().setTitle("회원가입");
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setHomeButtonEnabled(true);


        final WheelPicker wheelPicker = (WheelPicker) view.findViewById(R.id.main_wheel_left);
        final WheelPicker wheelPicker2 = (WheelPicker) view.findViewById(R.id.main_wheel_center);

        TFL_interested_language = (TagFlowLayout) view.findViewById(R.id.TFL_interested_language);

        final List<String> language_string = new ArrayList<>();
        language_string.add("한국어");
        language_string.add("영어");
        language_string.add("중국어");
        language_string.add("일본어");
        language_string.add("기타");

        final List<String> language_level = new ArrayList<>();
        language_level.add("입문");
        language_level.add("초급");
        language_level.add("중급");
        language_level.add("고급");
        language_level.add("네이티브");

        wheelPicker.setData(language_string);
        wheelPicker2.setData(language_level);


        BT_add = (Button) view.findViewById(R.id.BT_add);
        BT_yes = (Button) view.findViewById(R.id.BT_yes);
        title = (TextView) view.findViewById(R.id.title);

        BT_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activity.user.languages.size()!=1){
                    User.Language temp = activity.user.languages.get(0);
                    activity.user.languages.clear();
                    activity.user.languages.add(temp);
                }


                for(User.Language tempLanguage : language) {
                    tempLanguage._id = activity.user.languages.size();
                    activity.user.languages.add(tempLanguage);
                }
                if(activity.user.languages.size()<=1) {
                    Toast.makeText(activity, "관심언어는 하나 이상 필요해요!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i("zxc", "user : " + activity.user);
                Fragment fragment = new Signup6AddImageFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.activity_signup, fragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();
            }
        });


        tempLanguage = language_string.get(0);
        tempLanguageLevel = 0;


        wheelPicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker picker, Object data, int position) {
                setTempLanguage((String)data);
            }
        });
        wheelPicker2.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker picker, Object data, int position) {
                setTempLanguageLevel(position);
            }
        });


        addFlowChart(TFL_interested_language, language.toArray(new User.Language[language.size()])); //뒤로가기 눌렀을 경우 대비


        BT_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(language.size()>6) {
                    Toast.makeText(getActivity(), "최대 선택 가능한 언어는 6가지입니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                for(int i=0;i<language.size();i++)
                    if (language.get(i).name.equals(tempLanguage)) {
                        Toast.makeText(getActivity(), "이미 선택하신 언어입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                if(tempLanguage.equals(activity.user.languages.get(0).name)) {
                    Toast.makeText(getActivity(), "모국어 이외의 관심언어를 선택해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.i("zxc", "l :" + tempLanguage + " lv : " + tempLanguageLevel);
                //user.language.add(s.getSelectedItem().toString());
                User.Language temp = new User.Language();

                temp.name = tempLanguage;
                temp.level = tempLanguageLevel+1; //1부터 시작하는걸로. ex. 1 -> 입문
                language.add(temp);
                addFlowChart(TFL_interested_language, language.toArray(new User.Language[language.size()]));
            }
        });


        return view;

    }

    private void setTempLanguage(String language){
        tempLanguage = language;
    }

    private void setTempLanguageLevel(int languageLevel){
        tempLanguageLevel = languageLevel;
    }

    private void addFlowChart(final TagFlowLayout mFlowLayout, User.Language[] array) {
        final LayoutInflater mInflater = LayoutInflater.from(getActivity());

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
                        activity.user.languages.remove(position+1); //모국어가 1부터 시작. 따라서 외국어는 2부터 시작하니까 +1
                        addFlowChart(TFL_interested_language, language.toArray(new User.Language[language.size()]));
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

}
