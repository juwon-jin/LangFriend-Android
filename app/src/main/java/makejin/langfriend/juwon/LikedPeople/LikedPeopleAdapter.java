package makejin.langfriend.juwon.LikedPeople;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import makejin.langfriend.juwon.Model.Posting;
import makejin.langfriend.juwon.Model.User;
import makejin.langfriend.juwon.Profile.YourProfileActivity;
import makejin.langfriend.R;
import makejin.langfriend.juwon.Utils.Constants.Constants;
import makejin.langfriend.juwon.Utils.SharedManager.SharedManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static makejin.langfriend.juwon.Utils.Constants.Constants.IMAGE_BASE_URL;

/**
 * Created by kksd0900 on 16. 9. 30..
 */
public class LikedPeopleAdapter extends RecyclerView.Adapter<LikedPeopleAdapter.ViewHolder> {
    private static final int TYPE_ITEM = 0;

    public Context context;
    private OnItemClickListener mOnItemClickListener;
    public ArrayList<User> mDataset = new ArrayList<>();
    public LikedPeople likedPeople;

    public Posting posting;
    //List<String> friends = SharedManager.getInstance().getMe().friends;
    List<String> friends = SharedManager.getInstance().getMe().friends_id();

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public LikedPeopleAdapter(OnItemClickListener onItemClickListener, Context mContext, LikedPeople mlikedPeople, Posting mposting) {

        mOnItemClickListener = onItemClickListener;
        context = mContext;
        mDataset.clear();
        likedPeople = mlikedPeople;
        posting = mposting;
    }

    public void addData(User user) {
        mDataset.add(user);
    }

    public User getItem(int position) {
        return mDataset.get(position);
    }

    public void clear() {
        mDataset.clear();
    }

    @Override
    public LikedPeopleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_liked_person, parent, false);
            return new LikedPeopleViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (holder instanceof LikedPeopleViewHolder) {
            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, position);
                }
            });
            LikedPeopleViewHolder itemViewHolder = (LikedPeopleViewHolder) holder;
            final User user = mDataset.get(position);

            itemViewHolder.TV_user_name.setText(user.nickname);
            Log.i("makejin", "user._id "+user._id);
            itemViewHolder.TV_date.setText(cal_time(user._id,posting));

            List<String> comment_person_id = posting.comment_person_id();

            if(user.pic_small.contains("facebook")){
                Glide.with(context).
                        load(user.pic_small).
                        thumbnail(0.1f).
                        bitmapTransform(new CropCircleTransformation(context)).into(itemViewHolder.IV_profile);
            }else{
                Glide.with(context).
                        load(Constants.IMAGE_BASE_URL + user.pic_small).
                        thumbnail(0.1f).
                        bitmapTransform(new CropCircleTransformation(context)).into(itemViewHolder.IV_profile);
            }

            itemViewHolder.cell_liked_person.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, YourProfileActivity.class);
                    intent.putExtra("user_id", user._id);
                    context.startActivity(intent);
                }
            });
        }
    }

    private String cal_time(String user_id,Posting posting) {
        //posting.update 형식 : 2011-10-05T14:48:00.000Z
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String current_time = sdfNow.format(new Date(System.currentTimeMillis()));

        int su_arr[] = {0,4,5,7,8,10,11,13,14,16};
        String str_arr[] = {"년 전","개월 전","일 전","시간 전","분 전"};
        String like_date = null;
        int update, now;

        for(int i=0;i<posting.like_person.size();i++){
            if(posting.like_person.get(i).getUser_id().equals(user_id)){
                Log.i("makejin", "1 " + posting.like_person.get(i).getUser_id());
                Log.i("makejin", "2 " + user_id);
                Log.i("makejin", "3 " + posting.like_person.get(i).getLike_date());

                like_date = posting.like_person.get(i).getLike_date();

                for(int j=0;j<5;j++){
                    if(j==5)
                        return "방금 전";
                    else {
                        Log.i("makejin", "4 " + su_arr[j*2]);
                        Log.i("makejin", "5 " + su_arr[j*2+1]);
                        update = Integer.valueOf(like_date.substring(su_arr[j*2],su_arr[j*2+1]));
                        now = Integer.valueOf(current_time.substring(su_arr[j*2],su_arr[j*2+1]));
                        if (now-update > 0)
                            return now-update + str_arr[j];
                    }
                }
            }
        }

        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    /*
        ViewHolder
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public View container;
        public ViewHolder(View itemView) {
            super(itemView);
            container = itemView;
        }
    }
    public class LikedPeopleViewHolder extends ViewHolder {
        public TextView TV_user_name, TV_date;
        public ImageView IV_profile;
        public RelativeLayout cell_liked_person;

        public LikedPeopleViewHolder(View v) {
            super(v);
            TV_user_name = (TextView) v.findViewById(R.id.TV_user_name);
            TV_date = (TextView) v.findViewById(R.id.TV_date);
            IV_profile = (ImageView) v.findViewById(R.id.IV_profile);
            cell_liked_person = (RelativeLayout) v.findViewById(R.id.cell_liked_person);
        }
    }
}