package makejin.langfriend.juwon.Activity;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.opengl.GLES20;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import makejin.langfriend.R;
import makejin.langfriend.juwon.Model.Posting;
import makejin.langfriend.juwon.Utils.Connections.CSConnection;
import makejin.langfriend.juwon.Utils.Connections.ServiceGenerator;
import makejin.langfriend.juwon.Utils.Constants.Constants;
import makejin.langfriend.juwon.Utils.Loadings.LoadingUtil;
import makejin.langfriend.juwon.Utils.SharedManager.SharedManager;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@EActivity(R.layout.activity_register)
public class RegisterActivity extends AppCompatActivity {
    RegisterActivity activity;

    List<String> location_list = new ArrayList<>();
    List<String> language_list = new ArrayList<>();
    List<String> activity_list = new ArrayList<>();

    List<String> category_list_location = new ArrayList<String>();
    List<String> category_list_language = new ArrayList<String>();
    List<String> category_list_activity = new ArrayList<String>();


    Posting n_posting = new Posting();
    Float rate_num = 10.0f;

    private String imagepath=null;
    Boolean btn_push = false;
    @ViewById
    public LinearLayout indicator;
    @ViewById
    Toolbar cs_toolbar;
    @ViewById
    EditText edit_name;
    @ViewById
    ImageView posting_image;
    @ViewById
    Spinner location_spinner;
    @ViewById
    Spinner language_spinner;
    @ViewById
    Spinner activity_spinner;
    @ViewById
    EditText edit_posting;

    @ViewById
    TagFlowLayout category_result_location;
    @ViewById
    TagFlowLayout category_result_language;
    @ViewById
    TagFlowLayout category_result_activity;

    int resizedWidth;
    int resizedHeight;

