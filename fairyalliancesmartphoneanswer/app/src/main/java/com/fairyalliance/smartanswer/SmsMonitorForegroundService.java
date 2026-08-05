package com.fairyalliance.smartanswer;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Telephony;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SmsMonitorForegroundService extends Service {
    public static final String ACTION_SET_RULE_JSON = "ACTION_SET_RULE_JSON";
    public static final String EXTRA_RULE_JSON = "EXTRA_RULE_JSON";
    private NotificationManager mNotificationManager;
    private SmsContentObserver mSmsObserver;
    private final Handler mMainHandler = new Handler();
    private List<SmsRuleItem> mGlobalRuleList = new ArrayList<>();
    private final Gson gson = new Gson();

    public interface OnSmsHitCallback{
        void onHitSms(SmsRecordItem item);
    }
    private OnSmsHitCallback mHitCallback;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        Notification notification = buildNotification();
        startForeground(10086, notification);

        mSmsObserver = new SmsContentObserver(mMainHandler, new SmsContentObserver.SmsFilterCallback() {
            @Override
            public void onInboxSms(String address, String body, long dateTs) {
                boolean isHit = checkRules(address, body);
                if(isHit){
                    SmsRecordItem record = new SmsRecordItem();
                    record.smsAddress = address;
                    record.smsBody = body;
                    record.smsDateTs = dateTs;
                    if(mHitCallback != null){
                        mHitCallback.onHitSms(record);
                    }
                }
            }
        });
        mSmsObserver.register(getContentResolver());
    }

    /**
     * 核心匹配：遍历全部规则数组；一条规则内部：(号码正则列表任意命中) OR (正文正则列表任意命中)
     * @param address 发件号码
     * @param body 短信正文
     * @return true命中，false丢弃
     */
    private boolean checkRules(String address, String body){
        if(mGlobalRuleList == null || mGlobalRuleList.isEmpty()){
            return false;
        }
        for(SmsRuleItem rule : mGlobalRuleList){
            boolean hitNumber = false;
            boolean hitBody = false;

            //号码正则列表；list为空则号码条件不参与判断
            if(rule.numberRegexList != null && rule.numberRegexList.size()>0){
                for(String reg : rule.numberRegexList){
                    if(TextUtils.isEmpty(reg)) continue;
                    Pattern p = Pattern.compile(reg);
                    if(p.matcher(address).find()){
                        hitNumber = true;
                        break;
                    }
                }
            }else{
                hitNumber = true;
            }

            //正文正则列表；list为空正文条件不参与判断
            if(rule.bodyRegexList != null && rule.bodyRegexList.size()>0){
                for(String reg : rule.bodyRegexList){
                    if(TextUtils.isEmpty(reg)) continue;
                    Pattern p = Pattern.compile(reg, Pattern.DOTALL);
                    if(p.matcher(body).find()){
                        hitBody = true;
                        break;
                    }
                }
            }else{
                hitBody = true;
            }

            if(hitNumber || hitBody){
                return true;
            }
        }
        return false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null && ACTION_SET_RULE_JSON.equals(intent.getAction())){
            String jsonStr = intent.getStringExtra(EXTRA_RULE_JSON);
            if(!TextUtils.isEmpty(jsonStr)){
                Type type = new TypeToken<List<SmsRuleItem>>(){}.getType();
                mGlobalRuleList = gson.fromJson(jsonStr, type);
                Log.i("SERVICE_RULE", "已加载服务器下发规则条数："+mGlobalRuleList.size());
            }
        }
        return START_STICKY;
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("sms_monitor_ch","短信监控服务", NotificationManager.IMPORTANCE_LOW);
            mNotificationManager = getSystemService(NotificationManager.class);
            mNotificationManager.createNotificationChannel(channel);
        }
    }
    private Notification buildNotification(){
        return new NotificationCompat.Builder(this,"sms_monitor_ch")
                .setContentTitle("值守监控运行中")
                .setSmallIcon(android.R.drawable.ic_menu_info_details)
                .build();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mSmsObserver != null){
            mSmsObserver.unRegister(getContentResolver());
        }
    }

    //======== 内部短信ContentObserver =========
    public static class SmsContentObserver extends ContentObserver{
        private final SmsFilterCallback cb;
        private ContentResolver cr;

        public interface SmsFilterCallback{
            void onInboxSms(String address,String body,long dateTs);
        }
        public SmsContentObserver(Handler handler, SmsFilterCallback callback) {
            super(handler);
            cb = callback;
        }
        public void register(ContentResolver contentResolver){
            cr = contentResolver;
            cr.registerContentObserver(Telephony.Sms.CONTENT_URI,true,this);
        }
        public void unRegister(ContentResolver contentResolver){
            if(cr != null){
                cr.unregisterContentObserver(this);
            }
        }
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            if(cr == null) return;
            Cursor cur = cr.query(Telephony.Sms.Inbox.CONTENT_URI,
                    new String[]{Telephony.Sms.ADDRESS,Telephony.Sms.BODY,Telephony.Sms.DATE},
                    Telephony.Sms.READ + "=0",null, Telephony.Sms.Inbox.DEFAULT_SORT_ORDER);
            if(cur == null) return;
            try{
                while(cur.moveToNext()){
                    String addr = cur.getString(0);
                    String bod = cur.getString(1);
                    long dt = cur.getLong(2);
                    if(cb != null){
                        cb.onInboxSms(addr,bod,dt);
                    }
                }
            }finally {
                cur.close();
            }
        }
    }

    //================ 实体类占位，需要单独定义 =================
    public static class SmsRuleItem{
        public List<String> numberRegexList;
        public List<String> bodyRegexList;
    }
    public static class SmsRecordItem{
        public String smsAddress;
        public String smsBody;
        public long smsDateTs;
    }
}