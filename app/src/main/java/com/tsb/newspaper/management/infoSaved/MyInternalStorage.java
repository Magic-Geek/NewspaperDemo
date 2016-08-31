package com.tsb.newspaper.management.infoSaved;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by dusz2 on 2016/8/17 0017.
 */
public class MyInternalStorage {
    //需要保存当前调用对象的Context
         private Context context;

                 public MyInternalStorage(Context context) {
                 this.context = context;
             }
         /**
           * 保存内容到内部存储器中
           * @param filename 文件名
           * @param content 内容
           */
                 public void save(String filename, String content) throws IOException {
                 // FileOutputStream fos=context.openFileOutput(filename,
                 // Context.MODE_PRIVATE);
                 File file = new File(context.getFilesDir(), filename);
                 FileOutputStream fos = new FileOutputStream(file);

                 fos.write(content.getBytes());
                 fos.close();
             }
         /**
           *  通过文件名获取内容
           * @param filename 文件名
           * @return 文件内容
           */
                 public String get(String filename) throws IOException {
                 FileInputStream fis = context.openFileInput(filename);
                 ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 byte[] data = new byte[1024];
                 int len = -1;
                 while ((len = fis.read(data)) != -1) {
                         baos.write(data, 0, len);
                     }
                 return new String(baos.toByteArray());
             }
         /**
           * 以追加的方式在文件的末尾添加内容
           * @param filename 文件名
           * @param content 追加的内容
           */
                 public void append(String filename, String content) throws IOException {
                 FileOutputStream fos = context.openFileOutput(filename,
                                 Context.MODE_APPEND);
                 fos.write(content.getBytes());
                 fos.close();
             }
         /**
           * 删除文件
           * @param filename 文件名
           * @return 是否成功
           */
                 public boolean delete(String filename) {
                 return context.deleteFile(filename);
             }
         /**
           * 获取内部存储路径下的所有文件名
           * @return 文件名数组
           */
                 public String[] queryAllFile() {
                 return context.fileList();
             }
}
