package com.tsb.newspaper.management.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.tsb.newspaper.management.R;
import com.tsb.newspaper.management.infoSaved.MyInternalStorage;
import com.tsb.newspaper.management.internet.InternetUtil;
import com.tsb.newspaper.management.newspaper.GettingNewspaper;
import com.tsb.newspaper.management.newspaper.Newspaper;
import com.tsb.newspaper.management.util.Utils;

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

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = new AMapLocationClientOption();

    private final int WRITE_COARSE_LOCATION_REQUEST_CODE = 0;
    private final int WRITE_FINE_LOCATION_REQUEST_CODE = 1;

    private String myLocation;

    SimpleDateFormat formatter;
    private String myTime;

    private boolean isLogin = false;

    private String myPhone;

    private boolean isGet = false;
    private boolean isContinue = false;

    private int gettingResut; //已经领取过设为1，第一次领设为0

    final int REQUEST_CODE = 1;
    final int RESULT_CODE = 11;

    private int gettingHistory;
    private String userName;

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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    WRITE_COARSE_LOCATION_REQUEST_CODE);//自定义的code
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    WRITE_FINE_LOCATION_REQUEST_CODE);//自定义的code
        }

        initLocation();
        startLocation();


    }


    public void confirm_getting_onClick(View v){

        if (isLogin){

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

                    String url = getResources().getString(R.string.network_url) + "record/"+myPhone+"/?name="+InternetUtil.urlEncoder(myNewspaper.getName())+"&jou_id="+myNewspaper.getTotalIssue();
//                    Log.i("test",url);

                    InternetUtil internetUtil = new InternetUtil(url);
                    String getResult = internetUtil.getRecordMethod();
                    if(getResult != null && getResult != ""){
                        isContinue = true;
                        try {
                            JSONObject jsonObject = new JSONObject(getResult);
                            gettingHistory = jsonObject.getInt("news_num");
                            isGet = jsonObject.getBoolean("receive_state");
                            userName = jsonObject.getString("user_name");

                            Log.i("isGet",String.valueOf(isGet));
                            if(isGet){
                                lastGetting = new GettingNewspaper(myNewspaper,myPhone,jsonObject.getString("station"),jsonObject.getString("date"));
                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
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
                try
                {
                    gettingThread.join();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }else {
                isContinue = false;
                Toast.makeText(NewspaperInfoActivity.this,"网络不可用",Toast.LENGTH_SHORT).show();
            }

            Intent intent = new Intent(NewspaperInfoActivity.this,GettingResultActivity.class);

            Log.i("isGet",String.valueOf(isGet));

            if(!isGet){
                gettingResut = 0;

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
//                    try {
//                        addRecordThread.join();
//                    } catch (InterruptedException e)
//                    {
//                        e.printStackTrace();
//                    }
                }else {
                    isContinue = false;
                    Toast.makeText(NewspaperInfoActivity.this,"网络不可用",Toast.LENGTH_SHORT);
                }

            }else {
                gettingResut = 1;
                intent.putExtra("gettingInformation",lastGetting.getLastGetting());
                isContinue = true;
            }

            Log.i("isContinue",String.valueOf(isContinue));

            if(isContinue){
                intent.putExtra("isGet",isGet);
                intent.putExtra("gettingResult",gettingResut);
                intent.putExtra("gettingHistory",gettingHistory);
                intent.putExtra("userName",userName);

                startActivity(intent);
            }

        }
        else{
            Toast.makeText(NewspaperInfoActivity.this,"尚未登录，请登录",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(NewspaperInfoActivity.this,LoginActivity.class);
            startActivityForResult(intent,REQUEST_CODE);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_CODE && requestCode == REQUEST_CODE){
//            Toast.makeText(NewspaperInfoActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
            myPhone = data.getStringExtra("phone");
            isLogin = data.getBooleanExtra("isLogin",false);

            if(isLogin){

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

                        String url = getResources().getString(R.string.network_url) + "record/"+myPhone+"/?name="+InternetUtil.urlEncoder(myNewspaper.getName())+"&jou_id="+myNewspaper.getTotalIssue();
//                        Log.i("test",url);

                        InternetUtil internetUtil = new InternetUtil(url);
                        String getResult = internetUtil.getRecordMethod();
                        if(getResult != null && getResult != ""){
                            isContinue = true;
                            try {
                                JSONObject jsonObject = new JSONObject(getResult);
                                gettingHistory = jsonObject.getInt("news_num");
                                isGet = jsonObject.getBoolean("receive_state");
                                userName = jsonObject.getString("user_name");

                                Log.i("isGet",String.valueOf(isGet));
                                if(isGet){
                                    lastGetting = new GettingNewspaper(myNewspaper,myPhone,jsonObject.getString("station"),jsonObject.getString("date"));
                                }

                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }else {
                            isContinue = false;
                        }

                    }
                });

                if(new InternetUtil().isNetworkConnected(NewspaperInfoActivity.this)){
                    //开启线程
                    gettingThread.start();
                    try
                    {
                        gettingThread.join();
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }else {
                    isContinue = false;
                    Toast.makeText(NewspaperInfoActivity.this,"网络不可用",Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent(NewspaperInfoActivity.this,GettingResultActivity.class);

                if(isContinue){
                    if(!isGet){
                        gettingResut = 0;

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
//                    try {
//                        addRecordThread.join();
//                    } catch (InterruptedException e)
//                    {
//                        e.printStackTrace();
//                    }
                        }else {
                            isContinue = false;
                            Toast.makeText(NewspaperInfoActivity.this,"网络不可用",Toast.LENGTH_SHORT);
                        }

                    }else {
                        gettingResut = 1;
                        intent.putExtra("gettingInformation",lastGetting.getLastGetting());
                        isContinue = true;
                    }
                }else{
                    Toast.makeText(NewspaperInfoActivity.this,"报纸信息未入库或访问信息错误，请重试",Toast.LENGTH_SHORT).show();
                }


                if(isContinue){
                    intent.putExtra("isGet",isGet);
                    intent.putExtra("gettingResult",gettingResut);
                    intent.putExtra("gettingHistory",gettingHistory);
                    intent.putExtra("userName",userName);

                    startActivity(intent);
                    this.finish();
                }
            }

        }
    }

    @Override
    public void onResume(){
        super.onResume();

    }

    @Override
    public void onPause(){
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyLocation();
    }

    private void initLocation(){
        //初始化client
        locationClient = new AMapLocationClient(this.getApplicationContext());
        //设置定位参数
        locationClient.setLocationOption(getDefaultOption());
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
    }

    private AMapLocationClientOption getDefaultOption(){
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是ture
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        return mOption;
    }

    /**
     * 定位监听
     */
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation loc) {
            if (null != loc) {
                //解析定位结果
                String result = Utils.getMyLocation(loc);
                Log.i("location",result);
                myLocation = result;
            } else {
            }
        }
    };

    /**
     * 开始定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private void startLocation(){

        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }

    /**
     * 停止定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
//    private void stopLocation(){
//        // 停止定位
//        locationClient.stopLocation();
//    }

    /**
     * 销毁定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private void destroyLocation(){
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }


}
