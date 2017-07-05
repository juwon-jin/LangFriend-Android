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
import android.widget.CheckBox;
import android.widget.TextView;

import com.aigestudio.wheelpicker.WheelPicker;

import java.util.ArrayList;
import java.util.List;

import makejin.langfriend.R;

import static makejin.langfriend.juwon.Signup.SignupActivity.activity;

public class Signup3CountryFragment extends Fragment {
    TextView title;
    Button BT_yes;

    Toolbar cs_toolbar;
    public String tempCountry;
    CheckBox CB_yes;

    public static Signup3CountryFragment newInstance(int index) {
        Signup3CountryFragment fragment = new Signup3CountryFragment();
        Bundle b = new Bundle();
        b.putInt("index", index);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signup3_country, container, false);


        cs_toolbar = (Toolbar) view.findViewById(R.id.cs_toolbar);
        activity.setSupportActionBar(cs_toolbar);
        activity.getSupportActionBar().setTitle("회원가입");
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setHomeButtonEnabled(true);


        BT_yes = (Button) view.findViewById(R.id.BT_yes);
        title = (TextView) view.findViewById(R.id.title);
        CB_yes = (CheckBox) view.findViewById(R.id.CB_yes);

        final WheelPicker wheelPicker = (WheelPicker) view.findViewById(R.id.main_wheel_left);

        final List<String> country = new ArrayList<>();
        country.add("한국");
        country.add("미국");
        country.add("중국");
        country.add("일본");
        country.add("기타");

        tempCountry = "한국";

        wheelPicker.setData(country);
        wheelPicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker picker, Object data, int position) {
                setTempCountry((String)data);
            }
        });

        BT_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.user.country = tempCountry;

                if(CB_yes.isChecked())
                    activity.user.want_same_country = false;
                else{
                    activity.user.want_same_country = true;
                }
                            Log.i("zxc", "user : " + activity.user);
                Fragment fragment = new Signup4MyLanguageFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.activity_signup, fragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();
            }
        });



        return view;

    }
    private void setTempCountry(String country){
        tempCountry = country;
    }

}
