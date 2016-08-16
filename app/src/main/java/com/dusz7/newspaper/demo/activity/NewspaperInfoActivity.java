package com.dusz7.newspaper.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.dusz7.newspaper.demo.R;
import com.dusz7.newspaper.demo.newspaper.Newspaper;

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


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_newspaper_information);

        nameText = (TextView)findViewById(R.id.name_text);
        dateText = (TextView)findViewById(R.id.date_text);
        issueText = (TextView)findViewById(R.id.issue_text);
        totalIssueText = (TextView)findViewById(R.id.totalIssue_text);


        Intent intent = this.getIntent();
        decodeResult = intent.getStringExtra("decodeResult");

        myNewspaper = new Newspaper(decodeResult);

        nameText.setText(myNewspaper.getName());
        dateText.setText(myNewspaper.getDate());
        issueText.setText(myNewspaper.getIssue());
        totalIssueText.setText(myNewspaper.getTotalIssue());

    }

    public void confirm_getting_onClick(View v){

        myNewspaper.saveNewspaperInformation();
        Intent intent = new Intent(NewspaperInfoActivity.this,GetNewspaperActivity.class);
        startActivity(intent);
    }
}
