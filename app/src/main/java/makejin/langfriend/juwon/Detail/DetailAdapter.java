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
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import de.hdodenhof.circleimageview.CircleImageView;
import makejin.langfriend.juwon.LikedPeople.LikedPeople_;
import makejin.langfriend.juwon.Model.Posting;
import makejin.langfriend.juwon.Model.User;
import makejin.langfriend.R;
import makejin.langfriend.juwon.NearbyYou.NearByYou;
import makejin.langfriend.juwon.Utils.Connections.CSConnection;
import makejin.langfriend.juwon.Utils.Connections.ServiceGenerator;
import makejin.langfriend.juwon.Utils.Constants.Constants;
import makejin.langfriend.juwon.Utils.Loadings.LoadingUtil;
import makejin.langfriend.juwon.Utils.SharedManager.SharedManager;

import org.eazegraph.lib.charts.ValueLineChart;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by kksd0900 on 16. 10. 16..
 */
public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.ViewHolder> {
    private static final int TYPE_IMAGE_HEADER = 0;
    private static final int TYPE_BODY_DESC = 1;
    private static final int TYPE_RANK_GRAPH = 2;
    private static final int TYPE_LIKE_PERSON = 3;
    private static final int TYPE_TAIL_SIMILAR = 4;
    private static final int TYPE_COMMENT = 5;


    public DetailActivity activity;
    public Context context;
    private OnItemClickListener mOnItemClickListener;
    public Posting posting;
    public List<User> personList = new ArrayList<>();
    public static ImageHeaderViewHolder imageHolder;
    public static CommentViewHolder commentViewHolder;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public DetailAdapter(OnItemClickListener onItemClickListener, Context mContext, DetailActivity mActivity, Posting mposting) {
        mOnItemClickListener = onItemClickListener;
        context = mContext;
        activity = mActivity;
        posting = mposting;
    }

    @Override
    public DetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_IMAGE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_detail_image_header, parent, false);
            return new ImageHeaderViewHolder(v);
        } else if (viewType == TYPE_BODY_DESC) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_detail_desc_body, parent, false);
            return new DescBodyViewHolder(v);
