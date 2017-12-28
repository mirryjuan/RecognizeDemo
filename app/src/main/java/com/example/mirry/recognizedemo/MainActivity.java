package com.example.mirry.recognizedemo;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mirry.recognizedemo.service.OCRService;
import com.example.mirry.recognizedemo.utils.BitmapUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_camera,btn_album;
    private TextView text;

    private Boolean hasPermission = false;
    private String filePath = "";
    private File file;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initPermission();
        initListener();
    }

    private void initListener() {
        btn_camera.setOnClickListener(this);
        btn_album.setOnClickListener(this);
    }

    private void initView() {
        btn_camera = (Button) findViewById(R.id.btn_camera);
        btn_album = (Button) findViewById(R.id.btn_album);
        text = (TextView) findViewById(R.id.text);
    }

    private void recognize(final String filePath){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示")
                .setMessage("正在识别，请稍后...");
        final AlertDialog dialog = builder.create();
        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String path = BitmapUtil.compressImageUpload(filePath);
                final String result = OCRService.getRecognizeResult(path);
                try {
                    JSONObject json = new JSONObject(result);
                    final JSONArray wordsRes = json.getJSONArray("words_result");
                    final int wordsNum = json.getInt("words_result_num");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String words = "";
                            for(int i = 0; i < wordsNum; i++){
                                try {
                                    dialog.dismiss();
                                    words = wordsRes.getJSONObject(i).getString("words");
                                    text.append(words + "\n");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_camera:
                if(hasPermission){
                    openCamera();
                }else{
                    Toast.makeText(this, "没有权限打开相机", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_album:
                if(hasPermission){
                    openAlbum();
                }else{
                    Toast.makeText(this, "没有权限打开相册", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private void openCamera() {
        text.setText("");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 判断存储卡是否可用，存储照片文件
        if (hasSdcard()) {
            file = new File(Environment.getExternalStorageDirectory()+"/"+
                    getTime()+".jpg");
            imageUri = Uri.fromFile(file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, 1);
        }else{
            Toast.makeText(this, "没有内存卡", Toast.LENGTH_SHORT).show();
        }
    }

    private void openAlbum() {
        text.setText("");
        Intent intent1 = new Intent(Intent.ACTION_PICK, null);
        intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent1, 2);
    }

    private void initPermission(){
        String permissions[] = {
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        ArrayList<String> toApplyList = new ArrayList<String>();
        for (String perm :permissions){
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                //进入到这里代表没有权限.
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,perm)){
                    //已经禁止提示了
                    Toast.makeText(MainActivity.this, "您已禁止该权限，需要重新开启。", Toast.LENGTH_SHORT).show();
                }else{
                    toApplyList.add(perm);
                }
            }
        }

        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()){
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 1);
        }else{
            hasPermission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(grantResults.length >0 &&grantResults[0]==PackageManager.PERMISSION_GRANTED){
            //用户同意授权
            hasPermission = true;
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

    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            // 有存储的SDCard
            return true;
        } else {
            return false;
        }
    }

    private String getTime(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
        Date date = new Date();
        return format.format(date);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:
                if(resultCode == RESULT_OK){
                    filePath = getRealPathFromURI(this, imageUri);
                    recognize(filePath);
                }else{
                    Toast.makeText(this,"图片路径获取失败",Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    imageUri = data.getData();
                    filePath = getRealPathFromURI(this, imageUri);
                    recognize(filePath);
                }else{
                    Toast.makeText(this,"图片路径获取失败",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode,resultCode,data);
    }

    public static String getRealPathFromURI(Context context, Uri contentURI) {
        String result;
        Cursor cursor = context.getContentResolver().query(contentURI,
                new String[]{MediaStore.Images.ImageColumns.DATA},//
                null, null, null);
        if (cursor == null) result = contentURI.getPath();
        else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(index);
            cursor.close();
        }
        return result;
    }
}

