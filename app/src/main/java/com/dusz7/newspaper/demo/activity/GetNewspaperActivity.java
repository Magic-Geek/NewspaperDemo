package com.dusz7.newspaper.demo.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
public class GetNewspaperActivity extends AppCompatActivity {

    private EditText phoneEditText;

    private String myPhone;

    final int RESULT_CODE = 11;
//    private String newspaperInformation;

    private boolean isRegister = false;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_get_newspaper);

        phoneEditText = (EditText)findViewById(R.id.phone_edit_text);


        MyPhoneStateListener phoneListener = new MyPhoneStateListener(); //我们派生的类
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);

//        Intent intent = this.getIntent();
//        this.newspaperInformation = intent.getStringExtra("newspaper");
    }

    public void verification_getting_onClick(View v){
        myPhone = phoneEditText.getText().toString();


        Thread verificationThread = new Thread(new Runnable() {
            @Override
            public void run() {
                //线程执行内容

                //判断是否注册

                String url = "";
                InternetUtil internetUtil = new InternetUtil(url);
                try{
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("phone",myPhone);
                }catch (JSONException e){
                    e.printStackTrace();
                }
                //internetUtil.putMethod(jsonObject);


                //涉及返回结果
                Message msg = new Message();
                if(isRegister){
                    msg.what = 0;
                }else {
                    msg.what = 1;
                }

                handler.sendMessage(msg);

            }
        });
        //开启线程
        verificationThread.start();
    }



    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    Intent intent = new Intent();
                    intent.putExtra("phone",myPhone);
                    intent.putExtra("isLogin",true);
                    setResult(RESULT_CODE,intent);
                    finish();
                    //判断结果
//                    String gettingResult = "Getting sucessfully!!";
//                    intent.putExtra("gettingResult",gettingResult);

//                    startActivity(intent);
                    break;
                case 1:
                    AlertDialog.Builder builder = new AlertDialog.Builder(GetNewspaperActivity.this);

                    builder.setTitle("用户未注册");
                    builder.setMessage("如需继续操作，请为用户注册：\n"+myPhone);

                    builder.setPositiveButton("确定注册", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //注册

                            isRegister = true;

                            Thread registerThread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    //线程执行内容
                                    //为手机用户注册
                                    try{
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("phone",myPhone);
                                    }catch (JSONException e){
                                        e.printStackTrace();
                                    }
                                }
                            });
                            //开启线程
                            registerThread.start();

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
                    break;
            }

        }
    };




    //派生的phoneStateListener类
    class MyPhoneStateListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state,String incomingNumber){
            Log.e("PhoneCallState", "Incoming number "+incomingNumber); //incomingNumber就是来电号码

//            myPhone = incomingNumber;
            phoneEditText.setText(incomingNumber);
        }

    }

}
