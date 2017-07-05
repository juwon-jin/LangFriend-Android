//package makejin.langfriend.juwon.TabActivity.Tab2Request;
//
//import android.content.Context;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.bumptech.glide.Glide;
//import makejin.langfriend.juwon.Model.User;
//import makejin.langfriend.R;
//import makejin.langfriend.juwon.Utils.Connections.CSConnection;
//import makejin.langfriend.juwon.Utils.Connections.ServiceGenerator;
//import makejin.langfriend.juwon.Utils.Constants.Constants;
//import makejin.langfriend.juwon.Utils.Loadings.LoadingUtil;
//import makejin.langfriend.juwon.Utils.SharedManager.SharedManager;
//
//import java.util.ArrayList;
//
//import rx.Subscriber;
//import rx.android.schedulers.AndroidSchedulers;
//import rx.schedulers.Schedulers;
//
///**
// * Created by kksd0900 on 16. 9. 30..
// */
//public class Tab2RequestAdapter extends RecyclerView.Adapter<Tab2RequestAdapter.ViewHolder> {
//    private static final int TYPE_ITEM = 0;
//
//    public Context context;
//    private OnItemClickListener mOnItemClickListener;
//    public static ArrayList<User> mDataset = new ArrayList<>();
//    public Tab2RequestFragment tab2RequestFragment;
//
//    public interface OnItemClickListener {
//        void onItemClick(View view, int position);
//    }
//
//    public Tab2RequestAdapter(OnItemClickListener onItemClickListener, Context mContext, Tab2RequestFragment mTab2RequestFragment) {
//
//        mOnItemClickListener = onItemClickListener;
//        context = mContext;
//        mDataset.clear();
//        tab2RequestFragment = mTab2RequestFragment;
//    }
//
//    public void addData(User item) {
//        mDataset.add(item);
//    }
//
//    public User getItem(int position) {
//        return mDataset.get(position);
//    }
//
//    public void clear() {
//        mDataset.clear();
//    }
//
//    @Override
//    public Tab2RequestAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        if (viewType == TYPE_ITEM) {
//            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_request_user, parent, false);
//            return new RequestViewHolder(v);
//        }
//        return null;
//    }
//
//    @Override
//    public void onBindViewHolder(ViewHolder holder, final int position) {
//        if (holder instanceof RequestViewHolder) {
//            holder.container.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mOnItemClickListener.onItemClick(v, position);
//                }
//            });
//            RequestViewHolder itemViewHolder = (RequestViewHolder) holder;
//            final User user = mDataset.get(position);
//
//
//            itemViewHolder.TV_user_name.setText(user.nickname);
//            itemViewHolder.TV_category.setText(user.location + user.language + user.activity);
//            itemViewHolder.TV_about_me.setText(user.about_me);
//            itemViewHolder.BT_no.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    user_reject(user, position);
//                }
//            });
//            itemViewHolder.BT_yes.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    user_accept(user, position);
//                }
//            });
//
//            Glide.with(context).load(user.getPic(0)).into(itemViewHolder.IV_user);
//        }
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        return TYPE_ITEM;
//    }
//
//    @Override
//    public int getItemCount() {
//        return mDataset.size();
//    }
//
//
//    /*
//        ViewHolder
//     */
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        public View container;
//        public ViewHolder(View itemView) {
//            super(itemView);
//            container = itemView;
//        }
//    }
//    public class RequestViewHolder extends ViewHolder {
//        public TextView TV_user_name, TV_category, TV_about_me;
//        ImageView IV_user;
//        Button BT_yes, BT_no;
//
//
//        public RequestViewHolder(View v) {
//            super(v);
//            TV_user_name = (TextView) v.findViewById(R.id.TV_user_name);
//            TV_category = (TextView) v.findViewById(R.id.TV_category);
//            TV_about_me = (TextView) v.findViewById(R.id.TV_about_me);
//            IV_user = (ImageView) v.findViewById(R.id.IV_user);
//
//            BT_yes = (Button) v.findViewById(R.id.BT_yes);
//            BT_no = (Button) v.findViewById(R.id.BT_no);
//
//        }
//    }
//
//    public void user_accept(User you, final int index) {
//        LoadingUtil.startLoading(Tab2RequestFragment.indicator);
//        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
//        conn.acceptYou(you, SharedManager.getInstance().getMe()._id, you._id)
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<User>() {
//                    @Override
//                    public final void onCompleted() {
//                        LoadingUtil.stopLoading(Tab2RequestFragment.indicator);
//                    }
//                    @Override
//                    public final void onError(Throwable e) {
//                        e.printStackTrace();
//                        Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
//                    }
//                    @Override
//                    public final void onNext(User response) {
//                        if (response != null) {
//                            mDataset.remove(index);
//                            Tab2RequestFragment.actionBar.setTitle("친구 요청 - " + mDataset.size() + "명"); // requestingFriendCnt = index?
//                            notifyDataSetChanged();
//                        } else {
//                            Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }
//
//    public void user_reject(User you, final int index) {
//        LoadingUtil.startLoading(Tab2RequestFragment.indicator);
//        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
//        conn.rejectYou(you, SharedManager.getInstance().getMe()._id, you._id)
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<User>() {
//                    @Override
//                    public final void onCompleted() {
//                        LoadingUtil.stopLoading(Tab2RequestFragment.indicator);
//                    }
//                    @Override
//                    public final void onError(Throwable e) {
//                        e.printStackTrace();
//                        Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
//                    }
//                    @Override
//                    public final void onNext(User response) {
//                        if (response != null) {
//                            mDataset.remove(index);
//                            Tab2RequestFragment.actionBar.setTitle("친구 요청 - " + mDataset.size() + "명"); // requestingFriendCnt = index?
//                            notifyDataSetChanged();
//                        } else {
//                            Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }
//}