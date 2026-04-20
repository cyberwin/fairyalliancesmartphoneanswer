package com.fairyalliance.smartanswer;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_main);
        try {
            // 正常加载界面
            setContentView(R.layout.activity_main);
            
            // 弹出提示：界面加载成功
            Toast.makeText(this, "智能接听已启动", Toast.LENGTH_SHORT).show();
            
        } catch (Exception e) {
            // 捕获所有错误 → 绝不闪退
            Toast.makeText(this, "加载界面失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
            
            // 强制给一个纯代码界面，确保不闪退
          //  android.widget.TextView tv = new android.widget.TextView(this);
         //   tv.setText("APP 运行中\n错误已捕获");
          //  tv.setPadding(50,50,50,50);
          //  setContentView(tv);
        }
    }
}
