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

import com.aigestudio.wheelpicker.widgets.WheelDatePicker;

import java.util.Calendar;
import java.util.Date;

import makejin.langfriend.R;
import makejin.langfriend.juwon.Utils.Constants.Constants;

import static makejin.langfriend.juwon.Signup.SignupActivity.activity;

public class Signup2BirthdayFragment extends Fragment {
    TextView title;
    Button BT_yes;

    Toolbar cs_toolbar;

    String birthday;
    WheelDatePicker wheelDatePicker;

    public static Signup2BirthdayFragment newInstance(int index) {
        Signup2BirthdayFragment fragment = new Signup2BirthdayFragment();
        Bundle b = new Bundle();
        b.putInt("index", index);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signup2_birthday, container, false);


        cs_toolbar = (Toolbar) view.findViewById(R.id.cs_toolbar);
        activity.setSupportActionBar(cs_toolbar);
        activity.getSupportActionBar().setTitle("회원가입");
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setHomeButtonEnabled(true);


        BT_yes = (Button) view.findViewById(R.id.BT_yes);
        title = (TextView) view.findViewById(R.id.title);
        wheelDatePicker = (WheelDatePicker) view.findViewById(R.id.wheel_date_picker);

        wheelDatePicker.setSelectedDay(1);
        wheelDatePicker.setSelectedMonth(1);
        wheelDatePicker.setSelectedYear(2000);

        BT_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBirthday(wheelDatePicker.getCurrentYear(), wheelDatePicker.getCurrentMonth(), wheelDatePicker.getCurrentDay());
                activity.user.birthday = birthday;
                Log.i("zxc", "user : " + activity.user);

                Fragment fragment = new Signup3CountryFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.activity_signup, fragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        return view;

    }


    public void setBirthday(int year, int month, int day){
        String tempMonth = "";
        String tempDay = "";
        //month+=1;

        if(month<10)
            tempMonth = "0" + month;
        else
            tempMonth = ""+month;

        if(day<10)
            tempDay = "0" + day;
        else
            tempDay = ""+day;

        birthday = year +"-" + tempMonth + "-" + tempDay;
    }
}
