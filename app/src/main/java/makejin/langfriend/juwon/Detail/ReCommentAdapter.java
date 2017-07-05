package makejin.langfriend.juwon.Detail;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.eazegraph.lib.charts.ValueLineChart;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import makejin.langfriend.R;
import makejin.langfriend.juwon.LikedPeople.LikedPeople_;
import makejin.langfriend.juwon.Model.Posting;
import makejin.langfriend.juwon.Model.User;
import makejin.langfriend.juwon.Utils.Connections.CSConnection;
import makejin.langfriend.juwon.Utils.Connections.ServiceGenerator;
import makejin.langfriend.juwon.Utils.Constants.Constants;
import makejin.langfriend.juwon.Utils.Loadings.LoadingUtil;
import makejin.langfriend.juwon.Utils.SharedManager.SharedManager;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by kksd0900 on 16. 10. 16..
 */
public class ReCommentAdapter extends RecyclerView.Adapter<ReCommentAdapter.ViewHolder> {
    private static final int TYPE_IMAGE_HEADER = 0;
    private static final int TYPE_BODY_DESC = 1;
    private static final int TYPE_RANK_GRAPH = 2;
    private static final int TYPE_LIKE_PERSON = 3;
    private static final int TYPE_TAIL_SIMILAR = 4;
    private static final int TYPE_COMMENT = 5;


    public ReCommentActivity activity;
    public Context context;
    private OnItemClickListener mOnItemClickListener;
    public List<Posting.CommentPerson> comment_person = new ArrayList<>();
    public static CommentViewHolder commentViewHolder;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public ReCommentAdapter(OnItemClickListener onItemClickListener, Context mContext, ReCommentActivity mActivity, List<Posting.CommentPerson> mComment_person) {
        mOnItemClickListener = onItemClickListener;
        context = mContext;
        activity = mActivity;
        comment_person = mComment_person;
    }

