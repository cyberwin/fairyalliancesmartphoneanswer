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




import android.os.Environment;
 

import java.io.File;

import java.io.FileOutputStream;

// 下面这4个是你必须补的
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.FileWriter;
import java.util.Locale;

import CyberWinPHP.Cyber_CPU.Cyber_Public_Var;
 
public class CallReceiver extends BroadcastReceiver {
    
     // ===================== 【参数配置】 =====================
    public String  playVoiceType    ="both";// "onlyOther";       // both / onlyOther
    public int     startTime        =10;// 1000;              // 延时毫秒
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
    
    public String  fams_config     = "%CyberWinPHP_root%/cyberwin/fams/famsautoanswer/config.txt"; 
    public String  fams_phonelog     = "%CyberWinPHP_root%/cyberwin/fams/famsautoanswer/phonelog.txt"; 

    public String  incomingNumber   = "";
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler(Looper.getMainLooper());
 
    
     
    
     @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            
              CyberWinLogToFile.init(context);
              
              
              
               // case TelephonyManager.EXTRA_STATE_RINGING://TelephonyManager.CALL_STATE_RINGING:
                if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) 
                    // 来电响铃状态
                    {
                         writelog("onReceive","jt","来电响铃状态");
                         // 使用TelecomManager接听电话
                           TelecomManager telecomManager = 
                            (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
                             if (telecomManager != null) {
                                     writelog("onReceive","jt","已经接听");
                                    telecomManager.acceptRingingCall();
                                    
                                     incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                                      
                                      writelog("onReceive","jt","来电号码"+incomingNumber);
                                      
                                     String fams_phonelognow=  Cyber_Public_Var.getCyberWinPath(fams_phonelog,context);
                                      
                                     appendPhoneLog(fams_phonelognow,incomingNumber);
                                      
                                  //   handler.postDelayed(() -> playAudioPriority(context), startTime);
                                    
                                }
                    }
                    
                    
                    
               // case TelephonyManager.EXTRA_STATE_OFFHOOK://TelephonyManager.CALL_STATE_OFFHOOK:
                if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)) 
                    // 通话接通状态
                    {
                         writelog("onReceive","jt","通话接通状态");
                          handler.postDelayed(() -> playAudioPriority(context), startTime);
                    }
                    
                  
                    
              //  case TelephonyManager.EXTRA_STATE_IDLE://TelephonyManager.CALL_STATE_IDLE:
                    // 通话结束状态
                     if (TelephonyManager.EXTRA_STATE_IDLE.equals(state)) {
                         writelog("onReceive","jt","通话结束状态");
                          releaseAudioResources();
                    }
                    
                    
                    
              
          
        } catch (Exception e) {
            // 捕获异常，防止闪退
            e.printStackTrace();
             writelog("onReceive","err",e.getMessage());
        }
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
              writelog("playAudioToCall","err",e.getMessage());
           // endCall(context);
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
            return Environment.getExternalStorageDirectory().getAbsolutePath() + fams_approot+"/log/";
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
        
        
    // 优先级加载：raw → SD卡 → 默认音频
    private void playAudioPriority(Context context) {
        try {
            // 1. 优先 res/raw
            int resId = context.getResources().getIdentifier(audioFileName, "raw", context.getPackageName());
            
             writelog("playAudioPriority","jt","音频："+" audioFileName "+audioFileName+" rid="+resId);
              
            if (resId != 0) {
                mediaPlayer = MediaPlayer.create(context, resId);
                if (mediaPlayer != null) {
                     writelog("playAudioPriority","jt","startPlay："+" audioFileName "+audioFileName +"rid="+resId);
                    //  setVoiceChannel(mediaPlayer);
                    未来之窗_设置接电话MediaPlayer(mediaPlayer);
                    startPlay(context);
                    return;
                }
            }

            // 2. 不存在 → 读取 SD 卡（自动转换真实路径）
            String fullDiskPath = getRealDiskPath() + "/" + audioFileName + ".wav";
            File file = new File(fullDiskPath);
            if (file.exists()) {
                mediaPlayer = new MediaPlayer();
                setVoiceChannel(mediaPlayer);
                mediaPlayer.setDataSource(fullDiskPath);
                mediaPlayer.prepare();
                startPlay(context);
                return;
            }

            // 3. 都不存在 → 默认音频
            int defaultResId = context.getResources().getIdentifier(defaultAudio, "raw", context.getPackageName());
            if (defaultResId != 0) {
                mediaPlayer = MediaPlayer.create(context, defaultResId);
                if (mediaPlayer != null) {
                     setVoiceChannel(mediaPlayer);
                    startPlay(context);
                }
            }
        } catch (Exception e) {
               // 记录完整异常堆栈
             // String error = e.getStackTrace()+ e.getMessage();
              String error = e.getMessage();
           
            writelog("playAudioPriority", "err",  "异常：" + error);
            
           // endCall(context);
        }
    }
       // 开始播放
    private void startPlay(Context context) {
        if (mediaPlayer == null) return;
        
        writelog("startPlay","jt","开始："+" audioFileName "+audioFileName);

        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        
        // 1. 先切到电话音频模式（关键！）
        am.setMode(AudioManager.MODE_IN_COMMUNICATION);

        am.setMicrophoneMute(false); // = 让麦克风【不静音】= 打开麦克风
        
        am.setSpeakerphoneOn("both".equals(playVoiceType));

         writelog("startPlay","jt","开始："+" 模式 "+playVoiceType);
         
          mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
          
         未来之窗_设置接电话AudioManager(am);

        mediaPlayer.start();
        
         writelog("startPlay","jt","已经开始");
         
        
         /*
        mediaPlayer.setOnCompletionListener(mp -> {
            mp.release();
            if (hangupAfterPlay) endCall(context);
        });
        */
        mediaPlayer.setOnCompletionListener(mp -> {
            try {
                // 只有真正播放完成才挂断
                if (mp.getCurrentPosition() >= mp.getDuration() - 100) {
                    if (hangupAfterPlay) {
                        endCall(context);
                    }
                }
                mp.release();
            } catch (Exception e) {}
        });
    }
     // ===================== 【关键：自动转换真实磁盘路径】 =====================
    private String getRealDiskPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + diskBasePath;
    }
     // 通话语音通道（通话不静音）
    private void setVoiceChannel(MediaPlayer mp) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mp.setAudioAttributes(
                new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                    .build()
            );
        } else {
            mp.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
        }
    }
   //写入普通记录 
   private void appendPhoneLog(String filePath, String content) {
    try {
            File file = new File(filePath);
            // 自动创建不存在的文件
            if (!file.exists()) {
                file.createNewFile();
            }
            // 追加 + 换行
            FileOutputStream fos = new FileOutputStream(file, true);
            fos.write((content + "\n").getBytes());
            fos.flush();
            fos.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
    
    
   }
   
   // 释放音频资源（你缺失的方法）
    private void releaseAudioResources() {
        try {
            // 1. 移除所有延迟播放任务
            handler.removeCallbacksAndMessages(null);
    
            // 2. 释放 MediaPlayer
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
                mediaPlayer = null;
            }
    
            writelog("releaseAudioResources", "jt", "音频资源已释放");
    
        } catch (Exception e) {
            writelog("releaseAudioResources", "err", e.getMessage());
        }
    }
    
    private void 未来之窗_设置接电话MediaPlayer(MediaPlayer mp) {
       // mp.setMode(AudioManager.MODE_IN_CALL);
          mp.setContentType(AudioAttributes.CONTENT_TYPE_SPEECH);
          mp.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
          mp.setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
    }
    
    private void 未来之窗_设置接电话AudioManager(AudioManager am) {
        am.setMode(AudioManager.MODE_IN_CALL);
        am.setSpeakerphoneOn(true);
    }

}