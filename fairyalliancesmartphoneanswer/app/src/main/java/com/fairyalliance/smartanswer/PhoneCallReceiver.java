package com.fairyalliance.smartanswer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;

public class PhoneCallReceiver extends BroadcastReceiver {

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
}
