package makejin.langfriend.juwon.Detail;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;
import makejin.langfriend.juwon.Model.Posting;
import makejin.langfriend.juwon.Model.GlobalResponse;
import makejin.langfriend.juwon.Model.User;
import makejin.langfriend.R;
import makejin.langfriend.juwon.Utils.Connections.CSConnection;
import makejin.langfriend.juwon.Utils.Connections.ServiceGenerator;
import makejin.langfriend.juwon.Utils.Constants.Constants;
import makejin.langfriend.juwon.Utils.Loadings.LoadingUtil;
import makejin.langfriend.juwon.Utils.SharedManager.SharedManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@EActivity(R.layout.activity_detail)
public class DetailActivity extends AppCompatActivity {
    DetailActivity activity;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    DetailAdapter adapter;
    private Posting posting;

    public static int commentCnt =0 ;

    @ViewById
    SwipeRefreshLayout pullToRefresh;

    @ViewById
    Toolbar cs_toolbar;

    @ViewById
    public LinearLayout indicator;

    @ViewById
    public CircleImageView CIV_pic;

    @ViewById
    public EditText ET_comment;

    @ViewById
    public Button BT_comment;

    @Click
    void BT_comment() {
        connCommentPosting(ET_comment.getText().toString());
        ET_comment.setText("");
    }

    @AfterViews
    void afterBindingView() {
        this.activity = this;

        posting = (Posting) getIntent().getSerializableExtra("posting");

        commentCnt = posting.comment_cnt;

        setSupportActionBar(cs_toolbar);
        getSupportActionBar().setTitle(posting.name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (recyclerView == null) {
            recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
        }

        if (adapter == null) {
            adapter = new DetailAdapter(new DetailAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {

                }
            }, this, this, posting);
        }
        recyclerView.setAdapter(adapter);

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LoadingUtil.startLoading(indicator);
                refresh();
            }
        });

        Glide.with(activity).
                load(SharedManager.getInstance().getMe().getPic_small()).
                into(CIV_pic);



        refresh();
//        connPostingView();
//        connLikedPerson();
//        connGetCommentPosting();
    }



    void refresh() {
        connPostingView();
        connLikedPerson();
        connGetCommentPosting();
        adapter.notifyDataSetChanged();
        LoadingUtil.stopLoading(indicator);
        pullToRefresh.setRefreshing(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("hansjin", "onActivityResult");
        if (requestCode == Constants.ACTIVITY_CODE_TAB2_REFRESH_REQUEST) {
            if (resultCode == Constants.ACTIVITY_CODE_TAB2_REFRESH_RESULT) {
                Log.d("hansjin", "refresh");
                refresh();
            }
        }
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

    @Background
    void connPostingView() {
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.postingView(SharedManager.getInstance().getMe()._id, posting._id)
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
                            adapter.imageHolder.viewCnt.setText("View " + ++posting.view_cnt);
                            adapter.imageHolder.viewCnt.invalidate();
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Background
    void connLikedPerson() {
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.getLikedPerson(posting._id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<User>>() {
                    @Override
                    public final void onCompleted() {
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                    }
                    @Override
                    public final void onNext(List<User> response) {
                        if (response != null && response.size()>0) {
                            adapter.personList = response;
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    @Background
    void connCommentPosting(String comment){
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);

        Map field = new HashMap();
        field.put("me_id", SharedManager.getInstance().getMe()._id);
        field.put("comment", comment);// commenter_name
        field.put("me_name", SharedManager.getInstance().getMe().nickname);
        field.put("me_pic_small", SharedManager.getInstance().getMe().pic_small);
        conn.commentPosting(posting._id, field)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Posting.CommentPerson>>() {
                    @Override
                    public final void onCompleted() {
                        //setResult(Constants.ACTIVITY_CODE_TAB2_REFRESH_RESULT);
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                    }
                    @Override
                    public final void onNext(List<Posting.CommentPerson> response) {
                        if (response != null) {
                            Toast.makeText(activity, "댓글이 정상적으로 등록되었습니다.", Toast.LENGTH_SHORT).show();
                            adapter.posting.comment_person = response;
                            adapter.notifyDataSetChanged();
//                            mDataset.get(index).like_cnt = response.like_cnt;
//                            mDataset.get(index).like_person = response.like_person;
//                            notifyDataSetChanged();
                        } else {
                            Toast.makeText(activity, Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    @Background
    void connGetCommentPosting(){
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.getCommentPosting(posting._id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Posting.CommentPerson>>() {
                    @Override
                    public final void onCompleted() {

                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                    }
                    @Override
                    public final void onNext(List<Posting.CommentPerson> response) {
                        if (response.size() != 0) {
                            Toast.makeText(activity, "댓글이 정상적으로 리프레쉬되었습니다.", Toast.LENGTH_SHORT).show();
                            //posting.comment_person = response;
                            adapter.posting.comment_person = response;
                            adapter.notifyDataSetChanged();
                        } else {
                            //Toast.makeText(activity, "댓글이 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
