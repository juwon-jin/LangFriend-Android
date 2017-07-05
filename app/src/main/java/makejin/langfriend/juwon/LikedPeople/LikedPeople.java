package makejin.langfriend.juwon.LikedPeople;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import makejin.langfriend.juwon.Model.Posting;
import makejin.langfriend.juwon.Model.User;
import makejin.langfriend.R;
import makejin.langfriend.juwon.Utils.Connections.CSConnection;
import makejin.langfriend.juwon.Utils.Connections.ServiceGenerator;
import makejin.langfriend.juwon.Utils.Constants.Constants;
import makejin.langfriend.juwon.Utils.Loadings.LoadingUtil;
import makejin.langfriend.juwon.Utils.SharedManager.SharedManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@EActivity(R.layout.activity_liked_people)
public class LikedPeople extends AppCompatActivity {
    LikedPeople activity;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    LikedPeopleAdapter adapter;

    public static double lat = 0.0;
    public static double lon = 0.0;

    @ViewById
    SwipeRefreshLayout pullToRefresh;

    @ViewById
    Toolbar cs_toolbar;

    @ViewById
    public LinearLayout indicator;

    @ViewById
    Button BT_X;

    private Posting posting;

    public static ActionBar actionBar;

    @AfterViews
    void afterBindingView() {
        this.activity = this;

        posting = (Posting) getIntent().getSerializableExtra("posting");

        setSupportActionBar(cs_toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("<" + posting.name + "> 좋아한 사람들");


        if (recyclerView == null) {
            recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
        }

        if (adapter == null) {
            adapter = new LikedPeopleAdapter(new LikedPeopleAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {

                }
            }, this, this, posting);
        }
        recyclerView.setAdapter(adapter);

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
                connectTestCall(posting._id);
            }
        });

        connectTestCall(posting._id);

    }

    void refresh() {
        adapter.clear();
        adapter.notifyDataSetChanged();
        pullToRefresh.setRefreshing(false);
    }

    @UiThread
    void uiThread(List<User> response) {
        List<User> response_friends = new ArrayList<>();
        List<User> response_noFriends = new ArrayList<>();
        List<String> friend_list;

        try {
            friend_list = SharedManager.getInstance().getMe().friends_id();
            //friend_list -> null 아님

            for(User user : response){
                if(friend_list.contains(user._id)){
                    response_friends.add(0, user);
                }else{
                    //본인이 좋아요 눌렀으면 최상단에
                    if(SharedManager.getInstance().getMe()._id.equals(user._id)){
                        adapter.addData(user);
                        continue;
                    }
                    response_noFriends.add(0, user);
                }
            }
        }catch (NullPointerException NE){
            //friend_list -> null

            for (User user : response) {
                //본인이 좋아요 눌렀으면 최상단에
                if(SharedManager.getInstance().getMe()._id.equals(user._id)){
                    adapter.addData(user);
                    continue;
                }
                response_noFriends.add(0, user);
            }
        }


        for (User user : response_friends) {
            adapter.addData(user);
        }

        for (User user : response_noFriends) {
            adapter.addData(user);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.anim_exit_in, R.anim.anim_exit_out);
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void onBackPressed() {
        super.finish();
        overridePendingTransition(R.anim.anim_exit_in, R.anim.anim_exit_out);
    }

    @Override
    public void finish() {
        super.finish();
    }

    void connectTestCall(String posting_id) {
        LoadingUtil.startLoading(indicator);
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.getLikedPerson(posting_id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<User>>() {
                    @Override
                    public final void onCompleted() {
                        LoadingUtil.stopLoading(indicator);
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(List<User> response) {
                        if (response != null) {
                            uiThread(response);
                        } else {
                            Toast.makeText(getApplicationContext(), "좋아요한 사람이 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}

