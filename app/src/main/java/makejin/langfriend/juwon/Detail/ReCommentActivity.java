
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

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import makejin.langfriend.R;
import makejin.langfriend.juwon.Model.GlobalResponse;
import makejin.langfriend.juwon.Model.Posting;
import makejin.langfriend.juwon.Utils.Connections.CSConnection;
import makejin.langfriend.juwon.Utils.Connections.ServiceGenerator;
import makejin.langfriend.juwon.Utils.Constants.Constants;
import makejin.langfriend.juwon.Utils.Loadings.LoadingUtil;
import makejin.langfriend.juwon.Utils.SharedManager.SharedManager;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@EActivity(R.layout.activity_re_comment)
public class ReCommentActivity extends AppCompatActivity {
    ReCommentActivity activity;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    ReCommentAdapter adapter;


    public static int commentCnt =0 ;
    public String posting_id;
    public String comment_id;
    public List<Posting.CommentPerson> comment_person;


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
        connOneCommentPosting(ET_comment.getText().toString());
        ET_comment.setText("");
    }

    @AfterViews
    void afterBindingView() {
        this.activity = this;

        Log.i("Zxc", "asddas");

        posting_id = getIntent().getStringExtra("posting_id");
        comment_id = getIntent().getStringExtra("comment_id");
//        comment_person = getIntent().getSerializableExtra("comment_person");

        setSupportActionBar(cs_toolbar);
        getSupportActionBar().setTitle("댓글");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (recyclerView == null) {
            recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
        }



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
        //refresh();
        connGetOneCommentPosting(posting_id, comment_id);
    }



    void refresh() {
        connGetOneCommentPosting(posting_id, comment_id);
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
    void connOneCommentPosting(String comment){
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);

        Map field = new HashMap();
        field.put("me_id", SharedManager.getInstance().getMe()._id);
        field.put("comment", comment);// commenter_name
        field.put("me_name", SharedManager.getInstance().getMe().nickname);
        field.put("me_pic_small", SharedManager.getInstance().getMe().pic_small);
        conn.oneCommentPosting(posting_id, comment_id, field)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GlobalResponse>() {
                    @Override
                    public final void onCompleted() {
                        setResult(Constants.ACTIVITY_CODE_TAB2_REFRESH_RESULT);
                        //adapter.
                        //pullToRefresh.callOnClick(); // 댓글 작성하고 등록 누르면 내 댓글 새로고침 되도록 해야함
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                    }
                    @Override
                    public final void onNext(GlobalResponse response) {
                        if (response.code == 0) {
                            Toast.makeText(activity, "댓글이 정상적으로 등록되었습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(activity, Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    @Background
    void connGetOneCommentPosting(String posting_id, String comment_id){
        LoadingUtil.startLoading(indicator);
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.getOneCommentPosting(posting_id, comment_id)
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
                        if (response != null) {
                            //Toast.makeText(activity, "특정 댓글이 정상적으로 리프레쉬되었습니다.", Toast.LENGTH_SHORT).show();
                            if (adapter == null) {
                                adapter = new ReCommentAdapter(new ReCommentAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position) {

                                    }
                                }, activity, activity, response);
                            }
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            LoadingUtil.stopLoading(indicator);
                            pullToRefresh.setRefreshing(false);
                        } else {
                            Toast.makeText(activity, Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
