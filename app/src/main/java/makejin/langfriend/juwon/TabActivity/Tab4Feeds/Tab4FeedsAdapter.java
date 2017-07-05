package makejin.langfriend.juwon.TabActivity.Tab4Feeds;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import makejin.langfriend.juwon.Detail.DetailActivity_;
import makejin.langfriend.juwon.LikedPeople.LikedPeople_;
import makejin.langfriend.juwon.Model.Category;
import makejin.langfriend.juwon.Model.GlobalResponse;
import makejin.langfriend.juwon.Model.Posting;
import makejin.langfriend.juwon.Model.User;
import makejin.langfriend.R;
import makejin.langfriend.juwon.Profile.YourProfileActivity;
import makejin.langfriend.juwon.Utils.Connections.CSConnection;
import makejin.langfriend.juwon.Utils.Connections.ServiceGenerator;
import makejin.langfriend.juwon.Utils.Constants.Constants;
import makejin.langfriend.juwon.Utils.Loadings.LoadingUtil;
import makejin.langfriend.juwon.Utils.SharedManager.SharedManager;
import makejin.langfriend.juwon.ViewHolder.ViewHolderPosting;
import makejin.langfriend.juwon.ViewHolder.ViewHolderParent;
import makejin.langfriend.juwon.ViewHolder.ViewHolderPostingCategory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static makejin.langfriend.juwon.Detail.DetailAdapter.imageHolder;
import static makejin.langfriend.juwon.TabActivity.Tab4Feeds.Tab4FeedsFragment.activity;

/**
 * Created by kksd0900 on 16. 10. 11..
 */
public class Tab4FeedsAdapter extends RecyclerView.Adapter<ViewHolderParent> {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_HEADER = 1;

    public Context context;
    public Tab4FeedsFragment fragment;
    private OnItemClickListener mOnItemClickListener;
    public ArrayList<Posting> mDataset = new ArrayList<>();

    String image_url;

    List<String> push_tag = new ArrayList<>();

    //카테고리 띄워주기 위한 리스트들
    private String location_list[] = new String[]{};
    private String language_list[] = new String[]{};
    private String activity_list[] = new String[]{};
    //getRecommand에 실어보낼 Map
    private Category category_posting = new Category();
    boolean isExpanded = false;


    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public Tab4FeedsAdapter(OnItemClickListener onItemClickListener, Context mContext, Tab4FeedsFragment mFragment) {
        mOnItemClickListener = onItemClickListener;
        context = mContext;
        fragment = mFragment;
        mDataset.clear();

        location_list = SharedManager.getInstance().getCategory().location.toArray(
                new String[SharedManager.getInstance().getCategory().location.size()]);
        language_list = SharedManager.getInstance().getCategory().language.toArray(
                new String[SharedManager.getInstance().getCategory().language.size()]);
        activity_list = SharedManager.getInstance().getCategory().activity.toArray(
                new String[SharedManager.getInstance().getCategory().activity.size()]);

        category_posting.location = new ArrayList<String>();
        category_posting.language = new ArrayList<String>();
        category_posting.activity = new ArrayList<String>();
    }

    public void addData(Posting item) {
        Log.i("zxc", "addData1 : " + item._id);
        mDataset.add(0,item);
    }

    public Posting getItem(int position) {
        return mDataset.get(position);
    }

    public void clear() {
        category_posting.location.clear();
        category_posting.language.clear();
        category_posting.activity.clear();
        mDataset.clear();
        push_tag.clear();
    }

