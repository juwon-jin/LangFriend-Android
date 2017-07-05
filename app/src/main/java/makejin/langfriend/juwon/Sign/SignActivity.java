package makejin.langfriend.juwon.Sign;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.facebook.FacebookSdk;
import makejin.langfriend.R;
import makejin.langfriend.juwon.Signup.Signup8PopupActivity;


public class SignActivity extends AppCompatActivity {

    public static Context context;
    public static boolean isSignup = false;
    public static String isSignupType = ""; //facebook / Normal /  kakao

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        context = this;

        if(isSignup) {
            Intent intent = new Intent(getApplicationContext(), Signup8PopupActivity.class);
            startActivity(intent);
            isSignup = false;
        }else {
            Fragment fragment = new SignFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.activity_sign, fragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isSignup) {
            Intent intent = new Intent(getApplicationContext(), Signup8PopupActivity.class);
            startActivity(intent);
            //isSignup = false;
        }else {
            Fragment fragment = new SignFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.activity_sign, fragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack(null);
            ft.commit();
        }
    }
}