    @Click
    void posting_image() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @AfterViews
    void afterBindingView() {
        this.activity = this;

        setSupportActionBar(cs_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("새 글 등록");

        //TODO: 카테고리 서버 연동 테스트
        //only android code test
        location_list.add("[지역]");
        language_list.add("[언어]");
        activity_list.add("[활동]");

        for (String location : SharedManager.getInstance().getCategory().location) {
            location_list.add(location);
        }
        for (String language : SharedManager.getInstance().getCategory().language) {
            language_list.add(language);
        }
        for (String activity : SharedManager.getInstance().getCategory().activity) {
            activity_list.add(activity);
        }
        initSpinner(location_spinner,location_list,1);
        initSpinner(language_spinner,language_list,2);
        initSpinner(activity_spinner,activity_list,3);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        if (item.getItemId() == R.id.finish) {
            //현재 순서 : 빈칸 체크 -> 이미지 업로드 -> 음식 등록 -> 별점 평가
            if(!btn_push) {
                btn_push = true;
                check_blank();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == 1){
                Uri selectedImageUri = data.getData();
                imagepath = getPath(selectedImageUri);
                Log.e("imagepath : ", imagepath);
                Log.e("upload message : ", "Uploading file path:" + imagepath);
                SimpleDateFormat sdfNow = new SimpleDateFormat("yyMMddHHmmssSSS");
                String current_time = sdfNow.format(new Date(System.currentTimeMillis()));
                n_posting.image_url = SharedManager.getInstance().getMe()._id+current_time;

                int[] maxTextureSize = new int[1];
                GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0);

                final int[] tempMaxTextureSize = maxTextureSize;

                Glide.with(activity).load(imagepath).asBitmap().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        if (resource.getHeight() > tempMaxTextureSize[0]){
                            Log.i("zxc", "w : " + posting_image.getWidth() + " h : " + posting_image.getHeight());
                            resizedWidth = posting_image.getWidth();
                            resizedHeight = posting_image.getHeight();
                            posting_image.setImageBitmap(resource.createScaledBitmap(resource, resizedWidth, resizedHeight, false));
                        }
                    }
                });
            }
        }
    }
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void uploadFile1(final Posting posting) {
        File file = new File(imagepath);
        RequestBody fbody = RequestBody.create(MediaType.parse("image/*"), saveBitmapToFile(file));
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.fileUploadWrite(posting._id, fbody)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Posting>() {
                    @Override
                    public final void onCompleted() {
                        LoadingUtil.stopLoading(indicator);
                        Toast.makeText(getApplicationContext(), "새 글 업로드에 성공했습니다!", Toast.LENGTH_SHORT).show();
                        setResult(Constants.ACTIVITY_CODE_TAB2_REFRESH_RESULT);
                        finish();
                    }
                    @Override
                    public final void onError(Throwable e) {
                        Log.i("zxc", "uploadfile err2");
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(Posting response) {
                        if (response != null) {
                            Log.i("zxc", "uploadfile err3");
                        } else {
                            Log.i("zxc", "uploadfile err4");
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
            //final int REQUIRED_SIZE=75;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= resizedWidth &&
                    o.outHeight / scale / 2 >= resizedHeight) {
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

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);

            return file;
        } catch (Exception e) {
            return null;
        }
    }

    void RegisterPosting() {
        n_posting.author.author_id = SharedManager.getInstance().getMe()._id;
        n_posting.author.author_nickname = SharedManager.getInstance().getMe().nickname;
        n_posting.author.author_pic_small = SharedManager.getInstance().getMe().pic_small;
        n_posting.posting = edit_posting.getText().toString();
        n_posting.author.author_location_point = SharedManager.getInstance().getMe().location_point;

        final CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        LoadingUtil.startLoading(indicator);
        conn.postingPost(n_posting)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Posting>() {
                    @Override
                    public final void onCompleted() {

                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(Posting response) {
                        if (response != null) {
                            uploadFile1(response);
                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void check_blank(){
        //TODO:서버 이미지 처리 : 현재 rate,new_posting등록 후 이미지 업로드 하는데 업로드할때 posting_id보내므로 해당 posting_id활용해서 서버단에서 uri 만든 후 update할 것
        n_posting.name = edit_name.getText().toString();
        if(n_posting.name==null)
            Snackbar.make(edit_posting, "제목을 작성해주세요.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        else if(imagepath==null)
            Snackbar.make(edit_posting, "사진을 등록해주세요.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        else if(n_posting.location.size()==0||n_posting.language.size()==0||n_posting.activity.size()==0)
            Snackbar.make(edit_posting, "지역/언어/활동 각각 하나 이상 선택해주세요.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        else
            RegisterPosting();
    }

    private void initSpinner(final Spinner s, List<String> array, final int type){
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(),R.layout.spin,array);

        s.setAdapter(adapter);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (!s.getSelectedItem().toString().equals("[지역]")
                        && !s.getSelectedItem().toString().equals("[언어]")
                        && !s.getSelectedItem().toString().equals("[활동]")) {
                    switch (type) {
                        case 1:
                            n_posting.location.add(s.getSelectedItem().toString());
                            category_list_location.add(s.getSelectedItem().toString());
                            addFlowChart(category_result_location, category_list_location.toArray(new String[category_list_location.size()]));
                            break;
                        case 2:
                            n_posting.language.add(s.getSelectedItem().toString());
                            category_list_language.add(s.getSelectedItem().toString());
                            addFlowChart(category_result_language, category_list_language.toArray(new String[category_list_language.size()]));
                            break;
                        case 3:
                            n_posting.activity.add(s.getSelectedItem().toString());
                            category_list_activity.add(s.getSelectedItem().toString());
                            addFlowChart(category_result_activity, category_list_activity.toArray(new String[category_list_activity.size()]));
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


    }

    private void addFlowChart(final TagFlowLayout mFlowLayout, String[] array) {
        final LayoutInflater mInflater = LayoutInflater.from(getApplication());

        mFlowLayout.setAdapter(new TagAdapter<String>(array){
            @Override
            public View getView(final FlowLayout parent, final int position, String s) {
                final TextView tv = (TextView) mInflater.inflate(R.layout.tag_result, mFlowLayout, false);
                tv.setText(s + " X");

                int type = parent.getId();
                switch(type){
                    case R.id.category_result_location:
                        tv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                category_list_location.remove(position);
                                mFlowLayout.removeViewAt(position);
                                n_posting.location.remove(position);
                                addFlowChart(category_result_location, category_list_location.toArray(new String[category_list_location.size()]));
                            }
                        });
                        break;
                    case R.id.category_result_language:
                        tv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                category_list_language.remove(position);
                                mFlowLayout.removeViewAt(position);
                                n_posting.language.remove(position);
                                addFlowChart(category_result_language, category_list_language.toArray(new String[category_list_language.size()]));
                            }
                        });
                        break;
                    case R.id.category_result_activity:
                        tv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                category_list_activity.remove(position);
                                mFlowLayout.removeViewAt(position);
                                n_posting.activity.remove(position);
                                addFlowChart(category_result_activity, category_list_activity.toArray(new String[category_list_activity.size()]));
                            }
                        });
                        break;
                }

                return tv;
            }

            @Override
            public boolean setSelected(int position, String s) {
                return s.equals("Android");
            }
        });

    }

}