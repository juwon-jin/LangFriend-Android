package makejin.langfriend.juwon.TabActivity.Tab3List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.daimajia.easing.linear.Linear;

import java.util.ArrayList;

import makejin.langfriend.R;
import makejin.langfriend.juwon.Model.User;
import makejin.langfriend.juwon.Profile.YourProfileActivity;
import makejin.langfriend.juwon.Utils.Connections.CSConnection;
import makejin.langfriend.juwon.Utils.Connections.ServiceGenerator;
import makejin.langfriend.juwon.Utils.Constants.Constants;
import makejin.langfriend.juwon.Utils.Loadings.LoadingUtil;
import makejin.langfriend.juwon.Utils.SharedManager.SharedManager;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static makejin.langfriend.juwon.Utils.Constants.Constants.CalculationByDistance;
import static makejin.langfriend.juwon.Utils.Constants.Constants.getAgeFromBirthday;

/**
 * Created by kksd0900 on 16. 9. 30..
 */
public class Tab3ListAdapter3 extends RecyclerView.Adapter<Tab3ListAdapter3.ViewHolder> {
    private static final int TYPE_ITEM = 0;

    public Context context;
    private OnItemClickListener mOnItemClickListener;
    public static ArrayList<User> mDataset = new ArrayList<>();
    public Tab3ListFragment Tab3ListFragment;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public Tab3ListAdapter3(OnItemClickListener onItemClickListener, Context mContext, Tab3ListFragment mTab3ListFragment) {

        mOnItemClickListener = onItemClickListener;
        context = mContext;
        mDataset.clear();
        Tab3ListFragment = mTab3ListFragment;
    }

    public void addData(User item) {
        mDataset.add(item);
    }

    public User getItem(int position) {
        return mDataset.get(position);
    }

    public void clear() {
        mDataset.clear();
    }

    @Override
    public Tab3ListAdapter3.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_request_user_3, parent, false);
            return new RequestViewHolder3(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (holder instanceof RequestViewHolder3) {
            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, position);
                    Intent intent = new Intent(context, YourProfileActivity.class);
                    intent.putExtra("user_id", mDataset.get(position)._id);
                    context.startActivity(intent);
                }
            });
            final RequestViewHolder3 itemViewHolder = (RequestViewHolder3) holder;
            final User user = mDataset.get(position);

            itemViewHolder.TV_user_name.setText(user.nickname);
            //user_info -> 나이 / 거리
            String age = getAgeFromBirthday(user.birthday);

            String distance;
            if(user.location_point.lat!=0 && user.location_point.lon!=0) {
                distance = String.valueOf(CalculationByDistance(SharedManager.getInstance().getMe().location_point, user.location_point));
                distance += "km";
            }
            else
                distance = "비공개";

            itemViewHolder.TV_category.setText(age + " / " + distance + " / " + user.languages.get(0).name);
            itemViewHolder.TV_about_me.setText(user.about_me);
//            if(user.kakaotalk.equals(null) || user.kakaotalk.equals("")){
//                //Glide.with(context).load(R.drawable.kakaotalk_no).into(itemViewHolder.IV_kakaotalk);
//            }else{
//                Glide.with(context).load(R.drawable.kakaotalk).into(itemViewHolder.IV_kakaotalk);
//                itemViewHolder.IV_kakaotalk.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                       //Toast.makeText(context, "<"+user.kakaotalk+">\n카카오톡 아이디가 복사되었습니다.").show();
//                    }
//                });
//            }

            if(!user.social_type.equals("facebook")){
                //Glide.with(context).load(R.drawable.facebook_no).into(itemViewHolder.IV_facebook);
            }else{
                Glide.with(context).load(R.drawable.facebook).into(itemViewHolder.IV_facebook);
                itemViewHolder.IV_facebook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = "http://facebook.com/" + user.social_id;
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        context.startActivity(browserIntent);
                    }
                });
            }




            LoadingUtil.startLoading(itemViewHolder.indicator);
            Glide.with(context).
                    load(user.getPic(0)).
                    listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            LoadingUtil.stopLoading(itemViewHolder.indicator);
                            return false;
                        }
                    }).
                    into(itemViewHolder.IV_user);

        }
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
    public class RequestViewHolder3 extends ViewHolder {
        public TextView TV_user_name, TV_category, TV_about_me;
        ImageView IV_user;
        ImageView IV_kakaotalk, IV_facebook;
        LinearLayout indicator;

        public RequestViewHolder3(View v) {
            super(v);
            TV_user_name = (TextView) v.findViewById(R.id.TV_user_name);
            TV_category = (TextView) v.findViewById(R.id.TV_category);
            TV_about_me = (TextView) v.findViewById(R.id.TV_about_me);
            IV_user = (ImageView) v.findViewById(R.id.IV_user);

            IV_kakaotalk = (ImageView) v.findViewById(R.id.IV_kakaotalk);
            IV_facebook = (ImageView) v.findViewById(R.id.IV_facebook);

            indicator = (LinearLayout) v.findViewById(R.id.indicator);
        }
    }

}