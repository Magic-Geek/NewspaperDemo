package com.tsb.newspaper.management.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tsb.newspaper.management.R;
import com.tsb.newspaper.management.internet.InternetUtil;

import org.json.JSONException;
import org.json.JSONObject;


public class VerifyActivity extends AppCompatActivity {

    TextView registerPhoneTV;
    TextView comingPhoneTV;
    String registerPhone;
    String comingPhone;
    int isOK = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        registerPhoneTV = (TextView)findViewById(R.id.register_phone_text);
        comingPhoneTV = (TextView)findViewById(R.id.coming_phone_text);

        Intent intent = this.getIntent();
        registerPhone = intent.getStringExtra("thePhone");
        comingPhone = "";

        registerPhoneTV.setText(registerPhone);

        MyPhoneStateListener phoneListener = new MyPhoneStateListener(); //我们派生的类
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);

    }

    public void verification_registerPhone_onClick(View v){
        if(comingPhone!="" && registerPhone!=""){
            if(comingPhone.equals(registerPhone)){
                Toast.makeText(VerifyActivity.this,"验证成功，正在注册",Toast.LENGTH_SHORT).show();
                isOK = 1;


                            Thread registerThread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    //线程执行内容
                                    //为手机用户注册
                                    try{
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("phone_num",comingPhone);
                                        String url = getResources().getString(R.string.network_url)+"user/";
                                        InternetUtil internetUtil = new InternetUtil(url);

                                        String putResult = internetUtil.putUserMethod(jsonObject.toString());
                                        if (putResult == "OK"){
                                            Looper.prepare();
                                            Toast.makeText(VerifyActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                                            Looper.loop();
                                        }else {
                                            Looper.prepare();
                                            Toast.makeText(VerifyActivity.this,"服务器访问异常，注册失败",Toast.LENGTH_SHORT).show();
                                            Looper.loop();
                                        }

                                    }catch (JSONException e){
                                        e.printStackTrace();
                                    }

                                }
                            });
                            if(new InternetUtil().isNetworkConnected(VerifyActivity.this)){
                                //开启线程
                                registerThread.start();
//                                try{
//                                    registerThread.join();
//                                }catch (InterruptedException e){
//                                    e.printStackTrace();
//                                }
                            }else {
                                Toast.makeText(VerifyActivity.this,"网络不可用",Toast.LENGTH_SHORT).show();
                            }


                Intent ok = new Intent(VerifyActivity.this, LoginActivity.class);
                ok.putExtra("isOk",isOK);
                ok.putExtra("currentPhone",comingPhone);
                setResult(RESULT_OK,ok);
                finish();
            }
            else {
                Toast.makeText(VerifyActivity.this,"验证失败，号码错误，已更正号码",Toast.LENGTH_SHORT).show();
                isOK = 0;
                Intent ok = new Intent(VerifyActivity.this, LoginActivity.class);
                ok.putExtra("isOk",isOK);
                ok.putExtra("currentPhone",comingPhone);
                setResult(RESULT_OK,ok);
                finish();
            }
        }
    }

    class MyPhoneStateListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state,String incomingNumber){

            if(state == TelephonyManager.CALL_STATE_RINGING){
                comingPhone = incomingNumber;
                comingPhoneTV.setText(incomingNumber);
            }
        }

    }
}
