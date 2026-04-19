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

import java.lang.reflect.Method;

public class CallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!TelephonyManager.ACTION_PHONE_STATE_CHANGED.equals(intent.getAction())) {
            return;
        }

        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
            answerCall(context);

            // 延迟一点播放，确保电话已接通
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    playAudioToCall(context);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    /**
     * 自动接听
     */
    private void answerCall(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                TelecomManager tm = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
                tm.acceptRingingCall();
            } else {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                Method m = tm.getClass().getDeclaredMethod("getITelephony");
                m.setAccessible(true);
                Object iTelephony = m.invoke(tm);
                Method answer = iTelephony.getClass().getDeclaredMethod("answerRingingCall");
                answer.invoke(iTelephony);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放MP3到通话通道（对方能听见）
     */
    private void playAudioToCall(Context context) {
        try {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            audioManager.setSpeakerphoneOn(false);

            MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.wlzc_new_order);

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
            e.printStackTrace();
        }
    }

    /**
     * 播放完自动挂断
     */
    private void endCall(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                TelecomManager tm = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
                tm.endCall();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
