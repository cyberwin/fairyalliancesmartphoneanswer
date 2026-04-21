package com.fairyalliancesmartanswer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
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

    @Override
    public void onReceive(Context context, Intent intent) {
        
         try {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            
            if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
                // 检查是否有接听权限
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ANSWER_PHONE_CALLS) 
                        == PackageManager.PERMISSION_GRANTED) {
                            
                    writelog("onReceive","jt","收到电话");
                    
                    // 使用TelecomManager接听电话
                    TelecomManager telecomManager = 
                            (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
                    
                    if (telecomManager != null) {
                        telecomManager.acceptRingingCall();
                           writelog("onReceive","jt","已经接听");
                           
                          incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                          
                            writelog("onReceive","jt","来电号码"+incomingNumber);
                          
                          handler.postDelayed(() -> playAudioPriority(context), startTime);
                    }
                }
            }
        } catch (Exception e) {
            // 捕获异常，防止闪退
            e.printStackTrace();
            
             // 记录完整异常堆栈
            String error = e.getStackTrace()+ e.getMessage();
           
            writelog("onReceive", "err",  "异常：" + error);
            
            
        }
    }

    private void answerCall(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ANSWER_PHONE_CALLS)
                        == PackageManager.PERMISSION_GRANTED) {
                    TelecomManager tm = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
                    if (tm != null) tm.acceptRingingCall();
                }
            }
        } catch (Exception e) {}
    }

    // 优先级加载：raw → SD卡 → 默认音频
    private void playAudioPriority(Context context) {
        try {
            // 1. 优先 res/raw
            int resId = context.getResources().getIdentifier(audioFileName, "raw", context.getPackageName());
            if (resId != 0) {
                mediaPlayer = MediaPlayer.create(context, resId);
                if (mediaPlayer != null) {
                    startPlay(context);
                    return;
                }
            }

            // 2. 不存在 → 读取 SD 卡（自动转换真实路径）
            String fullDiskPath = getRealDiskPath() + "/" + audioFileName + ".mp3";
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
                    startPlay(context);
                }
            }
        } catch (Exception e) {
               // 记录完整异常堆栈
            String error = e.getStackTrace()+ e.getMessage();
           
            writelog("playAudioPriority", "err",  "异常：" + error);
            
           // endCall(context);
        }
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

    // 开始播放
    private void startPlay(Context context) {
        if (mediaPlayer == null) return;

        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.setSpeakerphoneOn("both".equals(playVoiceType));

        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(mp -> {
            mp.release();
            if (hangupAfterPlay) endCall(context);
        });
    }

    private void endCall(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                TelecomManager tm = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
                if (tm != null) tm.endCall();
            }
        } catch (Exception e) {
             String error = e.getStackTrace()+ e.getMessage();
           
            writelog("endCall", "err",  "异常：" + error);
        }
    }
    
    // 日志根目录
        private String getLogBasePath() {
            return Environment.getExternalStorageDirectory().getAbsolutePath() + fams_approot+"/log";
        }
        
        // 核心日志方法：writelog(type, name, msg);
        private void writelog(String type, String name, String msg) {
            try {
                return;
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