//        } else if (viewType == TYPE_RANK_GRAPH) {
//            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_detail_graph_body, parent, false);
//            return new GraphBodyViewHolder(v);
//        } else if (viewType == TYPE_LIKE_PERSON) {
//            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_detail_person_body, parent, false);
//            return new PersonBodyViewHolder(v);
//        } else if (viewType == TYPE_TAIL_SIMILAR) {
//            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_detail_simiral_tail, parent, false);
//            return new SimiralTailViewHolder(v);
        } else if (viewType == TYPE_COMMENT) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_detail_comment, parent, false);
            return new CommentViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (holder instanceof ImageHeaderViewHolder) {
            imageHolder = (ImageHeaderViewHolder) holder;
            imageHolder.indicator.setVisibility(View.VISIBLE);
            Glide.with(context).
                    load(Constants.IMAGE_BASE_URL+posting.image_url).
                    listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            imageHolder.indicator.setVisibility(View.GONE);
                            return false;
                        }
                    }).
                    into(imageHolder.postingImage);


            imageHolder.postingName.setText(posting.name);
            imageHolder.viewCnt.setText("View "+posting.view_cnt);
            imageHolder.heartImg.setImageDrawable(context.getResources().getDrawable(R.drawable.heart_gray));
            for (String uid : posting.like_person_id()) {
                if (uid.equals(SharedManager.getInstance().getMe()._id)) {
                    imageHolder.heartImg.setImageDrawable(context.getResources().getDrawable(R.drawable.heart_red));
                }
            }
            imageHolder.eatBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    posting_like(posting);
                }
            });

            imageHolder.layoutNearby.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, NearByYou.class);
                    intent.putExtra("location_point", posting.author.author_location_point);
                    activity.startActivity(intent);
                }
            });

        } else if (holder instanceof DescBodyViewHolder) {
            DescBodyViewHolder descBodyViewHolder = (DescBodyViewHolder) holder;
            descBodyViewHolder.txt_category.setText(combine_tag(posting)+"");
            descBodyViewHolder.txt_posting.setText(combine_posting_tag(posting)+"");
            if (posting.like_person.size()==0) {
                descBodyViewHolder.txt_people_like.setText("가장 먼저 좋아요를 눌러주세요!");
            }
            else {
                User me = SharedManager.getInstance().getMe();
                List<String> posting_like_list = posting.like_person_id();
                List<String> friend_list = me.friends_id();
                List<String> friend_NonFacebook_list = me.friends_NonFacebook_id();
                String tempFriend = "";
                String tempMe = "";
                boolean isYou = false;
                boolean isMe = false;

                int like_person_size = posting.like_person.size();

                for(int i=0;i<posting_like_list.size();i++){
                    if(posting_like_list.get(i).equals(me._id)){
                        isMe = true;
                    }
                    if(friend_list!=null) {
                        if(!isYou) {
                            for (int j = 0; j < friend_list.size(); j++) {
                                if (posting_like_list.get(i).equals(friend_list.get(j))) {
                                    tempFriend = me.friends.get(j).getUser_name();
                                    isYou = true;
                                    break;
                                }
                            }
                        }
                    }

                    if(friend_NonFacebook_list!=null) {
                        if (!isYou) {
                            for (int j = 0; j < friend_NonFacebook_list.size(); j++) {
                                if (posting_like_list.get(i).equals(friend_NonFacebook_list.get(j))) {
                                    tempFriend = me.friends_NonFacebook.get(j).getUser_name();
                                    isYou = true;
                                    break;
                                }
                            }
                        }
                    }
                }

                String txt = "";
                if(!isYou){
                    if(isMe) //me
                        if(like_person_size==1)
                            txt = "회원님이 좋아해요";
                        else
                            txt = "회원님 외 " + (like_person_size-1) + "명의 사람들이 좋아해요";
                    else{ // x
                        if(like_person_size==1)
                            txt = "1명의 사람이 좋아해요";
                        else
                            txt = like_person_size + "명의 사람들이 좋아해요";
                    }
                }else{//좋아요한 내 친구가 1명 이상일 때
                    if(isMe) { //you me
                        if(like_person_size==2)
                            txt = "회원님, " + tempFriend + "님이 좋아해요";
                        else
                            txt = "회원님, " + tempFriend + "님 외 " + (like_person_size - 2) + "명의 사람들이 좋아해요";
                    }
                    else //you
                        if(like_person_size==1)
                            txt = tempFriend+"님이 좋아해요";
                        else
                            txt = tempFriend+ "님 외 " + (like_person_size-1) + "명의 사람들이 좋아해요";
                }

                descBodyViewHolder.txt_people_like.setText(txt);

            }

                descBodyViewHolder.txt_people_like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(posting.like_cnt>0)
                            return;

                        Intent intent = new Intent(context, LikedPeople_.class);
                        intent.putExtra("posting", posting);
                        context.startActivity(intent);
                        activity.overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                    }
                });

        } else if (holder instanceof GraphBodyViewHolder) {
            /*
            GraphBodyViewHolder graphBodyViewHolder = (GraphBodyViewHolder) holder;

            ValueLineSeries series = new ValueLineSeries();
            series.setColor(0xFFF5C25A);
            series.addPoint(new ValueLinePoint(0));
            for (float rank : posting.rate_distribution) {
                series.addPoint(new ValueLinePoint(rank));
            }
            series.addPoint(new ValueLinePoint(0));
            graphBodyViewHolder.lineChart.addSeries(series);

            int cnt = 1;
            int total_cnt = 0; // 평가자 수
            float total_point = 0; // 총 별점
            int max_index = 0; // 최대 평점 인덱스
            float max = 0;
            for (float rank : posting.rate_distribution) {
                total_cnt += rank;
                total_point += (rank*((float)cnt/2.0));
                if (max < rank) {
                    max = rank;
                    max_index = cnt;
                }
                cnt++;
            }
            graphBodyViewHolder.rank_average.setText(String.format("%.2f",total_point/(float)total_cnt)+"");
            graphBodyViewHolder.rank_cnt.setText(total_cnt+"");
            graphBodyViewHolder.rank_max.setText((float)max_index/2+"");
            */
        } else if (holder instanceof PersonBodyViewHolder) {
            PersonBodyViewHolder personBodyViewHolder = (PersonBodyViewHolder) holder;
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, dm);
            int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, dm);

            int cnt = 0;
            if (personList.size() > 0) {
                personBodyViewHolder.layout_person.removeAllViews();
            }
            for (User person : personList) {
                ImageView iv = new ImageView(context);
                iv.setImageResource(R.drawable.icon_cart);
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                iv.setPadding(padding, padding, padding, padding);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, width);
                iv.setLayoutParams(layoutParams);

                personBodyViewHolder.layout_person.addView(iv);
                Glide.with(context).
                        load(person.getPic_small()).
                        bitmapTransform(new CropCircleTransformation(context)).into(iv);

                if (cnt > 10)
                    break;
                cnt++;
            }
            if (personList.size() == 0) {
                personBodyViewHolder.no_person.setVisibility(View.VISIBLE);
            } else {
                personBodyViewHolder.no_person.setVisibility(View.GONE);
            }
        } else if (holder instanceof SimiralTailViewHolder) {
            SimiralTailViewHolder simiralTailViewHolder = (SimiralTailViewHolder) holder;
        } else if (holder instanceof CommentViewHolder) {
            commentViewHolder = (CommentViewHolder) holder;

            addFlowChart_Comment(commentViewHolder.TFL_comment_all, posting.comment_person);
        }


    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_IMAGE_HEADER;
        else if (position == 1)
            return TYPE_BODY_DESC;
