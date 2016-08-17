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

    }


    public void scan_start_onClick(View v) {
        //打开扫描界面，返回扫描结果
        startActivityForResult(new Intent(MainActivity.this, CaptureActivity.class), 0);
    }

    public void scan_already_onClick(View v){
        startActivity(new Intent(MainActivity.this,NewspaperInfoActivity.class));
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
            }catch (IOException e){
                e.printStackTrace();
            }

            Intent intent = new Intent(MainActivity.this,NewspaperInfoActivity.class);
            //intent.putExtra("decodeResult",data.getStringExtra(CaptureActivity.EXTRA_RESULT));
            startActivity(intent);

            //测试部分
//            resultTv.setText(data.getStringExtra(CaptureActivity.EXTRA_RESULT));
//            resultIv.setImageBitmap((Bitmap)data.getParcelableExtra(CaptureActivity.EXTRA_BITMAP));
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),"扫描异常，请重试",Toast.LENGTH_SHORT);
            toast.show();

        }
    }


}
