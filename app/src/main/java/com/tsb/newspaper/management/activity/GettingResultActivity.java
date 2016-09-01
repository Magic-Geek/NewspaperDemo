package com.tsb.newspaper.management.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsb.newspaper.management.R;

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
    private LinearLayout alreadyGettingLinear;

    private boolean isGet;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_getting_result);
        resultText = (TextView)findViewById(R.id.gettingResult_textview) ;
        gettingHistoryText = (TextView)findViewById(R.id.gettingHistory_textview);
        lastGettingTimeText = (TextView)findViewById(R.id.lastGettingTime_textview);
        lastGettingLocationText = (TextView)findViewById(R.id.lastGettingLocation_textview);

        alreadyGettingLinear = (LinearLayout)findViewById(R.id.alreadyGetting);

        Intent intent = this.getIntent();

        gettingResult = intent.getStringExtra("gettingResult");
        resultText.setText(gettingResult);

        gettingHistory = intent.getIntExtra("gettingHistory",0);

        isGet = intent.getBooleanExtra("isGet",false);
        if(!isGet){
            alreadyGettingLinear.setVisibility(View.GONE);
            gettingHistory++;
        }
        gettingHistoryText.setText(String.valueOf(gettingHistory)+" 期");


        if(isGet){
            alreadyGettingLinear.setVisibility(View.VISIBLE);
            lastGetting = intent.getStringExtra("gettingInformation");
            if(lastGetting != "error"){
                try {
                    JSONObject jsonObject = new JSONObject(lastGetting);
                    lastGettingLocation = jsonObject.getString("station");
                    lastGettingTime = jsonObject.getString("date");
                    lastGettingLocationText.setText(lastGettingLocation);
                    lastGettingTimeText.setText(lastGettingTime);
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }

        }
    }
}