    @Override
    public ReCommentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_COMMENT) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_detail_comment, parent, false);
            return new CommentViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (holder instanceof CommentViewHolder) {
            commentViewHolder = (CommentViewHolder) holder;

            addFlowChart_Comment(commentViewHolder.TFL_comment_all, comment_person);
        }


    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_COMMENT;

        return TYPE_COMMENT;
    }

    @Override
    public int getItemCount() {
        return 1;
    }


    /*
        ViewHolder
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View container;
        public ViewHolder(View itemView) {
            super(itemView);
            container = itemView;
        }
    }


    public class CommentViewHolder extends ViewHolder {
        public TextView TV_commenter_name,TV_commenter2_name, TV_comment, TV_comment2, TV_comment_info, TV_comment2_info, TV_comment_write, TV_comment_middle_dot;
        public CircleImageView CIV_pic, CIV2_pic, CIV_me_pic;
        public TagFlowLayout TFL_comment_all;
        public TagFlowLayout TFL_comment_one;
        public CommentViewHolder(View v) {
            super(v);
            TV_commenter_name = (TextView) v.findViewById(R.id.TV_commenter_name);
            TV_commenter2_name = (TextView) v.findViewById(R.id.TV_commenter2_name);
            TV_comment = (TextView) v.findViewById(R.id.TV_comment);
            TV_comment2 = (TextView) v.findViewById(R.id.TV_comment2);
            TV_comment_info = (TextView) v.findViewById(R.id.TV_comment_info);
            TV_comment2_info = (TextView) v.findViewById(R.id.TV_comment2_info);
            TV_comment_write = (TextView) v.findViewById(R.id.TV_comment_write);
            CIV_pic = (CircleImageView) v.findViewById(R.id.CIV_pic);
            CIV2_pic = (CircleImageView) v.findViewById(R.id.CIV2_pic);
            CIV_me_pic = (CircleImageView) v.findViewById(R.id.CIV_me_pic);
            TFL_comment_all = (TagFlowLayout) v.findViewById(R.id.TFL_comment_all);
            TV_comment_middle_dot = (TextView) v.findViewById(R.id.TV_comment_middle_dot);

        }
    }



    private void addFlowChart_Comment(final TagFlowLayout mFlowLayout, final List<Posting.CommentPerson> commentPerson) {
        final LayoutInflater mInflater = LayoutInflater.from(context);

        mFlowLayout.setAdapter(new TagAdapter<Posting.CommentPerson>(commentPerson){
            @Override
            public View getView(final FlowLayout parent, final int position, final Posting.CommentPerson comment_person) {
                final LinearLayout lv = (LinearLayout) mInflater.inflate(R.layout.cell_detail_comment1, mFlowLayout, false);
                Log.i("zxc555", position + " : " + comment_person.comment);

                TextView TV_commenter_name;
                TextView TV_comment;
                TextView TV_comment_info;
                CircleImageView CIV_pic;
                TextView TV_comment_write;
                TextView TV_comment_middle_dot;

                CIV_pic = (CircleImageView) lv.findViewById(R.id.CIV_pic);
                TV_commenter_name = (TextView) lv.findViewById(R.id.TV_commenter_name);
                TV_comment = (TextView) lv.findViewById(R.id.TV_comment);
                TV_comment_info = (TextView) lv.findViewById(R.id.TV_comment_info);
                TV_comment_write = (TextView) lv.findViewById(R.id.TV_comment_write);
                TV_comment_middle_dot = (TextView) lv.findViewById(R.id.TV_comment_middle_dot);

                Glide.with(context).
                        load(comment_person.user_pic_small).
                        thumbnail(0.1f).
                        into(CIV_pic);
                TV_commenter_name.setText(comment_person.user_name);
                TV_comment.setText(comment_person.comment);
                TV_comment_info.setText(comment_person.comment_date);
                TV_comment_middle_dot.setVisibility(View.GONE);
                TV_comment_write.setVisibility(View.GONE);

                //LinearLayout cell_detail_comment2 = (LinearLayout) lv.findViewById(R.id.cell_detail_comment2);
                TagFlowLayout TFL_comment_all_1 = (TagFlowLayout) lv.findViewById(R.id.TFL_comment_all_1);

                if(comment_person.re_comment_person.size()!=0) {
                    addFlowChart_Re_Comment(TFL_comment_all_1, comment_person.re_comment_person);
                }
                return lv;
            }

            @Override
            public boolean setSelected(int position, Posting.CommentPerson s) {
                return true;
            }

        });

    }


    private void addFlowChart_Re_Comment(final TagFlowLayout mFlowLayout, final List<Posting.ReCommentPerson> reCommentPerson) {
        final LayoutInflater mInflater = LayoutInflater.from(context);

        mFlowLayout.setAdapter(new TagAdapter<Posting.ReCommentPerson>(reCommentPerson){
            @Override
            public View getView(final FlowLayout parent, final int position, Posting.ReCommentPerson re_comment_person) {
                final LinearLayout lv = (LinearLayout) mInflater.inflate(R.layout.cell_detail_comment2, mFlowLayout, false);
                Log.i("zxc555 re", position + " : " + re_comment_person.comment);
                TextView TV_commenter2_name;
                TextView TV_comment2;
                TextView TV_comment2_info;
                CircleImageView CIV2_pic;

                CIV2_pic = (CircleImageView) lv.findViewById(R.id.CIV2_pic);
                TV_commenter2_name = (TextView) lv.findViewById(R.id.TV_commenter2_name);
                TV_comment2 = (TextView) lv.findViewById(R.id.TV_comment2);
                TV_comment2_info = (TextView) lv.findViewById(R.id.TV_comment2_info);

                Glide.with(context).
                        load(re_comment_person.user_pic_small).
                        thumbnail(0.1f).
                        into(CIV2_pic);
                TV_commenter2_name.setText(re_comment_person.user_name);
                TV_comment2.setText(re_comment_person.comment);
                TV_comment2_info.setText(re_comment_person.comment_date);
                return lv;
            }

            @Override
            public boolean setSelected(int position, Posting.ReCommentPerson s) {
                return true;
            }
        });

    }

}