    @Override
    public ViewHolderParent onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            Log.i("zxc", "jinjudnjs1 : ");
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_posting, parent, false);
            return new ViewHolderPosting(v);
        } else if (viewType == TYPE_HEADER) {
            Log.i("zxc", "jinjudnjs2 : ");
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_posting_category, parent, false);
            return new ViewHolderPostingCategory(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolderParent holder, final int position) {
        if (holder instanceof ViewHolderPosting) {
            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, position-1);
                }
            });
            final ViewHolderPosting itemViewHolder = (ViewHolderPosting) holder;
            final Posting posting = mDataset.get(position-1);

            Log.i("zxc", "addData2 : " + posting._id);

            //itemViewHolder.cellPostingHeader.setVisibility(View.GONE);
            itemViewHolder.authorName.setText(posting.author.author_nickname);
            itemViewHolder.authorName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, YourProfileActivity.class);
                    intent.putExtra("user_id", posting.author.author_id);
                    context.startActivity(intent);
                }
            });

            image_url = posting.author.author_pic_small;
            if(image_url.contains("facebook")){
                Glide.with(context).
                        load(image_url).
                        into(itemViewHolder.author_image);
            }else{
                Glide.with(context).
                        load(Constants.IMAGE_BASE_URL + image_url).
                        into(itemViewHolder.author_image);
            }
            itemViewHolder.author_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, YourProfileActivity.class);
                    intent.putExtra("user_id", posting.author.author_id);
                    context.startActivity(intent);
                }
            });

            itemViewHolder.postingName.setText(posting.name);

            Glide.with(context).
                    load(Constants.IMAGE_BASE_URL + posting.image_url).
                    listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            itemViewHolder.indicator.setVisibility(View.GONE);
                            return false;
                        }
                    }).
                    thumbnail(0.1f).
                    into(itemViewHolder.posting_img);

            Log.i("zxc", "Constants.IMAGE_BASE_URL + posting.image_url : " + Constants.IMAGE_BASE_URL + posting.image_url);
            itemViewHolder.category_tag.setText(combine_tag(posting));
            itemViewHolder.posting.setText(combine_posting_tag(posting));

            if (posting.like_person.size()==0)
                itemViewHolder.people_like.setText("가장 먼저 좋아요를 눌러주세요!");
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

                String txt = "";
                txt = (like_person_size) + "명의 사람들이 좋아해요";


                /*
                for(int i=0;i<posting_like_list.size();i++){
                    if(isMe && isYou) break;
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
                                Log.i("zxcxccxcxc", posting_like_list.get(i));
                                Log.i("zxcxccxcxc", friend_NonFacebook_list.get(j));


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
*/
                itemViewHolder.people_like.setText(txt);

            }

            String distance= ", ";
            User.LocationPoint location_point = posting.author.author_location_point;
            if(location_point.lat!=0 && location_point.lon!=0) {
                distance += String.valueOf(CalculationByDistance(SharedManager.getInstance().getMe().location_point, location_point));
                distance += "km";
            }
            else
                distance = "";

            itemViewHolder.user_info.setText(cal_time(posting) + distance);

            itemViewHolder.heart.setImageDrawable(fragment.getResources().getDrawable(R.drawable.heart_gray));
            itemViewHolder.star.setImageDrawable(fragment.getResources().getDrawable(R.drawable.comment3));
            itemViewHolder.TV_comment.setText("댓글\t" + posting.comment_cnt);
            setImamge(posting.like_person_id(),itemViewHolder.heart,R.drawable.heart_red);
            itemViewHolder.eat_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    posting_like(posting, position-1);
                    setImamge(posting.like_person_id(),itemViewHolder.heart,R.drawable.heart_red);
                }
            });

            itemViewHolder.comment_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DetailActivity_.class);
                    intent.putExtra("posting", posting);
                    context.startActivity(intent);
                    activity.overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                }
            });

            itemViewHolder.report_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final PopupMenu popup = new PopupMenu(context, itemViewHolder.report_btn);
                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.popup, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            //TODO: 신고하기 테스트 하기
                            report_posting(posting._id);
                            return true;
                        }
                    });

                    popup.show();
                }
            });


            itemViewHolder.posting_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DetailActivity_.class);
                    intent.putExtra("posting", posting);
                    context.startActivity(intent);
                    activity.overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                }
            });


            itemViewHolder.people_like.setOnClickListener(new View.OnClickListener() {
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
        }else if (holder instanceof ViewHolderPostingCategory) {

            final ViewHolderPostingCategory itemViewHolder = (ViewHolderPostingCategory) holder;
            Map<String[], TagFlowLayout> tagFlowLayouts = new HashMap<String[], TagFlowLayout>();
            tagFlowLayouts.put(location_list,itemViewHolder.location);
            tagFlowLayouts.put(language_list,itemViewHolder.language);
            tagFlowLayouts.put(activity_list,itemViewHolder.activity);

            for(final Map.Entry<String[], TagFlowLayout> entry : tagFlowLayouts.entrySet()) {
                entry.getValue().setAdapter(new TagAdapter<String>(entry.getKey()) {
                    @Override
                    public View getView(FlowLayout parent, int position, String s) {
                        TextView tv = (TextView) LayoutInflater.from(context).inflate(R.layout.category_btn, parent, false);
                        tv.setText(s);
                        if(push_tag.contains(s)){
                            Drawable r = fragment.getResources().getDrawable(R.drawable.category_btn_selected_red);
                            tv.setBackground(r);
                            tv.setTextColor(0xFFFFFFFF);
                        }
                        return tv;
                    }
                });
                entry.getValue().setOnTagClickListener(new TagFlowLayout.OnTagClickListener()
                {
                    @Override
                    public boolean onTagClick(View view, int position, FlowLayout parent)
                    {
                        //Log.i("qweqw", entry.getValue()));

                        String s = entry.getKey()[position];
                        if(push_tag.contains(s)) {
                            if(entry.getKey()==location_list)
                                category_posting.location.remove(s);
                            else if(entry.getKey()==language_list)
                                category_posting.language.remove(s);
                            else if(entry.getKey()==activity_list)
                                category_posting.activity.remove(s);

                            Log.i("zxc", "Zxc1");
                            fragment.connRecommand(category_posting, fragment.page);

                            push_tag.remove(s);

                        }else {
                            if(entry.getKey()==location_list)
                                category_posting.location.add(s);
                            else if(entry.getKey()==language_list)
                                category_posting.language.add(s);
                            else if(entry.getKey()==activity_list)
                                category_posting.activity.add(s);

                            Log.i("zxc", "Zxc2");
                            fragment.connRecommand(category_posting, fragment.page);
                            push_tag.add(s);
                        }
                        return true;
                    }
                });
            }


            itemViewHolder.expand_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isExpanded) {
                        itemViewHolder.layout_category.animate()
                                .translationY(-itemViewHolder.layout_category.getHeight())
                                .setDuration(300)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        itemViewHolder.layout_category.setVisibility(View.GONE);
                                        isExpanded = !isExpanded;
                                        itemViewHolder.expand_btn.setImageResource(R.drawable.expand_up);
                                    }
                                });
                    } else {
                        itemViewHolder.layout_category.animate()
                                .translationY(10)
                                .setDuration(300)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        itemViewHolder.layout_category.setVisibility(View.VISIBLE);
                                        isExpanded = !isExpanded;
                                        itemViewHolder.expand_btn.setImageResource(R.drawable.expand_down);
                                    }
                                });
                    }
                }
            });


        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return mDataset.size()+1;
    }

    public double CalculationByDistance(User.LocationPoint StartP, User.LocationPoint EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.lat;
        double lat2 = EndP.lat;
        double lon1 = StartP.lon;
        double lon2 = EndP.lon;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        double distance = (Radius * c);
        return Math.round(distance*100d) / 100d;
    }


    private String cal_time(Posting posting) {
        //posting.update 형식 : 2011-10-05T14:48:00.000Z
        Log.i("aa","time : "+posting.create_date);

        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String current_time = sdfNow.format(new Date(System.currentTimeMillis()));

        int su_arr[] = {0,4,5,7,8,10,11,13,14,16};
        String str_arr[] = {"년 전","개월 전","일 전","시간 전","분 전"};
        int update, now;

        for(int i=0;i<5;i++){
            if(i==5)
                return "방금 전";
            else {
                update = Integer.valueOf(posting.update_date.substring(su_arr[i*2],su_arr[i*2+1]));
                now = Integer.valueOf(current_time.substring(su_arr[i*2],su_arr[i*2+1]));
                if (now-update > 0)
                    return now-update + str_arr[i];
            }
        }

        return null;
    }

    private String cal_friend(Posting posting) {
        //TODO: 페이스북 친구 연동 부분 보고 할 것
        String result="좋아하는 친구가 없어요";
        /*
        친구 목록 받아와서 posting.likeuser와 비교한 후
        총 cnt알아내고 대표 친구 2명 알아 내서 string 완성할 것
         */
        return result;
    }

    private String combine_posting_tag(Posting posting) {
        String result = posting.posting;
        if(result == null)
            return "";
        return result;
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

    public void report_posting(String posting_id) {
        LoadingUtil.startLoading(fragment.indicator);
        final CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.reportPosting(SharedManager.getInstance().getMe()._id, posting_id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GlobalResponse>() {
                    @Override
                    public final void onCompleted() {
                        LoadingUtil.stopLoading(fragment.indicator);
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(GlobalResponse response) {
                        if (response != null && response.code == 0 && response.message.equals("success")) {
                            Toast.makeText(context, "신고가 접수되었습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void posting_like(Posting posting, final int index) {
        LoadingUtil.startLoading(fragment.indicator);
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.likePosting(SharedManager.getInstance().getMe()._id, posting._id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Posting>() {
                    @Override
                    public final void onCompleted() {
                        LoadingUtil.stopLoading(fragment.indicator);
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(Posting response) {
                        if (response != null) {
                            mDataset.get(index).like_cnt = response.like_cnt;
                            mDataset.get(index).like_person = response.like_person;
                            notifyDataSetChanged();
                        } else {
                            Toast.makeText(context, Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setImamge(List<String> array, ImageView imageview, int image) {
        for (String uid : array) {
            if (uid.equals(SharedManager.getInstance().getMe()._id)) {
                imageview.setImageDrawable(fragment.getResources().getDrawable(image));
            }
        }
    }
}