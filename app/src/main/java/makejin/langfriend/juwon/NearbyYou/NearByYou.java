package makejin.langfriend.juwon.NearbyYou;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import makejin.langfriend.juwon.Model.User;
import makejin.langfriend.juwon.NearbyYou.search.Item;
import makejin.langfriend.juwon.NearbyYou.search.OnFinishSearchListener;
import makejin.langfriend.juwon.NearbyYou.search.Searcher;
import makejin.langfriend.R;
import makejin.langfriend.juwon.Splash.SplashActivity;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;



public class NearByYou extends AppCompatActivity implements MapView.CurrentLocationEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener, MapView.MapViewEventListener, MapView.POIItemEventListener {

    private static final String LOG_TAG = "MapActivity";
    public final String DAUM_MAPS_ANDROID_APP_API_KEY = "42a8e37881acc7053da1b8b54d14da57";
    private MapView mMapView;
    private EditText mEditTextQuery;
    private Button mButtonSearch;
    private HashMap<Integer, Item> mTagItemMap = new HashMap<Integer, Item>();

    private ProgressBar pb_1 = null;

    private static final String TAG = "debug";

    String keyword = null;
    User.LocationPoint location_point;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    Button BT_X;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_near_by_you);
        Toolbar cs_toolbar = (Toolbar)findViewById(R.id.cs_toolbar);
        setSupportActionBar(cs_toolbar);
        getSupportActionBar().setTitle("나와 너의 거리");

        location_point = (User.LocationPoint) getIntent().getSerializableExtra("location_point");


        mMapView = (MapView) findViewById(R.id.map_view);
        mMapView.setDaumMapApiKey(DAUM_MAPS_ANDROID_APP_API_KEY);
        mMapView.setMapViewEventListener(this);
        mMapView.setPOIItemEventListener(this);
        mMapView.setCalloutBalloonAdapter(new CustomCalloutBalloonAdapter());
        mMapView.setCurrentLocationEventListener(this);

        mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);

        mEditTextQuery = (EditText) findViewById(R.id.ET_searchbar); // 검색창
        mEditTextQuery.setText(keyword);

        mEditTextQuery.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if(keyCode ==  KeyEvent.KEYCODE_ENTER && KeyEvent.ACTION_DOWN == event.getAction())
                {

                    mButtonSearch.callOnClick();
                    return true;
                }
                // TODO Auto-generated method stub
                return false;
            }
        });

        mButtonSearch = (Button) findViewById(R.id.BT_search); // 검색버튼

        BT_X = (Button) findViewById(R.id.BT_X);
        BT_X.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mButtonSearch.setOnClickListener(new View.OnClickListener() { // 검색버튼 클릭 이벤트 리스너
            @Override
            public void onClick(View v) {

                String query = mEditTextQuery.getText().toString();
                if (query == null || query.length() == 0) {
                    showToast("검색어를 입력하세요.");
                    return;
                }
                hideSoftKeyboard(); // 키보드 숨김
                MapPoint.GeoCoordinate geoCoordinate = mMapView.getMapCenterPoint().getMapPointGeoCoord();
                double latitude = geoCoordinate.latitude; // 위도
                double longitude = geoCoordinate.longitude; // 경도
                int radius = 10000; // 중심 좌표부터의 반경거리. 특정 지역을 중심으로 검색하려고 할 경우 사용. meter 단위 (0 ~ 10000)
                int page = 1; // 페이지 번호 (1 ~ 3). 한페이지에 15개
                String apikey = DAUM_MAPS_ANDROID_APP_API_KEY;

                Searcher searcher = new Searcher(); // net.daum.android.map.openapi.search.Searcher
                searcher.searchKeyword(getApplicationContext(), query, latitude, longitude, radius, page, apikey, new OnFinishSearchListener() {
                    @Override
                    public void onSuccess(List<Item> itemList) {
                        if(itemList.size()==0) {
                            showToast("결과가 없습니다.");
                            return;
                        }
                        mMapView.removeAllPOIItems(); // 기존 검색 결과 삭제
                        showResult(itemList);
                    }

                    @Override
                    public void onFail() {
                        showResult();
                    }
                });
            }
        });

        //mButtonSearch.callOnClick();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    protected void alertbox(String title, String mymessage) {
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

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Map Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    //현재 위치 정보를 받기위해 선택한 프로바이더에 위치 업데이터 요청! requestLocationUpdates()메소드를 사용함.
    private class MyLocationListener implements LocationListener {

        @Override
        //LocationListener을 이용해서 위치정보가 업데이트 되었을때 동작 구현
        public void onLocationChanged(Location loc) {

            pb_1.setVisibility(View.INVISIBLE);
            //좌표 정보 얻어 토스트메세지 출력
            Toast.makeText(getBaseContext(), "Location changed : Lat" + loc.getLatitude() +
                    "Lng: " + loc.getLongitude(), Toast.LENGTH_SHORT).show();


            /*
            RealSplash.lat = loc.getLatitude();
            RealSplash.lon = loc.getLongitude();
*/

            mMapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(SplashActivity.lat, SplashActivity.lon), 2, true);
            //mMapView.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(MapPoint.mapPointWithGeoCoord(lat, lon), 2)));
            //뷰에 출력하기 위해 스트링으로 저장
            String longitude = "Longitude: " + loc.getLongitude();
            Log.d(TAG, longitude);
            String latitude = "Latitude: " + loc.getLatitude();
            Log.d(TAG, latitude);

            // 도시명 구하기
            String cityName = null;
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
            String s = longitude + "n" + latitude + "nn당신의 현재 도시명 : " + cityName;
            Toast.makeText(getBaseContext(), "Location changed : Lat" + loc.getLatitude() +
                    "Lng: " + loc.getLongitude(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

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

    public void onStop() {
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        //locationManager.removeUpdates(locationListener);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        mMapView.setShowCurrentLocationMarker(false);

    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint currentLocation, float accuracyInMeters) {
        MapPoint.GeoCoordinate mapPointGeo = currentLocation.getMapPointGeoCoord();
        Log.i(LOG_TAG, String.format("MapView onCurrentLocationUpdate (%f,%f) accuracy (%f)", mapPointGeo.latitude, mapPointGeo.longitude, accuracyInMeters));
    }


    class CustomCalloutBalloonAdapter implements CalloutBalloonAdapter {

        private final View mCalloutBalloon;

        public CustomCalloutBalloonAdapter() {
            mCalloutBalloon = getLayoutInflater().inflate(R.layout.custom_callout_balloon, null);
        }

        @Override
        public View getCalloutBalloon(MapPOIItem poiItem) {
            if (poiItem == null) return null;
            Item item = mTagItemMap.get(poiItem.getTag());
            if (item == null) return null;
            ImageView imageViewBadge = (ImageView) mCalloutBalloon.findViewById(R.id.badge);
            TextView textViewTitle = (TextView) mCalloutBalloon.findViewById(R.id.title);
            textViewTitle.setText(item.title);
            TextView textViewDesc = (TextView) mCalloutBalloon.findViewById(R.id.desc);
            textViewDesc.setText(item.address);
            imageViewBadge.setImageDrawable(createDrawableFromUrl(item.imageUrl));
            return mCalloutBalloon;
        }

        @Override
        public View getPressedCalloutBalloon(MapPOIItem poiItem) {
            return null;
        }

    }

    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditTextQuery.getWindowToken(), 0);
    }

    public void onMapViewInitialized(MapView mapView) {
        Log.i(LOG_TAG, "MapView had loaded. Now, MapView APIs could be called safely");

        double lat = SplashActivity.lat;
        double lon = SplashActivity.lon;

        //getLastKnownLocation()
        //mMapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(37.537229,127.005515), 2, true);
        mMapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(lat, lon), 2, true);
        //mMapView.setMapCenterPointAndZoomLevel(, 2, true);'
        //mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);

        Searcher searcher = new Searcher();
        String query = mEditTextQuery.getText().toString();
        double latitude = lat;
        double longitude = lon;
        int radius = 10000; // 중심 좌표부터의 반경거리. 특정 지역을 중심으로 검색하려고 할 경우 사용. meter 단위 (0 ~ 10000)
        int page = 1;
        String apikey = DAUM_MAPS_ANDROID_APP_API_KEY;

        searcher.searchKeyword(getApplicationContext(), query, latitude, longitude, radius, page, apikey, new OnFinishSearchListener() {
            @Override
            public void onSuccess(final List<Item> itemList) {
                showResult(itemList);
                showToast(itemList.size() + "개의 " + keyword + " 음식점을 찾았습니다!");
            }

            @Override
            public void onFail() {
                showResult();
                //showToast("API_KEY의 제한 트래픽이 초과되었습니다.");
            }
        });

    }

    private void showToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(NearByYou.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean showResult() {
        MapPointBounds mapPointBounds = new MapPointBounds();
        int i = 0;

        //현재 내 위치
        Item item = new Item();
        item.title = "현재 내 위치";

        MapPOIItem poiItem = new MapPOIItem();
        poiItem.setItemName(item.title);
        poiItem.setTag(i++);
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(SplashActivity.lat, SplashActivity.lon);
        poiItem.setMapPoint(mapPoint);
        poiItem.setMapPoint(mapPoint);
        mapPointBounds.add(mapPoint);
        poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
        poiItem.setCustomImageResourceId(R.drawable.location_me);
        poiItem.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
        poiItem.setCustomSelectedImageResourceId(R.drawable.location_me);
        poiItem.setCustomImageAutoscale(false);
        poiItem.setCustomImageAnchor(0.5f, 1.0f);

        mMapView.addPOIItem(poiItem);
        mTagItemMap.put(poiItem.getTag(), item);



        //이 글을 쓸 당시 위치
        Item item2 = new Item();
        item2.title = "글 쓴 당시 위치";

        MapPOIItem poiItem2 = new MapPOIItem();
        poiItem2.setItemName(item2.title);
        poiItem2.setTag(i++);
        MapPoint mapPoint2 = MapPoint.mapPointWithGeoCoord(location_point.lat, location_point.lon);
        poiItem2.setMapPoint(mapPoint2);
        poiItem2.setMapPoint(mapPoint2);
        mapPointBounds.add(mapPoint2);
        poiItem2.setMarkerType(MapPOIItem.MarkerType.CustomImage);
        poiItem2.setCustomImageResourceId(R.drawable.location_you);
        poiItem2.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
        poiItem2.setCustomSelectedImageResourceId(R.drawable.location_you);
        poiItem2.setCustomImageAutoscale(false);
        poiItem2.setCustomImageAnchor(0.5f, 1.0f);

        mMapView.addPOIItem(poiItem2);
        mTagItemMap.put(poiItem2.getTag(), item2);



        MapPOIItem[] poiItems = mMapView.getPOIItems();

        if (poiItems.length > 0) {

            //mMapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(SharedManager.getInstance().getMe().location_point.lat, SharedManager.getInstance().getMe().location_point.lon), 3, true);
            mMapView.selectPOIItem(poiItems[1], true);
            mMapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds));


            return true;
        }

        mMapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds));
        return false; // 검색결과 없음

    }

    private boolean showResult(List<Item> itemList) {
        MapPointBounds mapPointBounds = new MapPointBounds();
        int i;
        for (i = 0; i < itemList.size(); i++) {
            Item item = itemList.get(i);

            MapPOIItem poiItem = new MapPOIItem();
            poiItem.setItemName(item.title);
            poiItem.setTag(i);
            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(item.latitude, item.longitude);
            poiItem.setMapPoint(mapPoint);
            poiItem.setMapPoint(mapPoint);
            mapPointBounds.add(mapPoint);
            poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
            poiItem.setCustomImageResourceId(R.drawable.map_pin_blue);
            poiItem.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
            poiItem.setCustomSelectedImageResourceId(R.drawable.map_pin_red);
            poiItem.setCustomImageAutoscale(false);
            poiItem.setCustomImageAnchor(0.5f, 1.0f);

            mMapView.addPOIItem(poiItem);
            mTagItemMap.put(poiItem.getTag(), item);
        }


        //현재 내 위치
        Item item = new Item();
        item.title = "현재 내 위치";

        MapPOIItem poiItem = new MapPOIItem();
        poiItem.setItemName(item.title);
        poiItem.setTag(i++);
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(SplashActivity.lat, SplashActivity.lon);
        poiItem.setMapPoint(mapPoint);
        poiItem.setMapPoint(mapPoint);
        mapPointBounds.add(mapPoint);
        poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
        poiItem.setCustomImageResourceId(R.drawable.location_me);
        poiItem.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
        poiItem.setCustomSelectedImageResourceId(R.drawable.location_me);
        poiItem.setCustomImageAutoscale(false);
        poiItem.setCustomImageAnchor(0.5f, 1.0f);

        mMapView.addPOIItem(poiItem);
        mTagItemMap.put(poiItem.getTag(), item);



        //이 글을 쓸 당시 위치
        Item item2 = new Item();
        item2.title = "글 쓴 당시 위치";

        MapPOIItem poiItem2 = new MapPOIItem();
        poiItem2.setItemName(item2.title);
        poiItem2.setTag(i++);
        MapPoint mapPoint2 = MapPoint.mapPointWithGeoCoord(location_point.lat, location_point.lon);
        poiItem2.setMapPoint(mapPoint2);
        poiItem2.setMapPoint(mapPoint2);
        mapPointBounds.add(mapPoint2);
        poiItem2.setMarkerType(MapPOIItem.MarkerType.CustomImage);
        poiItem2.setCustomImageResourceId(R.drawable.location_you);
        poiItem2.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
        poiItem2.setCustomSelectedImageResourceId(R.drawable.location_you);
        poiItem2.setCustomImageAutoscale(false);
        poiItem2.setCustomImageAnchor(0.5f, 1.0f);

        mMapView.addPOIItem(poiItem2);
        mTagItemMap.put(poiItem2.getTag(), item2);



        MapPOIItem[] poiItems = mMapView.getPOIItems();

        if (poiItems.length > 0) {

            //mMapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(SharedManager.getInstance().getMe().location_point.lat, SharedManager.getInstance().getMe().location_point.lon), 3, true);
            //mMapView.selectPOIItem(poiItems[0], true);
            mMapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds));


            return true;
        }

        mMapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds));
        return false; // 검색결과 없음

    }

    private Drawable createDrawableFromUrl(String url) {
        try {
            InputStream is = (InputStream) this.fetch(url);
            Drawable d = Drawable.createFromStream(is, "src");
            return d;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Object fetch(String address) throws MalformedURLException, IOException {
        URL url = new URL(address);
        Object content = url.getContent();
        return content;
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {
//        Item item = mTagItemMap.get(mapPOIItem.getTag());
//        StringBuilder sb = new StringBuilder();
//        sb.append("title=").append(item.title).append("\n");
//        sb.append("imageUrl=").append(item.imageUrl).append("\n");
//        sb.append("address=").append(item.address).append("\n");
//        sb.append("newAddress=").append(item.newAddress).append("\n");
//        sb.append("zipcode=").append(item.zipcode).append("\n");
//        sb.append("phone=").append(item.phone).append("\n");
//        sb.append("category=").append(item.category).append("\n");
//        sb.append("longitude=").append(item.longitude).append("\n");
//        sb.append("latitude=").append(item.latitude).append("\n");
//        sb.append("distance=").append(item.distance).append("\n");
//        sb.append("direction=").append(item.direction).append("\n");
//        Toast.makeText(this, sb.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }

    @Override
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {
        mapReverseGeoCoder.toString();
        onFinishReverseGeoCoding(s);
    }

    @Override
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {
        onFinishReverseGeoCoding("Fail");
    }

    private void onFinishReverseGeoCoding(String result) {
        Toast.makeText(getApplicationContext(), "Reverse Geo-coding : " + result, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }
}
