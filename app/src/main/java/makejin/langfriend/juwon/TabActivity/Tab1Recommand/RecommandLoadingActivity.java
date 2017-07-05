package makejin.langfriend.juwon.TabActivity.Tab1Recommand;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Timer;
import java.util.TimerTask;

import makejin.langfriend.R;
import makejin.langfriend.juwon.Sign.SignActivity;
import makejin.langfriend.juwon.Utils.Constants.Constants;

public class RecommandLoadingActivity extends AppCompatActivity {
    public static RecommandLoadingActivity recommandLoadingActivity;

    ImageView IV_logo;
    ImageView IV_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommand_loading);

        this.recommandLoadingActivity = this;

        IV_logo=(ImageView) findViewById(R.id.IV_logo);
        IV_user=(ImageView) findViewById(R.id.IV_user);

        //IV_logo.setBackground(new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.logo_splash)));
        //Glide.with(getApplicationContext()).load(R.drawable.logo_splash).into(IV_logo);


        final int[] slide_id = new int[8];
        int i=0;
        slide_id[i++] = R.anim.slide_in_2;
        slide_id[i++] = R.anim.slide_in_5;
        slide_id[i++] = R.anim.slide_in_8;
        slide_id[i++] = R.anim.slide_in_11;
        slide_id[i++] = R.anim.slide_in_up;
        slide_id[i++] = R.anim.slide_in_down;
        slide_id[i++] = R.anim.slide_in_left;
        slide_id[i++] = R.anim.slide_in_right;

        final String[] image_url = new String[4];
        image_url[0] = Constants.IMAGE_BASE_URL + "sample_user_1.jpg";
        image_url[1] = Constants.IMAGE_BASE_URL + "sample_user_2.jpg";
        image_url[2] = Constants.IMAGE_BASE_URL + "sample_user_3.jpg";
        image_url[3] = Constants.IMAGE_BASE_URL + "sample_user_4.jpg";

        Glide.with(getApplicationContext()).
                load(image_url[0]).
                //animate(R.anim.slide_in_2).
                        animate(slide_id[(int) (Math.random() * 8)]).
                into(IV_user);

        new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
            @Override
            public void run() {
                Glide.with(getApplicationContext()).
                        load(image_url[1]).
                        //animate(R.anim.slide_in_5).
                                animate(slide_id[(int) (Math.random() * 8)]).
                        into(IV_user);
            }
        }, 1000);

        new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
            @Override
            public void run() {
                Glide.with(getApplicationContext()).
                        load(image_url[2]).
                        //animate(R.anim.slide_in_8).
                                animate(slide_id[(int) (Math.random() * 8)]).
                        into(IV_user);
            }
        }, 2000);

        new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
            @Override
            public void run() {
                Glide.with(getApplicationContext()).
                        load(image_url[3]).
                        //animate(R.anim.slide_in_11).
                                animate(slide_id[(int) (Math.random() * 8)]).
                        into(IV_user);
            }
        }, 3000);



        Timer timer = new Timer();
        timer.schedule(this.spashScreenFinished, 4000);


    }

    private final TimerTask spashScreenFinished = new TimerTask() {
        @Override
        public void run() {

            finish();
        }
    };

}
