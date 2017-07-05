package makejin.langfriend.juwon.TabActivity.Tab5MyPage;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import de.hdodenhof.circleimageview.CircleImageView;
import makejin.langfriend.juwon.Detail.DetailActivity_;
import makejin.langfriend.juwon.Model.Posting;
import makejin.langfriend.juwon.Model.User;
import makejin.langfriend.R;
import makejin.langfriend.juwon.TabActivity.ParentFragment.TabParentFragment;
import makejin.langfriend.juwon.TabActivity.TabActivity;
import makejin.langfriend.juwon.Utils.Connections.CSConnection;
import makejin.langfriend.juwon.Utils.Connections.ServiceGenerator;
import makejin.langfriend.juwon.Utils.Constants.Constants;
import makejin.langfriend.juwon.Utils.Loadings.LoadingUtil;
import makejin.langfriend.juwon.Utils.SharedManager.SharedManager;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by kksd0900 on 16. 10. 11..
 */

public class Tab5MyPageFragment extends TabParentFragment {
    public static TabActivity activity;

    public Tab5MyPageAdapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    public static LinearLayout indicator;
    public int page = 1;
    public boolean endOfPage = false;
    SwipeRefreshLayout pullToRefresh;
    Button BT_setting;
    public static CircleImageView IV_profile;

    TextView TV_user_name;
    public static TextView TV_about_me;

    String image_url;

    RelativeLayout RL_pencil;



    /**
     * Create a new instance of the fragment
     */
    public static Tab5MyPageFragment newInstance(int index) {
        Tab5MyPageFragment fragment = new Tab5MyPageFragment();
        Bundle b = new Bundle();
        b.putInt("index", index);
        fragment.setArguments(b);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mypage, container, false);
        initViewSetting(view);
        return view;
    }

    private void initViewSetting(View view) {
        final TabActivity tabActivity = (TabActivity) getActivity();
        this.activity = tabActivity;

        BT_setting = (Button)view.findViewById(R.id.BT_setting);
        IV_profile = (CircleImageView) view.findViewById(R.id.IV_profile);

        Toolbar cs_toolbar = (Toolbar)view.findViewById(R.id.cs_toolbar);
        activity.setSupportActionBar(cs_toolbar);
        activity.getSupportActionBar().setTitle("");

        TV_user_name = (TextView) view.findViewById(R.id.TV_user_name);
        TV_about_me = (TextView) view.findViewById(R.id.TV_about_me);

        RL_pencil = (RelativeLayout) view.findViewById(R.id.RL_pencil);

        if (recyclerView == null) {
            recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new GridLayoutManager(activity, 2));
        }
        if (adapter == null) {
            adapter = new Tab5MyPageAdapter(new Tab5MyPageAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent intent = new Intent(activity, DetailActivity_.class);
                    intent.putExtra("posting", adapter.mDataset.get(position));
                    startActivity(intent);
                    activity.overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                }
            }, activity, this);
        }
        recyclerView.setAdapter(adapter);

        indicator = (LinearLayout)view.findViewById(R.id.indicator);
        pullToRefresh = (SwipeRefreshLayout) view.findViewById(R.id.pull_to_refresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefresh.setRefreshing(false);
                refresh();
            }
        });

        BT_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Setting.class));
            }
        });
        IV_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ProfileDetailActivity.class);
                startActivityForResult(intent, Constants.ACTIVITY_CODE_TAB5_REFRESH_REQUEST);
            }
        });
        RL_pencil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ProfileDetailActivity.class);
                intent.putExtra("edit", true);
                startActivityForResult(intent, Constants.ACTIVITY_CODE_TAB5_REFRESH_REQUEST);
            }
        });

    }

    @Override
    public void refresh() {
        page = 1;
        endOfPage = false;
        adapter.clear();
        adapter.notifyDataSetChanged();
        //connectTestCall();
        connectTestCall_UserInfo();
    }

    @Override
    public void reload() {
        refresh();
    }

    void connectTestCall() {
        LoadingUtil.startLoading(indicator);
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.getLikedPosting(SharedManager.getInstance().getMe()._id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Posting>>() {
                    @Override
                    public final void onCompleted() {
                        LoadingUtil.stopLoading(indicator);
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(List<Posting> response) {
                        if (response != null) {
                            adapter.clear();
                            for (Posting posting : response) {
                                adapter.addData(posting);
                            }
                            adapter.notifyDataSetChanged();

                        } else {
                            Toast.makeText(getActivity(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    void connectTestCall_UserInfo() {
//        LoadingUtil.startLoading(indicator);
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.getUserInfo(SharedManager.getInstance().getMe()._id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public final void onCompleted() {
//                        LoadingUtil.stopLoading(indicator);
                        TV_user_name.setText(SharedManager.getInstance().getMe().nickname);
                        TV_about_me.setText(SharedManager.getInstance().getMe().about_me);
                        image_url = SharedManager.getInstance().getMe().getPic(0); //SharedManager.getInstance().getMe().pic_small;

                        if(image_url == null)
                            return;

                        LoadingUtil.startLoading(indicator);
                        Glide.with(activity).
                                load(image_url).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).
                                listener(new RequestListener<String, GlideDrawable>() {
                                    @Override
                                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                        LoadingUtil.stopLoading(indicator);
                                        return false;
                                    }
                                }).
                                into(IV_profile);
                        Log.i("zxc", "image_url : " + image_url);


                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(User response) {
                        if (response != null) {
                            SharedManager.getInstance().setMe(response);
                        } else {
                            Toast.makeText(getActivity(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.ACTIVITY_CODE_TAB5_REFRESH_REQUEST) {
            if (resultCode == Constants.ACTIVITY_CODE_TAB5_REFRESH_RESULT) {
                refresh();
            }
        }
    }

}
