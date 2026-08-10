package com.fairyalliance.smartanswer;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


import android.os.Environment;
 

import java.io.File;

import java.io.FileOutputStream;

import android.content.pm.PackageManager;


import androidx.annotation.NonNull;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import CyberWinPHP.Cyber_CPU.Cyber_Public_Var;


 
import android.content.Context;
 
 
import android.media.MediaPlayer;
 
 
import android.view.View;
 
 
 
 

import android.media.AudioAttributes;
import android.media.AudioManager;
 
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import android.speech.tts.TextToSpeech;

 
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.media.MediaRecorder;

import java.io.FileInputStream;

import java.io.InputStream;

import java.util.Locale;

import com.fairyalliance.smartanswer.CyberWinEnterpriseAutoPhoneInfo;
import com.fairyalliance.smartanswer.CyberWinEnterpriseAutoSmsRuleHelper;
import com.fairyalliance.smartanswer.CyberWinLoginHelper;
 
 

// 下面这4个是你必须补的
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.FileWriter;
 

 

import java.net.URL;

import java.net.HttpURLConnection;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import java.io.BufferedReader;

import java.io.InputStreamReader;
import java.io.IOException;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.ConnectException;

import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
 
 import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static final String[] PERMISSIONS_老的 = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS
    };
    
    // 仅保留 能动态授权、必须授权、不会失效 的权限
    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_PHONE_STATE,    // 必须：检测来电
            Manifest.permission.READ_CONTACTS,       // 必须：读取联系人
            Manifest.permission.WRITE_CONTACTS,      // 可选：修改联系人
            Manifest.permission.READ_EXTERNAL_STORAGE ,// 必须：读取文件/录音
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
        
       private static final int REQUEST_MANAGE_ALL_FILES_PERMISSION = 4;
       
       private static final int PERMISSION_CODE = 100;
       
        // 音频文件名（res/raw 目录下）
    public String audioFileName  = "fams_aa_dc_01";
    public String defaultAudio   = "fams_aa_default";
    public String audioFileName2 = "fams_aa_jyyq_01";
    public String audioFileName3 = "fams_aa_default";
    
    public String audioFileName5 = "fams_aa_dc_03";
    
     public Context cyber_cpu =null ;

       private MediaPlayer mediaPlayer;
       
       //2026-05-01
       // 通话专用 音频写入器（虚拟MIC）
    private AudioTrack mCallAudioWriter;
    private TextToSpeech mTts;
    
    // 采样率固定 安卓10通话标准
    private static final int CALL_SAMPLE_RATE = 16000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cyber_cpu = this;
        Cyber_Public_Var.cyber_main_instance=this;
        try {
            
             CyberWinLogToFile.init(this);
             
             writelog("MainActivity","jt","启动开始");
             
            setContentView(R.layout.activity_main);
            
              writelog("MainActivity","jt","布局");

            // 申请存储、通讯录权限
            // 检查并申请权限
            /*
        if (!hasPermissions()) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_CODE);
        }
           */

            Toast.makeText(this, "点击屏幕开启无障碍", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            writelog("MainActivity","jt","布局"+ e.getMessage());
        }
    }
    
    //2026-04-22
      /**
     * 检查是否拥有所有权限
     */
    private boolean hasPermissions() {
        for (String p : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 【必须重写】权限申请结果回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                Toast.makeText(this, "权限已全部授权", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "请授权所有权限才能使用", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
     /**
     * 【可选但推荐重写】页面返回时刷新
     */
    @Override
    protected void onResume() {
        super.onResume();
    }
    
   
    private  void 仙盟_权限_判断_文件权限() {
        // 检查系统版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 检查是否已经拥有管理所有文件的权限
            if (!android.os.Environment.isExternalStorageManager()) {
                // 请求管理所有文件的权限
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, REQUEST_MANAGE_ALL_FILES_PERMISSION);
            } else {
                Toast.makeText(this, "已经拥有管理所有文件的权限", Toast.LENGTH_SHORT).show();
            }
        } else {
            // 对于 Android 10 及以下版本，不需要该权限
            Toast.makeText(this, "此设备不需要管理所有文件的权限", Toast.LENGTH_SHORT).show();
        }
    }
    
      private void 仙盟_加载_判断文件夹(){
        try {
            // 创建 File 对象
            String filePath = "/cyberwin/归一编程/1.html";
            String filePath1 = "/cyberwin";

            File file = new File(Environment.getExternalStorageDirectory(), filePath1);

            if (!file.exists()) {
                file.mkdirs();
            }

            String filePath2 = "/cyberwin/归一编程";
            File file2 = new File(Environment.getExternalStorageDirectory(), filePath2);
            if (!file2.exists()) {
                file2.mkdirs();
            }


        }catch (Exception e) {

            Toast.makeText(this, "文件写入失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
     @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MANAGE_ALL_FILES_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (android.os.Environment.isExternalStorageManager()) {
                    Toast.makeText(this, "已成功获取管理所有文件的权限", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "未获取到管理所有文件的权限", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    
     // 核心日志方法：writelog(type, name, msg);
        private void writelog(String type, String name, String msg) {
            try {
               
               
                // 1. 时间格式化：yyyy-MM-dd HH:mm:ss
              //  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
              //  String time = sdf.format(new Date());
        
                // 2. 日志内容
              //  String logContent = time + " | " + type + " | " + name + " | " + msg + "\n";
                 String logContent = type + " | " + name + " | " + msg + "\n";
                 CyberWinLogToFile.d_windows(type,name,logContent);
              
        
        
            } catch (Exception e) {
                // 不处理，避免崩溃
            }
        }
        
        //2026-04-23
        
    // ---------------------- 音频播放 ----------------------
    public void playDefaultAudio(View view) {
        playRawAudio(defaultAudio);
    }

    public void playAudio2(View view) {
        playRawAudio(audioFileName2);
    }

    public void playAudio3(View view) {
        playRawAudio(audioFileName3);
    }
    
    //
    
    public void playAudio5(View view) {
        playRawAudio(audioFileName5);
    }

    // 播放 raw 下的音频（通用方法）
    private void playRawAudio(String fileName) {
        try {
            // 先停止上一个
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }

            // 获取音频 ID
            int resId = getResources().getIdentifier(fileName, "raw", getPackageName());
            mediaPlayer = MediaPlayer.create(this, resId);
            mediaPlayer.start();
            Toast.makeText(this, "正在播放：" + fileName, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "播放失败：音频文件不存在", Toast.LENGTH_SHORT).show();
        }
    }
    
     public void playAudio2byphone(View view) {
        
         try {
        // 先释放上一个
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        
          AudioManager am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
           未来之窗_设置接电话AudioManager(am);

        int resId = getResources().getIdentifier(audioFileName2, "raw", getPackageName());
        mediaPlayer = MediaPlayer.create(this, resId);

        // ======================
        // 关键：强制使用 电话通话声道
        // ======================
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION) // 电话通道
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
            );
            
        } else {
           
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL); // 旧版本
        }
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL); // 设置音频流为通话流

        mediaPlayer.start();
        Toast.makeText(this, "电话通道播放：" + audioFileName2, Toast.LENGTH_SHORT).show();

    } catch (Exception e) {
        Toast.makeText(this, "播放失败", Toast.LENGTH_SHORT).show();
    }
    }
    
     private void 未来之窗_设置接电话AudioManager(AudioManager am) {
        am.setMode(AudioManager.MODE_IN_CALL);
        am.setMode(AudioManager.MODE_IN_COMMUNICATION);
        am.setSpeakerphoneOn(true);
    }
    
      // ---------------------- 权限授权 ----------------------
    public void grantStoragePermission(View view) {
        /*
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERM_CODE);
        } else {
            Toast.makeText(this, "文件权限已授权", Toast.LENGTH_SHORT).show();
        }
        */
        仙盟_权限_判断_文件权限();
    }

    public void grantAllPermissions(View view) {
          ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_CODE);
        /*
        if (!hasAllPermissions()) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, ALL_PERM_CODE);
        } else {
            Toast.makeText(this, "全部权限已授权", Toast.LENGTH_SHORT).show();
        }
        */
    }
    
    //2026-05-01
    // 初始化：打通 通话MIC写入通道
    public void initCallAudioWriterV20260501(Context context) {
        // 先锁死安卓10通话音频模式
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.setMode(AudioManager.MODE_IN_COMMUNICATION);
        am.setSpeakerphoneOn(true);
    
        // 构造通话专属属性
        AudioAttributes attr = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build();
    
        AudioFormat format = new AudioFormat.Builder()
                .setSampleRate(CALL_SAMPLE_RATE)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build();
    
        int bufferSize = AudioTrack.getMinBufferSize(
                CALL_SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
        );
    
        mCallAudioWriter = new AudioTrack(
                attr,
                format,
                bufferSize * 2,
                AudioTrack.MODE_STREAM,
                AudioManager.AUDIO_SESSION_ID_GENERATE
        );
    
        mCallAudioWriter.play();
    
        // 初始化TTS
        mTts = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
               // mTts.setLanguage(Locale.CHINA);
                mTts.setLanguage(Locale.CHINESE); // 这里改成 CHINESE
                mTts.setPitch(1.0f);
                mTts.setSpeechRate(1.0f);
            }
        });
    }
    
    // 释放：关闭虚拟写入，恢复正常打电话
    public void releaseCallAudioWriterV20260501() {
        if (mCallAudioWriter != null) {
            mCallAudioWriter.stop();
            mCallAudioWriter.release();
            mCallAudioWriter = null;
        }
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
            mTts = null;
        }
    }
    
    /**
     * TTS文字 → 直接写入通话上行，对方听到，手机不外放
     * @param content 要发送给对方的文字
     */
    public void ttsWriteToCallMic(String content) {
        if (mTts == null || mCallAudioWriter == null) return;
        
         // 修复：TTS 输出到 AudioTrack（通话通道）
       // mTts.setAudioTrack(mCallAudioWriter);
    
        // 把TTS音频流定向输出到 通话AudioTrack
        mTts.speak(
                content,
                TextToSpeech.QUEUE_ADD,
                null,
                null
        );
    }
    
    // 你原来的方法名、参数、结构 完全不动！只改内部
    private void playRawAudiov20260501(String fileName) {
        try {
            // 先释放上一个（保持你原来的逻辑）
            if (mCallAudioWriter != null) {
                mCallAudioWriter.stop();
                mCallAudioWriter.release();
                mCallAudioWriter = null;
            }
    
            // 获取音频ID（你原来的代码）
            int resId = getResources().getIdentifier(fileName, "raw", getPackageName());
            if (resId == 0) {
                Toast.makeText(this, "播放失败：音频文件不存在", Toast.LENGTH_SHORT).show();
                return;
            }
    
            // 初始化通话写入通道（关键：让声音进电话）
           // initCallAudioWriterV20260501(this);
    
            // 读取 RAW 音频并写入通话
            InputStream inputStream = getResources().openRawResource(resId);
            byte[] buffer = new byte[4096];
            inputStream.skip(44); // 跳过WAV头
    
            new Thread(() -> {
                try {
                    int len;
                    while ((len = inputStream.read(buffer)) != -1) {
                        if (mCallAudioWriter != null) {
                            mCallAudioWriter.write(buffer, 0, len);
                        }
                    }
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
    
            Toast.makeText(this, "正在播放：" + fileName, Toast.LENGTH_SHORT).show();
    
        } catch (Exception e) {
            Toast.makeText(this, "播放失败：音频文件不存在", Toast.LENGTH_SHORT).show();
        }
    }
    
     public void play4xml6res(View view) {
         // 1. 电话接通 OFFHOOK 第一件事
        initCallAudioWriterV20260501(this);

        playRawAudiov20260501(audioFileName5);
        
          // 4. 通话结束 / 挂断
      // releaseCallAudioWriterV20260501();
    }
     public void play4xml7tts(View view) {
          // 1. 电话接通 OFFHOOK 第一件事
        initCallAudioWriterV20260501(this);
        
        // 2. 想用文字应答
        ttsWriteToCallMic("您好，欢迎使用东方仙盟自动接机，自动应答");
        
        // 4. 通话结束 / 挂断
       //releaseCallAudioWriterV20260501();
    }
    
    //2026-08-06
public void getContactBytag(View view) {
    
        // 点击事件只做一件事：开启子线程，所有网络逻辑全部放进run()里面
        //NetworkOnMainThreadException
    new Thread(new Runnable() {
        @Override
            public void run() {
                //
                
             try {
                    //规整本地标准时间
                     String contactJson = CyberWinEnterpriseAutoPhoneInfo.readAllContactGroupsToJson(cyber_cpu);
                     
                      writelog("本地推送","联系人","推送本地服务异常："+contactJson);
                      
                               //拿到凭证
           CyberWinLoginHelper.LoginSpModel loginInfo = CyberWinLoginHelper.getStoredLoginInfo(getApplicationContext());
           
          writelog("本地推送","登录信息","登录信息："+loginInfo.loginStatus+",session："+loginInfo.cwpd_session);
        
                     
                     String localHttpApi = "http://51.onelink.ynwlzc.net/o2o/wap.php?g=Wap&c=FAMS_smartanswer&a=fastgo&action=embedContactlist";
                      localHttpApi=localHttpApi+"&cwpd_session="+loginInfo.cwpd_session;
                    //原生GET请求携带来电号码+时间两个参数推送到本地服务
                    
                     URL url = new URL(localHttpApi);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setConnectTimeout(3000);
                    conn.setReadTimeout(3000);
                    conn.setDoOutput(true);
                    //conn.setRequestProperty("Content‑Type", "application/json;charset=utf‑8");
                    conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
        
                    // 删掉StandardCharsets，用"UTF-8"字符串兼容所有Android版本
                    OutputStream os = conn.getOutputStream();
                    OutputStreamWriter osw = new OutputStreamWriter(os, "UTF‑8");
                    osw.write(contactJson);
                    osw.flush();
                    osw.close();
                    os.close();
        
                    int code = conn.getResponseCode();
                    String responseText = "";
                    //读取返回内容，区分成功流/错误流
                    InputStream is;
                    if(code >=200 && code <300){
                        is = conn.getInputStream();
                    }else{
                        is = conn.getErrorStream();
                    }
                    if(is != null){
                        BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF‑8"));
                        String line;
                        StringBuilder sb = new StringBuilder();
                        while((line=br.readLine())!=null){
                            sb.append(line);
                        }
                        br.close();
                        is.close();
                        responseText = sb.toString();
                    }
        
                    writelog("本地推送","成功","HTTP状态码:"+code+" 服务返回："+responseText);
                    conn.disconnect();
                    
                    
                } 
              catch (android.os.NetworkOnMainThreadException e) {
                writelog("本地推送","失败","错误【NetworkOnMainThreadException】网络请求运行在UI主线程，必须放到子线程");
            } catch (java.net.SocketTimeoutException e) {
                writelog("本地推送","失败","错误【SocketTimeoutException】超时：连接/读取超时，服务器3秒无响应");
            } catch (java.net.ConnectException e) {
                writelog("本地推送","失败","错误【ConnectException】无法建立TCP连接：地址不可达、服务未启动、端口不通");
            } catch (java.net.MalformedURLException e) {
                writelog("本地推送","失败","错误【MalformedURLException】URL格式非法");
            } catch (java.io.FileNotFoundException e) {
                writelog("本地推送","失败","错误【FileNotFoundException】404资源不存在，getInputStream对4xx/5xx会抛该异常");
            } catch (Exception e) {
                   // writelog("本地推送","失败","推送本地服务异常："+e.getMessage());
                    // getMessage为null时打印完整堆栈，定位真实错误
                        String errMsg = e.getMessage();
                      //  if(errMsg == null){
                        //    StringWriter sw = new StringWriter();
                            //e.printStackTrace(new PrintWriter(sw));
                       //     errMsg = sw.toString();
                       // }
                        writelog("本地推送","失败","异常详情："+errMsg);
                }
           
            
                
            }//     
        }).start(); //必须调用start()，启动后台子线程
        
        showShortToast(this,"提交关键联系");     
     
    }
    
    //getSmsRecoredAnalysis
    public void synctaskrules(View view) {
            //1.读取原始短信
                 //拿到凭证
           CyberWinLoginHelper.LoginSpModel loginInfo = CyberWinLoginHelper.getStoredLoginInfo(getApplicationContext());
           
          writelog("本地推送","登录信息","登录信息："+loginInfo.loginStatus+",session："+loginInfo.cwpd_session);
        
        
         String localHttpApi = "http://51.onelink.ynwlzc.net/o2o/wap.php?g=Wap&c=FAMS_smartanswer&a=fastgo&action=gainsmsrules";
         localHttpApi=localHttpApi+"&cwpd_session="+loginInfo.cwpd_session;
         CyberWinEnterpriseAutoSmsRuleHelper.loadRuleFromServer(localHttpApi);
         
          showShortToast(this,"获取分析规则");
    }
    public void getSmsRecoredAnalysis(View view) {
        
        List<SmsRecordItem> rawList = CyberWinEnterpriseAutoSmsRuleHelper.readSmsList(this, 300, null, null);
        //2.执行规则过滤，输出已经填充rule_id、level的命中集合
        List<SmsRecordItem> hitList = CyberWinEnterpriseAutoSmsRuleHelper.filterSmsByGlobalRule(rawList);
        //3.直接转json上传服务器
        String json = CyberWinEnterpriseAutoSmsRuleHelper.GSON.toJson(hitList);
        
        String rawjson = CyberWinEnterpriseAutoSmsRuleHelper.GSON.toJson(rawList);
        
         writelog("本地推送","短信推送","jsonhitList："+json);
         
         // writelog("本地推送","短信推送","jsonreadSms："+rawjson);
              //拿到凭证
           CyberWinLoginHelper.LoginSpModel loginInfo = CyberWinLoginHelper.getStoredLoginInfo(getApplicationContext());
           
          writelog("本地推送","登录信息","登录信息："+loginInfo.loginStatus+",session："+loginInfo.cwpd_session);
        
         
         String 短信任务localHttpApi = "http://51.onelink.ynwlzc.net/o2o/wap.php?g=Wap&c=FAMS_smartanswer&a=fastgo&action=embedSmsTaskauto";
         短信任务localHttpApi=短信任务localHttpApi+"&cwpd_session="+loginInfo.cwpd_session;
         
         fn_cyberwin_senddata(短信任务localHttpApi,json);
          
        
        showShortToast(this,"已经提交分析短信");
   
    }
    
       public void getSmsRecoredAnalysisAll(View view) {
        
        List<SmsRecordItem> rawList = CyberWinEnterpriseAutoSmsRuleHelper.readSmsList(this, 300, null, null);
        //2.执行规则过滤，输出已经填充rule_id、level的命中集合
       // List<SmsRecordItem> hitList = CyberWinEnterpriseAutoSmsRuleHelper.filterSmsByGlobalRule(rawList);
        //3.直接转json上传服务器
      //  String json = CyberWinEnterpriseAutoSmsRuleHelper.GSON.toJson(hitList);
        
        String rawjson = CyberWinEnterpriseAutoSmsRuleHelper.GSON.toJson(rawList);
        
        // writelog("本地推送","短信推送","jsonhitList："+json);
         
          writelog("本地推送","短信推送all","jsonreadSms："+rawjson);
          
             //拿到凭证
           CyberWinLoginHelper.LoginSpModel loginInfo = CyberWinLoginHelper.getStoredLoginInfo(getApplicationContext());
           
          writelog("本地推送","登录信息","登录信息："+loginInfo.loginStatus+",session："+loginInfo.cwpd_session);
          
         
         String 短信任务localHttpApi = "http://51.onelink.ynwlzc.net/o2o/wap.php?g=Wap&c=FAMS_smartanswer&a=fastgo&action=embedSmsTaskauto";
         短信任务localHttpApi=短信任务localHttpApi+"&cwpd_session="+loginInfo.cwpd_session;
         fn_cyberwin_senddata(短信任务localHttpApi,rawjson);
           
         showShortToast(this,"已经提交短信");
     
   
    }
    
    //2026-08-10
      public void CyberWinLoginHelper_login(View view) {
         String 登录localHttpApi = "http://51.onelink.ynwlzc.net/o2o/index.php/appdevgo/app迷你登录/o2o/index.php/appdevgo/app迷你登录";
        
       //1.唤起登录弹窗
        CyberWinLoginHelper loginHelper = new CyberWinLoginHelper(this,登录localHttpApi);
        loginHelper.showLoginDialog();
        
        //2.读取存储的登录凭证，用于其他http请求
        LoginHelper.LoginSpModel loginInfo = CyberWinLoginHelper.getStoredLoginInfo(this);
        String userId = loginInfo.userId;
        String token = loginInfo.token;
        
        //3.登录状态判断
        if (LoginHelper.isLogined(this)) {
            //已登录，可以携带token发起其他业务POST
        } else {
            //未登录，需要登录
        }
        // writelog("本地推送","短信推送","jsonhitList："+json);
         
        
     
     
   
    }
    
    
       public void CyberWinLoginHelper_logincheck(View view) {
         
             //拿到凭证
           CyberWinLoginHelper.LoginSpModel loginInfo = CyberWinLoginHelper.getStoredLoginInfo(getApplicationContext());
           
         // writelog("本地推送","登录信息","登录信息："+loginInfo.loginStatus+",session："+loginInfo.cwpd_session);
          showShortToast(this,"登录信息："+loginInfo.loginStatus+",session："+loginInfo.cwpd_session);
         
        
     
     
   
    }
    
     public void fn_cyberwin_senddata(String localHttpApi,String postdata) {
           new Thread(new Runnable() {
            @Override
                public void run() {
                    //
                    
                 try {
                        //规整本地标准时间
                          
                       
                        //原生GET请求携带来电号码+时间两个参数推送到本地服务
                        
                         URL url = new URL(localHttpApi);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("POST");
                        conn.setConnectTimeout(3000);
                        conn.setReadTimeout(3000);
                        conn.setDoOutput(true);
                        //conn.setRequestProperty("Content‑Type", "application/json;charset=utf‑8");
                        conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            
                        // 删掉StandardCharsets，用"UTF-8"字符串兼容所有Android版本
                        OutputStream os = conn.getOutputStream();
                        OutputStreamWriter osw = new OutputStreamWriter(os, "UTF‑8");
                        osw.write(postdata);
                        osw.flush();
                        osw.close();
                        os.close();
            
                        int code = conn.getResponseCode();
                        String responseText = "";
                        //读取返回内容，区分成功流/错误流
                        InputStream is;
                        if(code >=200 && code <300){
                            is = conn.getInputStream();
                        }else{
                            is = conn.getErrorStream();
                        }
                        if(is != null){
                            BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF‑8"));
                            String line;
                            StringBuilder sb = new StringBuilder();
                            while((line=br.readLine())!=null){
                                sb.append(line);
                            }
                            br.close();
                            is.close();
                            responseText = sb.toString();
                        }
            
                        writelog("本地推送","短信推送","HTTP状态码:"+code+" 服务返回："+responseText);
                        conn.disconnect();
                        
                        
                    } 
                  catch (android.os.NetworkOnMainThreadException e) {
                    writelog("本地推送","统一推送","错误【NetworkOnMainThreadException】网络请求运行在UI主线程，必须放到子线程");
                } catch (java.net.SocketTimeoutException e) {
                    writelog("本地推送","统一推送","错误【SocketTimeoutException】超时：连接/读取超时，服务器3秒无响应");
                } catch (java.net.ConnectException e) {
                    writelog("本地推送","统一推送","错误【ConnectException】无法建立TCP连接：地址不可达、服务未启动、端口不通");
                } catch (java.net.MalformedURLException e) {
                    writelog("本地推送","统一推送","错误【MalformedURLException】URL格式非法");
                } catch (java.io.FileNotFoundException e) {
                    writelog("本地推送","统一推送","错误【FileNotFoundException】404资源不存在，getInputStream对4xx/5xx会抛该异常");
                } catch (Exception e) {
                       // writelog("本地推送","失败","推送本地服务异常："+e.getMessage());
                        // getMessage为null时打印完整堆栈，定位真实错误
                            String errMsg = e.getMessage();
                          //  if(errMsg == null){
                            //    StringWriter sw = new StringWriter();
                                //e.printStackTrace(new PrintWriter(sw));
                           //     errMsg = sw.toString();
                           // }
                            writelog("本地推送","统一推送","异常详情："+errMsg);
                    }
               
                
                    
                }//     
        }).start(); //必须调用start()，启动后台子线程
     }
    
    //2026-08-10
    /**
     * 短时间Toast
     * @param context 上下文
     * @param msg 显示文本
     */
    public static void showShortToast(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 长时间Toast
     * @param context 上下文
     * @param msg 显示文本
     */
    public static void showLongToast(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }
}