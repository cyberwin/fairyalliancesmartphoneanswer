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
    // ==========================================================

    public String  incomingNumber   = "";
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (!TelephonyManager.ACTION_PHONE_STATE_CHANGED.equals(intent.getAction()))
                return;

            incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

            if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
                answerCall(context);
                handler.postDelayed(() -> playAudioPriority(context), startTime);
            }
        } catch (Exception e) {}
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
            endCall(context);
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
        } catch (Exception e) {}
    }
}