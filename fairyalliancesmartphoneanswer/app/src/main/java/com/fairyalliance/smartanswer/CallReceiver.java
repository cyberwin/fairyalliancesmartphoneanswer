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

import java.net.URL;
import java.net.HttpURLConnection;
 
 
 
 
public class CallReceiver extends BroadcastReceiver {
    
     // ===================== 【参数配置】 =====================
    public String  playVoiceType    ="both";// "onlyOther";       // both / onlyOther
    public int     startTime        =10;// 1000;              // 延时毫秒
    public boolean hangupAfterPlay  = true;              // 播完挂断

    // 音频文件名（不带后缀）
    public String  audioFileName    = "fams_aa_jyyq_01";

    // 默认兜底音频（res/raw 下）
    public String  defaultAudio     = "fams_aa_default";
    
    //fams_aa_default.mp3  fams_aa_jyyq_01.mp3

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
                                     
                                     pushCallNumAndStartPlay(incomingNumber);
                                      
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
           // e.printStackTrace();
            
               writelog("error", "onReceive",  "异常：" + e.getMessage());
        }
    }
 
   
 
   
 
    private void endCall(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                TelecomManager tm = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
                if (tm != null) tm.endCall();
            }
        } catch (Exception e) {
              writelog("error", "endCall",  "异常：" + e.getMessage());
        }
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
           
            writelog("error", "playAudioPriority",  "异常：" + error);
            
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
            } catch (Exception e) {
                 writelog("error", "startPlay",  "异常：" + e.getMessage());
            }
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
        
         writelog("error", "appendPhoneLog",  "异常：" +"filePath="+filePath+","+ e.getMessage());
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
           
             writelog("error", "releaseAudioResources",  "异常：" + e.getMessage());
        }
    }
    
    private void 未来之窗_设置接电话MediaPlayer(MediaPlayer mp) {
       // mp.setMode(AudioManager.MODE_IN_CALL);
         // mp.setContentType(AudioAttributes.CONTENT_TYPE_SPEECH);
        //  mp.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
        //  mp.setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION);
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        // 5.0以上：打包成 AudioAttributes 一起设置
                 writelog("未来之窗vers", "版本", "5.0以上");
                mp.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
                mp.setAudioAttributes(
                    new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                        .build()
                );
            } else {
                // 5.0以下：老方法
                 writelog("未来之窗vers", "版本", "5.0以下：老方法");
                mp.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
            }
    }
    
    private void 未来之窗_设置接电话AudioManager(AudioManager am) {
        am.setMode(AudioManager.MODE_IN_CALL);
        am.setSpeakerphoneOn(true);
    }
    
    ////2026-04-23
    //独立封装来电推送触发播放共用方法，来电监听器解析出来电号码后直接调用本方法即可
    private void pushCallNumAndStartPlay( String callPhone){
        //单独子线程执行网络推送，绝不占用来电广播主线程
        new Thread(() -> {
            try {
                //规整本地标准时间
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String nowTime = dateFormat.format(System.currentTimeMillis());
                //替换填写你本地电脑内网固定IP+你搭建好的接口路径，例http://192.168.1.105:8080/callnotify
                String localHttpApi = "http://51.onelink.ynwlzc.net/o2o/wap.php?g=Wap&c=FAMS_smartanswer&a=fastgo&action=phonecalllog";
    
                //原生GET请求携带来电号码+时间两个参数推送到本地服务
                URL url = new URL(localHttpApi + "&phone="+callPhone+"&client_time="+nowTime+"&mer_id=77&store_id=72&eco_type=phonecall");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(3000);
                int code = conn.getResponseCode();
                writelog("本地推送","成功","号码"+callPhone+"时间"+nowTime+"已推送本地服务，返回状态码"+code);
                conn.disconnect();
            } catch (Exception e) {
                writelog("本地推送","失败","推送本地服务异常："+e.getMessage());
            }
        }).start();
    
        //推送动作同步并行，本机直接独立触发你原有全套合规音频播放逻辑
        //playAudioPriority(context);
    }

}