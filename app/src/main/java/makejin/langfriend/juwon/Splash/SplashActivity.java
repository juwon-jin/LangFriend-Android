package makejin.langfriend.juwon.Splash;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import makejin.langfriend.R;
import makejin.langfriend.juwon.Model.User;
import makejin.langfriend.juwon.Sign.SignActivity;
import makejin.langfriend.juwon.TabActivity.TabActivity;
import makejin.langfriend.juwon.TabActivity.TabActivity_;
import makejin.langfriend.juwon.Utils.Connections.CSConnection;
import makejin.langfriend.juwon.Utils.Connections.ServiceGenerator;
import makejin.langfriend.juwon.Utils.Constants.Constants;
import makejin.langfriend.juwon.Utils.Loadings.LoadingUtil;
import makejin.langfriend.juwon.Utils.Session.AlertDialogManager;
import makejin.langfriend.juwon.Utils.Session.SessionManager;
import makejin.langfriend.juwon.Utils.SharedManager.PreferenceManager;
import makejin.langfriend.juwon.Utils.SharedManager.SharedManager;
import makejin.langfriend.juwon.Utils.VersionUpdate.MarketVersionChecker;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.facebook.FacebookSdk.getApplicationContext;
import static makejin.langfriend.juwon.TabActivity.Tab1Recommand.Tab1RecommandFragment.isConnRecommand;

@EActivity(R.layout.activity_splash_test2)
public class SplashActivity extends AppCompatActivity {
    public SplashActivity activity;

    private LocationManager locationManager = null; // 위치 정보 프로바이더
    private LocationListener locationListener = null; //위치 정보가 업데이트시 동작

    private static final String TAG = "debug";
    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    public static double lat = 0.0;
    public static double lon = 0.0;

    public static String  cityName;

    public String deviceVersion;
    public String storeVersion;

    private BackgroundThread mBackgroundThread;

    ImageView IV_logo, IV_user;

    SharedPreferences prefs;

    public boolean isFirst = false;

    @AfterViews
    void afterBindingView() {
        this.activity = this;

        uiThread4(); //pref 체크

    }

    public class BackgroundThread extends Thread {
        @Override
        public void run() {

            // 패키지 네임 전달
            Log.i("zxc", "storeVersion : " + storeVersion);
            storeVersion = MarketVersionChecker.getMarketVersionFast(getPackageName());

            // 디바이스 버전 가져옴
            try {
                deviceVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            deviceVersionCheckHandler.sendMessage(deviceVersionCheckHandler.obtainMessage());
            // 핸들러로 메세지 전달
        }
    }

    private final DeviceVersionCheckHandler deviceVersionCheckHandler = new DeviceVersionCheckHandler(this);

    // 핸들러 객체 만들기
    private class DeviceVersionCheckHandler extends Handler {
        private final WeakReference<SplashActivity> mainActivityWeakReference;
        public DeviceVersionCheckHandler(SplashActivity mainActivity) {
            mainActivityWeakReference = new WeakReference<SplashActivity>(mainActivity);
        }
        @Override
        public void handleMessage(Message msg) {
            SplashActivity activity = mainActivityWeakReference.get();
            if (activity != null) {
                locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                //GPS_PROVIDER: GPS를 통해 위치를 알려줌
                isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                //NETWORK_PROVIDER: WI-FI 네트워크나 통신사의 기지국 정보를 통해 위치를 알려줌
                isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if(isGPSEnabled && isNetworkEnabled){
                    //Toast.makeText(getApplicationContext(), "내 위치정보를 가져오는 중입니다.", Toast.LENGTH_SHORT).show();
                    isFirst = true;
                    locationListener = new MyLocationListener();

                    //선택된 프로바이더를 사용해 위치정보를 업데이트
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 600000, 500, locationListener); //아마 5000 = 5초, 10 = 10m?
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 600000, 500, locationListener); //600000 = 600초 = 10분, 5000 = 5000m = 5km
//                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, locationListener); //아마 5000 = 5초, 10 = 10m?
//                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 10, locationListener); //600000 = 600초 = 10분, 5000 = 5000m = 5km

                }else{
                    alertbox("gps 상태!!", "당신의 gps 상태 : off");
                }


