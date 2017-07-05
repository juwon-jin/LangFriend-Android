package makejin.langfriend.juwon.TabActivity.Tab3List;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import makejin.langfriend.juwon.Model.User;
import makejin.langfriend.R;
import makejin.langfriend.juwon.Profile.YourProfileActivity;
import makejin.langfriend.juwon.Utils.Connections.CSConnection;
import makejin.langfriend.juwon.Utils.Connections.ServiceGenerator;
import makejin.langfriend.juwon.Utils.Constants.Constants;
import makejin.langfriend.juwon.Utils.Loadings.LoadingUtil;
import makejin.langfriend.juwon.Utils.SharedManager.SharedManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static makejin.langfriend.juwon.Utils.Constants.Constants.CalculationByDistance;
import static makejin.langfriend.juwon.Utils.Constants.Constants.getAgeFromBirthday;

/**
 * Created by kksd0900 on 16. 9. 30..
 */


public class Tab3ListAdapter extends RecyclerView.Adapter<Tab3ListAdapter.ViewHolder> {
    private static final int TYPE_ITEM = 0;

    public Context context;
    private OnItemClickListener mOnItemClickListener;
    public static ArrayList<User> mDataset = new ArrayList<>();
    public Tab3ListFragment Tab3ListFragment;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public Tab3ListAdapter(OnItemClickListener onItemClickListener, Context mContext, Tab3ListFragment mTab3ListFragment) {

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
    public Tab3ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_request_user, parent, false);
            return new RequestViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (holder instanceof RequestViewHolder) {
            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, position);
                    Intent intent = new Intent(context, YourProfileActivity.class);
                    intent.putExtra("user_id", mDataset.get(position)._id);
                    context.startActivity(intent);
                }
            });
            final RequestViewHolder itemViewHolder = (RequestViewHolder) holder;
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
            itemViewHolder.BT_no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    user_reject(user, position);
                    Tab3ListFragment.RL_IV_no_2.setVisibility(View.VISIBLE);
                    Tab3ListFragment.RL_IV_yes_2.setVisibility(View.INVISIBLE);
                    new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
                        @Override
                        public void run() {
                            Tab3ListFragment.RL_IV_no_2.setVisibility(View.INVISIBLE);
                        }
                    }, 1000);
                }
            });
            itemViewHolder.BT_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    user_accept(user, position);
                    Tab3ListFragment.RL_IV_yes_2.setVisibility(View.VISIBLE);
                    Tab3ListFragment.RL_IV_no_2.setVisibility(View.INVISIBLE);
                    new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
                        @Override
                        public void run() {
                            Tab3ListFragment.RL_IV_yes_2.setVisibility(View.INVISIBLE);
                        }
                    }, 1000);
                }
            });

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
    public class RequestViewHolder extends ViewHolder {
        public TextView TV_user_name, TV_category, TV_about_me;
        ImageView IV_user;
        Button BT_yes, BT_no;

        LinearLayout indicator;

        RelativeLayout RL_IV_yes_2;

        public RequestViewHolder(View v) {
            super(v);
            TV_user_name = (TextView) v.findViewById(R.id.TV_user_name);
            TV_category = (TextView) v.findViewById(R.id.TV_category);
            TV_about_me = (TextView) v.findViewById(R.id.TV_about_me);
            IV_user = (ImageView) v.findViewById(R.id.IV_user);

            BT_yes = (Button) v.findViewById(R.id.BT_yes);
            BT_no = (Button) v.findViewById(R.id.BT_no);

            indicator = (LinearLayout) v.findViewById(R.id.indicator);

            RL_IV_yes_2 = (RelativeLayout) v.findViewById(R.id.RL_IV_yes_2);

        }
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
                        Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(User response) {
                        if (response != null) {
                            SharedManager.getInstance().setMe(response);
                            mDataset.remove(index);
                            notifyDataSetChanged();

                            Tab3ListFragment.refresh(1);
                            Tab3ListFragment.refresh(2);
                            Tab3ListFragment.refresh(3);
                        } else {
                            Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(User response) {
                        if (response != null) {
                            SharedManager.getInstance().setMe(response);
                            mDataset.remove(index);
                            notifyDataSetChanged();
                            Tab3ListFragment.refresh(1);
                            Tab3ListFragment.refresh(2);
                            Tab3ListFragment.refresh(3);
                        } else {
                            Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}