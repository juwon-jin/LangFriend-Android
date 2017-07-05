//package makejin.langfriend.juwon.TabActivity.Tab2Request;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.widget.SwipeRefreshLayout;
//import android.support.v7.app.ActionBar;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.support.v7.widget.Toolbar;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import makejin.langfriend.juwon.Model.User;
//import makejin.langfriend.R;
//import makejin.langfriend.juwon.TabActivity.ParentFragment.TabParentFragment;
//import makejin.langfriend.juwon.TabActivity.Tab2Request.Tab2RequestAdapter;
//import makejin.langfriend.juwon.TabActivity.TabActivity;
//import makejin.langfriend.juwon.Utils.Connections.CSConnection;
//import makejin.langfriend.juwon.Utils.Connections.ServiceGenerator;
//import makejin.langfriend.juwon.Utils.Constants.Constants;
//import makejin.langfriend.juwon.Utils.Loadings.LoadingUtil;
//import makejin.langfriend.juwon.Utils.SharedManager.SharedManager;
//
//import com.bumptech.glide.Glide;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import rx.Subscriber;
//import rx.android.schedulers.AndroidSchedulers;
//import rx.schedulers.Schedulers;
//
///**
// * Created by kksd0900 on 16. 10. 11..
// */
//public class Tab2RequestFragment extends TabParentFragment {
//    TabActivity activity;
//
//    public static LinearLayout indicator;
//
//    public Tab2RequestAdapter adapter;
//
//    private RecyclerView recyclerView;
//    private RecyclerView.LayoutManager layoutManager;
//    public int page = 1;
//    public boolean endOfPage = false;
//    SwipeRefreshLayout pullToRefresh;
//
//
//    List<User> recommandedUsers = new ArrayList<>();
//
//    public static int requestingFriendCnt = 0;
//    public static ActionBar actionBar;
//
//    public int index = 0;
//    /**
//     * Create a new instance of the fragment
//     */
//    public static Tab2RequestFragment newInstance(int index) {
//        Tab2RequestFragment fragment = new Tab2RequestFragment();
//        Bundle b = new Bundle();
//        b.putInt("index", index);
//        fragment.setArguments(b);
//        return fragment;
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_request, container, false);
//        initViewSetting(view);
//        return view;
//    }
//
//    private void initViewSetting(View view) {
//        final TabActivity tabActivity = (TabActivity) getActivity();
//        this.activity = tabActivity;
//
//        Toolbar cs_toolbar = (Toolbar)view.findViewById(R.id.cs_toolbar);
//        activity.setSupportActionBar(cs_toolbar);
//        actionBar = activity.getSupportActionBar();
//
//
//        if (recyclerView == null) {
//            recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
//            recyclerView.setHasFixedSize(true);
//            layoutManager = new LinearLayoutManager(activity);
//            recyclerView.setLayoutManager(layoutManager);
//        }
//
//        if (adapter == null) {
//            adapter = new Tab2RequestAdapter(new Tab2RequestAdapter.OnItemClickListener() {
//                @Override
//                public void onItemClick(View view, int position) {
//                }
//            }, activity, this);
//        }
//        recyclerView.setAdapter(adapter);
//
//        indicator = (LinearLayout)view.findViewById(R.id.indicator);
//        pullToRefresh = (SwipeRefreshLayout) view.findViewById(R.id.pull_to_refresh);
//        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                pullToRefresh.setRefreshing(false);
//                refresh();
//            }
//        });
//
//        connRequest(1);
//    }
//
//    @Override
//    public void refresh() {
//        page = 1;
//        endOfPage = false;
//        adapter.clear();
//        adapter.notifyDataSetChanged();
//        connRequest(page);
//    }
//
//    @Override
//    public void reload() {
//        page = 1;
//        endOfPage = false;
//        adapter.clear();
//        adapter.notifyDataSetChanged();
//        connRequest(page);
//    }
//
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == Constants.ACTIVITY_CODE_TAB2_REFRESH_REQUEST) {
//            if (resultCode == Constants.ACTIVITY_CODE_TAB2_REFRESH_RESULT) {
//                page = 1;
//                endOfPage = false;
//                adapter.clear();
//                adapter.notifyDataSetChanged();
//                connRequest(page);
//            }
//        }
//    }
//
//    void connRequest(final int page_num) {
//        LoadingUtil.startLoading(indicator);
//        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
//        conn.getRequests(SharedManager.getInstance().getMe()._id, page_num)
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<List<User>>() {
//                    @Override
//                    public final void onCompleted() {
//                        LoadingUtil.stopLoading(indicator);
//                        adapter.notifyDataSetChanged();
//                        actionBar.setTitle("친구 요청 - " + adapter.getItemCount() + "명");
//                        pullToRefresh.setRefreshing(false);
//                    }
//                    @Override
//                    public final void onError(Throwable e) {
//                        e.printStackTrace();
//                        actionBar.setTitle("친구 요청 - " + 0 + "명");
//                        Toast.makeText(getActivity().getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
//                    }
//                    @Override
//                    public final void onNext(List<User> response) {
//                        if (response != null && response.size()>0) {
//                            for (User user : response) {
//                                adapter.addData(user);
//                            }
//                            adapter.notifyDataSetChanged();
//                            actionBar.setTitle("친구 요청 - " + adapter.getItemCount() + "명");
//                        } else {
//                            endOfPage = true;
//                        }
//                    }
//                });
//    }
//
//}
