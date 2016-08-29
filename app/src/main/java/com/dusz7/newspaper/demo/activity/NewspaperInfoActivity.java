package com.dusz7.newspaper.demo.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dusz7.newspaper.demo.R;
import com.dusz7.newspaper.demo.infoSaved.MyInternalStorage;
import com.dusz7.newspaper.demo.internet.InternetUtil;
import com.dusz7.newspaper.demo.newspaper.GettingNewspaper;
import com.dusz7.newspaper.demo.newspaper.Newspaper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dusz2 on 2016/7/20 0020.
 */
public class NewspaperInfoActivity extends AppCompatActivity {

    private  Newspaper myNewspaper;

    private String decodeResult;
    private TextView nameText;
    private TextView dateText;
    private TextView issueText;
    private TextView totalIssueText;

    protected LocationManager locationManager;

    private String myLatitude;
    private String myAltitude;
    private String myLocation;

    SimpleDateFormat formatter;
    private String myTime;

    private boolean isLogin = false;

    private String myPhone;

    private boolean isGet = false;
    private boolean isContinue = false;

    private String gettingResut;

    final int REQUEST_CODE = 1;
    final int RESULT_CODE = 11;

    final int REQUEST_PERMISSION_FINE_LOCATION_CODE = 20;

    private int gettingHistory;

