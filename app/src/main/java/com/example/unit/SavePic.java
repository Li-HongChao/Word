package com.example.unit;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SavePic {

    private static SimpleDateFormat dateFormat;

    public static boolean SavaImage(Context context,Bitmap bitmap, String path){
        File file=new File(path);
        FileOutputStream fileOutputStream;
        Date date = new Date();
        //文件夹不存在，则创建它
        if(!file.exists()){
            file.mkdir();
        }
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                dateFormat = new SimpleDateFormat("YYYY-MM-dd");
                path =path+"/"+dateFormat.format(date)+".png";
            }
            fileOutputStream=new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100,fileOutputStream);
            fileOutputStream.close();
            //如果保存成功就返回true
            if (file.exists()) {
                scanFile(context,path);
                return true;
            }
        } catch (Exception e) {
            System.out.println(path);
            e.printStackTrace();
        }
        return false;
    }

    public static void scanFile(Context context, String filePath) {
        try {
            System.out.println("媒体库已更新");
            MediaScannerConnection.scanFile(context, new String[]{filePath}, null, null);} catch (Exception e) {
            e.printStackTrace();
        }
    }
}