//        else if (position == 2)
//            return TYPE_RANK_GRAPH;
//        else if (position == 3)
//            return TYPE_LIKE_PERSON;
//        else if (position == 4)
//            return TYPE_TAIL_SIMILAR;
        else if (position == 2)
            return TYPE_COMMENT;

        return TYPE_IMAGE_HEADER;
    }

    @Override
    public int getItemCount() {
        return 3;
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

    public static class ImageHeaderViewHolder extends ViewHolder {
        public TextView postingName;
        public static TextView viewCnt;
        public ImageView postingImage, heartImg, starImg;
        public LinearLayout eatBtn, commentBtn, layoutNearby, indicator;
        public ImageHeaderViewHolder(View v) {
            super(v);
            postingImage = (ImageView) v.findViewById(R.id.posting_image);
            heartImg = (ImageView) v.findViewById(R.id.heart_img);
            starImg = (ImageView) v.findViewById(R.id.star_img);
            postingName = (TextView) v.findViewById(R.id.posting_name);
            viewCnt = (TextView) v.findViewById(R.id.view_cnt);
            eatBtn = (LinearLayout) v.findViewById(R.id.eat_btn);
            commentBtn = (LinearLayout) v.findViewById(R.id.comment_btn);
            layoutNearby = (LinearLayout) v.findViewById(R.id.layout_nearby);
            indicator = (LinearLayout) v.findViewById(R.id.indicator);
        }
    }

    public class DescBodyViewHolder extends ViewHolder {
        public TextView txt_category, txt_posting, txt_people_like;
        public DescBodyViewHolder(View v) {
            super(v);
            txt_category = (TextView) v.findViewById(R.id.txt_category);
            txt_posting = (TextView) v.findViewById(R.id.txt_posting);
            txt_people_like = (TextView) v.findViewById(R.id.txt_people_like);
        }
    }

    public class GraphBodyViewHolder extends ViewHolder {
        public ValueLineChart lineChart;
        public TextView rank_average, rank_cnt, rank_max;
        public GraphBodyViewHolder(View v) {
            super(v);
            lineChart = (ValueLineChart) v.findViewById(R.id.linechart);
            lineChart.setShowIndicator(false);
            rank_average = (TextView) v.findViewById(R.id.rank_average);
            rank_cnt = (TextView) v.findViewById(R.id.rank_cnt);
            rank_max = (TextView) v.findViewById(R.id.rank_max);
        }
    }

    public class PersonBodyViewHolder extends ViewHolder {
        public LinearLayout layout_person;
        public TextView no_person;
        public PersonBodyViewHolder(View v) {
            super(v);
            layout_person = (LinearLayout) v.findViewById(R.id.layout_person);
            no_person = (TextView) v.findViewById(R.id.no_person);
        }
    }

    public class SimiralTailViewHolder extends ViewHolder {
        public TextView postingName, postingDesc;
        public SimiralTailViewHolder(View v) {
            super(v);
        }
    }

    public class CommentViewHolder extends ViewHolder {
        public TextView TV_commenter_name,TV_commenter2_name, TV_comment, TV_comment2, TV_comment_info, TV_comment2_info, TV_comment_write;
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

        }
    }

    private String combine_tag(Posting posting) {
        List<List<String>> category = new ArrayList<>();
        category.add(posting.location);
        category.add(posting.language);
        category.add(posting.activity);

        String result ="";
        int cnt = 1;
        for(int i=0;i<3;i++){
            for (String str:category.get(i)) {
                if(cnt>7){
                    result+="…";
                    return result;
                }
                result+=("#"+str+" ");
                cnt++;
            }
        }
        return result;
    }

    private String combine_posting_tag(Posting posting) {
        String result = posting.posting;
        if(result == null)
            return "";
        return result;
    }

    public void posting_like(Posting mposting) {
        LoadingUtil.startLoading(activity.indicator);
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.likePosting(SharedManager.getInstance().getMe()._id, mposting._id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Posting>() {
                    @Override
                    public final void onCompleted() {
                        LoadingUtil.stopLoading(activity.indicator);
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(Posting response) {
                        if (response != null) {
                            posting = response;
                            notifyDataSetChanged();
                        } else {
                            Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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

                CIV_pic = (CircleImageView) lv.findViewById(R.id.CIV_pic);
                TV_commenter_name = (TextView) lv.findViewById(R.id.TV_commenter_name);
                TV_comment = (TextView) lv.findViewById(R.id.TV_comment);
                TV_comment_info = (TextView) lv.findViewById(R.id.TV_comment_info);
                TV_comment_write = (TextView) lv.findViewById(R.id.TV_comment_write);

                Glide.with(context).
                        load(comment_person.user_pic_small).
                        into(CIV_pic);
                TV_commenter_name.setText(comment_person.user_name);
                TV_comment.setText(comment_person.comment);
                TV_comment_info.setText(comment_person.comment_date);
                TV_comment_write.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ReCommentActivity_.class);
                        intent.putExtra("posting_id", posting._id);
                        intent.putExtra("comment_id", comment_person.comment_id);
                        context.startActivity(intent);

                    }
                });

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
                        into(CIV2_pic);
                TV_commenter2_name.setText(re_comment_person.user_name);
                TV_comment2.setText(re_comment_person.comment);
                TV_comment2_info.setText(re_comment_person.comment_date);

                TagFlowLayout TFL_comment_all_2 = (TagFlowLayout) lv.findViewById(R.id.TFL_comment_all_2);

                List<Integer> me = new ArrayList<>();
                me.add(0,1);

                if(position == reCommentPerson.size()-1){
                    addFlowChart_Comment_me(TFL_comment_all_2, me);
                }
                return lv;
            }

            @Override
            public boolean setSelected(int position, Posting.ReCommentPerson s) {
                return true;
            }
        });

    }


    private void addFlowChart_Comment_me(final TagFlowLayout mFlowLayout, final List<Integer> me) {
        final LayoutInflater mInflater = LayoutInflater.from(context);

        mFlowLayout.setAdapter(new TagAdapter<Integer>(me){
            @Override
            public View getView(final FlowLayout parent, final int position, Integer re_comment_person) { //list size가 무조건 1인 me를 임의로 만듬 -> recylcerview 세로 길이 때문에 이렇게 코딩함
                final LinearLayout lv = (LinearLayout) mInflater.inflate(R.layout.cell_detail_comment_me, mFlowLayout, false);
                CircleImageView CIV_me_pic;

                CIV_me_pic = (CircleImageView) lv.findViewById(R.id.CIV_me_pic);

                Glide.with(context).
                        load(SharedManager.getInstance().getMe().pic_small).
                        into(CIV_me_pic);
                return lv;
            }

            @Override
            public boolean setSelected(int position, Integer s) {
                return true;
            }
        });

    }
}