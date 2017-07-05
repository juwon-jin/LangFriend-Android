package makejin.langfriend.juwon.Signup;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import makejin.langfriend.R;
import makejin.langfriend.juwon.Utils.Constants.Constants;

import static makejin.langfriend.juwon.Signup.SignupActivity.activity;

public class Signup1AgreementFragment extends Fragment {
    WebView webView;
    TextView title;
    Button BT_english, BT_yes;
    CheckBox CB_yes;

    Toolbar cs_toolbar;

    public static Signup1AgreementFragment newInstance(int index) {
        Signup1AgreementFragment fragment = new Signup1AgreementFragment();
        Bundle b = new Bundle();
        b.putInt("index", index);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signup1_agreement, container, false);


        cs_toolbar = (Toolbar) view.findViewById(R.id.cs_toolbar);
        activity.setSupportActionBar(cs_toolbar);
        activity.getSupportActionBar().setTitle("회원가입");
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setHomeButtonEnabled(true);


        BT_english = (Button) view.findViewById(R.id.BT_english);
        BT_yes = (Button) view.findViewById(R.id.BT_yes);
        title = (TextView) view.findViewById(R.id.title);
        CB_yes = (CheckBox) view.findViewById(R.id.CB_yes);


        webView = (WebView)view.findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());//클릭시 새창 안뜸
        webView.loadUrl(Constants.API_BASE_URL+"/agreement");

        BT_english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        BT_yes.setEnabled(false);

        CB_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CB_yes.isChecked())
                    BT_yes.setEnabled(true);
                else
                    BT_yes.setEnabled(false);
            }
        });

        BT_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new Signup2BirthdayFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.activity_signup, fragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        return view;
    }


}
