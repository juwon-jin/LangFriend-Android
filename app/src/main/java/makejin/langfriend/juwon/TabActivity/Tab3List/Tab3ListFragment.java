package makejin.langfriend.juwon.TabActivity.Tab3List;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import makejin.langfriend.juwon.Model.User;
import makejin.langfriend.R;
import makejin.langfriend.juwon.TabActivity.ParentFragment.TabParentFragment;
import makejin.langfriend.juwon.TabActivity.TabActivity;
import makejin.langfriend.juwon.Utils.Connections.CSConnection;
import makejin.langfriend.juwon.Utils.Connections.ServiceGenerator;
import makejin.langfriend.juwon.Utils.Constants.Constants;
import makejin.langfriend.juwon.Utils.Loadings.LoadingUtil;
import makejin.langfriend.juwon.Utils.SharedManager.SharedManager;

import java.util.ArrayList;
import java.util.List;

import me.xiaopan.psts.PagerSlidingTabStrip;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static makejin.langfriend.juwon.TabActivity.Tab3List.Tab3ListAdapter.mDataset;

/**
 * Created by kksd0900 on 16. 10. 11..
 */
public class Tab3ListFragment extends TabParentFragment {
    TabActivity activity;

    public static LinearLayout indicator;
    public Tab3ListAdapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    public int page = 1;
    public boolean endOfPage = false;
    SwipeRefreshLayout pullToRefresh;


    public static LinearLayout indicator2;
    public Tab3ListAdapter2 adapter2;
    private RecyclerView recyclerView2;
    private RecyclerView.LayoutManager layoutManager2;
    public int page2 = 1;
    public boolean endOfPage2 = false;
    SwipeRefreshLayout pullToRefresh2;

    public static LinearLayout indicator3;
    public Tab3ListAdapter3 adapter3;
    private RecyclerView recyclerView3;
    private RecyclerView.LayoutManager layoutManager3;
    public int page3 = 1;
    public boolean endOfPage3 = false;
    SwipeRefreshLayout pullToRefresh3;

    public int index = 0;

    public int current_tab_num = 1;

    public LinearLayout LL_left;

    public TextView TV_request;
    public TextView TV_request2;
    public TextView TV_request3;

    RelativeLayout RL_IV_yes_2;
    RelativeLayout RL_IV_no_2;



    /**
     * Create a new instance of the fragment
     */
    public static Tab3ListFragment newInstance(int index) {
        Tab3ListFragment fragment = new Tab3ListFragment();
        Bundle b = new Bundle();
        b.putInt("index", index);
        fragment.setArguments(b);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        PagerSlidingTabStrip pagerSlidingTabStrip1 = (PagerSlidingTabStrip) view.findViewById(R.id.slidingTabStrip_1);
        ViewPager viewPager1 = (ViewPager) view.findViewById(R.id.viewPager_1);

        init(0, pagerSlidingTabStrip1, viewPager1, view);

        Toolbar cs_toolbar = (Toolbar) view.findViewById(R.id.cs_toolbar);
        activity.setSupportActionBar(cs_toolbar);
        activity.getSupportActionBar().setTitle("");


        return view;
    }

    private void init(int index, PagerSlidingTabStrip pagerSlidingTabStrip, ViewPager viewPager, View view){
        int length = pagerSlidingTabStrip.getTabCount();
        List<View> views = new ArrayList<View>(length);

        views.add(view.inflate(getActivity(), R.layout.fragment_request, null)); // 1. 내가 친구 요청
        views.add(view.inflate(getActivity(), R.layout.fragment_request2, null)); // 2. 너가 친구 요청
        views.add(view.inflate(getActivity(), R.layout.fragment_request3, null)); // 3. 우린 이미 친구

        viewPager.setAdapter(new ViewPagerAdapter(views));
        viewPager.setCurrentItem(index < length ? index : length);
        pagerSlidingTabStrip.setViewPager(viewPager);

        pagerSlidingTabStrip.setOnClickTabListener(new PagerSlidingTabStrip.OnClickTabListener() {
            @Override
            public void onClickTab(View tab, int index) {
                //Toast.makeText(getActivity(), "index : " + (index + 1) + "TAB", Toast.LENGTH_SHORT).show();
                current_tab_num = index+1;
                refresh(current_tab_num);
            }
        });

        TV_request = (TextView) view.findViewById(R.id.TV_request);

        TV_request2 = (TextView) view.findViewById(R.id.TV_request2);

        TV_request3 = (TextView) view.findViewById(R.id.TV_request3);

        RL_IV_yes_2 = (RelativeLayout) view.findViewById(R.id.RL_IV_yes_2);
        RL_IV_no_2 = (RelativeLayout) view.findViewById(R.id.RL_IV_no_2);


        initViewSetting(views);
    }

