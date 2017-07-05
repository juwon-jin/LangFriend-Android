package makejin.langfriend.juwon.TabActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;
import com.facebook.FacebookSdk;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import makejin.langfriend.R;
import makejin.langfriend.juwon.TabActivity.ParentFragment.TabParentFragment;

@EActivity(R.layout.activity_tab)
public class TabActivity extends AppCompatActivity {
    private long backKeyPressedTime = 0;
    private Toast toast;
    SharedPreferences sp;

    @ViewById
    public LinearLayout indicator;


    @ViewById
    AHBottomNavigationViewPager viewPager;

    //private TabParentFragment currentFragment;
    public TabParentFragment currentFragment;
    //private BottomTabPagerAdapter adapter;
    public BottomTabPagerAdapter adapter;
    private AHBottomNavigationAdapter navigationAdapter;
    private ArrayList<AHBottomNavigationItem> bottomNavigationItems = new ArrayList<>();

    //SharedPreferences sp = getSharedPreferences("TodayFood", Context.MODE_PRIVATE);
    @AfterViews
    void afterBindingView() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        AHBottomNavigation bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);

        // Create items
        AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.tab_1, R.drawable.icon_recommand, R.color.colorBottomTabBackGround);
        //AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.tab_2, R.drawable.icon_request, R.color.colorBottomTabBackGround);//request
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.tab_3, R.drawable.icon_friends, R.color.colorBottomTabBackGround);//list
        //AHBottomNavigationItem item4 = new AHBottomNavigationItem(R.string.tab_4, R.drawable.icon_feed, R.color.colorBottomTabBackGround);
        AHBottomNavigationItem item5 = new AHBottomNavigationItem(R.string.tab_5, R.drawable.icon_mypage, R.color.colorBottomTabBackGround);

        // Add items
        bottomNavigation.addItem(item1);
        //bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);
        //bottomNavigation.addItem(item4);
        bottomNavigation.addItem(item5);

        // Set Color
        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#FFFFFF"));
        bottomNavigation.setBehaviorTranslationEnabled(false);
        bottomNavigation.setAccentColor(Color.parseColor("#FC5052"));
        bottomNavigation.setInactiveColor(Color.parseColor("#999999"));

        bottomNavigation.setForceTint(true);
        bottomNavigation.setForceTitlesDisplay(true);
        bottomNavigation.setColored(false);
        bottomNavigation.setCurrentItem(0);

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                // Do something cool here...

                currentFragment = adapter.getCurrentFragment();

                if (wasSelected) {
                    currentFragment.refresh();
                    return true;
                }

                viewPager.setCurrentItem(position, false);
                currentFragment = adapter.getCurrentFragment();
                currentFragment.willBeDisplayed();
                currentFragment.reload();

                return true;
            }
        });
        bottomNavigation.setOnNavigationPositionListener(new AHBottomNavigation.OnNavigationPositionListener() {
            @Override
            public void onPositionChange(int y) {
                // Manage the new y position
            }
        });

        viewPager.setOffscreenPageLimit(3);
        adapter = new BottomTabPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        currentFragment = adapter.getCurrentFragment();
    }

    @UiThread
    void uiThread() {

    }
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        if (intent.hasExtra("click_action")) {
//            ClickActionHelper.startActivity(intent.getStringExtra("click_action"), intent.getExtras(), this);
//        }
//
//    }
    /// EXIT
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish();
            toast.cancel();
        }
    }

    public void showGuide() {
        toast = Toast.makeText(getApplicationContext(), getString(R.string.exit_app), Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

}
