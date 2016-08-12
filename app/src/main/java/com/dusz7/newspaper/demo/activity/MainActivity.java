package com.dusz7.newspaper.demo.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dusz7.newspaper.demo.R;
import com.dusz7.newspaper.demo.encode.EncodeUtil;
import com.dusz7.newspaper.demo.internet.InternetUtil;

/**
 *
 */
public class MainActivity extends Activity implements Runnable{

    private TextView resultTv;
    private ImageView resultIv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resultTv = (TextView) findViewById(R.id.tv_result);
        resultIv = (ImageView)findViewById(R.id.iv_result);

        Button b=(Button)findViewById(R.id.button_internet);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread t = new Thread(MainActivity.this);
                //启动线程
                t.start();
            }
        });

        Button encodeBu = (Button)findViewById(R.id.button_encode);
        encodeBu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int size = 500;
                String text = "nihaoa wobuhao";
                EncodeUtil encodeUtil = new EncodeUtil();
                Bitmap bitmap = encodeUtil.createBitmap(text,size);
                resultIv.setImageBitmap(bitmap);

            }
        });
    }


    public void scan_onClick(View v) {
        startActivityForResult(new Intent(MainActivity.this, CaptureActivity.class), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {

            Intent intent = new Intent(MainActivity.this,NewspaperActivity.class);
            intent.putExtra("decodeResult",data.getStringExtra(CaptureActivity.EXTRA_RESULT));
            startActivity(intent);

            //测试部分
            resultTv.setText(data.getStringExtra(CaptureActivity.EXTRA_RESULT));
            resultIv.setImageBitmap((Bitmap)data.getParcelableExtra(CaptureActivity.EXTRA_BITMAP));
        } else {
            resultTv.setText("");
            resultIv.setImageDrawable(null);
        }
    }

    public void internet_test(){
        InternetUtil internetUtil = new InternetUtil("http://");
        internetUtil.getMethod();
        internetUtil.putMethod();
    }




    @Override
    public void run() {
        //执行线程操作
        internet_test();
    }
}
