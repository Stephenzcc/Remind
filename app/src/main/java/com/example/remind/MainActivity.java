package com.example.remind;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapException;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.ServiceSettings;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements LocationSource, AMapLocationListener, AMap.OnMapClickListener{

    //AMap是地图对象
    private AMap aMap;
    private MapView mapView;
    //声明AMapLocationClient类对象，定位发起端
    private AMapLocationClient mLocationClient = null;
    //声明mLocationOption对象，定位参数
    public AMapLocationClientOption mLocationOption = null;
    //声明mListener对象，定位监听器
    private OnLocationChangedListener mListener = null;
    //标识，用于判断是否只显示一次定位信息和用户重新定位
    private boolean isFirstLoc = true;
    private AMapLocation amapLocation=null;
    private int returnedInt = 2;
    private String returnedData = null;
    private double lat, latitude;//纬度
    private double lon, longitude;//经度
    private static final char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    private static final String APP_ID = "20190320000279208";
    private static final String SECURITY_KEY = "BOcDuR1L6EwH1HU8GKbD";
    private Marker marker = null;
    private Polyline line = null;
    private List<LatLng> latLngs = null;
    private String transData = null;
    private Vibrator vibrator = null;
    private MediaPlayer mPlayer = null;
    private boolean ifshake = true;
    private Timer timer = null;
    private boolean isConfirm = false;
    private boolean ismapClick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取地图控件引用
        mapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
        mapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.setMapLanguage(AMap.ENGLISH);
            aMap.setOnMapClickListener(this);
            //设置显示定位按钮 并且可以点击
            UiSettings settings = aMap.getUiSettings();
            aMap.setLocationSource(this);//设置了定位的监听
            // 是否显示定位按钮
            settings.setMyLocationButtonEnabled(true);
            aMap.setMyLocationEnabled(true);//显示定位层并且可以触发定位,默认是false
            settings.setScaleControlsEnabled(true);//控制比例尺控件是否显示
            settings.setCompassEnabled(true);//指南针

        }
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);//连续定位（1秒1次定位）
        myLocationStyle.interval(1000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        //aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。


        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){//未开启定位权限
            //开启定位权限,200是标识码
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},200);
        }else{
            //开始定位
            location();
            Toast.makeText(MainActivity.this,"已开启定位权限",Toast.LENGTH_LONG).show();
        }



        findViewById(R.id.searchText).bringToFront();
        findViewById(R.id.imageButton).bringToFront();
        findViewById(R.id.confirmBtn).bringToFront();
        findViewById(R.id.cancelBtn).bringToFront();

        mPlayer = MediaPlayer.create(this, R.raw.ring);
        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 200://刚才的识别码
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){//用户同意权限,执行我们的操作
                    location();//开始定位
                }else{//用户拒绝之后,当然我们也可以弹出一个窗口,直接跳转到系统设置页面
                    Toast.makeText(MainActivity.this,"未开启定位权限,请手动到设置去开启权限",Toast.LENGTH_LONG).show();
                }
                break;
            default:break;
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if(!isConfirm){
            ismapClick = true;
            aMap.clear();
            latitude = latLng.latitude;
            longitude = latLng.longitude;
            aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(latitude, longitude)));

            if(marker!=null){
                marker.remove();
            }
            LatLng latLng1 = new LatLng(latitude,longitude);
            marker = aMap.addMarker(new MarkerOptions().position(latLng1).title("Destination").snippet("latitude:"+latitude+";longitude:"+longitude));
            try {
                aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(createBounds(lat, lon, latitude, longitude), 150),1000L,null);
            } catch (AMapException e) {
                e.printStackTrace();
            }
        }
    }

    private void location() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为Hight_Accuracy高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mapView.onDestroy();
        mLocationClient.stopLocation();//停止定位
        mLocationClient.onDestroy();//销毁定位客户端。
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，实现地图生命周期管理
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，实现地图生命周期管理
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            amapLocation = aMapLocation;
            mListener.onLocationChanged(aMapLocation);
            if (aMapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见官方定位类型表
                aMapLocation.getLatitude();//获取纬度
                lat = aMapLocation.getLatitude();
                aMapLocation.getLongitude();//获取经度
                lon = aMapLocation.getLongitude();
                aMapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(aMapLocation.getTime());
                df.format(date);//定位时间
                aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                aMapLocation.getCountry();//国家信息
                aMapLocation.getProvince();//省信息
                aMapLocation.getCity();//城市信息
                aMapLocation.getDistrict();//城区信息
                aMapLocation.getStreet();//街道信息
                aMapLocation.getStreetNum();//街道门牌号信息
                aMapLocation.getCityCode();//城市编码
                aMapLocation.getAdCode();//地区编码

                // 如果不设置标志位，此时再拖动地图时，它会不断将地图移动到当前的位置
                if (isFirstLoc) {
                    //设置缩放级别
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(14));
                    //将地图移动到定位点
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
                    //点击定位按钮 能够将地图的中心移动到定位点
                    mListener.onLocationChanged(aMapLocation);
                    //添加图钉
                    //aMap.addMarker(getMarkerOptions(amapLocation));
                    //获取定位信息
                    StringBuffer buffer = new StringBuffer();
                    buffer.append(aMapLocation.getCountry() + ""
                            + aMapLocation.getProvince() + ""
                            + aMapLocation.getCity() + ""
                            + aMapLocation.getProvince() + ""
                            + aMapLocation.getDistrict() + ""
                            + aMapLocation.getStreet() + ""
                            + aMapLocation.getStreetNum());
                    //Toast.makeText(getApplicationContext(), buffer.toString(), Toast.LENGTH_LONG).show();
                    isFirstLoc = false;
                }


            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
                Toast.makeText(getApplicationContext(), "Location failed!", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        mListener = null;
    }

    public void settingsClick(View view) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, Settings.class);
        startActivityForResult(intent, 1);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case 1:
                if(resultCode == RESULT_OK){
                    returnedInt = data.getIntExtra("rangenum",2);
                    Log.d("MainActivity", Integer.toString(returnedInt));

                    SharedPreferences sharedPreferences = getSharedPreferences("isChecked", MODE_PRIVATE);
                    ifshake = sharedPreferences.getBoolean("check", false);
                }
                break;
            case 2:
                if(resultCode == RESULT_OK){
                    returnedData = data.getStringExtra("location");
                    Log.d("MainActivity", returnedData);
                    TextView searchText = findViewById(R.id.searchText);
                    searchText.setText(returnedData);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            transData = getTransResult(returnedData, "auto", "zh");
                            Log.e("MainActivity", transData);
                            getLatlon(transData);
                        }
                    }).start();
                    //Log.e("MainActivity", returnedData);
                    //getLatlon(returnedData);
                }
            default:
        }
    }

    public void POISearch(View view) {
        String searchPlace=amapLocation.getCity();//获取所在城市信息
        Log.d("MainActivity", searchPlace);
        Intent intent_search=new Intent(MainActivity.this, POISearch.class);
        intent_search.getStringExtra("location");//将定位的城市信息传入poi搜索事件处理
        startActivityForResult(intent_search, 2);
    }

    private void getLatlon(String cityName){
        ServiceSettings.getInstance().setLanguage(ServiceSettings.ENGLISH);//默认是中文ServiceSettings.CHINESE
        GeocodeSearch geocodeSearch=new GeocodeSearch(this);
        geocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

                if (i==1000){
                    if (geocodeResult!=null && geocodeResult.getGeocodeAddressList()!=null && geocodeResult.getGeocodeAddressList().size()>0){

                        GeocodeAddress geocodeAddress = geocodeResult.getGeocodeAddressList().get(0);
                        latitude = geocodeAddress.getLatLonPoint().getLatitude();//纬度
                        longitude = geocodeAddress.getLatLonPoint().getLongitude();//经度
                        String adcode= geocodeAddress.getAdcode();//区域编码


                        aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(latitude, longitude)));

                        if(marker!=null){
                            marker.remove();
                        }
                        LatLng latLng = new LatLng(latitude,longitude);
                        marker = aMap.addMarker(new MarkerOptions().position(latLng).title("Destination").snippet("latitude:"+String.valueOf(latitude)+";longitude:"+String.valueOf(longitude)));
                        try {
                            aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(createBounds(lat, lon, latitude, longitude), 150),1000L,null);
                        } catch (AMapException e) {
                            e.printStackTrace();
                        }

                        Log.e("lgq地理编码", geocodeAddress.getAdcode()+"");
                        Log.e("lgq纬度latitude",latitude+"");
                        Log.e("lgq经度longititude",longitude+"");

                        Log.i("lgq","dddwww===="+longitude);

                    }else {
                        Toast.makeText(MainActivity.this,"Location wrong!",Toast.LENGTH_SHORT).show();
                        Log.e("错误","error");
                        //ToastUtils.show(context,"地址名出错");
                    }
                }
            }
        });

        GeocodeQuery geocodeQuery=new GeocodeQuery(cityName.trim(),"29");
        geocodeSearch.getFromLocationNameAsyn(geocodeQuery);


    }



    public static LatLngBounds createBounds(Double latA, Double lngA, Double latB, Double lngB) throws AMapException {
        LatLng northeastLatLng;
        LatLng southwestLatLng;

        Double topLat,topLng;
        Double bottomLat,bottomLng;
        if(latA>=latB){
            topLat=latA;
            bottomLat=latB;
        }else{
            topLat=latB;
            bottomLat=latA;
        }
        if(lngA>=lngB){
            topLng=lngA;
            bottomLng=lngB;
        }else{
            topLng=lngB;
            bottomLng=lngA;
        }
        northeastLatLng=new LatLng(topLat,topLng);
        southwestLatLng=new LatLng(bottomLat,bottomLng);
        return new LatLngBounds(southwestLatLng, northeastLatLng);
    }
    public static String md5(String input) {
        if (input == null)
            return null;

        try {
            // 拿到一个MD5转换器（如果想要SHA1参数换成”SHA1”）
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            // 输入的字符串转换成字节数组
            byte[] inputByteArray = input.getBytes("utf-8");
            // inputByteArray是输入字符串转换得到的字节数组
            messageDigest.update(inputByteArray);
            // 转换并返回结果，也是字节数组，包含16个元素
            byte[] resultByteArray = messageDigest.digest();
            // 字符数组转换成字符串返回
            return byteArrayToHex(resultByteArray);
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
    private static String byteArrayToHex(byte[] byteArray) {
        // new一个字符数组，这个就是用来组成结果字符串的（解释一下：一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方））
        char[] resultCharArray = new char[byteArray.length * 2];
        // 遍历字节数组，通过位运算（位运算效率高），转换成字符放到字符数组中去
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];
        }

        // 字符数组组合成字符串返回
        return new String(resultCharArray);

    }


    private String getTransResult(String query, String from, String to){
        String salt = String.valueOf(System.currentTimeMillis());
        String src = APP_ID + query + salt + SECURITY_KEY;
        String sign = md5(src);
        try {
            query = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String stringUrl = "https://api.fanyi.baidu.com/api/trans/vip/translate";
        String realUrl=stringUrl+"?q="+query+"&from="+from+"&to="+to+"&appid="+APP_ID+"&salt="+salt+"&sign="+sign;
        HttpURLConnection urlConnection = null;
        BufferedReader reader;

        try {
            URL url = new URL(realUrl);

            // Create the request to get the information from the server, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Mainly needed for debugging
                Log.d("TAG", line);
                buffer.append(line + "\n");
            }
            String str = buffer.toString();
            JSONObject jsonObject = new JSONObject(str);
            JSONArray jsonArray = jsonObject.getJSONArray("trans_result");
            JSONObject trans_result = jsonArray.getJSONObject(0);
            String result = trans_result.getString("dst");
            return result;

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (ProtocolException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void onConfirm(View view) {
        if(transData!=null||ismapClick){
            isConfirm = true;
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {//定时器循环执行的代码
                    float distance = AMapUtils.calculateLineDistance(new LatLng(lat,lon),new LatLng(latitude,longitude));//米
                    if(line!=null){
                        line.remove();
                    }
                    if(latLngs!=null){
                        latLngs.clear();
                    }
                    latLngs = new ArrayList<>();
                    latLngs.add(new LatLng(lat,lon));
                    latLngs.add(new LatLng(latitude,longitude));
                    line = aMap.addPolyline(new PolylineOptions().addAll(latLngs).width(10).color(Color.argb(255, 255, 69, 0)));
                    line.setVisible(true);
                    if(distance<=(float)returnedInt*1000){
                        mPlayer.start();
                        mPlayer.setLooping(true);

                        if(ifshake){

                            long[] patter = {1000, 1000, 2000, 50};
                            vibrator.vibrate(patter, 0);//重复两次上面的pattern 如果只想震动一次，index设为-1 参数2是从指定下标开始重复
                        }

                    }
                    //Log.e("MainActivity",Float.toString(distance));
                }
            },1,2000);
        }
    }

    public void onCancel(View view) {
        if(isConfirm){
            ismapClick = false;
            isConfirm = false;
            timer.cancel();
            if(mPlayer.isPlaying()){
                mPlayer.pause();
            }
            marker.remove();
            latLngs.clear();
            line.remove();
            if(vibrator!=null){
                if(vibrator.hasVibrator()){
                    vibrator.cancel();
                }
            }
        }
    }
}

