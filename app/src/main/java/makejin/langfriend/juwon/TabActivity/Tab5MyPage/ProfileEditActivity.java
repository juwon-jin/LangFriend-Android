package makejin.langfriend.juwon.TabActivity.Tab5MyPage;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.opengl.GLES20;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import makejin.langfriend.R;
import makejin.langfriend.juwon.Model.User;
import makejin.langfriend.juwon.Utils.Connections.CSConnection;
import makejin.langfriend.juwon.Utils.Connections.ServiceGenerator;
import makejin.langfriend.juwon.Utils.Constants.Constants;
import makejin.langfriend.juwon.Utils.Loadings.LoadingUtil;
import makejin.langfriend.juwon.Utils.SharedManager.SharedManager;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static makejin.langfriend.juwon.TabActivity.Tab5MyPage.ProfileDetailActivity.edit;

public class ProfileEditActivity extends AppCompatActivity {
    Button BT_X;


    ImageView [] IV_pic = new ImageView[6];


    public List<String> imagepath = new ArrayList<>();
    public String tempImagePath = null;

    int resizedWidth;
    int resizedHeight;

    public LinearLayout indicator1;
    public LinearLayout indicator2;
    public LinearLayout indicator3;
    public LinearLayout indicator4;
    public LinearLayout indicator5;
    public LinearLayout indicator6;

    EditText ET_about_me;

    public static User user;

    RelativeLayout[] RL_RL_no = new RelativeLayout[6];

    LinearLayout indicator;

    CoordinatorLayout view;

    ProfileEditActivity activity;

    public static int REQUEST_PIC_NUM = -1;
    public final int PIC_0 = 10; //don't know where pic
    public final int PIC_1 = 11;
    public final int PIC_2 = 12;
    public final int PIC_3 = 13;
    public final int PIC_4 = 14;
    public final int PIC_5 = 15;
    public final int PIC_6 = 16;

