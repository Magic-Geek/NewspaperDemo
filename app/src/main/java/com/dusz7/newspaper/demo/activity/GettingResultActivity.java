package com.dusz7.newspaper.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.dusz7.newspaper.demo.R;

/**
 * Created by dusz2 on 2016/8/17 0017.
 */
public class GettingResultActivity extends AppCompatActivity {

    private String gettingResult;

    private TextView resultText;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_getting_result);
        resultText = (TextView)findViewById(R.id.gettingResult_textview) ;

        Intent intent = this.getIntent();
        gettingResult = intent.getStringExtra("gettingResult");

        resultText.setText(gettingResult);
    }
}
