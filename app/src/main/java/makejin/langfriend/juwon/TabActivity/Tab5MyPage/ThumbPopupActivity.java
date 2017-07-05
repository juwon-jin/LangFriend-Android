package makejin.langfriend.juwon.TabActivity.Tab5MyPage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import makejin.langfriend.juwon.Model.User;
import makejin.langfriend.R;
import makejin.langfriend.juwon.Utils.Connections.CSConnection;
import makejin.langfriend.juwon.Utils.Connections.ServiceGenerator;
import makejin.langfriend.juwon.Utils.Constants.Constants;
import makejin.langfriend.juwon.Utils.Loadings.LoadingUtil;
import makejin.langfriend.juwon.Utils.SharedManager.SharedManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static makejin.langfriend.juwon.TabActivity.Tab5MyPage.Tab5MyPageFragment.IV_profile;
import static makejin.langfriend.juwon.TabActivity.Tab5MyPage.Tab5MyPageFragment.indicator;


public class ThumbPopupActivity extends Activity {

    Button BT_take_a_picture;
    Button BT_choose_a_picture;
    Button BT_take_from_facebook;
    Button BT_close_up_a_picture;
    Button BT_remove_a_picture;
    Button BT_close;
    private String imagepath=null;

    SharedPreferences prefs;

    private int TAKE_CAMERA = 1;					// 카메라 리턴 코드값 설정
    private int TAKE_GALLERY = 2;				// 앨범선택에 대한 리턴 코드값 설정
    String image_url;

    LinearLayout LL_popup_thumb;
    RelativeLayout RR_take_from_facebook;
    View V_take_from_facebook_line;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thumb_popup);
        prefs = getSharedPreferences("LangFriend",0);

        //BT_take_a_picture = (Button) findViewById(R.id.BT_take_a_picture);
        BT_choose_a_picture = (Button) findViewById(R.id.BT_choose_a_picture);

        BT_close_up_a_picture = (Button) findViewById(R.id.BT_close_up_a_picture);
        BT_remove_a_picture = (Button) findViewById(R.id.BT_remove_a_picture);
        BT_close = (Button) findViewById(R.id.BT_close);

        LL_popup_thumb = (LinearLayout) findViewById(R.id.LL_popup_thumb);
        RR_take_from_facebook = (RelativeLayout) findViewById(R.id.RR_take_from_facebook);
        V_take_from_facebook_line = (View) findViewById(R.id.V_take_from_facebook_line);

        LL_popup_thumb.removeView(BT_close_up_a_picture);
        LL_popup_thumb.removeView(BT_remove_a_picture);


//        BT_take_a_picture.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent, TAKE_CAMERA);
//            }
//        });
        BT_choose_a_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, TAKE_GALLERY);
            }
        });

        BT_close_up_a_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish();
            }
        });
        BT_remove_a_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish();
            }
        });
        BT_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(SharedManager.getInstance().getMe().social_type.equals("facebook")){
            //BT_take_from_facebook.setVisibility(View.GONE);
            BT_take_from_facebook = (Button) findViewById(R.id.BT_take_from_facebook);
            BT_take_from_facebook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map field = new HashMap();
                    field.put("thumbnail_url",
                            "http://graph.facebook.com/"+SharedManager.getInstance().getMe().social_id + "/picture?type=normal");
                    field.put("thumbnail_url_small",
                            "http://graph.facebook.com/"+SharedManager.getInstance().getMe().social_id + "/picture?type=small");

                    connectUpdateUserImage_Facebook(SharedManager.getInstance().getMe()._id, field);
                }
            });
        }else{
            LL_popup_thumb.removeView(RR_take_from_facebook);
            LL_popup_thumb.removeView(V_take_from_facebook_line);

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == TAKE_CAMERA){
                Bitmap bm = (Bitmap) data.getExtras().get("data");
                imagepath = getPath(getImageUri(getApplicationContext(), bm));

                uploadFile1(SharedManager.getInstance().getMe(),0);
                finish();
            }else if(requestCode == TAKE_GALLERY){
                    Uri selectedImageUri = data.getData();
                imagepath = getPath(selectedImageUri);

                uploadFile1(SharedManager.getInstance().getMe(),0);
                finish();
            }

        }

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    private void uploadFile1(final User user, int index) {
        LoadingUtil.startLoading(Tab5MyPageFragment.indicator);
        String tempIndex = String.valueOf(index);
        File file = new File(imagepath);
        Log.i("asd", "imagepath : " + imagepath);

        Bitmap bmp = BitmapFactory.decodeFile(imagepath);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 70, bos);
        InputStream in = new ByteArrayInputStream(bos.toByteArray());

        try {
            // 출력할 파일명과 읽어들일 파일명을지정한다.
            OutputStream outStream = new FileOutputStream(file);
            // 읽어들일 버퍼크기를 메모리에 생성
            byte[] buf = new byte[1024];
            int len = 0;
            // 끝까지 읽어들이면서 File 객체에 내용들을 쓴다
            while ((len = in.read(buf)) > 0){
                outStream.write(buf, 0, len);
            }
            // Stream 객체를 모두 닫는다.
            outStream.close();
            in.close();
            Log.i("zxc","zxc");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        RequestBody ubody = RequestBody.create(MediaType.parse("image/*"), file);


        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.fileUploadWrite_User(user._id, tempIndex, ubody)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public final void onCompleted() {
                        LoadingUtil.stopLoading(Tab5MyPageFragment.indicator);
                        Log.i("zxc", "response.pic_small2 : " + image_url);
                        if(image_url.contains("facebook")){
                            indicator.setVisibility(View.VISIBLE);
                            Glide.with(Tab5MyPageFragment.activity).
                                    load(image_url).
                                    listener(new RequestListener<String, GlideDrawable>() {
                                        @Override
                                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                            indicator.setVisibility(View.GONE);
                                            return false;
                                        }
                                    }).
                                    thumbnail(0.1f).
                                    bitmapTransform(new CropCircleTransformation(Tab5MyPageFragment.activity)).into(IV_profile);
                            Log.i("zxc", "image_url : " + image_url);
                        }else{
                            indicator.setVisibility(View.VISIBLE);
                            Glide.with(Tab5MyPageFragment.activity).
                                    load(Constants.IMAGE_BASE_URL + image_url).
                                    listener(new RequestListener<String, GlideDrawable>() {
                                        @Override
                                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                            indicator.setVisibility(View.GONE);
                                            return false;
                                        }
                                    }).
                                    thumbnail(0.1f).
                                    bitmapTransform(new CropCircleTransformation(Tab5MyPageFragment.activity)).into(IV_profile);
                            Log.i("zxc", "Constants.IMAGE_BASE_URL + image_url : " + Constants.IMAGE_BASE_URL + image_url);
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
                            Log.i("zxc", "response.pic_small : " + response.pic_small);
                            SharedManager.getInstance().getMe().pic_small = response.pic_small;
                            image_url = response.pic_small;

                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    void connectUpdateUserImage_Facebook(final String user_id, final Map field) {
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.updateUserImage_Facebook(user_id, field)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public final void onCompleted() {
                        IV_profile.invalidate();
                        finish();
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(User response) {
                        if (response != null) {
                            Log.i("makejin", "response "+response);
                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
