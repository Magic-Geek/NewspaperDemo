package com.tsb.newspaper.management.internet;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


//{"jou_id":"154","sub_jou_id":"124","name":"6039日报","pub_date":"2014-01-01"}
/**
 * Created by dusz2 on 2016/8/12 0012.
 */
public class InternetUtil {
    private String urlDate;

    private HttpURLConnection conn;
    private URL url;

    public InternetUtil(){

    }

    public InternetUtil(String urlDate){
        this.urlDate = urlDate;
    }

    public boolean isNetworkConnected(Context context){
        if(context != null){
            ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if(networkInfo != null){
                return networkInfo.isAvailable();
            }
        }
        return false;
    }

    //验证用户是否注册
    public String getUserMethod(){
        String state = "";
        try {
            //封装访问服务器的地址
            url=new URL(urlDate);
            try {
                //打开对服务器的连接
                conn=(HttpURLConnection) url.openConnection();

                conn.setConnectTimeout(5000);           //设置连接超时时间
                conn.setDoInput(true);                  //打开输入流，以便从服务器获取数据
                conn.setRequestMethod("GET");
                try {

                    int response = conn.getResponseCode();            //获得服务器的响应码

                    if(response == HttpURLConnection.HTTP_OK) {

                        Log.i("response","getuser"+response);//处理服务器的响应结果

                        /**读入服务器数据的过程**/
                        //得到输入流
                        InputStream is=conn.getInputStream();
                        //创建包装流
                        BufferedReader br=new BufferedReader(new InputStreamReader(is));
                        //定义String类型用于储存单行数据
                        String line=null;
                        //创建StringBuffer对象用于存储所有数据
                        StringBuffer sb=new StringBuffer();
                        while((line=br.readLine())!=null){
                            sb.append(line);
                        }

                        Log.i("get_responsed:",sb.toString());
                        state = sb.toString();
                    }

                }finally {
                    conn.disconnect();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return state;
    }

    //添加新用户（为用户注册）
    public String putUserMethod(String user){

        String state = "";

        try {
            try {
                JSONObject jsonTest = new JSONObject(user);

                //封装访问服务器的地址
                url=new URL(urlDate);
                try {
                    //打开对服务器的连接
                    conn=(HttpURLConnection) url.openConnection();

                    conn.setConnectTimeout(3000);           //设置连接超时时间
                    conn.setDoOutput(true);                 //打开输出流，以便向服务器提交数据
                    conn.setRequestMethod("PUT");
                    conn.setUseCaches(false);

                    byte[] data = jsonTest.toString().getBytes();

                    //设置请求体的类型是文本类型
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setRequestProperty("Charset", "UTF-8");
                    //设置请求体的长度
                    conn.setRequestProperty("Content-Length", String.valueOf(data.length));
                    //获得输出流，向服务器写入数据
                    OutputStream outputStream = conn.getOutputStream();

                    DataOutputStream dos = new DataOutputStream(outputStream);
                    dos.write(data);

                    dos.flush();
                    dos.close();

                    int response = conn.getResponseCode();            //获得服务器的响应码
                    Log.i("response","adduser"+response);//处理服务器的响应结果

                    if(response == HttpURLConnection.HTTP_OK) {

                        state = "OK";

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }catch (JSONException e){
                e.printStackTrace();
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return state;
    }

    //验证是否已经领取
    public String getRecordMethod(){
        String state = "";
        try {
            //封装访问服务器的地址
            url=new URL(urlDate);
            try {

                //打开对服务器的连接
                conn=(HttpURLConnection) url.openConnection();

                conn.setConnectTimeout(3000);           //设置连接超时时间
                conn.setDoInput(true);                  //打开输入流，以便从服务器获取数据
                conn.setRequestMethod("GET");

                int response = conn.getResponseCode();            //获得服务器的响应码
                Log.i("response","getrecord"+response);          //处理服务器的响应结果

                if(response == HttpURLConnection.HTTP_OK) {

                    /**读入服务器数据的过程**/
                    //得到输入流
                    InputStream is=conn.getInputStream();
                    //创建包装流
                    BufferedReader br=new BufferedReader(new InputStreamReader(is));
                    //定义String类型用于储存单行数据
                    String line=null;
                    //创建StringBuffer对象用于存储所有数据
                    StringBuffer sb=new StringBuffer();
                    while((line=br.readLine())!=null){
                        sb.append(line);
                    }

                    Log.i("get_responsed:",sb.toString());

                    state = sb.toString();

                }

            } catch (IOException e) {
                e.printStackTrace();
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return state;
    }

    //添加领取记录（领取成功后）
    public String putRecordMethod(String record){

        String state = "";

        try {
            try {
                JSONObject jsonTest = new JSONObject(record);

                //封装访问服务器的地址
                url=new URL(urlDate);
                try {
                    //打开对服务器的连接
                    conn=(HttpURLConnection) url.openConnection();

                    conn.setConnectTimeout(3000);           //设置连接超时时间
                    conn.setDoOutput(true);                 //打开输出流，以便向服务器提交数据
                    conn.setRequestMethod("PUT");
                    conn.setUseCaches(false);

                    byte[] data = jsonTest.toString().getBytes();

                    //设置请求体的类型是文本类型
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setRequestProperty("Charset", "UTF-8");
                    //设置请求体的长度
                    conn.setRequestProperty("Content-Length", String.valueOf(data.length));
                    //获得输出流，向服务器写入数据
                    OutputStream outputStream = conn.getOutputStream();

                    DataOutputStream dos = new DataOutputStream(outputStream);
                    dos.write(data);

                    dos.flush();
                    dos.close();

                    int response = conn.getResponseCode();            //获得服务器的响应码
                    Log.i("response","addrecord"+response);//处理服务器的响应结果

                    if(response == HttpURLConnection.HTTP_OK) {

                        state = "OK";


                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }catch (JSONException e){
                e.printStackTrace();
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return state;
    }


}
