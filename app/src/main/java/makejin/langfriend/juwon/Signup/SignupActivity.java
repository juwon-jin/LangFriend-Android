package makejin.langfriend.juwon.Signup;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.google.firebase.FirebaseApp;

import makejin.langfriend.R;
import makejin.langfriend.juwon.Model.User;

import static makejin.langfriend.juwon.Signup.Signup6AddImageFragment.imagepath;

public class SignupActivity extends ActionBarActivity {
    public static SignupActivity activity;
    User user;
    public static String isSignupType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        this.activity = this;

        user = (User) getIntent().getSerializableExtra("user");
        isSignupType = (String) getIntent().getStringExtra("isSignupType");

        this.activity = this;
        imagepath.clear();


        Fragment fragment = new Signup1AgreementFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.activity_signup, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);
        ft.commit();

    }
}
