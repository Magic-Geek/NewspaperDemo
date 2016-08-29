package com.dusz7.newspaper.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.dusz7.newspaper.demo.R;
import com.dusz7.newspaper.demo.infoSaved.MyInternalStorage;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //应该设置成只在第一次程序运行时执行
//        MyInternalStorage myInternalStorage = new MyInternalStorage(MainActivity.this);
//        String filename = "myNewspaper";
//        String content = "";
//        try{
//            myInternalStorage.save(filename,content);
//        }catch (IOException e){
//            e.printStackTrace();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {

            MyInternalStorage myInternalStorage = new MyInternalStorage(MainActivity.this);
            String filename = "myNewspaper";
            String content = data.getStringExtra(CaptureActivity.EXTRA_RESULT);
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
            Toast toast = Toast.makeText(getApplicationContext(),"扫描异常，请重试",Toast.LENGTH_SHORT);
            toast.show();

        }
    }


}
