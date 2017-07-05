package makejin.langfriend.juwon.TabActivity.Tab1Recommand;

import android.content.Intent;
import android.os.Bundle;

import de.hdodenhof.circleimageview.CircleImageView;
import makejin.langfriend.R;
import makejin.langfriend.juwon.Model.GlobalResponse;
import makejin.langfriend.juwon.Model.User;
import makejin.langfriend.juwon.Utils.Connections.CSConnection;
import makejin.langfriend.juwon.Utils.Connections.ServiceGenerator;
import makejin.langfriend.juwon.Utils.Constants.Constants;
import makejin.langfriend.juwon.Utils.Loadings.LoadingUtil;
import makejin.langfriend.juwon.Utils.SharedManager.SharedManager;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

public class DetailUserActivity extends FragmentActivity{
    ImageFragmentPagerAdapter imageFragmentPagerAdapter;
    public static ViewPager viewPager;
    public static final String[] IMAGE_NAME = {"sample_user_1", "sample_user_2", "sample_user_3", "sample_user_4",};

    public static User user;
    public static int num_items;
    public static LinearLayout LL_dot_indicator;
    public static boolean isFirst;

    public static LinearLayout indicator;

    public TextView name;
    public String image_url;

    public static DetailUserActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pager);
        imageFragmentPagerAdapter = new ImageFragmentPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(imageFragmentPagerAdapter);

        this.activity = this;
        isFirst = true;

        LL_dot_indicator = (LinearLayout) findViewById(R.id.LL_dot_indicator);

        user = (User) getIntent().getSerializableExtra("user");

        connUserView(user._id);

        num_items = user.pic_list.size();
        imageFragmentPagerAdapter.notifyDataSetChanged();

    }




    public static class ImageFragmentPagerAdapter extends FragmentPagerAdapter {
        public ImageFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return num_items;
        }

        @Override
        public Fragment getItem(int position) {
            Log.i("zxc3", "position : " + position);
            SwipeFragment fragment = new SwipeFragment();
            return SwipeFragment.newInstance(position);
        }
    }

    public static class SwipeFragment extends Fragment {

        @Override
        public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {


            View swipeView = inflater.inflate(R.layout.swipe_fragment, container, false);
            final ImageView imageView = (ImageView) swipeView.findViewById(R.id.imageView);
            Bundle bundle = getArguments();
            final int position = bundle.getInt("position");

            indicator = (LinearLayout) swipeView.findViewById(R.id.indicator);

            imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    imageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    String image_url = user.pic_list.get(position);
                    try {
                        if (image_url.contains("facebook"))
                            image_url = image_url.replace("type=large", "width=" + imageView.getWidth() + "&height=" + imageView.getHeight());
                        else
                            image_url = Constants.IMAGE_BASE_URL + image_url;
                    }catch (NullPointerException e){
                        e.printStackTrace();
                        image_url = "";
                    }
                    Log.i("asd", "image_url : " + image_url);

                    Log.i("zxc", "w : " + imageView.getWidth() + " h : " + imageView.getHeight());


                    LoadingUtil.startLoading(indicator);
                    Glide.with(activity).
                            load(image_url).diskCacheStrategy(DiskCacheStrategy.ALL).
                            listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    Log.i("zxc", "image loading err");
                                    LoadingUtil.stopLoading(indicator);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    LoadingUtil.stopLoading(indicator);
                                    return false;
                                }
                            }).
                            into(imageView);

                }
            });


            if(isFirst) { //처음
                final List<View> v = new ArrayList<>();
                setIndicator(v, 0,inflater);
                isFirst = false;
            }

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageSelected(int position) {
                    final List<View> v = new ArrayList<>();

                    switch (position){
                        case 0:
                            setIndicator(v, 0,inflater);
                            break;
                        case 1:
                            setIndicator(v, 1,inflater);
                            break;
                        case 2:
                            setIndicator(v, 2,inflater);
                            break;
                        case 3:
                            setIndicator(v, 3,inflater);
                            break;
                        case 4:
                            setIndicator(v, 4,inflater);
                            break;
                        case 5:
                            setIndicator(v, 5,inflater);
                            break;
                        default:
                            break;
                    }


                }
                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {

                }

                @Override
                public void onPageScrollStateChanged(int arg0) {

                }


            });





            return swipeView;
        }

        static SwipeFragment newInstance(int position) {
            SwipeFragment swipeFragment = new SwipeFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            swipeFragment.setArguments(bundle);
            Log.i("zxc2", "position : " + position);

            return swipeFragment;
        }

        private void setIndicator(List<View> v, int position, LayoutInflater inflater){

            for(int i=0;i<num_items;i++) {
                if (i == position) {
                    v.add(inflater.inflate(R.layout.dot_indicator_red, null));
                }
                else {
                    v.add(inflater.inflate(R.layout.dot_indicator_black, null));
                }
            }
            LL_dot_indicator.removeAllViews();
            for(int i=0;i<num_items;i++) {
                LL_dot_indicator.addView(v.get(i));
            }
        }

    }


    void connUserView(String you_id) {
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.userView(SharedManager.getInstance().getMe()._id, user._id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GlobalResponse>() {
                    @Override
                    public final void onCompleted() {
                    }

                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public final void onNext(GlobalResponse response) {
                        if (response != null && response.code == 0) {
                            //Toast.makeText(getApplicationContext(), "정상적으로 뷰이벤트 등록됨", Toast.LENGTH_SHORT).show();
                        } else {
                            //Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}

