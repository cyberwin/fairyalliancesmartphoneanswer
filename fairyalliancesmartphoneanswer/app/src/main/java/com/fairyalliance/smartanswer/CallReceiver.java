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
}