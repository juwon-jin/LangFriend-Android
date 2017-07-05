package makejin.langfriend.juwon.TabActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import makejin.langfriend.juwon.TabActivity.ParentFragment.TabParentFragment;
import makejin.langfriend.juwon.TabActivity.Tab1Recommand.Tab1RecommandFragment;
import makejin.langfriend.juwon.TabActivity.Tab3List.Tab3ListFragment;
import makejin.langfriend.juwon.TabActivity.Tab4Feeds.Tab4FeedsFragment;
import makejin.langfriend.juwon.TabActivity.Tab5MyPage.Tab5MyPageFragment;

import java.util.ArrayList;

/**
 * Created by kksd0900 on 16. 10. 11..
 */
public class BottomTabPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragments = new ArrayList<>();
    private TabParentFragment currentFragment;

    public BottomTabPagerAdapter(FragmentManager fm) {
        super(fm);

        fragments.clear();
        fragments.add(Tab1RecommandFragment.newInstance(0));
        //fragments.add(Tab2RequestFragment.newInstance(1));
        fragments.add(Tab3ListFragment.newInstance(2));
        //fragments.add(Tab4FeedsFragment.newInstance(3));
        fragments.add(Tab5MyPageFragment.newInstance(4));
        //fragments.add(ProfileDetailFragment.newInstance(5));
    }

    @Override
    public TabParentFragment getItem(int position) {
        return (TabParentFragment) fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (getCurrentFragment() != object) {
            currentFragment = ((TabParentFragment) object);
        }
        super.setPrimaryItem(container, position, object);
    }

    /**
     * Get the current fragment
     */
    public TabParentFragment getCurrentFragment() {
        return currentFragment;
    }
}
