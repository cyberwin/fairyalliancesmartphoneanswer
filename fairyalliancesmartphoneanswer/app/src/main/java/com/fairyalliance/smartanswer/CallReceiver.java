package com.fairyalliance.smartanswer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.os.Build;
import java.util.Arrays;
import java.util.List;

public class CallReceiver extends BroadcastReceiver {

    // 白名单
    private final List<String> whiteList = Arrays.asList("10086", "10000", "10010");
    // 黑名单
    private final List<String> blackList = Arrays.asList("400", "95", "0755", "021");

    @Override
    public void onReceive(Context context, Intent intent) {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
            String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            if (number == null || whiteList.contains(number)) return;
            answerAndPlay(context);
        }
    }

    private void answerAndPlay(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                TelecomManager tm = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
                tm.acceptRingingCall();
            }

            MediaPlayer mp = MediaPlayer.create(context, R.raw.answer);
            mp.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
            mp.start();
            mp.setOnCompletionListener(mediaPlayer -> {
                mediaPlayer.release();
                endCall(context);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