    int pic_upload_count = 0;
    int pic_upload_count_2 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        registerView();
    }


    private void registerView(){
        this.activity = this;

        view = (CoordinatorLayout) findViewById(R.id.view);

        indicator1 = (LinearLayout) findViewById(R.id.indicator1);
        indicator2 = (LinearLayout) findViewById(R.id.indicator2);
        indicator3 = (LinearLayout) findViewById(R.id.indicator3);
        indicator4 = (LinearLayout) findViewById(R.id.indicator4);
        indicator5 = (LinearLayout) findViewById(R.id.indicator5);
        indicator6 = (LinearLayout) findViewById(R.id.indicator6);

        BT_X = (Button) findViewById(R.id.BT_X);

        indicator = (LinearLayout) findViewById(R.id.indicator);

        user = SharedManager.getInstance().getMe();


        Log.i("zxc", "activity.user.social_type : " + user.social_type);
        Log.i("zxc", "activity.user.pic_list : " + user.getPic(0));


        IV_pic[0] = (ImageView) findViewById(R.id.IV_pic1);
        IV_pic[1] = (ImageView) findViewById(R.id.IV_pic2);
        IV_pic[2] = (ImageView) findViewById(R.id.IV_pic3);
        IV_pic[3] = (ImageView) findViewById(R.id.IV_pic4);
        IV_pic[4] = (ImageView) findViewById(R.id.IV_pic5);
        IV_pic[5] = (ImageView) findViewById(R.id.IV_pic6);

        ET_about_me = (EditText) findViewById(R.id.ET_about_me);


        RL_RL_no[0] = (RelativeLayout) findViewById(R.id.RL_RL_no_1);
        RL_RL_no[1] = (RelativeLayout) findViewById(R.id.RL_RL_no_2);
        RL_RL_no[2] = (RelativeLayout) findViewById(R.id.RL_RL_no_3);
        RL_RL_no[3] = (RelativeLayout) findViewById(R.id.RL_RL_no_4);
        RL_RL_no[4] = (RelativeLayout) findViewById(R.id.RL_RL_no_5);
        RL_RL_no[5] = (RelativeLayout) findViewById(R.id.RL_RL_no_6);



        ET_about_me.setText(user.about_me);

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(user.pic_list.size() <= 0)
                    return;

                RL_RL_no[0].setVisibility(View.VISIBLE);
                IV_pic[0].getViewTreeObserver().removeOnGlobalLayoutListener(this);
                String image_url = user.getPic(0);
                imagepath.add(image_url);
                //String image_url = "http://graph.facebook.com/"+activity.user.social_id+"/picture?width=" +230 +"&height="+250;
                Log.i("zxc", "image_url : " + image_url);
                LoadingUtil.startLoading(indicator1);
                Glide.with(activity).
                        load(image_url).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).
                        listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                LoadingUtil.stopLoading(indicator1);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                LoadingUtil.stopLoading(indicator1);
                                return false;
                            }
                        }).
                        //bitmapTransform(new CropCircleTransformation(activity)).
                                into(IV_pic[0]);
            }
        });

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(user.pic_list.size() <= 1)
                    return;

                RL_RL_no[1].setVisibility(View.VISIBLE);
                IV_pic[1].getViewTreeObserver().removeOnGlobalLayoutListener(this);
                String image_url = user.getPic(1);
                imagepath.add(image_url);
                //String image_url = "http://graph.facebook.com/"+activity.user.social_id+"/picture?width=" +230 +"&height="+250;
                Log.i("zxc", "image_url : " + image_url);
                LoadingUtil.startLoading(indicator2);
                Glide.with(activity).
                        load(image_url).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).
                        listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                LoadingUtil.stopLoading(indicator2);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                LoadingUtil.stopLoading(indicator2);
                                return false;
                            }
                        }).
                        //bitmapTransform(new CropCircleTransformation(activity)).
                                into(IV_pic[1]);
            }
        });

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(user.pic_list.size() <= 2)
                    return;

                RL_RL_no[2].setVisibility(View.VISIBLE);
                IV_pic[2].getViewTreeObserver().removeOnGlobalLayoutListener(this);
                String image_url = user.getPic(2);
                imagepath.add(image_url);
                //String image_url = "http://graph.facebook.com/"+activity.user.social_id+"/picture?width=" +230 +"&height="+250;
                Log.i("zxc", "image_url : " + image_url);
                LoadingUtil.startLoading(indicator3);
                Glide.with(activity).
                        load(image_url).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).
                        listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                LoadingUtil.stopLoading(indicator3);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                LoadingUtil.stopLoading(indicator3);
                                return false;
                            }
                        }).
                        //bitmapTransform(new CropCircleTransformation(activity)).
                                into(IV_pic[2]);
            }
        });

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(user.pic_list.size() <= 3)
                    return;

                RL_RL_no[3].setVisibility(View.VISIBLE);
                IV_pic[3].getViewTreeObserver().removeOnGlobalLayoutListener(this);
                String image_url = user.getPic(3);
                imagepath.add(image_url);
                //String image_url = "http://graph.facebook.com/"+activity.user.social_id+"/picture?width=" +230 +"&height="+250;
                Log.i("zxc", "image_url : " + image_url);
                LoadingUtil.startLoading(indicator4);
                Glide.with(activity).
                        load(image_url).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).
                        listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                LoadingUtil.stopLoading(indicator4);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                LoadingUtil.stopLoading(indicator4);
                                return false;
                            }
                        }).
                        //bitmapTransform(new CropCircleTransformation(activity)).
                                into(IV_pic[3]);
            }
        });

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(user.pic_list.size() <= 4)
                    return;

                RL_RL_no[4].setVisibility(View.VISIBLE);
                IV_pic[4].getViewTreeObserver().removeOnGlobalLayoutListener(this);
                String image_url = user.getPic(4);
                imagepath.add(image_url);
                //String image_url = "http://graph.facebook.com/"+activity.user.social_id+"/picture?width=" +230 +"&height="+250;
                Log.i("zxc", "image_url : " + image_url);
                LoadingUtil.startLoading(indicator5);
                Glide.with(activity).
                        load(image_url).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).
                        listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                LoadingUtil.stopLoading(indicator5);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                LoadingUtil.stopLoading(indicator5);
                                return false;
                            }
                        }).
                        //bitmapTransform(new CropCircleTransformation(activity)).
                                into(IV_pic[4]);
            }
        });

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(user.pic_list.size() <= 5)
                    return;

                RL_RL_no[5].setVisibility(View.VISIBLE);
                IV_pic[5].getViewTreeObserver().removeOnGlobalLayoutListener(this);
                String image_url = user.getPic(5);
                imagepath.add(image_url);
                //String image_url = "http://graph.facebook.com/"+activity.user.social_id+"/picture?width=" +230 +"&height="+250;
                Log.i("zxc", "image_url : " + image_url);
                LoadingUtil.startLoading(indicator6);
                Glide.with(activity).
                        load(image_url).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).
                        listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                LoadingUtil.stopLoading(indicator6);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                LoadingUtil.stopLoading(indicator6);
                                return false;
                            }
                        }).
                        //bitmapTransform(new CropCircleTransformation(activity)).
                                into(IV_pic[5]);
            }
        });



        IV_pic[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                if(IV_pic[0].getDrawable() == null){
                    startActivityForResult(intent, 0);
                }
                else{
                    startActivityForResult(intent, 1);
                }

            }
        });

        IV_pic[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                if(IV_pic[1].getDrawable() == null)
                    startActivityForResult(intent, 0);
                else
                    startActivityForResult(intent, 2);
            }
        });
        IV_pic[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                if(IV_pic[2].getDrawable() == null)
                    startActivityForResult(intent, 0);
                else
                    startActivityForResult(intent, 3);
            }
        });
        IV_pic[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                if(IV_pic[3].getDrawable() == null)
                    startActivityForResult(intent, 0);
                else
                    startActivityForResult(intent, 4);
            }
        });
        IV_pic[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                if(IV_pic[4].getDrawable() == null)
                    startActivityForResult(intent, 0);
                else
                    startActivityForResult(intent, 5);
            }
        });
        IV_pic[5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                if(IV_pic[5].getDrawable() == null)
                    startActivityForResult(intent, 0);
                else
                    startActivityForResult(intent, 6);
            }
        });


        RL_RL_no[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imagepath.size()==1) {
                    Toast.makeText(activity, "사진이 하나 이상 필요해요!", Toast.LENGTH_SHORT).show();
                    return;
                }
                RL_RL_no[0].setVisibility(View.INVISIBLE);
                imagepath.remove(0);
                refresh();
            }
        });
        RL_RL_no[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RL_RL_no[1].setVisibility(View.INVISIBLE);
                imagepath.remove(1);
                refresh();
            }
        });
        RL_RL_no[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RL_RL_no[2].setVisibility(View.INVISIBLE);
                imagepath.remove(2);
                refresh();
            }
        });
        RL_RL_no[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RL_RL_no[3].setVisibility(View.INVISIBLE);
                imagepath.remove(3);
                refresh();
            }
        });
        RL_RL_no[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RL_RL_no[4].setVisibility(View.INVISIBLE);
                imagepath.remove(4);
                refresh();
            }
        });
        RL_RL_no[5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RL_RL_no[5].setVisibility(View.INVISIBLE);
                imagepath.remove(5);
                refresh();
            }
        });


        BT_X.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> temp_pic_list = new ArrayList<String>();
                String temp_pic_small;
                String temp_about_me = ET_about_me.getText().toString();

                SimpleDateFormat sdfNow = new SimpleDateFormat("yyMMddHHmmssSSS");
                String current_time = sdfNow.format(new Date(System.currentTimeMillis()));

                Log.i("zxc", "imagepath.length : " + imagepath.size());
                for(int i=0;i<imagepath.size();i++) {
                    Log.i("zxc", "imagepath : "  + i + " " + imagepath.get(i));
                    if(imagepath.get(i)==null)
                        continue;

                    if(imagepath.get(i).contains("facebook")) {
                        temp_pic_list.add(imagepath.get(i));
                    }else if(imagepath.get(i).contains("http://")) {
                        temp_pic_list.add(imagepath.get(i).substring(imagepath.get(i).lastIndexOf("images/")+7));
                    }else {
                        temp_pic_list.add(user.nickname + i + current_time);
                    }
                }

                Log.i("zxc", "size : " + temp_pic_list.size());
                if(temp_pic_list.size()==0){
                    Toast.makeText(activity, "사진이 하나 이상 필요해요!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i("zxc", "user : " + user);

                temp_pic_small = temp_pic_list.get(0);

                Map field = new HashMap();
                field.put("pic_list", temp_pic_list);
                field.put("pic_small", temp_pic_small);
                field.put("about_me", temp_about_me);

                connEditProfile(field);

                setResult(Constants.ACTIVITY_CODE_TAB5_REFRESH_RESULT);
//
//                Fragment fragment = new Signup7AboutMeFragment();
//                FragmentTransaction ft = getFragmentManager().beginTransaction();
//                ft.replace(R.id.activity_signup, fragment);
//                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//                ft.addToBackStack(null);
//                ft.commit();
            }
        });

    }
    private void refresh(){
        int i;
        Log.i("asd", imagepath.toString());
        for(i=0;i<imagepath.size();i++){
            Glide.with(activity).
                    load(imagepath.get(i)).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).
                    into(IV_pic[i]);
            RL_RL_no[i].setVisibility(View.VISIBLE);
        }
        for(;i<6;i++){
            IV_pic[i].setImageDrawable(null);
            RL_RL_no[i].setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Uri resultUri = result.getUri();
                tempImagePath = resultUri.toString();
                Log.i("xzc", "resultUri : " + resultUri);
                int[] maxTextureSize = new int[1];
                GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0);
                final int[] tempMaxTextureSize = maxTextureSize;

                switch (REQUEST_PIC_NUM){
                    case PIC_0 : //REQUEST_PIC_NUM = PIC_1;
                        Glide.with(activity).load(resultUri).asBitmap().centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                if (resource.getHeight() > tempMaxTextureSize[0]) {

                                    imagepath.add(tempImagePath);
                                    Log.i("asd", imagepath.toString());
                                    ImageView tempImageView;
                                    if (IV_pic[0].getDrawable() == null) {
                                        tempImageView = IV_pic[0];
                                        RL_RL_no[0].setVisibility(View.VISIBLE);
                                    }
                                    else if (IV_pic[1].getDrawable() == null) {
                                        tempImageView = IV_pic[1];
                                        RL_RL_no[1].setVisibility(View.VISIBLE);
                                    }
                                    else if (IV_pic[2].getDrawable() == null) {
                                        tempImageView = IV_pic[2];
                                        RL_RL_no[2].setVisibility(View.VISIBLE);
                                    }
                                    else if (IV_pic[3].getDrawable() == null) {
                                        tempImageView = IV_pic[3];
                                        RL_RL_no[3].setVisibility(View.VISIBLE);
                                    }
                                    else if (IV_pic[4].getDrawable() == null) {
                                        tempImageView = IV_pic[4];
                                        RL_RL_no[4].setVisibility(View.VISIBLE);
                                    }
                                    else {
                                        tempImageView = IV_pic[5];
                                        RL_RL_no[5].setVisibility(View.VISIBLE);
                                    }
                                    Log.i("zxc", "w : " + tempImageView.getWidth() + " h : " + tempImageView.getHeight());
                                    resizedWidth = tempImageView.getWidth();
                                    resizedHeight = tempImageView.getHeight();
                                    tempImageView.setImageBitmap(resource.createScaledBitmap(resource, resizedWidth, resizedHeight, false));
                                }
                            }
                        });
                        break;
                    case PIC_1 : //REQUEST_PIC_NUM = PIC_1;
                        if(imagepath.size()==0){
                            //imagepath.add(tempImagePath);
                            imagepath.add(tempImagePath);
                        }else{
                            Log.i("zxc", "1"+tempImagePath);
                            Log.i("zxc", "2"+Uri.parse(tempImagePath));

                            //imagepath.set(0, tempImagePath);
                            imagepath.set(0, tempImagePath);
                        }
                        Glide.with(activity).load(imagepath.get(0)).asBitmap().centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                if (resource.getHeight() > tempMaxTextureSize[0]) {

                                    ImageView tempImageView;
                                    tempImageView = IV_pic[0];


                                    Log.i("zxc", "w : " + tempImageView.getWidth() + " h : " + tempImageView.getHeight());
                                    resizedWidth = tempImageView.getWidth();
                                    resizedHeight = tempImageView.getHeight();
                                    tempImageView.setImageBitmap(resource.createScaledBitmap(resource, resizedWidth, resizedHeight, false));
                                }
                            }
                        });
                        break;
                    case PIC_2 : //REQUEST_PIC_NUM = PIC_1;
                        if(imagepath.size()<=1){
                            imagepath.add(tempImagePath);
                        }else{
                            imagepath.set(1, tempImagePath);
                        }
                        Glide.with(activity).load(imagepath.get(1)).asBitmap().centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                if (resource.getHeight() > tempMaxTextureSize[0]) {

                                    ImageView tempImageView;
                                    tempImageView = IV_pic[1];


                                    Log.i("zxc", "w : " + tempImageView.getWidth() + " h : " + tempImageView.getHeight());
                                    resizedWidth = tempImageView.getWidth();
                                    resizedHeight = tempImageView.getHeight();
                                    tempImageView.setImageBitmap(resource.createScaledBitmap(resource, resizedWidth, resizedHeight, false));
                                }
                            }
                        });
                        break;
                    case PIC_3 : //REQUEST_PIC_NUM = PIC_1;
                        if(imagepath.size()<=2){
                            imagepath.add(tempImagePath);
                        }else{
                            imagepath.set(2, tempImagePath);
                        }
                        Glide.with(activity).load(imagepath.get(2)).asBitmap().centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                if (resource.getHeight() > tempMaxTextureSize[0]) {

                                    ImageView tempImageView;
                                    tempImageView = IV_pic[2];


                                    Log.i("zxc", "w : " + tempImageView.getWidth() + " h : " + tempImageView.getHeight());
                                    resizedWidth = tempImageView.getWidth();
                                    resizedHeight = tempImageView.getHeight();
                                    tempImageView.setImageBitmap(resource.createScaledBitmap(resource, resizedWidth, resizedHeight, false));
                                }
                            }
                        });
                        break;
                    case PIC_4 : //REQUEST_PIC_NUM = PIC_1;
                        if(imagepath.size()<=3){
                            imagepath.add(tempImagePath);
                        }else{
                            imagepath.set(3, tempImagePath);
                        }
                        Glide.with(activity).load(imagepath.get(3)).asBitmap().centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                if (resource.getHeight() > tempMaxTextureSize[0]) {

                                    ImageView tempImageView;
                                    tempImageView = IV_pic[3];


                                    Log.i("zxc", "w : " + tempImageView.getWidth() + " h : " + tempImageView.getHeight());
                                    resizedWidth = tempImageView.getWidth();
                                    resizedHeight = tempImageView.getHeight();
                                    tempImageView.setImageBitmap(resource.createScaledBitmap(resource, resizedWidth, resizedHeight, false));
                                }
                            }
                        });
                        break;
                    case PIC_5 : //REQUEST_PIC_NUM = PIC_1;
                        if(imagepath.size()<=4){
                            imagepath.add(tempImagePath);
                        }else{
                            imagepath.set(4, tempImagePath);
                        }
                        Glide.with(activity).load(imagepath.get(4)).asBitmap().centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                if (resource.getHeight() > tempMaxTextureSize[0]) {

                                    ImageView tempImageView;
                                    tempImageView = IV_pic[4];


                                    Log.i("zxc", "w : " + tempImageView.getWidth() + " h : " + tempImageView.getHeight());
                                    resizedWidth = tempImageView.getWidth();
                                    resizedHeight = tempImageView.getHeight();
                                    tempImageView.setImageBitmap(resource.createScaledBitmap(resource, resizedWidth, resizedHeight, false));
                                }
                            }
                        });
                        break;
                    case PIC_6 : //REQUEST_PIC_NUM = PIC_1;
                        if(imagepath.size()<=5){
                            imagepath.add(tempImagePath);
                        }else{
                            imagepath.set(5, tempImagePath);
                        }
                        Glide.with(activity).load(imagepath.get(5)).asBitmap().centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                if (resource.getHeight() > tempMaxTextureSize[0]) {

                                    ImageView tempImageView;
                                    tempImageView = IV_pic[5];


                                    Log.i("zxc", "w : " + tempImageView.getWidth() + " h : " + tempImageView.getHeight());
                                    resizedWidth = tempImageView.getWidth();
                                    resizedHeight = tempImageView.getHeight();
                                    tempImageView.setImageBitmap(resource.createScaledBitmap(resource, resizedWidth, resizedHeight, false));
                                }
                            }
                        });
                        break;
                    default :
                        break;
                }

                return;
            }
            else if(requestCode == 0) {
                REQUEST_PIC_NUM = PIC_0;
            }else if(requestCode == 1) {
                REQUEST_PIC_NUM = PIC_1;
            }else if(requestCode == 2) {
                REQUEST_PIC_NUM = PIC_2;
            }else if(requestCode == 3) {
                REQUEST_PIC_NUM = PIC_3;
            }else if(requestCode == 4) {
                REQUEST_PIC_NUM = PIC_4;
            }else if(requestCode == 5) {
                REQUEST_PIC_NUM = PIC_5;
            }else if(requestCode == 6) {
                REQUEST_PIC_NUM = PIC_6;
            }

            Uri selectedImageUri = data.getData();
            CropImage.activity(selectedImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setMinCropResultSize(600,960)
                    .setAspectRatio(5,8)
                    .setAutoZoomEnabled(true)
                    .start(activity);
        }


    }
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public void connEditProfile(Map field) {
        LoadingUtil.startLoading(indicator);
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.editProfile(user._id, field)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public final void onCompleted() {

                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                    }
                    @Override
                    public final void onNext(User response) {
                        if (response != null) {
                            SharedManager.getInstance().setMe(response);

                            Log.i("zxc","zxc1");
                            Log.i("zxc","zxc1" + response.pic_list.size());

                            for(int i=0;i<response.pic_list.size();i++) {
                                Log.i("zxc","zxc4");
                                if(imagepath.get(i) == null || imagepath.get(i).contains("http://"))
                                    continue;

                                Log.i("zxc","zxc3");
                                Log.i("makejin", "response.pic_list.get(" +i +") : " + response.pic_list.get(i));
                                if(imagepath.get(i).contains("facebook")) {
                                    continue;
                                }
                                pic_upload_count_2++;
                                uploadFile1(response, i);
                            }

                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public void uploadFile1(final User user, final int index) {
        String tempIndex = String.valueOf(index);

        File file;
        if(imagepath.get(index).contains("/makejin.langfriend/cache/cropped")){
            file = new File(getCacheDir(),imagepath.get(index).substring(imagepath.get(index).lastIndexOf("cache/")+6,imagepath.get(index).length()));
        }else{
            file = new File(imagepath.get(index));
        }


        RequestBody ubody = RequestBody.create(MediaType.parse("image/*"), saveBitmapToFile(file));

        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.fileUploadWrite_User(user._id, tempIndex, ubody)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public final void onCompleted() {
                        pic_upload_count++;
                        Log.i("finish", "finish : " + pic_upload_count + " " + imagepath.size() + " " + pic_upload_count_2);
                        if(pic_upload_count == pic_upload_count_2) {//마지막 업로드일때
                            Log.i("finish", "finish2 : " + pic_upload_count);
                            pic_upload_count = 0;
                            pic_upload_count_2 = 0;
                            LoadingUtil.stopLoading(indicator);
                            setResult(Constants.ACTIVITY_CODE_TAB5_PROFILE_DETAIL_REFRESH_RESULT);
                            edit = false;
                            finish();
                        }
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(User response) {
                        if (response != null) {
                            SharedManager.getInstance().setMe(response);
                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public File saveBitmapToFile(File file){
        try {

            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE=75;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 80 , outputStream);

            return file;
        } catch (Exception e) {
            return null;
        }
    }
}
