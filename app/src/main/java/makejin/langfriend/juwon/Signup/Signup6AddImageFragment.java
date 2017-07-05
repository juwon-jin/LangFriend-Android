package makejin.langfriend.juwon.Signup;

//
//import android.app.Fragment;
//import android.app.FragmentTransaction;
//import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.opengl.GLES20;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import makejin.langfriend.R;
import makejin.langfriend.juwon.Utils.Loadings.LoadingUtil;

import static android.app.Activity.RESULT_OK;
import static makejin.langfriend.juwon.Signup.SignupActivity.activity;

public class Signup6AddImageFragment extends Fragment {
    Signup6AddImageFragment fragment;

    TextView title;
    Button BT_yes;

    Toolbar cs_toolbar;

    ImageView [] IV_pic = new ImageView[6];

    RelativeLayout [] RL_RL_no = new RelativeLayout[6];

    public static List<String> imagepath = new ArrayList<>();

    public static String tempImagePath = null;

    int resizedWidth;
    int resizedHeight;

    public LinearLayout indicator1;
    public LinearLayout indicator2;
    public LinearLayout indicator3;
    public LinearLayout indicator4;
    public LinearLayout indicator5;
    public LinearLayout indicator6;

    public static int REQUEST_PIC_NUM = -1;
    public final int PIC_0 = 10; //don't know where pic
    public final int PIC_1 = 11;
    public final int PIC_2 = 12;
    public final int PIC_3 = 13;
    public final int PIC_4 = 14;
    public final int PIC_5 = 15;
    public final int PIC_6 = 16;


    //public boolean isNotFacebookProfile = true;

    public static Signup6AddImageFragment newInstance(int index) {
        Signup6AddImageFragment fragment = new Signup6AddImageFragment();
        Bundle b = new Bundle();
        b.putInt("index", index);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signup6_add_image, container, false);

        this.fragment = this;

        indicator1 = (LinearLayout) view.findViewById(R.id.indicator1);
        indicator2 = (LinearLayout) view.findViewById(R.id.indicator2);
        indicator3 = (LinearLayout) view.findViewById(R.id.indicator3);
        indicator4 = (LinearLayout) view.findViewById(R.id.indicator4);
        indicator5 = (LinearLayout) view.findViewById(R.id.indicator5);
        indicator6 = (LinearLayout) view.findViewById(R.id.indicator6);


        cs_toolbar = (Toolbar) view.findViewById(R.id.cs_toolbar);
        activity.setSupportActionBar(cs_toolbar);
        activity.getSupportActionBar().setTitle("회원가입");
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setHomeButtonEnabled(true);

        BT_yes = (Button) view.findViewById(R.id.BT_yes);
        title = (TextView) view.findViewById(R.id.title);

        IV_pic[0] = (ImageView) view.findViewById(R.id.IV_pic1);
        IV_pic[1] = (ImageView) view.findViewById(R.id.IV_pic2);
        IV_pic[2] = (ImageView) view.findViewById(R.id.IV_pic3);
        IV_pic[3] = (ImageView) view.findViewById(R.id.IV_pic4);
        IV_pic[4] = (ImageView) view.findViewById(R.id.IV_pic5);
        IV_pic[5] = (ImageView) view.findViewById(R.id.IV_pic6);

        RL_RL_no[0] = (RelativeLayout) view.findViewById(R.id.RL_RL_no_1);
        RL_RL_no[1] = (RelativeLayout) view.findViewById(R.id.RL_RL_no_2);
        RL_RL_no[2] = (RelativeLayout) view.findViewById(R.id.RL_RL_no_3);
        RL_RL_no[3] = (RelativeLayout) view.findViewById(R.id.RL_RL_no_4);
        RL_RL_no[4] = (RelativeLayout) view.findViewById(R.id.RL_RL_no_5);
        RL_RL_no[5] = (RelativeLayout) view.findViewById(R.id.RL_RL_no_6);

        Log.i("zxc", "activity.user.social_type : " + activity.user.social_type);
        Log.i("zxc", "activity.user.pic_list : " + activity.user.getPic(0));


        refresh();

        if(activity.user.social_type.equals("facebook")) {

            //String image_url = "http://graph.facebook.com/"+activity.user.social_id+"/picture?type=large";
            view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    RL_RL_no[0].setVisibility(View.VISIBLE);
                    IV_pic[0].getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    String image_url = "http://graph.facebook.com/"+activity.user.social_id+"/picture?width=" +IV_pic[0].getWidth() +"&height="+IV_pic[0].getHeight();
                    imagepath.add(image_url);
                    //String image_url = "http://graph.facebook.com/"+activity.user.social_id+"/picture?width=" +230 +"&height="+250;
                    Log.i("zxc", "image_url : " + image_url);
                    LoadingUtil.startLoading(indicator1);
                    Glide.with(activity).
                            load(image_url).
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


        }



        IV_pic[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                if(IV_pic[0].getDrawable() == null)
                    startActivityForResult(intent, 0);
                else
                    startActivityForResult(intent, 1);
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




        BT_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i("zxc", "user : " + activity.user.lang_type);

                SimpleDateFormat sdfNow = new SimpleDateFormat("yyMMddHHmmssSSS");
                String current_time = sdfNow.format(new Date(System.currentTimeMillis()));

                Log.i("zxc", "imagepath.size() : " + imagepath.size());
                for(int i=0;i<imagepath.size();i++) {
                    Log.i("zxc", "imagepath : "  + i + " " + imagepath.get(i));
                    if(imagepath.get(i)==null)
                        continue;
                    if(imagepath.get(i).contains("facebook")) {
                        activity.user.pic_list.add(imagepath.get(i));
                        activity.user.pic_small = imagepath.get(i);
                    }else {
                        activity.user.pic_list.add(activity.user.nickname + i + current_time);
                    }
                }

                Log.i("zxc", "size : " + activity.user.pic_list.size());
                if(activity.user.pic_list.size()==0){
                    Toast.makeText(activity, "사진이 하나 이상 필요해요!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i("zxc", "user : " + activity.user);
                Fragment fragment = new Signup7AboutMeFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.activity_signup, fragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();
            }
        });


        return view;

    }

    private void refresh(){
        int i;
        Log.i("asd", imagepath.toString());
        for(i=0;i<imagepath.size();i++){
            Glide.with(activity).
                    load(imagepath.get(i)).
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
                            imagepath.add(tempImagePath);
                        }else{
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
                        imagepath.set(1, tempImagePath);
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
            }else if(requestCode == 0) {
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
                    .start(getContext(), fragment);
        }
    }



    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

}