                if (storeVersion.compareTo(deviceVersion) > 0) {
                    // 업데이트 필요

                    AlertDialog.Builder alertDialogBuilder =
                            new AlertDialog.Builder(new ContextThemeWrapper(activity, android.R.style.Theme_DeviceDefault_Light));
                    alertDialogBuilder.setTitle("업데이트");
                    alertDialogBuilder
                            .setMessage("새로운 버전이 있습니다.\n보다 나은 사용을 위해 업데이트 해 주세요.")
                            .setPositiveButton("업데이트 바로가기", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 구글플레이 업데이트 링크

                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.setCanceledOnTouchOutside(true);
                    alertDialog.show();

                } else {
                    // 업데이트 불필요
                    Toast.makeText(activity, "업데이트 필요없음", Toast.LENGTH_SHORT).show();
                }
                //uiThread2();




                //uiThread3();

            }
        }
    }


    @UiThread
    void uiThread5() {
        IV_logo=(ImageView) findViewById(R.id.IV_logo);
        Glide.with(activity).load(R.drawable.logo_splash).into(IV_logo);
        //IV_logo.setBackground(new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.logo_splash_2)));

//        Timer timer = new Timer();
//        timer.schedule(this.spashScreenFinished2, 3000);
    }

    private final TimerTask spashScreenFinished2 = new TimerTask() {
        @Override
        public void run() {
            Intent splash = new Intent(activity, SignActivity.class);
            splash.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(splash);
            finish();
        }
    };


    @UiThread
    void uiThread4() {
        String temp_id = PreferenceManager.getInstance(getApplicationContext()).get_id();
        Long temp_time = PreferenceManager.getInstance(getApplicationContext()).getExpireDate();


        Log.i("zxc", "temp_id : " + temp_id);
        if(temp_id.equals("")){
            mBackgroundThread = new BackgroundThread();
            mBackgroundThread.start();

            //uiThread5(); //splash
            //IV_logo=(ImageView) findViewById(R.id.IV_logo);
            //Glide.with(activity).load(R.drawable.logo_splash_2).into(IV_logo);
            //IV_logo.setBackground(new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.logo_splash_2)));

        }else{
            Long current_time = System.currentTimeMillis();
            if(temp_time > current_time){
                Map field = new HashMap();
                field.put("_id", temp_id);
                connectAccessUser_all(field);
            }else{ // 기간 만료. 재발급 및 재access
                PreferenceManager.getInstance(getApplicationContext()).setExpireDate(current_time+86400);
                mBackgroundThread = new BackgroundThread();
                mBackgroundThread.start();
            }

        }
    }

