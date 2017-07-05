//package makejin.langfriend.juwon.TabActivity.Tab5MyPage;
//
//
//import android.app.Fragment;
//import android.app.FragmentTransaction;
//import android.content.Context;
//import android.content.Intent;
//import android.database.Cursor;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Color;
//import android.net.Uri;
//import android.opengl.GLES20;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.support.v7.widget.Toolbar;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.ViewTreeObserver;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.engine.DiskCacheStrategy;
//import com.bumptech.glide.load.resource.drawable.GlideDrawable;
//import com.bumptech.glide.request.RequestListener;
//import com.bumptech.glide.request.animation.GlideAnimation;
//import com.bumptech.glide.request.target.SimpleTarget;
//import com.bumptech.glide.request.target.Target;
//import com.theartofdev.edmodo.cropper.CropImage;
//import com.theartofdev.edmodo.cropper.CropImageView;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import makejin.langfriend.R;
//import makejin.langfriend.juwon.Model.User;
//import makejin.langfriend.juwon.Sign.SignActivity;
//import makejin.langfriend.juwon.Utils.Connections.CSConnection;
//import makejin.langfriend.juwon.Utils.Connections.ServiceGenerator;
//import makejin.langfriend.juwon.Utils.Constants.Constants;
//import makejin.langfriend.juwon.Utils.Loadings.LoadingUtil;
//import makejin.langfriend.juwon.Utils.SharedManager.SharedManager;
//import okhttp3.MediaType;
//import okhttp3.RequestBody;
//import rx.Subscriber;
//import rx.android.schedulers.AndroidSchedulers;
//import rx.schedulers.Schedulers;
//
//import static android.app.Activity.RESULT_OK;
//import static com.facebook.FacebookSdk.getApplicationContext;
//import static makejin.langfriend.juwon.TabActivity.Tab5MyPage.ProfileDetailActivity.activity;
//import static makejin.langfriend.juwon.TabActivity.Tab5MyPage.ProfileDetailActivity.edit;
//
//public class ProfileEditFragment extends Fragment {
//    Button BT_X;
//
//
//    ImageView [] IV_pic = new ImageView[6];
//
//
//    public List<String> imagepath = new ArrayList<>();
//    public String tempImagePath = null;
//
//    int resizedWidth;
//    int resizedHeight;
//
//    public LinearLayout indicator1;
//    public LinearLayout indicator2;
//    public LinearLayout indicator3;
//    public LinearLayout indicator4;
//    public LinearLayout indicator5;
//    public LinearLayout indicator6;
//
//    EditText ET_about_me;
//
//    public static User user;
//
//    public boolean [] isChanged = new boolean[6];
//
//    RelativeLayout [] RL_RL_no = new RelativeLayout[6];
//
//    LinearLayout indicator;
//
//    ProfileEditFragment fragment;
//    public static Context context;
//
//    public static ProfileEditFragment newInstance(int index) {
//        ProfileEditFragment fragment = new ProfileEditFragment();
//        Bundle b = new Bundle();
//        b.putInt("index", index);
//        fragment.setArguments(b);
//        return fragment;
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_profile_edit, container, false);
//        this.fragment = this;
//        //this.context = getApplicationContext();
//
//        indicator1 = (LinearLayout) view.findViewById(R.id.indicator1);
//        indicator2 = (LinearLayout) view.findViewById(R.id.indicator2);
//        indicator3 = (LinearLayout) view.findViewById(R.id.indicator3);
//        indicator4 = (LinearLayout) view.findViewById(R.id.indicator4);
//        indicator5 = (LinearLayout) view.findViewById(R.id.indicator5);
//        indicator6 = (LinearLayout) view.findViewById(R.id.indicator6);
//
//        BT_X = (Button) view.findViewById(R.id.BT_X);
//
//        indicator = (LinearLayout) view.findViewById(R.id.indicator);
//
//        for(int i=0;i<isChanged.length;i++)
//            isChanged[i] = false;
//
//
//        user = SharedManager.getInstance().getMe();
//
//
//        Log.i("zxc", "activity.user.social_type : " + user.social_type);
//        Log.i("zxc", "activity.user.pic_list : " + user.getPic(0));
//
//
//        IV_pic[0] = (ImageView) view.findViewById(R.id.IV_pic1);
//        IV_pic[1] = (ImageView) view.findViewById(R.id.IV_pic2);
//        IV_pic[2] = (ImageView) view.findViewById(R.id.IV_pic3);
//        IV_pic[3] = (ImageView) view.findViewById(R.id.IV_pic4);
//        IV_pic[4] = (ImageView) view.findViewById(R.id.IV_pic5);
//        IV_pic[5] = (ImageView) view.findViewById(R.id.IV_pic6);
//
//        ET_about_me = (EditText) view.findViewById(R.id.ET_about_me);
//
//
//        RL_RL_no[0] = (RelativeLayout) view.findViewById(R.id.RL_RL_no_1);
//        RL_RL_no[1] = (RelativeLayout) view.findViewById(R.id.RL_RL_no_2);
//        RL_RL_no[2] = (RelativeLayout) view.findViewById(R.id.RL_RL_no_3);
//        RL_RL_no[3] = (RelativeLayout) view.findViewById(R.id.RL_RL_no_4);
//        RL_RL_no[4] = (RelativeLayout) view.findViewById(R.id.RL_RL_no_5);
//        RL_RL_no[5] = (RelativeLayout) view.findViewById(R.id.RL_RL_no_6);
//
//
//
//        ET_about_me.setText(user.about_me);
//
//
//
//
//
//        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                if(user.pic_list.size() <= 0)
//                    return;
//
//                RL_RL_no[0].setVisibility(View.VISIBLE);
//                IV_pic[0].getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                String image_url = user.getPic(0);
//                imagepath.add(image_url);
//                //String image_url = "http://graph.facebook.com/"+activity.user.social_id+"/picture?width=" +230 +"&height="+250;
//                Log.i("zxc", "image_url : " + image_url);
//                LoadingUtil.startLoading(indicator1);
//                Glide.with(activity).
//                        load(image_url).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).
//                        listener(new RequestListener<String, GlideDrawable>() {
//                            @Override
//                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                                LoadingUtil.stopLoading(indicator1);
//                                return false;
//                            }
//
//                            @Override
//                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                                LoadingUtil.stopLoading(indicator1);
//                                return false;
//                            }
//                        }).
//                        //bitmapTransform(new CropCircleTransformation(activity)).
//                                into(IV_pic[0]);
//            }
//        });
//
//        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                if(user.pic_list.size() <= 1)
//                    return;
//
//                RL_RL_no[1].setVisibility(View.VISIBLE);
//                IV_pic[1].getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                String image_url = user.getPic(1);
//                imagepath.add(image_url);
//                //String image_url = "http://graph.facebook.com/"+activity.user.social_id+"/picture?width=" +230 +"&height="+250;
//                Log.i("zxc", "image_url : " + image_url);
//                LoadingUtil.startLoading(indicator2);
//                Glide.with(activity).
//                        load(image_url).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).
//                        listener(new RequestListener<String, GlideDrawable>() {
//                            @Override
//                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                                LoadingUtil.stopLoading(indicator2);
//                                return false;
//                            }
//
//                            @Override
//                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                                LoadingUtil.stopLoading(indicator2);
//                                return false;
//                            }
//                        }).
//                        //bitmapTransform(new CropCircleTransformation(activity)).
//                                into(IV_pic[1]);
//            }
//        });
//
//        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                if(user.pic_list.size() <= 2)
//                    return;
//
//                RL_RL_no[2].setVisibility(View.VISIBLE);
//                IV_pic[2].getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                String image_url = user.getPic(2);
//                imagepath.add(image_url);
//                //String image_url = "http://graph.facebook.com/"+activity.user.social_id+"/picture?width=" +230 +"&height="+250;
//                Log.i("zxc", "image_url : " + image_url);
//                LoadingUtil.startLoading(indicator3);
//                Glide.with(activity).
//                        load(image_url).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).
//                        listener(new RequestListener<String, GlideDrawable>() {
//                            @Override
//                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                                LoadingUtil.stopLoading(indicator3);
//                                return false;
//                            }
//
//                            @Override
//                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                                LoadingUtil.stopLoading(indicator3);
//                                return false;
//                            }
//                        }).
//                        //bitmapTransform(new CropCircleTransformation(activity)).
//                                into(IV_pic[2]);
//            }
//        });
//
//        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                if(user.pic_list.size() <= 3)
//                    return;
//
//                RL_RL_no[3].setVisibility(View.VISIBLE);
//                IV_pic[3].getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                String image_url = user.getPic(3);
//                imagepath.add(image_url);
//                //String image_url = "http://graph.facebook.com/"+activity.user.social_id+"/picture?width=" +230 +"&height="+250;
//                Log.i("zxc", "image_url : " + image_url);
//                LoadingUtil.startLoading(indicator4);
//                Glide.with(activity).
//                        load(image_url).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).
//                        listener(new RequestListener<String, GlideDrawable>() {
//                            @Override
//                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                                LoadingUtil.stopLoading(indicator4);
//                                return false;
//                            }
//
//                            @Override
//                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                                LoadingUtil.stopLoading(indicator4);
//                                return false;
//                            }
//                        }).
//                        //bitmapTransform(new CropCircleTransformation(activity)).
//                                into(IV_pic[4]);
//            }
//        });
//
//        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                if(user.pic_list.size() <= 4)
//                    return;
//
//                RL_RL_no[4].setVisibility(View.VISIBLE);
//                IV_pic[4].getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                String image_url = user.getPic(4);
//                imagepath.add(image_url);
//                //String image_url = "http://graph.facebook.com/"+activity.user.social_id+"/picture?width=" +230 +"&height="+250;
//                Log.i("zxc", "image_url : " + image_url);
//                LoadingUtil.startLoading(indicator5);
//                Glide.with(activity).
//                        load(image_url).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).
//                        listener(new RequestListener<String, GlideDrawable>() {
//                            @Override
//                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                                LoadingUtil.stopLoading(indicator5);
//                                return false;
//                            }
//
//                            @Override
//                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                                LoadingUtil.stopLoading(indicator5);
//                                return false;
//                            }
//                        }).
//                        //bitmapTransform(new CropCircleTransformation(activity)).
//                                into(IV_pic[4]);
//            }
//        });
//
//        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                if(user.pic_list.size() <= 5)
//                    return;
//
//                RL_RL_no[5].setVisibility(View.VISIBLE);
//                IV_pic[5].getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                String image_url = user.getPic(5);
//                imagepath.add(image_url);
//                //String image_url = "http://graph.facebook.com/"+activity.user.social_id+"/picture?width=" +230 +"&height="+250;
//                Log.i("zxc", "image_url : " + image_url);
//                LoadingUtil.startLoading(indicator6);
//                Glide.with(activity).
//                        load(image_url).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).
//                        listener(new RequestListener<String, GlideDrawable>() {
//                            @Override
//                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                                LoadingUtil.stopLoading(indicator6);
//                                return false;
//                            }
//
//                            @Override
//                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                                LoadingUtil.stopLoading(indicator6);
//                                return false;
//                            }
//                        }).
//                        //bitmapTransform(new CropCircleTransformation(activity)).
//                                into(IV_pic[5]);
//            }
//        });
//
//
//
//        IV_pic[0].setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/*");
//                if(IV_pic[0].getDrawable() == null)
//                    startActivityForResult(intent, 0);
//                else
//                    startActivityForResult(intent, 1);
//            }
//        });
//
//        IV_pic[1].setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/*");
//                if(IV_pic[1].getDrawable() == null)
//                    startActivityForResult(intent, 0);
//                else
//                    startActivityForResult(intent, 2);
//            }
//        });
//        IV_pic[2].setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/*");
//                if(IV_pic[2].getDrawable() == null)
//                    startActivityForResult(intent, 0);
//                else
//                    startActivityForResult(intent, 3);
//            }
//        });
//        IV_pic[3].setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/*");
//                if(IV_pic[3].getDrawable() == null)
//                    startActivityForResult(intent, 0);
//                else
//                    startActivityForResult(intent, 4);
//            }
//        });
//        IV_pic[4].setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/*");
//                if(IV_pic[4].getDrawable() == null)
//                    startActivityForResult(intent, 0);
//                else
//                    startActivityForResult(intent, 5);
//            }
//        });
//        IV_pic[5].setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/*");
//                if(IV_pic[5].getDrawable() == null)
//                    startActivityForResult(intent, 0);
//                else
//                    startActivityForResult(intent, 6);
//            }
//        });
//
//
//        RL_RL_no[0].setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(imagepath.size()==1) {
//                    Toast.makeText(activity, "사진이 하나 이상 필요해요!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                RL_RL_no[0].setVisibility(View.INVISIBLE);
//                imagepath.remove(0);
//                refresh();
//            }
//        });
//        RL_RL_no[1].setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                RL_RL_no[1].setVisibility(View.INVISIBLE);
//                imagepath.remove(1);
//                refresh();
//            }
//        });
//        RL_RL_no[2].setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                RL_RL_no[2].setVisibility(View.INVISIBLE);
//                imagepath.remove(2);
//                refresh();
//            }
//        });
//        RL_RL_no[3].setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                RL_RL_no[3].setVisibility(View.INVISIBLE);
//                imagepath.remove(3);
//                refresh();
//            }
//        });
//        RL_RL_no[4].setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                RL_RL_no[4].setVisibility(View.INVISIBLE);
//                imagepath.remove(4);
//                refresh();
//            }
//        });
//        RL_RL_no[5].setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                RL_RL_no[5].setVisibility(View.INVISIBLE);
//                imagepath.remove(5);
//                refresh();
//            }
//        });
//
//
//        BT_X.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                List<String> temp_pic_list = new ArrayList<String>();
//                String temp_pic_small;
//                String temp_about_me = ET_about_me.getText().toString();
//
//                SimpleDateFormat sdfNow = new SimpleDateFormat("yyMMddHHmmssSSS");
//                String current_time = sdfNow.format(new Date(System.currentTimeMillis()));
//
//                Log.i("zxc", "imagepath.length : " + imagepath.size());
//                for(int i=0;i<imagepath.size();i++) {
//                    Log.i("zxc", "imagepath : "  + i + " " + imagepath.get(i));
//                    if(imagepath.get(i)==null)
//                        continue;
//
//                    if(imagepath.get(i).contains("facebook")) {
//                        temp_pic_list.add(imagepath.get(i));
//                    }else if(imagepath.get(i).contains("http://")) {
//                        temp_pic_list.add(imagepath.get(i).substring(imagepath.get(i).lastIndexOf("images/")+7));
//                    }else {
//                        temp_pic_list.add(user.nickname + i + current_time);
//                    }
//                }
//
//                Log.i("zxc", "size : " + temp_pic_list.size());
//                if(temp_pic_list.size()==0){
//                    Toast.makeText(activity, "사진이 하나 이상 필요해요!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                Log.i("zxc", "user : " + user);
//
//                temp_pic_small = temp_pic_list.get(0);
//
//                Map field = new HashMap();
//                field.put("pic_list", temp_pic_list);
//                field.put("pic_small", temp_pic_small);
//                field.put("about_me", temp_about_me);
//
//                connEditProfile(field);
//
//                getActivity().setResult(Constants.ACTIVITY_CODE_TAB5_REFRESH_RESULT);
////
////                Fragment fragment = new Signup7AboutMeFragment();
////                FragmentTransaction ft = getFragmentManager().beginTransaction();
////                ft.replace(R.id.activity_signup, fragment);
////                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
////                ft.addToBackStack(null);
////                ft.commit();
//            }
//        });
//
//
//        return view;
//
//    }
//
//    private void refresh(){
//        int i;
//        Log.i("asd", imagepath.toString());
//        for(i=0;i<imagepath.size();i++){
//            Glide.with(activity).
//                    load(imagepath.get(i)).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).
//                    into(IV_pic[i]);
//            RL_RL_no[i].setVisibility(View.VISIBLE);
//        }
//        for(;i<6;i++){
//            IV_pic[i].setImageDrawable(null);
//            RL_RL_no[i].setVisibility(View.INVISIBLE);
//        }
//
//        for(int j=0;j<6;j++)
//        Log.i("zxc", "isChanged[j] : " + j + " " + isChanged[j]);
//
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        if(resultCode == RESULT_OK){
//            if(requestCode == 0) {
//                Uri selectedImageUri = data.getData();
//                tempImagePath = getPath(selectedImageUri);
//
//                int[] maxTextureSize = new int[1];
//                GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0);
//
//                final int[] tempMaxTextureSize = maxTextureSize;
//
//                Glide.with(activity).load(tempImagePath).asBitmap().centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).into(new SimpleTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                        if (resource.getHeight() > tempMaxTextureSize[0]) {
//
//                            imagepath.add(tempImagePath);
//                            Log.i("asd", imagepath.toString());
//                            ImageView tempImageView;
//                            if (IV_pic[0].getDrawable() == null) {
//                                tempImageView = IV_pic[0];
//                                isChanged[0] = true;
//                                RL_RL_no[0].setVisibility(View.VISIBLE);
//                            }
//                            else if (IV_pic[1].getDrawable() == null) {
//                                tempImageView = IV_pic[1];
//                                isChanged[1] = true;
//                                RL_RL_no[1].setVisibility(View.VISIBLE);
//                            }
//                            else if (IV_pic[2].getDrawable() == null) {
//                                tempImageView = IV_pic[2];
//                                isChanged[2] = true;
//                                RL_RL_no[2].setVisibility(View.VISIBLE);
//                            }
//                            else if (IV_pic[3].getDrawable() == null) {
//                                tempImageView = IV_pic[3];
//                                isChanged[3] = true;
//                                RL_RL_no[3].setVisibility(View.VISIBLE);
//                            }
//                            else if (IV_pic[4].getDrawable() == null) {
//                                tempImageView = IV_pic[4];
//                                isChanged[4] = true;
//                                RL_RL_no[4].setVisibility(View.VISIBLE);
//                            }
//                            else {
//                                tempImageView = IV_pic[5];
//                                isChanged[5] = true;
//                                RL_RL_no[5].setVisibility(View.VISIBLE);
//                            }
//                            Log.i("zxc", "w : " + tempImageView.getWidth() + " h : " + tempImageView.getHeight());
//                            resizedWidth = tempImageView.getWidth();
//                            resizedHeight = tempImageView.getHeight();
//                            tempImageView.setImageBitmap(resource.createScaledBitmap(resource, resizedWidth, resizedHeight, false));
//                        }
//                    }
//                });
//            }else if(requestCode == 1) {
//                //isNotFacebookProfile = true;
//                Uri selectedImageUri = data.getData();
//                imagepath.set(0, getPath(selectedImageUri));
//                SimpleDateFormat sdfNow = new SimpleDateFormat("yyMMddHHmmssSSS");
//                String current_time = sdfNow.format(new Date(System.currentTimeMillis()));
//                //user.image_url = user.nickname+current_time;
//
//                int[] maxTextureSize = new int[1];
//                GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0);
//
//                final int[] tempMaxTextureSize = maxTextureSize;
//
//                Glide.with(activity).load(imagepath.get(0)).asBitmap().centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).into(new SimpleTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                        if (resource.getHeight() > tempMaxTextureSize[0]) {
//
//                            ImageView tempImageView;
//                            tempImageView = IV_pic[0];
//
//
//                            Log.i("zxc", "w : " + tempImageView.getWidth() + " h : " + tempImageView.getHeight());
//                            resizedWidth = tempImageView.getWidth();
//                            resizedHeight = tempImageView.getHeight();
//                            tempImageView.setImageBitmap(resource.createScaledBitmap(resource, resizedWidth, resizedHeight, false));
//                        }
//                    }
//                });
//
//                isChanged[0] = true;
//            }else if(requestCode == 2) {
//                Uri selectedImageUri = data.getData();
//                imagepath.set(1, getPath(selectedImageUri));
//                SimpleDateFormat sdfNow = new SimpleDateFormat("yyMMddHHmmssSSS");
//                String current_time = sdfNow.format(new Date(System.currentTimeMillis()));
//                //user.image_url = user.nickname+current_time;
//
//                int[] maxTextureSize = new int[1];
//                GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0);
//
//                final int[] tempMaxTextureSize = maxTextureSize;
//
//                Glide.with(activity).load(imagepath.get(1)).asBitmap().centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).into(new SimpleTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                        if (resource.getHeight() > tempMaxTextureSize[0]) {
//
//                            ImageView tempImageView;
//                            tempImageView = IV_pic[1];
//
//
//                            Log.i("zxc", "w : " + tempImageView.getWidth() + " h : " + tempImageView.getHeight());
//                            resizedWidth = tempImageView.getWidth();
//                            resizedHeight = tempImageView.getHeight();
//                            tempImageView.setImageBitmap(resource.createScaledBitmap(resource, resizedWidth, resizedHeight, false));
//                        }
//                    }
//                });
//
//                isChanged[1] = true;
//            }else if(requestCode == 3) {
//                Uri selectedImageUri = data.getData();
//                imagepath.set(2, getPath(selectedImageUri));
//                SimpleDateFormat sdfNow = new SimpleDateFormat("yyMMddHHmmssSSS");
//                String current_time = sdfNow.format(new Date(System.currentTimeMillis()));
//                //user.image_url = user.nickname+current_time;
//
//                int[] maxTextureSize = new int[1];
//                GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0);
//
//                final int[] tempMaxTextureSize = maxTextureSize;
//
//                Glide.with(activity).load(imagepath.get(2)).asBitmap().centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).into(new SimpleTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                        if (resource.getHeight() > tempMaxTextureSize[0]) {
//
//                            ImageView tempImageView;
//                            tempImageView = IV_pic[2];
//
//
//                            Log.i("zxc", "w : " + tempImageView.getWidth() + " h : " + tempImageView.getHeight());
//                            resizedWidth = tempImageView.getWidth();
//                            resizedHeight = tempImageView.getHeight();
//                            tempImageView.setImageBitmap(resource.createScaledBitmap(resource, resizedWidth, resizedHeight, false));
//                        }
//                    }
//                });
//                isChanged[2] = true;
//            }else if(requestCode == 4) {
//                Uri selectedImageUri = data.getData();
//                imagepath.set(3, getPath(selectedImageUri));
//                SimpleDateFormat sdfNow = new SimpleDateFormat("yyMMddHHmmssSSS");
//                String current_time = sdfNow.format(new Date(System.currentTimeMillis()));
//                //user.image_url = user.nickname+current_time;
//
//                int[] maxTextureSize = new int[1];
//                GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0);
//
//                final int[] tempMaxTextureSize = maxTextureSize;
//
//                Glide.with(activity).load(imagepath.get(3)).asBitmap().centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).into(new SimpleTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                        if (resource.getHeight() > tempMaxTextureSize[0]) {
//
//                            ImageView tempImageView;
//                            tempImageView = IV_pic[3];
//
//
//                            Log.i("zxc", "w : " + tempImageView.getWidth() + " h : " + tempImageView.getHeight());
//                            resizedWidth = tempImageView.getWidth();
//                            resizedHeight = tempImageView.getHeight();
//                            tempImageView.setImageBitmap(resource.createScaledBitmap(resource, resizedWidth, resizedHeight, false));
//                        }
//                    }
//                });
//                isChanged[3] = true;
//            }else if(requestCode == 5) {
//                Uri selectedImageUri = data.getData();
//                imagepath.set(4, getPath(selectedImageUri));
//                SimpleDateFormat sdfNow = new SimpleDateFormat("yyMMddHHmmssSSS");
//                String current_time = sdfNow.format(new Date(System.currentTimeMillis()));
//                //user.image_url = user.nickname+current_time;
//
//                int[] maxTextureSize = new int[1];
//                GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0);
//
//                final int[] tempMaxTextureSize = maxTextureSize;
//
//                Glide.with(activity).load(imagepath.get(4)).asBitmap().centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).into(new SimpleTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                        if (resource.getHeight() > tempMaxTextureSize[0]) {
//
//                            ImageView tempImageView;
//                            tempImageView = IV_pic[4];
//
//
//                            Log.i("zxc", "w : " + tempImageView.getWidth() + " h : " + tempImageView.getHeight());
//                            resizedWidth = tempImageView.getWidth();
//                            resizedHeight = tempImageView.getHeight();
//                            tempImageView.setImageBitmap(resource.createScaledBitmap(resource, resizedWidth, resizedHeight, false));
//                        }
//                    }
//                });
//                isChanged[4] = true;
//            }else if(requestCode == 6) {
//                Uri selectedImageUri = data.getData();
//                imagepath.set(5, getPath(selectedImageUri));
//                SimpleDateFormat sdfNow = new SimpleDateFormat("yyMMddHHmmssSSS");
//                String current_time = sdfNow.format(new Date(System.currentTimeMillis()));
//                //user.image_url = user.nickname+current_time;
//
//                int[] maxTextureSize = new int[1];
//                GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0);
//
//                final int[] tempMaxTextureSize = maxTextureSize;
//
//                Glide.with(activity).load(imagepath.get(5)).asBitmap().centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).into(new SimpleTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                        if (resource.getHeight() > tempMaxTextureSize[0]) {
//
//                            ImageView tempImageView;
//                            tempImageView = IV_pic[5];
//
//
//                            Log.i("zxc", "w : " + tempImageView.getWidth() + " h : " + tempImageView.getHeight());
//                            resizedWidth = tempImageView.getWidth();
//                            resizedHeight = tempImageView.getHeight();
//                            tempImageView.setImageBitmap(resource.createScaledBitmap(resource, resizedWidth, resizedHeight, false));
//                        }
//                    }
//                });
//                isChanged[5] = true;
//            }
//        }
//
//
//    }
//    public String getPath(Uri uri) {
//        String[] projection = { MediaStore.Images.Media.DATA };
//        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
//        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//        cursor.moveToFirst();
//        return cursor.getString(column_index);
//    }
//
//    public void connEditProfile(Map field) {
//        LoadingUtil.startLoading(indicator);
//        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
//        conn.editProfile(user._id, field)
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<User>() {
//                    @Override
//                    public final void onCompleted() {
//                        LoadingUtil.startLoading(indicator);
//                        activity.setResult(Constants.ACTIVITY_CODE_TAB5_REFRESH_RESULT);
//                        edit = false;
//                        activity.finish();
//                    }
//                    @Override
//                    public final void onError(Throwable e) {
//                        e.printStackTrace();
//                    }
//                    @Override
//                    public final void onNext(User response) {
//                        if (response != null) {
//                            SharedManager.getInstance().setMe(response);
//
//                            Log.i("zxc","zxc1");
//                            Log.i("zxc","zxc1" + response.pic_list.size());
//
//                            for(int i=0;i<response.pic_list.size();i++) {
//                                Log.i("zxc","zxc4");
//                                if(imagepath.get(i) == null || imagepath.get(i).contains("http://"))
//                                    continue;
//
//                                Log.i("zxc","zxc3");
//                                Log.i("makejin", "response.pic_list.get(" +i +") : " + response.pic_list.get(i));
//                                if(imagepath.get(i).contains("facebook")) {
//                                    continue;
//                                }
//                                Log.i("zxc","zxc2");
//                                uploadFile1(response, i);
//                            }
//
//
//                        } else {
//                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }
//
//
//    public void uploadFile1(final User user, final int index) {
//        String tempIndex = String.valueOf(index);
//        Log.i("asd", "imagepath.length : " + imagepath.size());
//
//        for(int i=0;i<imagepath.size();i++)
//            Log.i("asd", "imagepath.get(i) : " +i +" : " + imagepath.get(i));
//
//
//        File file = new File(imagepath.get(index));
//
//        RequestBody ubody = RequestBody.create(MediaType.parse("image/*"), file);
//
//        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
//        conn.fileUploadWrite_User(user._id, tempIndex, ubody)
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<User>() {
//                    @Override
//                    public final void onCompleted() {
//
//                    }
//                    @Override
//                    public final void onError(Throwable e) {
//                        e.printStackTrace();
//                        Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
//                    }
//                    @Override
//                    public final void onNext(User response) {
//                        if (response != null) {
//                            SharedManager.getInstance().setMe(response);
////                            image_url = response.pic_list.get(index);
//                        } else {
//                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }
//}
