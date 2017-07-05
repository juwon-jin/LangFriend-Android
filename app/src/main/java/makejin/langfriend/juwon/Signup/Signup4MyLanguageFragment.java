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

import com.aigestudio.wheelpicker.WheelPicker;

import java.util.ArrayList;
import java.util.List;

import makejin.langfriend.R;
import makejin.langfriend.juwon.Model.User;

import static makejin.langfriend.juwon.Signup.SignupActivity.activity;

public class Signup4MyLanguageFragment extends Fragment {
    TextView title;
    Button BT_yes;

    Toolbar cs_toolbar;

    public String tempMyLanguage;

    public static Signup4MyLanguageFragment newInstance(int index) {
        Signup4MyLanguageFragment fragment = new Signup4MyLanguageFragment();
        Bundle b = new Bundle();
        b.putInt("index", index);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signup4_my_language, container, false);


        cs_toolbar = (Toolbar) view.findViewById(R.id.cs_toolbar);
        activity.setSupportActionBar(cs_toolbar);
        activity.getSupportActionBar().setTitle("회원가입");
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setHomeButtonEnabled(true);


        final WheelPicker wheelPicker = (WheelPicker) view.findViewById(R.id.main_wheel_left);


        final List<String> language = new ArrayList<>();
        language.add("한국어");
        language.add("영어");
        language.add("중국어");
        language.add("일본어");
        language.add("기타");

        tempMyLanguage = "한국어";

        wheelPicker.setData(language);
        wheelPicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker picker, Object data, int position) {
                setTempMyLanguage((String)data);
            }
        });

        BT_yes = (Button) view.findViewById(R.id.BT_yes);
        title = (TextView) view.findViewById(R.id.title);

        BT_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activity.user.languages.size()!=0){
                    activity.user.languages.clear();
                }

                User.Language temp = new User.Language();

                temp._id = 0;
                temp.name = tempMyLanguage;
                temp.level = 5; //모국어는 항상 네이티브어
                activity.user.languages.add(temp);

                Log.i("zxc", "user : " + activity.user);
                Fragment fragment = new Signup5InterestedLanguageFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.activity_signup, fragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();
            }
        });


        return view;

    }

    private void setTempMyLanguage(String myLanguage){
        tempMyLanguage = myLanguage;
    }

}
