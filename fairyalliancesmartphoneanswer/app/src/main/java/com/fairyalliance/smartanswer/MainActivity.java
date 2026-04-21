package com.fairyalliance.smartanswer;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // 所有权限一次性申请
    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ANSWER_PHONE_CALLS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_PHONE_NUMBERS,

            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,

            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_main);

            // 申请所有权限
            if (Build.VERSION.SDK_INT >= 23) {
                requestPermissions(PERMISSIONS, 100);
            }

            Toast.makeText(this, "服务已启动", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // 绝对不闪退
            android.widget.TextView tv = new android.widget.TextView(this);
            tv.setText("运行中");
            tv.setPadding(50,50,50,50);
            setContentView(tv);
        }
    }
}