package com.tsb.newspaper.management.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.tsb.newspaper.management.R;
import com.tsb.newspaper.management.encode.EncodeUtil;
import com.tsb.newspaper.management.internet.InternetUtil;
import com.tsb.newspaper.management.printerUtil.PrintService;
import com.tsb.newspaper.management.printerUtil.PrinterClass;
import com.tsb.newspaper.management.printerUtil.PrinterClassSerialPort;

import org.json.JSONException;
import org.json.JSONObject;

public class PrinterActivity extends AppCompatActivity {

    private EditText phoneEditText;

    private ImageView qrcodeImage;
    private Button printerButton;

    private String myPhone;

    private boolean isRegister = false;

    private Bitmap btMap = null;
    public static PrinterClassSerialPort printerClass = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer);

        phoneEditText = (EditText)findViewById(R.id.phone_edit_text1);
        phoneEditText.setInputType(InputType.TYPE_CLASS_PHONE);

        qrcodeImage = (ImageView)findViewById(R.id.qrcode_image);
        printerButton = (Button)findViewById(R.id.printer_qrcode_button);
        qrcodeImage.setVisibility(View.INVISIBLE);
        printerButton.setVisibility(View.INVISIBLE);
        MyPhoneStateListener phoneListener = new MyPhoneStateListener(); //我们派生的类
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);

        printerClass = new PrinterClassSerialPort(printhandler);
        printerClass.open(this);
    }

    public void verification_search_onClick(View v){
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
                        Toast.makeText(PrinterActivity.this,"服务器访问异常",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                }
            });
            //开启线程
            if(new InternetUtil().isNetworkConnected(PrinterActivity.this)){
                verificationThread.start();
            }
            else {
                Toast.makeText(PrinterActivity.this,"网络不可用",Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(PrinterActivity.this,"非法手机号！",Toast.LENGTH_SHORT).show();
        }
    }


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    EncodeUtil encodeUtil = new EncodeUtil();
                    String phoneNum = "";
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("phone_num",myPhone);
                        phoneNum = jsonObject.toString();
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    Bitmap bitmap = encodeUtil.createBitmap(phoneNum,200);
                    btMap = bitmap;
                    qrcodeImage.setImageBitmap(bitmap);
                    qrcodeImage.setVisibility(View.VISIBLE);
                    printerButton.setVisibility(View.VISIBLE);
                    break;

                case 1:
                    AlertDialog.Builder builder = new AlertDialog.Builder(PrinterActivity.this);

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
                                            Toast.makeText(PrinterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                                            Looper.loop();
                                            isRegister = true;
                                        }else {
                                            Looper.prepare();
                                            Toast.makeText(PrinterActivity.this,"服务器访问异常，注册失败",Toast.LENGTH_SHORT).show();
                                            Looper.loop();
                                            isRegister = false;
                                        }

                                    }catch (JSONException e){
                                        e.printStackTrace();
                                    }

                                }
                            });
                            if(new InternetUtil().isNetworkConnected(PrinterActivity.this)){
                                //开启线程
                                registerThread.start();
                            }else {
                                Toast.makeText(PrinterActivity.this,"网络不可用",Toast.LENGTH_SHORT).show();
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

    public void printer_qrcode_onClick(View v){
        if (btMap != null) {
            printerClass.printImage(btMap);
							/*
							 * Message msgMessage = hanler.obtainMessage();
							 * msgMessage.what = 0;
							 * hanler.sendMessage(msgMessage);
							 */
        }
    }

    Handler printhandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PrinterClass.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;

                    if (readBuf[0] == 0x13) {
                        // PrintService.isFUll = true;
                        // ShowMsg(getResources().getString(R.string.str_printer_state)+":"+getResources().getString(R.string.str_printer_bufferfull));
                    } else if (readBuf[0] == 0x11) {
                        // PrintService.isFUll = false;
                        // ShowMsg(getResources().getString(R.string.str_printer_state)+":"+getResources().getString(R.string.str_printer_buffernull));
                    } else if (readBuf[0] == 0x08) {

                    } else if (readBuf[0] == 0x01) {
                        // ShowMsg(getResources().getString(R.string.str_printer_state)+":"+getResources().getString(R.string.str_printer_printing));
                    } else if (readBuf[0] == 0x04) {

                    } else if (readBuf[0] == 0x02) {

                    } else {
                        String readMessage = new String(readBuf, 0, msg.arg1);
                        if (readMessage.contains("800"))// 80mm paper
                        {
                            PrintService.imageWidth = 72;
                            Toast.makeText(getApplicationContext(), "80mm",
                                    Toast.LENGTH_SHORT).show();
                        } else if (readMessage.contains("580"))// 58mm paper
                        {
                            PrintService.imageWidth = 48;
                            Toast.makeText(getApplicationContext(), "58mm",
                                    Toast.LENGTH_SHORT).show();
                        } else {

                        }
                    }
                    break;
                case PrinterClass.MESSAGE_STATE_CHANGE:// 6��l��״
                    switch (msg.arg1) {
                        case PrinterClass.STATE_CONNECTED:// �Ѿ�l��
                            break;
                        case PrinterClass.STATE_CONNECTING:// ����l��
                            Toast.makeText(getApplicationContext(),
                                    "STATE_CONNECTING", Toast.LENGTH_SHORT).show();
                            break;
                        case PrinterClass.STATE_LISTEN:
                        case PrinterClass.STATE_NONE:
                            break;
                        case PrinterClass.SUCCESS_CONNECT:
                            printerClass.write(new byte[] { 0x1b, 0x2b });// ����ӡ���ͺ�
                            Toast.makeText(getApplicationContext(),
                                    "SUCCESS_CONNECT", Toast.LENGTH_SHORT).show();
                            break;
                        case PrinterClass.FAILED_CONNECT:
                            Toast.makeText(getApplicationContext(),
                                    "FAILED_CONNECT", Toast.LENGTH_SHORT).show();

                            break;
                        case PrinterClass.LOSE_CONNECT:
                            Toast.makeText(getApplicationContext(), "LOSE_CONNECT",
                                    Toast.LENGTH_SHORT).show();
                    }
                    break;
                case PrinterClass.MESSAGE_WRITE:

                    break;
            }
            super.handleMessage(msg);
        }
    };

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
//            Log.e("PhoneCallState", "Incoming number "+incomingNumber); //incomingNumber就是来电号码

//            myPhone = incomingNumber;
            phoneEditText.setText(incomingNumber);
        }

    }
}