    /*
    @UiThread
    void uiThread3() {
        IV_logo=(ImageView) findViewById(R.id.IV_logo);
        IV_user=(ImageView) findViewById(R.id.IV_user);

        IV_logo.setBackground(new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.logo_splash_2)));



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
        }, 3000);

        new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
            @Override
            public void run() {
                Glide.with(getApplicationContext()).
                        load(image_url[2]).
                        //animate(R.anim.slide_in_8).
                                animate(slide_id[(int) (Math.random() * 8)]).
                        into(IV_user);
            }
        }, 6000);

        new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
            @Override
            public void run() {
                Glide.with(getApplicationContext()).
                        load(image_url[3]).
                        //animate(R.anim.slide_in_11).
                                animate(slide_id[(int) (Math.random() * 8)]).
                        into(IV_user);
            }
        }, 9000);

//        Timer timer = new Timer();
//        timer.schedule(this.spashScreenFinished, 12000);

//
//        Timer timer = new Timer();
//        timer.schedule(this.spashScreenFinished, 0);


    }

    private final TimerTask spashScreenFinished = new TimerTask() {
        @Override
        public void run() {
            ActivityManager am = (ActivityManager)getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
            Log.i("zxc", "cn : " + cn.getClassName());
            Log.i("zxc", "cn : " + cn.getPackageName());
            Log.i("zxc", "cn : " + cn.getShortClassName());


            Intent splash = new Intent(activity, SignActivity.class);
            splash.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(splash);
            finish();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        recycleView(findViewById(R.id.layout));
    }

    private void recycleView(View view) {
        if(view != null) {
            Drawable bg = view.getBackground();
            if(bg != null) {
                bg.setCallback(null);
                ((BitmapDrawable)bg).getBitmap().recycle();
                view.setBackground(null);
            }
        }
    }
*/
    @UiThread
    void uiThread2() {
        //핸들러에서 넘어온 값 체크
        if (storeVersion.compareTo(deviceVersion) > 0) {
            // 업데이트 필요

            AlertDialog.Builder alertDialogBuilder =
                    new AlertDialog.Builder(new ContextThemeWrapper(activity, android.R.style.Theme_DeviceDefault_Light));
            alertDialogBuilder.setTitle("업데이트");
            alertDialogBuilder
                    .setMessage("새로운 버전이 있습니다.\n보다 나은 사용을 위해 업데이트 해 주세요.")
                    .setPositiveButton("업데이트 바로가기", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 구글플레이 업데이트 링크

                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.setCanceledOnTouchOutside(true);
            alertDialog.show();

        } else {
            // 업데이트 불필요
            Toast.makeText(activity, "업데이트 필요없음", Toast.LENGTH_SHORT).show();
        }
    }

    @UiThread
    void uiThread() {
        Intent intent = new Intent(activity, TabActivity_.class);
        startActivity(intent);
        finish();
    }

    @Background
    void connectSigninUser_NonFacebook(final Map field) {
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.signinUser_NonFacebook(field)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public final void onCompleted() {
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(User response) {
                        if (response != null) {
                            if(!response.auth) {
                                Toast.makeText(getApplicationContext(), "등록하신 이메일로 가서, 본인 인증을 먼저 완료해주세요!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            SharedManager.getInstance().setMe(response);

                            uiThread();
                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Background
    void connectSigninUser(final Map field) {
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.signinUser(field)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public final void onCompleted() {
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(User response) {
                        if (response != null) {
                            if(!response.auth) {
                                Toast.makeText(getApplicationContext(), "등록하신 이메일로 가서, 본인 인증을 먼저 완료해주세요!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            SharedManager.getInstance().setMe(response);

                            uiThread();
                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    //gps 관련
    //현재 위치 정보를 받기위해 선택한 프로바이더에 위치 업데이터 요청! requestLocationUpdates()메소드를 사용함.
    private class MyLocationListener implements LocationListener {

        @Override
        //LocationListener을 이용해서 위치정보가 업데이트 되었을때 동작 구현
        public void onLocationChanged(Location loc) {
            lat = loc.getLatitude();
            lon = loc.getLongitude();
            //좌표 정보 얻어 토스트메세지 출력
            //Toast.makeText(getBaseContext(), "Location changed : Lat" + lat +  "Lng: " + lon, Toast.LENGTH_SHORT).show();

            // 도시명 구하기
            cityName = null;
            //int cnt = 0;
//            while(cityName==null) {
//                if(cnt!=0){
//                    String s = "현재 위치 탐색에 "+cnt+"번 실패했습니다. 잠시만 더 기다려주세요!";
//                    Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
//                }
//                cnt++;
                Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
                List<Address> addresses;
                try {
                    addresses = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                    if (addresses.size() > 0)
                        System.out.println(addresses.get(0).getLocality());
                    cityName = addresses.get(0).getLocality();
                } catch (IOException e) {
                    e.printStackTrace();
                }
//            }
            String s = "현재, <" + "lat : " + lat + " lon : "+ lon + ">에 계시군요 !";
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

            if(isFirst || SharedManager.getInstance().getMe() == null){ //
                isFirst = false;
                Intent splash = new Intent(activity, SignActivity.class);
                splash.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(splash);
                finish();
            }else{
                Map field = new HashMap();
                field.put("_id", SharedManager.getInstance().getMe()._id);
                field.put("location", cityName);
                User.LocationPoint location_point = new User.LocationPoint(lat, lon);
                field.put("location_point", location_point);
                connUpdateLocation(field);
            }



            //locationManager.removeUpdates(locationListener);
            return;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generatedchmethod stub

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }

    }


    protected void alertbox(String title, String mymessage){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("your device's gps is disable")
                .setCancelable(false)
                .setTitle("**gps status**")
                .setPositiveButton("gps on", new DialogInterface.OnClickListener() {

                    //  폰 위치 설정 페이지로 넘어감
                    public void onClick(DialogInterface dialog, int id) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                        dialog.cancel();
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    @Background
    void connectAccessUser_all(final Map field) {
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.accessUser_all(field)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public final void onCompleted() {
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(User response) {
                        if (response != null) {
                            Log.i("zxc", "response._id : " + response._id);
                            SharedManager.getInstance().setMe(response);
                            uiThread();
                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Background
    void connUpdateLocation(final Map field) {
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.updateLocation(field)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public final void onCompleted() {
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(User response) {
                        if (response != null) {
                            Log.i("zxc", "response._id : " + response._id);
                            SharedManager.getInstance().setMe(response);
                            isConnRecommand = false;

                            //uiThread();
                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
