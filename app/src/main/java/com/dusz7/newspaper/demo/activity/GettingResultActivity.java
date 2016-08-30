package com.dusz7.newspaper.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.dusz7.newspaper.demo.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dusz2 on 2016/8/17 0017.
 */
public class GettingResultActivity extends AppCompatActivity {

    private String gettingResult;
    private int gettingHistory;
    private String lastGettingTime;
    private String lastGettingLocation;
    private String lastGetting;

    private TextView resultText;
    private TextView gettingHistoryText;
    private TextView lastGettingTimeText;
    private TextView lastGettingLocationText;

    private boolean isGet;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_getting_result);
        resultText = (TextView)findViewById(R.id.gettingResult_textview) ;
        gettingHistoryText = (TextView)findViewById(R.id.gettingHistory_textview);
        lastGettingTimeText = (TextView)findViewById(R.id.lastGettingTime_textview);
        lastGettingLocationText = (TextView)findViewById(R.id.lastGettingLocation_textview);

        Intent intent = this.getIntent();

        gettingResult = intent.getStringExtra("gettingResult");
        resultText.setText(gettingResult);

        gettingHistory = intent.getIntExtra("gettingHistory",0);

        isGet = intent.getBooleanExtra("isGet",false);
        if(!isGet){
            gettingHistory++;
        }
        gettingHistoryText.setText("该用户已经领取的期数： "+String.valueOf(gettingHistory));

        isGet = intent.getBooleanExtra("isGet",false);
        if(isGet){
            lastGetting = intent.getStringExtra("gettingInformation");
            if(lastGetting != "error"){
                try {
                    JSONObject jsonObject = new JSONObject(lastGetting);
                    lastGettingLocation = jsonObject.getString("station");
                    lastGettingTime = jsonObject.getString("date");
                    lastGettingLocationText.setText("该期领取地点："+lastGettingLocation);
                    lastGettingTimeText.setText("该期领取时间："+lastGettingTime);
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }

        }
    }
}