    private void initViewSetting(List<View> views) {
        final TabActivity tabActivity = (TabActivity) getActivity();
        this.activity = tabActivity;


        // 1. 내가 친구 요청 <시작>
        if (recyclerView == null) {
            recyclerView = (RecyclerView) views.get(0).findViewById(R.id.recycler_view);
            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(activity);
            recyclerView.setLayoutManager(layoutManager);
        }

        if (adapter == null) {
            adapter = new Tab3ListAdapter(new Tab3ListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                }
            }, activity, this);
        }
        recyclerView.setAdapter(adapter);

        LL_left = (LinearLayout) views.get(0).findViewById(R.id.LL_left);
        indicator = (LinearLayout) views.get(0).findViewById(R.id.indicator);
        pullToRefresh = (SwipeRefreshLayout) views.get(0).findViewById(R.id.pull_to_refresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefresh.setRefreshing(false);
                refresh(1);
            }
        });


        // 1. 내가 친구 요청 <끝>

        // 2. 너가 친구 요청 <시작>
        if (recyclerView2 == null) {
            recyclerView2 = (RecyclerView) views.get(1).findViewById(R.id.recycler_view2);
            recyclerView2.setHasFixedSize(true);
            layoutManager2 = new LinearLayoutManager(activity);
            recyclerView2.setLayoutManager(layoutManager2);
        }

        if (adapter2 == null) {
            adapter2 = new Tab3ListAdapter2(new Tab3ListAdapter2.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                }
            }, activity, this);
        }
        recyclerView2.setAdapter(adapter2);

        indicator2 = (LinearLayout) views.get(1).findViewById(R.id.indicator2);
        pullToRefresh2 = (SwipeRefreshLayout) views.get(1).findViewById(R.id.pull_to_refresh2);
        pullToRefresh2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefresh2.setRefreshing(false);
                refresh(2);
            }
        });


        // 2. 너가 친구 요청 <끝>

        // 3. 우린 이미 친구 <시작>
        if (recyclerView3 == null) {
            recyclerView3 = (RecyclerView) views.get(2).findViewById(R.id.recycler_view3);
            recyclerView3.setHasFixedSize(true);
            layoutManager3 = new LinearLayoutManager(activity);
            recyclerView3.setLayoutManager(layoutManager3);
        }

        if (adapter3 == null) {
            adapter3 = new Tab3ListAdapter3(new Tab3ListAdapter3.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                }
            }, activity, this);
        }
        recyclerView3.setAdapter(adapter3);

        indicator3 = (LinearLayout) views.get(2).findViewById(R.id.indicator3);
        pullToRefresh3 = (SwipeRefreshLayout) views.get(2).findViewById(R.id.pull_to_refresh3);
        pullToRefresh3.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefresh3.setRefreshing(false);
                refresh(3);
            }
        });


        // 3. 우린 이미 친구 <끝>

        ItemTouchHelper.SimpleCallback simpleCallback_right =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }




                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        //do things
                        user_accept(mDataset.get(viewHolder.getAdapterPosition()), viewHolder.getAdapterPosition());
                        RL_IV_yes_2.setVisibility(View.VISIBLE);
                        RL_IV_no_2.setVisibility(View.INVISIBLE);
                        new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
                            @Override
                            public void run() {
                                RL_IV_yes_2.setVisibility(View.INVISIBLE);
                            }
                        }, 1000);
                    }
                };

        ItemTouchHelper.SimpleCallback simpleCallback_left =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        //do things
                        user_reject(mDataset.get(viewHolder.getAdapterPosition()), viewHolder.getAdapterPosition());
                        RL_IV_no_2.setVisibility(View.VISIBLE);
                        RL_IV_yes_2.setVisibility(View.INVISIBLE);
                        new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
                            @Override
                            public void run() {
                                RL_IV_no_2.setVisibility(View.INVISIBLE);
                            }
                        }, 1000);
                    }
                };


        ItemTouchHelper itemTouchHelper_right = new ItemTouchHelper(simpleCallback_right);
        ItemTouchHelper itemTouchHelper_left = new ItemTouchHelper(simpleCallback_left);

        itemTouchHelper_right.attachToRecyclerView(recyclerView);
        itemTouchHelper_left.attachToRecyclerView(recyclerView);

        refresh(1);
        refresh(2);
        refresh(3);
    }

    public void user_accept(User you, final int index) {
        LoadingUtil.startLoading(Tab3ListFragment.indicator);
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.acceptYou(you, SharedManager.getInstance().getMe()._id, you._id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public final void onCompleted() {
                        LoadingUtil.stopLoading(Tab3ListFragment.indicator);
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
                            mDataset.remove(index);
                            adapter.notifyDataSetChanged();
                            refresh(1);
                            refresh(2);
                            refresh(3);
                        } else {
                            Toast.makeText(getActivity(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void user_reject(User you, final int index) {
        LoadingUtil.startLoading(Tab3ListFragment.indicator);
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.rejectYou(you, SharedManager.getInstance().getMe()._id, you._id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public final void onCompleted() {
                        LoadingUtil.stopLoading(Tab3ListFragment.indicator);
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
                            mDataset.remove(index);
                            adapter.notifyDataSetChanged();
                            refresh(1);
                            refresh(2);
                            refresh(3);
                        } else {
                            Toast.makeText(getActivity(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    @Override
    public void refresh() {
        refresh(current_tab_num);
    }

    @Override
    public void reload() {
        //refresh(current_tab_num);
        refresh(1);
        refresh(2);
        refresh(3);
    }

    public void refresh(int i) {
        Log.i("zxc", "refresh " + i);
        switch (i) {
            case 1:
                page = 1;
                endOfPage = false;
                adapter.clear();
                adapter.notifyDataSetChanged();
                connRequest(page);
                break;
            case 2:
                page2 = 1;
                endOfPage2 = false;
                adapter2.clear();
                adapter2.notifyDataSetChanged();
                connRequest2(page2);
                break;
            case 3:
                page3 = 1;
                endOfPage3 = false;
                adapter3.clear();
                adapter3.notifyDataSetChanged();
                connRequest3(page3);
                break;
        }
    }

    // 1. 너가 친구 요청
    void connRequest(final int page_num) {
        LoadingUtil.startLoading(indicator);
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.getRequests(SharedManager.getInstance().getMe()._id, page_num)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<User>>() {
                    @Override
                    public final void onCompleted() {
                        LoadingUtil.stopLoading(indicator);
                        adapter.notifyDataSetChanged();
                        //actionBar.setTitle("친구 요청 - " + adapter.getItemCount() + "명");
                        pullToRefresh.setRefreshing(false);
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        //actionBar.setTitle("친구 요청 - " + 0 + "명");
                        Toast.makeText(getActivity().getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(List<User> response) {
                        if (response != null && response.size()>0) {
                            for (User user : response) {
                                //Log.i("zxc", "user.nickname 2 : " +  user.nickname);
                                adapter.addData(user);
                            }
                            TV_request.setText(Integer.toString(response.size()));
                            adapter.notifyDataSetChanged();
                            //actionBar.setTitle("친구 요청 - " + adapter.getItemCount() + "명");
                        } else {
                            TV_request.setText(Integer.toString(0));
                            endOfPage = true;
                        }
                    }

               });
    }


    // 2. 내가 친구 요청
    void connRequest2(final int page_num) {
        LoadingUtil.startLoading(indicator2);
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.getRequests2(SharedManager.getInstance().getMe()._id, page_num)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<User>>() {
                    @Override
                    public final void onCompleted() {
                        LoadingUtil.stopLoading(indicator2);
                        adapter2.notifyDataSetChanged();
                        //actionBar.setTitle("친구 요청 - " + adapter.getItemCount() + "명");
                        pullToRefresh2.setRefreshing(false);
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        //actionBar.setTitle("친구 요청 - " + 0 + "명");
                        Toast.makeText(getActivity().getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(List<User> response) {
                        if (response != null && response.size()>0) {
                            for (User user : response) {
                                //Log.i("zxc", "user.nickname 2 : " +  user.nickname);
                                adapter2.addData(user);
                            }
                            TV_request2.setText(Integer.toString(response.size()));
                            adapter2.notifyDataSetChanged();
                            //actionBar.setTitle("친구 요청 - " + adapter.getItemCount() + "명");
                        } else {
                            TV_request2.setText(Integer.toString(0));
                            endOfPage2 = true;
                        }
                    }

                });
    }

    // 3. 우린 이미 친구
    void connRequest3(final int page_num) {
        LoadingUtil.startLoading(indicator3);
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.getRequests3(SharedManager.getInstance().getMe()._id, page_num)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<User>>() {
                    @Override
                    public final void onCompleted() {
                        LoadingUtil.stopLoading(indicator3);
                        adapter3.notifyDataSetChanged();
                        //actionBar.setTitle("친구 요청 - " + adapter.getItemCount() + "명");
                        pullToRefresh3.setRefreshing(false);
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        //actionBar.setTitle("친구 요청 - " + 0 + "명");
                        Toast.makeText(getActivity().getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(List<User> response) {
                        if (response != null && response.size()>0) {
                            for (User user : response) {
                                //Log.i("zxc", "user.nickname 2 : " +  user.nickname);
                                adapter3.addData(user);
                            }
                            TV_request3.setText(Integer.toString(response.size()));
                            adapter3.notifyDataSetChanged();
                            //actionBar.setTitle("친구 요청 - " + adapter.getItemCount() + "명");
                        } else {
                            TV_request3.setText(Integer.toString(0));
                            endOfPage3 = true;
                        }
                    }

                });
    }

}
