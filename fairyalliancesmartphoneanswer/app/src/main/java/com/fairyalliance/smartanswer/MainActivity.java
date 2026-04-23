package com.fairyalliance.smartanswer;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


import android.os.Environment;
 

import java.io.File;

import java.io.FileOutputStream;

import android.content.pm.PackageManager;


import androidx.annotation.NonNull;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import CyberWinPHP.Cyber_CPU.Cyber_Public_Var;


 
import android.content.Context;
import android.content.Intent;
 
import android.media.MediaPlayer;
 
 
import android.view.View;
 
 
 
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.media.AudioAttributes;
import android.media.AudioManager;
 
 

public class MainActivity extends AppCompatActivity {

    private static final String[] PERMISSIONS_老的 = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS
    };
    
    // 仅保留 能动态授权、必须授权、不会失效 的权限
    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_PHONE_STATE,    // 必须：检测来电
            Manifest.permission.READ_CONTACTS,       // 必须：读取联系人
            Manifest.permission.WRITE_CONTACTS,      // 可选：修改联系人
            Manifest.permission.READ_EXTERNAL_STORAGE ,// 必须：读取文件/录音
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
        
       private static final int REQUEST_MANAGE_ALL_FILES_PERMISSION = 4;
       
       private static final int PERMISSION_CODE = 100;
       
        // 音频文件名（res/raw 目录下）
    public String audioFileName  = "fams_aa_dc_01";
    public String defaultAudio   = "fams_aa_default";
    public String audioFileName2 = "fams_aa_jyyq_01";
    public String audioFileName3 = "fams_aa_default";
    
    public String audioFileName5 = "fams_aa_dc_03";
    
    

       private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            
             CyberWinLogToFile.init(this);
             
             writelog("MainActivity","jt","启动开始");
             
            setContentView(R.layout.activity_main);
            
              writelog("MainActivity","jt","布局");

            // 申请存储、通讯录权限
            // 检查并申请权限
            /*
        if (!hasPermissions()) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_CODE);
        }
           */

            Toast.makeText(this, "点击屏幕开启无障碍", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            writelog("MainActivity","jt","布局"+ e.getMessage());
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
    
     // 核心日志方法：writelog(type, name, msg);
        private void writelog(String type, String name, String msg) {
            try {
               
               
                // 1. 时间格式化：yyyy-MM-dd HH:mm:ss
              //  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
              //  String time = sdf.format(new Date());
        
                // 2. 日志内容
              //  String logContent = time + " | " + type + " | " + name + " | " + msg + "\n";
                 String logContent = type + " | " + name + " | " + msg + "\n";
                 CyberWinLogToFile.d_windows(type,name,logContent);
              
        
        
            } catch (Exception e) {
                // 不处理，避免崩溃
            }
        }
        
        //2026-04-23
        
    // ---------------------- 音频播放 ----------------------
    public void playDefaultAudio(View view) {
        playRawAudio(defaultAudio);
    }

    public void playAudio2(View view) {
        playRawAudio(audioFileName2);
    }

    public void playAudio3(View view) {
        playRawAudio(audioFileName3);
    }
    
    //
    
    public void playAudio5(View view) {
        playRawAudio(audioFileName5);
    }

    // 播放 raw 下的音频（通用方法）
    private void playRawAudio(String fileName) {
        try {
            // 先停止上一个
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }

            // 获取音频 ID
            int resId = getResources().getIdentifier(fileName, "raw", getPackageName());
            mediaPlayer = MediaPlayer.create(this, resId);
            mediaPlayer.start();
            Toast.makeText(this, "正在播放：" + fileName, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "播放失败：音频文件不存在", Toast.LENGTH_SHORT).show();
        }
    }
    
     public void playAudio2byphone(View view) {
        
         try {
        // 先释放上一个
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        
          AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
           未来之窗_设置接电话AudioManager(am);

        int resId = getResources().getIdentifier(audioFileName2, "raw", getPackageName());
        mediaPlayer = MediaPlayer.create(this, resId);

        // ======================
        // 关键：强制使用 电话通话声道
        // ======================
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION) // 电话通道
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
            );
            
        } else {
           
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL); // 旧版本
        }
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL); // 设置音频流为通话流

        mediaPlayer.start();
        Toast.makeText(this, "电话通道播放：" + audioFileName2, Toast.LENGTH_SHORT).show();

    } catch (Exception e) {
        Toast.makeText(this, "播放失败", Toast.LENGTH_SHORT).show();
    }
    }
    
     private void 未来之窗_设置接电话AudioManager(AudioManager am) {
        am.setMode(AudioManager.MODE_IN_CALL);
        am.setMode(AudioManager.MODE_IN_COMMUNICATION);
        am.setSpeakerphoneOn(true);
    }
    
      // ---------------------- 权限授权 ----------------------
    public void grantStoragePermission(View view) {
        /*
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERM_CODE);
        } else {
            Toast.makeText(this, "文件权限已授权", Toast.LENGTH_SHORT).show();
        }
        */
        仙盟_权限_判断_文件权限();
    }

    public void grantAllPermissions(View view) {
          ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_CODE);
        /*
        if (!hasAllPermissions()) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, ALL_PERM_CODE);
        } else {
            Toast.makeText(this, "全部权限已授权", Toast.LENGTH_SHORT).show();
        }
        */
    }
    
}