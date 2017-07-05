package makejin.langfriend.juwon.TabActivity.Tab4Feeds;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import makejin.langfriend.juwon.Activity.RegisterActivity_;
import makejin.langfriend.R;
import makejin.langfriend.juwon.Model.Category;
import makejin.langfriend.juwon.Model.Posting;
import makejin.langfriend.juwon.TabActivity.ParentFragment.TabParentFragment;
import makejin.langfriend.juwon.TabActivity.TabActivity;
import makejin.langfriend.juwon.Utils.Connections.CSConnection;
import makejin.langfriend.juwon.Utils.Connections.ServiceGenerator;
import makejin.langfriend.juwon.Utils.Constants.Constants;
import makejin.langfriend.juwon.Utils.Loadings.LoadingUtil;
import makejin.langfriend.juwon.Utils.SharedManager.SharedManager;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by kksd0900 on 16. 10. 11..
 */
public class Tab4FeedsFragment extends TabParentFragment {
    public static TabActivity activity;

    public Tab4FeedsAdapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    public LinearLayout indicator;
    ImageButton fab;
    public int page = 1;
    public boolean endOfPage = false;
    SwipeRefreshLayout pullToRefresh;

    /**
     * Create a new instance of the fragment
     */
    public static Tab4FeedsFragment newInstance(int index) {
        Tab4FeedsFragment fragment = new Tab4FeedsFragment();
        Bundle b = new Bundle();
        b.putInt("index", index);
        fragment.setArguments(b);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feeds, container, false);
        //initViewSetting(view);
        connectCategory(view);
        return view;
    }

    private void initViewSetting(View view) {
        final TabActivity tabActivity = (TabActivity) getActivity();
        this.activity = tabActivity;

        Toolbar cs_toolbar = (Toolbar)view.findViewById(R.id.cs_toolbar);
        activity.setSupportActionBar(cs_toolbar);
        activity.getSupportActionBar().setTitle("LangFriend");

        if (recyclerView == null) {
            recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(activity);
            recyclerView.setLayoutManager(layoutManager);
        }

        if (adapter == null) {
            adapter = new Tab4FeedsAdapter(new Tab4FeedsAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
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

        fab = (ImageButton)view.findViewById(R.id.add_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: 음식 업로드 페이지로 이동
                Intent intent = new Intent(activity, RegisterActivity_.class);
                //intent.putExtra("UserInfo", user); //user 정보가 들어있는 객체 전달
                startActivityForResult(intent, Constants.ACTIVITY_CODE_TAB2_REFRESH_REQUEST);
            }
        });



    }

    @Override
    public void refresh() {
        page = 1;
        endOfPage = false;
        adapter.clear();
        adapter.notifyDataSetChanged();
        connRecommand(getField(), 1);
    }

    @Override
    public void reload() {
        refresh();
    }

    public Category getField() {
        Category field = new Category();
        field.location = new ArrayList<String>();
        field.language = new ArrayList<String>();
        field.activity = new ArrayList<String>();
        return field;
    }

    void connectCategory(final View view) {
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.getCategoryList()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Category>() {
                    @Override
                    public final void onCompleted() {
                        LoadingUtil.stopLoading(indicator);
                        initViewSetting(view);
                        //adapter.notifyDataSetChanged();
                        //pullToRefresh.setRefreshing(false);
                    }

                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity().getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public final void onNext(Category response) {
                        if (response != null) {
                            SharedManager.getInstance().setCategory(response);
                            connRecommand(getField(), 1);
                        }
                    }
                });
    }

    void connRecommand(Category field, final int page_num) {
        LoadingUtil.startLoading(indicator);
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.getRecommandPosts(field, SharedManager.getInstance().getMe()._id, page_num)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Posting>>() {
                    @Override
                    public final void onCompleted() {
                        LoadingUtil.stopLoading(indicator);
                        adapter.notifyDataSetChanged();
                        pullToRefresh.setRefreshing(false);
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity().getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(List<Posting> response) {
                        if(response.size()==0) {
                            endOfPage = true;
                            return;
                        }

                        adapter.mDataset.clear();
                        if (response != null && response.size()>0) {
                            for (Posting posting : response) {
                                adapter.addData(posting);
                            }
                        }else{
                            Log.i("zxc", "이건 뭐지?");
                        }
                    }
                });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.ACTIVITY_CODE_TAB2_REFRESH_REQUEST) {
            if (resultCode == Constants.ACTIVITY_CODE_TAB2_REFRESH_RESULT) {
                page = 1;
                endOfPage = false;
                adapter.clear();
                adapter.notifyDataSetChanged();
                connRecommand(getField(),page);
            }
        }
    }

}