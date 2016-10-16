package com.tsb.newspaper.management.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.tsb.newspaper.management.R;
import com.tsb.newspaper.management.internet.InternetUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dusz2 on 2016/8/16 0016.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText phoneEditText;

    private String myPhone;

    final int RESULT_CODE = 11;

    private boolean isRegister = false;



    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        phoneEditText = (EditText)findViewById(R.id.phone_edit_text);
        phoneEditText.setInputType(InputType.TYPE_CLASS_PHONE);


        MyPhoneStateListener phoneListener = new MyPhoneStateListener(); //我们派生的类
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);

    }

    public void verification_getting_onClick(View v){
        myPhone = phoneEditText.getText().toString();
        if(isMobile(myPhone)){
//            Toast.makeText(LoginActivity.this,"")
            Thread verificationThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    //线程执行内容
                    //判断是否注册

                    //handle传递
                    Message msg = new Message();

                    String url = getResources().getString(R.string.network_url)+"user/"+myPhone+"/";
                    InternetUtil internetUtil = new InternetUtil(url);
                    String registerResult = internetUtil.getUserMethod();
                    if(registerResult != null && registerResult != ""){
                        try{
                            JSONObject jsonObject = new JSONObject(registerResult);
                            isRegister = jsonObject.getBoolean("login_state");

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                        if(isRegister){
                            msg.what = 0;
                            Log.i("isRegister","用户已注册");
                        }else {
                            msg.what = 1;
                            Log.i("isRegister","用户未注册");
                        }
                        handler.sendMessage(msg);
                    }
                    else {
                        Looper.prepare();
                        Toast.makeText(LoginActivity.this,"服务器访问异常",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                }
            });
            //开启线程
            if(new InternetUtil().isNetworkConnected(LoginActivity.this)){
                verificationThread.start();
            }
            else {
                Toast.makeText(LoginActivity.this,"网络不可用",Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(LoginActivity.this,"非法手机号！",Toast.LENGTH_SHORT).show();
        }

    }


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    final Intent intent = new Intent();
                    intent.putExtra("phone",myPhone);
                    intent.putExtra("isLogin",true);
                    setResult(RESULT_CODE,intent);
                    finish();
                    break;

                case 1:
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

                    builder.setTitle("用户未注册");
                    builder.setMessage("如需继续操作，请为用户注册：\n"+myPhone);
                    builder.setPositiveButton("确定注册", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //注册
                            Thread registerThread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    //线程执行内容
                                    //为手机用户注册
                                    try{
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("phone_num",myPhone);
                                        String url = getResources().getString(R.string.network_url)+"user/";
                                        InternetUtil internetUtil = new InternetUtil(url);

                                        String putResult = internetUtil.putUserMethod(jsonObject.toString());
                                        if (putResult == "OK"){
                                            Looper.prepare();
                                            Toast.makeText(LoginActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                                            Looper.loop();
                                            isRegister = true;
                                        }else {
                                            Looper.prepare();
                                            Toast.makeText(LoginActivity.this,"服务器访问异常，注册失败",Toast.LENGTH_SHORT).show();
                                            Looper.loop();
                                            isRegister = false;
                                        }

                                    }catch (JSONException e){
                                        e.printStackTrace();
                                    }

                                }
                            });
                            if(new InternetUtil().isNetworkConnected(LoginActivity.this)){
                                //开启线程
                                registerThread.start();
                            }else {
                                Toast.makeText(LoginActivity.this,"网络不可用",Toast.LENGTH_SHORT).show();
                            }

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

    public void scan_phonenum_onClick(View v){
        startActivityForResult(new Intent(LoginActivity.this, CaptureActivity.class), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {

            String content = data.getStringExtra(CaptureActivity.EXTRA_RESULT);
            String scanPhone = "";

            Log.i("phoneNum_qrcode",content);
            try {
                JSONObject jsonObject = new JSONObject(content);
                scanPhone = jsonObject.getString("phone_num");
            }catch (JSONException e){
                e.printStackTrace();
            }

            phoneEditText.setText(scanPhone);

        } else {
            Toast toast = Toast.makeText(getApplicationContext(),"扫描失败，请重试",Toast.LENGTH_SHORT);
            toast.show();

        }
    }

    /**
     * 验证手机格式
     */
    public static boolean isMobile(String number) {
    /*
    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、180、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
        //第二位可以为7
        String num = "[1]\\d{10}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(number)) {
            return false;
        } else {
            //matches():字符串是否在给定的正则表达式匹配
            return number.matches(num);
        }
    }

    //派生的phoneStateListener类
    class MyPhoneStateListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state,String incomingNumber){

            if(state == TelephonyManager.CALL_STATE_RINGING){
                phoneEditText.setText(incomingNumber);
            }
        }

    }

}
