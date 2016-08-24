package com.dusz7.newspaper.demo.activity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dusz7.newspaper.demo.R;
import com.dusz7.newspaper.demo.infoSaved.MyInternalStorage;
import com.dusz7.newspaper.demo.newspaper.Newspaper;

import java.io.IOException;

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
    protected LocationListener locationListener;
    protected Context context;

    private boolean isLogin = false;

    private String myPhone;

    final int REQUEST_CODE = 1;
    final int RESULT_CODE = 11;


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

    }

    public void confirm_getting_onClick(View v){

        if (isLogin){
            Toast.makeText(NewspaperInfoActivity.this,"已登录，查看结果",Toast.LENGTH_SHORT).show();
            Thread gettingThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                    //线程执行内容
                    //发送手机号、报纸内容、地理位置
                    //得到返回值：是否已领取，领取历史记录
                }
            });
            //开启线程
            gettingThread.start();

            Intent intent = new Intent(NewspaperInfoActivity.this,GettingResultActivity.class);
            startActivity(intent);
        }
        else{
            Toast.makeText(NewspaperInfoActivity.this,"尚未登录，请登录",Toast.LENGTH_SHORT).show();
//            myNewspaper.saveNewspaperInformation();
            Intent intent = new Intent(NewspaperInfoActivity.this,GetNewspaperActivity.class);
            startActivityForResult(intent,REQUEST_CODE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_CODE && requestCode == REQUEST_CODE){
            myPhone = data.getStringExtra("phone");
            isLogin = data.getBooleanExtra("isLogin",false);

        }
    }


}
