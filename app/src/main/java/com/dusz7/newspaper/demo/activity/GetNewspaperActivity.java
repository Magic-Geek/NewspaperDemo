package com.dusz7.newspaper.demo.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.dusz7.newspaper.demo.R;
import com.dusz7.newspaper.demo.internet.InternetUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dusz2 on 2016/8/16 0016.
 */
public class GetNewspaperActivity extends AppCompatActivity implements Runnable {

    private EditText phoneEditText;

    private String myPhone;

    private String newspaperInformation;

    private boolean isRegister = false;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_get_newspaper);

        phoneEditText = (EditText)findViewById(R.id.phone_edit_text);
        myPhone = phoneEditText.getText().toString();

        MyPhoneStateListener phoneListener = new MyPhoneStateListener(); //我们派生的类
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);

        Intent intent = this.getIntent();
        this.newspaperInformation = intent.getStringExtra("newpaper");
    }

    public void verification_getting_onClick(View v){

        Thread verificationThread = new Thread(GetNewspaperActivity.this);
        verificationThread.start();
    }

    @Override
    public void run(){

        String url = "";
        InternetUtil internetUtil = new InternetUtil(url);
        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("newspaper",this.newspaperInformation);
            jsonObject.put("phone",this.myPhone);
        }catch (JSONException e){
            e.printStackTrace();
        }
        //internetUtil.putMethod(jsonObject);

//        isRegister = false;
        if(isRegister){

            Intent intent = new Intent(GetNewspaperActivity.this,GettingResultActivity.class);
            //判断结果
            String gettingResult = "Getting sucessfully!!";
            intent.putExtra("gettingResult",gettingResult);

            startActivity(intent);

        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("用户未注册");
            builder.setMessage("是否要为手机用户："+this.myPhone+"进行注册");
            builder.setPositiveButton("确定注册", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //注册

                    isRegister = true;
                }
            });

            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            builder.setCancelable(true);
            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }




    //派生的phoneStateListener类
    class MyPhoneStateListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state,String incomingNumber){
            Log.e("PhoneCallState", "Incoming number "+incomingNumber); //incomingNumber就是来电号码

            myPhone = incomingNumber;
            phoneEditText.setText(myPhone);
        }

    }

}
