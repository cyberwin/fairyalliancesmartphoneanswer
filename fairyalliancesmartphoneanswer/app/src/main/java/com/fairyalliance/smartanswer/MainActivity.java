package com.fairyalliance.smartanswer;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS
    };
    
       private static final int REQUEST_MANAGE_ALL_FILES_PERMISSION = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_main);

            // 申请存储、通讯录权限
            // 检查并申请权限
        if (!hasPermissions()) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_CODE);
        }
           

            Toast.makeText(this, "点击屏幕开启无障碍", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            // 绝不闪退
           // android.widget.TextView tv = new android.widget.TextView(this);
           // tv.setText("点击开启无障碍");
           // tv.setPadding(50,50,50,50);
           // setContentView(tv);
        }
    }
    
    //2026-04-22
      /**
     * 检查是否拥有所有权限
     */
    private boolean hasPermissions() {
        for (String p : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 【必须重写】权限申请结果回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                Toast.makeText(this, "权限已全部授权", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "请授权所有权限才能使用", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
     /**
     * 【可选但推荐重写】页面返回时刷新
     */
    @Override
    protected void onResume() {
        super.onResume();
    }
    
   
    private  void 仙盟_权限_判断_文件权限() {
        // 检查系统版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 检查是否已经拥有管理所有文件的权限
            if (!android.os.Environment.isExternalStorageManager()) {
                // 请求管理所有文件的权限
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, REQUEST_MANAGE_ALL_FILES_PERMISSION);
            } else {
                Toast.makeText(this, "已经拥有管理所有文件的权限", Toast.LENGTH_SHORT).show();
            }
        } else {
            // 对于 Android 10 及以下版本，不需要该权限
            Toast.makeText(this, "此设备不需要管理所有文件的权限", Toast.LENGTH_SHORT).show();
        }
    }
    
      private void 仙盟_加载_判断文件夹(){
        try {
            // 创建 File 对象
            String filePath = "/cyberwin/归一编程/1.html";
            String filePath1 = "/cyberwin";

            File file = new File(Environment.getExternalStorageDirectory(), filePath1);

            if (!file.exists()) {
                file.mkdirs();
            }

            String filePath2 = "/cyberwin/归一编程";
            File file2 = new File(Environment.getExternalStorageDirectory(), filePath2);
            if (!file2.exists()) {
                file2.mkdirs();
            }


        }catch (Exception e) {

            Toast.makeText(this, "文件写入失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
     @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MANAGE_ALL_FILES_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (android.os.Environment.isExternalStorageManager()) {
                    Toast.makeText(this, "已成功获取管理所有文件的权限", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "未获取到管理所有文件的权限", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}