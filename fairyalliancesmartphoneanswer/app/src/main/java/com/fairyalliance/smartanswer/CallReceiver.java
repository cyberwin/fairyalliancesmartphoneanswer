package com.fairyalliance.smartanswer;
 
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.os.Handler;
import android.os.Looper;


 
// 必须加这个 import！解决 package Manifest does not exist
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;





 

import java.io.File;

// 下面这4个是你必须补的
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.FileWriter;
import java.util.Locale;
 
public class CallReceiver extends BroadcastReceiver {
    
     // ===================== 【参数配置】 =====================
    public String  playVoiceType    = "onlyOther";       // both / onlyOther
    public int     startTime        = 1000;              // 延时毫秒
    public boolean hangupAfterPlay  = true;              // 播完挂断

    // 音频文件名（不带后缀）
    public String  audioFileName    = "wlzc_new_order";

    // 默认兜底音频（res/raw 下）
    public String  defaultAudio     = "wlzc_new_order";

    // ===================== 【磁盘路径变量】 =====================
    // 你在这里写相对路径，代码会自动转成真实路径
    public String  diskBasePath     = "cyberwin/fams/famsautoanswer/audio";
    
   public String  fams_approot     = "/cyberwin/fams/famsautoanswer/";
    // ==========================================================

    public String  incomingNumber   = "";
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler(Looper.getMainLooper());
 
    
    public void wlzcdobaoonReceive(Context context, Intent intent) {
        try {
            if (!TelephonyManager.ACTION_PHONE_STATE_CHANGED.equals(intent.getAction())) return;
 
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
                answerCall(context);
 
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    try {
                        playAudioToCall(context);
                    } catch (Exception e) {}
                }, 1000);
            }
        } catch (Exception e) {}
    }
    
     @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            
            if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
                // 检查是否有接听权限
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ANSWER_PHONE_CALLS) 
                        == PackageManager.PERMISSION_GRANTED) {
                    
                    // 使用TelecomManager接听电话
                    TelecomManager telecomManager = 
                            (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
                    
                    if (telecomManager != null) {
                        telecomManager.acceptRingingCall();
                    }
                }
            }
        } catch (Exception e) {
            // 捕获异常，防止闪退
            e.printStackTrace();
        }
    }
 
    private void answerCall(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                TelecomManager tm = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
                if (tm != null) tm.acceptRingingCall();
            }
        } catch (Exception e) {}
    }
 
    private void playAudioToCall(Context context) {
        MediaPlayer mediaPlayer = null;
        try {
            mediaPlayer = MediaPlayer.create(context, R.raw.wlzc_new_order);
            if (mediaPlayer == null) {
                endCall(context);
                return;
            }
 
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mediaPlayer.setAudioAttributes(
                        new AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                                .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                                .build()
                );
            } else {
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
            }
 
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(mp -> {
                mp.release();
                endCall(context);
            });
 
        } catch (Exception e) {
            try { if (mediaPlayer != null) mediaPlayer.release(); } catch (Exception ex) {}
            endCall(context);
        }
    }
 
    private void endCall(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                TelecomManager tm = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
                if (tm != null) tm.endCall();
            }
        } catch (Exception e) {}
    }
    
     // 日志根目录
        private String getLogBasePath() {
            return Environment.getExternalStorageDirectory().getAbsolutePath() + fams_approot+"/log";
        }
        
        // 核心日志方法：writelog(type, name, msg);
        private void writelog(String type, String name, String msg) {
            try {
               if(1==1){
                   return;
               }
                // 1. 时间格式化：yyyy-MM-dd HH:mm:ss
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String time = sdf.format(new Date());
        
                // 2. 日志内容
                String logContent = time + " | " + type + " | " + name + " | " + msg + "\n";
        
                // 3. 文件路径：log/ + type + name + .txt
                File dir = new File(getLogBasePath());
                if (!dir.exists()) dir.mkdirs(); // 自动创建文件夹
        
                File logFile = new File(dir, type + name + ".txt");
        
                // 4. 写入（追加模式，不会覆盖）
                FileWriter fos = new FileWriter(logFile, true);
                fos.write(logContent);
                fos.flush();
                fos.close();
        
            } catch (Exception e) {
                // 不处理，避免崩溃
            }
        }
}