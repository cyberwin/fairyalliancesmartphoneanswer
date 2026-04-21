package com.fairyalliance.smartanswer;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_PHONE_STATE,
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

            // 申请存储、通讯录权限
            if (Build.VERSION.SDK_INT >= 23) {
                requestPermissions(PERMISSIONS, 100);
            }

            // 跳转到无障碍设置
            /*
            findViewById(android.R.id.content).setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivity(intent);
                    Toast.makeText(this, "找到【智能接听】并开启", Toast.LENGTH_LONG).show();
                } catch (Exception e) {}
            });
            */

            Toast.makeText(this, "点击屏幕开启无障碍", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            // 绝不闪退
           // android.widget.TextView tv = new android.widget.TextView(this);
           // tv.setText("点击开启无障碍");
           // tv.setPadding(50,50,50,50);
           // setContentView(tv);
        }
    }
}