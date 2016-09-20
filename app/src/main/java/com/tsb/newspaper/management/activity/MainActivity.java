package com.tsb.newspaper.management.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.tsb.newspaper.management.R;
import com.tsb.newspaper.management.infoSaved.MyInternalStorage;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private final int MY_PERMISSIONS_CAMERA = 0;
    private final int MY_PERMISSIONS_PHONE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)
        {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_PHONE_STATE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_CAMERA);
        }

//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.READ_PHONE_STATE)
//                != PackageManager.PERMISSION_GRANTED)
//        {
//
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.READ_PHONE_STATE},
//                    MY_PERMISSIONS_PHONE);
//        }


    }


    public void scan_start_onClick(View v) {

            //打开扫描界面，返回扫描结果
            startActivityForResult(new Intent(MainActivity.this, CaptureActivity.class), 0);

    }

    public void scan_already_onClick(View v){
        MyInternalStorage myInternalStorage = new MyInternalStorage(MainActivity.this);
        String filename = "myNewspaper";
        String result = "";
        try{
            result = myInternalStorage.get(filename);
        }catch (IOException e){
            e.printStackTrace();
        }

        if(!result.equals("")){
            startActivity(new Intent(MainActivity.this,NewspaperInfoActivity.class));
        }else {
            Toast.makeText(MainActivity.this,"之前未扫描过任何二维码！",Toast.LENGTH_SHORT).show();
        }

    }

    public void printer_activity_onClick(View v){
        startActivity(new Intent(MainActivity.this,PrinterActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {

            MyInternalStorage myInternalStorage = new MyInternalStorage(MainActivity.this);
            String filename = "myNewspaper";
            String content = data.getStringExtra(CaptureActivity.EXTRA_RESULT);

            Log.i("qrcode",content);
            try{
                myInternalStorage.save(filename,content);
//                Toast.makeText(MainActivity.this,"saved sucessfully",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this,NewspaperInfoActivity.class);
                startActivity(intent);
            }catch (IOException e){
                Toast.makeText(MainActivity.this,"信息存储失败，请重试！",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        } else {
            Toast toast = Toast.makeText(getApplicationContext(),"扫描失败，请重试",Toast.LENGTH_SHORT);
            toast.show();

        }
    }


}
