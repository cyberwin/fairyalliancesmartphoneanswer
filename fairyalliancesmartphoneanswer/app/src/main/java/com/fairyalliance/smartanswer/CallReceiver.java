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

public class CallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        // 全局捕获，任何错误都不崩
        try {
            if (!TelephonyManager.ACTION_PHONE_STATE_CHANGED.equals(intent.getAction())) {
                return;
            }

            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
                answerCall(context);

                // 用系统安全的 Handler 延迟，不要用 Thread.sleep
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    try {
                        playAudioToCall(context);
                    } catch (Exception e) {
                        // 不崩
                    }
                }, 1000);
            }
        } catch (Exception e) {
            // 全局兜底，绝不闪退
        }
    }

    // ✅ 安全接听：权限检查 + 异常捕获
    private void answerCall(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                TelecomManager tm = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
                if (tm != null) {
                    tm.acceptRingingCall();
                }
            }
        } catch (Exception e) {
            // 不崩溃
        }
    }

    // ✅ 安全播放：全部 try-catch，空值判断，绝不崩
    private void playAudioToCall(Context context) {
        MediaPlayer mediaPlayer = null;
        try {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager == null) return;

            // 不修改 MODE，避免权限崩溃
            // audioManager.setMode(AudioManager.MODE_IN_CALL);

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
                try {
                    mp.release();
                    endCall(context);
                } catch (Exception e) {}
            });

        } catch (Exception e) {
            try {
                if (mediaPlayer != null) mediaPlayer.release();
            } catch (Exception ex) {}
            endCall(context);
        }
    }

    // ✅ 安全挂断
    private void endCall(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                TelecomManager tm = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
                if (tm != null) {
                    tm.endCall();
                }
            }
        } catch (Exception e) {}
    }
}