package com.example.mirry.recognizedemo;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.mirry.recognizedemo.service.OCRService;
import com.example.mirry.recognizedemo.utils.BitmapUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private String filePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        filePath = Environment.getExternalStorageDirectory().getPath() + "/IMG20171211094219.jpg";

        //权限检查
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //进入到这里代表没有权限.
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                //已经禁止提示了
                Toast.makeText(MainActivity.this, "您已禁止该权限，需要重新开启。", Toast.LENGTH_SHORT).show();
            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        } else {
            recognize(filePath);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(grantResults.length >0 &&grantResults[0]==PackageManager.PERMISSION_GRANTED){
            //用户同意授权
            recognize(filePath);
        }else{
            //用户拒绝授权
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");

            String pkg = "com.android.settings";
            String cls = "com.android.settings.applications.InstalledAppDetails";

            intent.setComponent(new ComponentName(pkg, cls));
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }
    }

    private void recognize(final String filePath){
        new Thread(new Runnable() {
            @Override
            public void run() {
//                byte[] bytes = getBitmapFromAssets("pic1.jpg");
                String path = BitmapUtil.compressImageUpload(filePath);
                final String result = OCRService.getRecognizeResult(path);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).start();
    }

    private byte[] getBitmapFromAssets(String fileName){
        try {
            AssetManager manager = getResources().getAssets();
            InputStream is = manager.open(fileName);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            Bitmap bm = compressionImg(bitmap);
            is.close();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] datas = baos.toByteArray();
            return datas;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Bitmap compressionImg(Bitmap bitmap){
        Bitmap bm = null;
        Matrix matrix = new Matrix();
        matrix.setScale(0.5f, 0.5f);
        bm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        return bm;
    }
}

