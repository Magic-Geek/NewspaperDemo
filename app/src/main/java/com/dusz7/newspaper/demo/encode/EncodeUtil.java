package com.dusz7.newspaper.demo.encode;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * Created by dusz2 on 2016/8/12 0012.
 */
public class EncodeUtil {

    private String text;
    private int size;

    public EncodeUtil(){
    }

    public Bitmap createBitmap(String text,int size){
        this.text = text;
        this.size = size;

        try{

            if(text == null || "".equals(text) || text.length() < 1){
                return null;
            }

            BitMatrix bitMatrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE,size,size);
            int[] pixels = new int[size*size];

            for(int y = 0; y < size; y++){
                for(int x = 0; x < size; x++){
                    if (bitMatrix.get(x,y)){
                        pixels[y * size + x] = 0xff000000;
                    }else{
                        pixels[y * size + x] = 0xffffffff;
                    }
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(size,size,Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels,0,size,0,0,size,size);

            return bitmap;

        }catch (WriterException e){
            e.printStackTrace();

            return null;
        }
    }
}