    private GettingNewspaper gettingNewspaper;
    private GettingNewspaper lastGetting = new GettingNewspaper();


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_newspaper_information);

        nameText = (TextView)findViewById(R.id.name_text);
        dateText = (TextView)findViewById(R.id.date_text);
        issueText = (TextView)findViewById(R.id.issue_text);
        totalIssueText = (TextView)findViewById(R.id.totalIssue_text);


        MyInternalStorage myInternalStorage = new MyInternalStorage(NewspaperInfoActivity.this);
        String filename = "myNewspaper";
        try{
            decodeResult = myInternalStorage.get(filename);
        }catch (IOException e){
            e.printStackTrace();
        }

        myNewspaper = new Newspaper(decodeResult);

        nameText.setText(myNewspaper.getName());
        dateText.setText(myNewspaper.getDate());
        issueText.setText(myNewspaper.getIssue());
        totalIssueText.setText(myNewspaper.getTotalIssue());

        formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if(!isGpsAble(locationManager)){
            Toast.makeText(NewspaperInfoActivity.this, "定位服务未打开", Toast.LENGTH_SHORT).show();
            openGPS2();
        }

        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(NewspaperInfoActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            //has permission, do operation directly

            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            updateLocation(location);
            //设置间隔两秒获得一次GPS定位信息
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 8, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    // 当GPS定位信息发生改变时，更新定位
                    updateLocation(location);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {
                    // 当GPS LocationProvider可用时，更新定位
                    if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(NewspaperInfoActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        updateLocation(locationManager.getLastKnownLocation(provider));
                    }
                }

                @Override
                public void onProviderDisabled(String provider) {
                    updateLocation(null);
                }
            });

        } else {
            ActivityCompat.requestPermissions(NewspaperInfoActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_FINE_LOCATION_CODE);
        }

    }

    public void confirm_getting_onClick(View v){

        if (isLogin){
            Toast.makeText(NewspaperInfoActivity.this,"登录成功",Toast.LENGTH_SHORT).show();

            Thread gettingThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    //线程执行内容
                    //发送手机号、报纸内容、地理位置
                    //得到返回值：是否已领取，领取历史记录

                    Date curDate =  new Date(System.currentTimeMillis());
                    myTime = formatter.format(curDate);

                    gettingNewspaper = new GettingNewspaper(myNewspaper,myPhone,myLocation,myTime);

                    Log.i("领取情况",gettingNewspaper.toString());

                    String url = getResources().getString(R.string.network_url) + "record/"+myPhone+"/?name="+myNewspaper.getName()+"&jou_id="+myNewspaper.getTotalIssue();

                    InternetUtil internetUtil = new InternetUtil(url);
                    String getResult = internetUtil.getRecordMethod();
                    if(getResult != null && getResult != ""){
                        try {
                            JSONObject jsonObject = new JSONObject(getResult);
                            gettingHistory = jsonObject.getInt("news_num");
                            isGet = jsonObject.getBoolean("receive_state");

                            if(isGet){
                                lastGetting = new GettingNewspaper(myNewspaper,myPhone,jsonObject.getString("station"),jsonObject.getString("time"));
                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                        isContinue = true;
                    }else {
                        isContinue = false;
                        Looper.prepare();
                        Toast.makeText(NewspaperInfoActivity.this,"访问服务器异常",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                }
            });

            if(new InternetUtil().isNetworkConnected(NewspaperInfoActivity.this)){
                //开启线程
                gettingThread.start();
            }else {
                isContinue = false;
                Toast.makeText(NewspaperInfoActivity.this,"网络不可用",Toast.LENGTH_SHORT).show();
            }

            Intent intent = new Intent(NewspaperInfoActivity.this,GettingResultActivity.class);

            if(!isGet){
                gettingResut = "领取成功";

                Thread addRecordThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String url = getResources().getString(R.string.network_url)+"record/"+myPhone+"/";
                        InternetUtil internetUtil = new InternetUtil(url);
                        String gettingResult  = internetUtil.putRecordMethod(gettingNewspaper.getGettingInformation());

                        if(gettingResult == "OK"){
                            isContinue = true;
                            Looper.prepare();
                            Toast.makeText(NewspaperInfoActivity.this,"领取成功",Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }else {
                            isContinue = false;
                            Looper.prepare();
                            Toast.makeText(NewspaperInfoActivity.this,"访问服务器异常，领取失败",Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                });
                if(new InternetUtil().isNetworkConnected(NewspaperInfoActivity.this)){
                    addRecordThread.start();
                }else {
                    isContinue = false;
                    Toast.makeText(NewspaperInfoActivity.this,"网络不可用",Toast.LENGTH_SHORT);
                }

            }else {
                gettingResut = "该用户已领取";
                intent.putExtra("gettingInformation",lastGetting.toString());
                isContinue = true;
            }

            if(isContinue){
                intent.putExtra("isGet",isGet);
                intent.putExtra("gettingResult",gettingResut);
                intent.putExtra("gettingHistory",gettingHistory);

                startActivity(intent);
            }

        }
        else{
            Toast.makeText(NewspaperInfoActivity.this,"尚未登录，请登录",Toast.LENGTH_SHORT).show();
//            myNewspaper.saveNewspaperInformation();
            Intent intent = new Intent(NewspaperInfoActivity.this,GetNewspaperActivity.class);
            startActivityForResult(intent,REQUEST_CODE);
        }

    }

    private boolean isGpsAble(LocationManager lm){
        return lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)?true:false;
    }

    private void openGPS2(){
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent,0);
    }

    private void updateLocation(Location location) {
        if (location != null) {
//            StringBuilder sb = new StringBuilder();
//            sb.append("当前的位置信息：\n");
//            sb.append("精度：" + location.getLongitude() + "\n");
//            sb.append("纬度：" + location.getLatitude() + "\n");
//            sb.append("高度：" + location.getAltitude() + "\n");
//            sb.append("速度：" + location.getSpeed() + "\n");
//            sb.append("方向：" + location.getBearing() + "\n");
//            sb.append("定位精度：" + location.getAccuracy() + "\n");

            myLatitude = String.valueOf(location.getLatitude());
            myAltitude = String.valueOf(location.getAltitude());
            myLocation = myLatitude+","+myAltitude;

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_FINE_LOCATION_CODE) {
            int grantResult = grantResults[0];
            boolean granted = grantResult == PackageManager.PERMISSION_GRANTED;
//            Log.i(DEBUG_TAG, "onRequestPermissionsResult granted=" + granted);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_CODE && requestCode == REQUEST_CODE){
            Toast.makeText(NewspaperInfoActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
            myPhone = data.getStringExtra("phone");
            isLogin = data.getBooleanExtra("isLogin",false);
        }
    }


}
