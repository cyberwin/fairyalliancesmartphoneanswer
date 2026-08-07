package com.fairyalliance.smartanswer;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Locale;


// ===================== 规则实体【修正，增加level】=====================
class SmsRuleItem {
    public String data_id; //规则id
    public List<String> numberRegexList;
    public List<String> bodyRegexList;
    /** 预警紧急等级 */
    public int level;
}

// ===================== 短信输出记录实体 =====================
class SmsRecordItem {
    public String smsAddress;
    public String smsBody;
    public long smsDateTs;
    public int level; /** 预警紧急等级 */
    public String rule_id; //规则id
}

/**
 * 独立静态短信规则&短信读取工具模块
 * 【注意】本类仅做纯数据处理，不启动后台、不注册广播；上层（Activity/Service）负责调用、权限申请、网络请求、后台保活
 */
public final class CyberWinEnterpriseAutoSmsRuleHelper {
    private static final String TAG = "cwSmsRuleHelper";
    public static final Gson GSON = new Gson();

    // ========== 全局静态变量：内存中保存服务器下发全部规则 ==========
    public static List<SmsRuleItem> GLOBAL_SMS_RULES = new ArrayList<>();

    private CyberWinEnterpriseAutoSmsRuleHelper() {
        // 禁止实例化，纯静态类
    }

    /**
     * 函数1：从服务器下载并加载规则到全局静态变量 GLOBAL_SMS_RULES
     * @param localHttpApi 接口地址
     */
    public static void loadRuleFromServer(String localHttpApi) {
        new Thread(() -> {
            try {
                //规整本地标准时间
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String nowTime = dateFormat.format(System.currentTimeMillis());
                //替换填写你本地电脑内网固定IP+你搭建好的接口路径，例http://192.168.1.105:8080/callnotify

                //原生GET请求携带来电号码+时间两个参数推送到本地服务
                URL url = new URL(localHttpApi +"&client_time="+nowTime+"&mer_id=77&store_id=72&eco_type=phonecall");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(3000);
                int code = conn.getResponseCode();
              //  writelog("本地推送","下载规则","时间"+nowTime+"返回状态码"+code);

                String responseBody = "";
                InputStream is = null;
                try {
                    if(code >= 200 && code < 300){
                        is = conn.getInputStream();
                    }else{
                        is = conn.getErrorStream(); // 错误状态码拿错误流
                    }
                    // 流转字符串
                    BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    responseBody = sb.toString();
                    br.close();
                    //writelog("本地推送","下载规则","时间"+nowTime+"返回数据："+responseBody);
                    loadRuleFromServerJson(responseBody);

                } finally {
                    conn.disconnect();
                }
            } catch (Exception e) {
               // writelog("本地推送","失败","推送本地服务异常："+e.getMessage());
            }
        }).start();
    }

    /**
     * 函数1：从服务器下载并加载规则到全局静态变量 GLOBAL_SMS_RULES
     * @param jsonStr 服务器返回完整JSON字符串
     * @return true解析成功；false解析失败
     */
    public static boolean loadRuleFromServerJson(String jsonStr) {
        if (jsonStr == null || jsonStr.trim().isEmpty()) {
            Log.e(TAG, "loadRuleFromServerJson: json为空");
            return false;
        }
        try {
            Type listType = new TypeToken<List<SmsRuleItem>>() {}.getType();
            List<SmsRuleItem> tempList = GSON.fromJson(jsonStr, listType);
            if(tempList == null){
                return false;
            }
            // 赋值给全局静态变量
            GLOBAL_SMS_RULES.clear();
            GLOBAL_SMS_RULES.addAll(tempList);
            Log.i(TAG, "加载规则完成，共 " + GLOBAL_SMS_RULES.size() + " 条规则");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "解析规则JSON异常", e);
            return false;
        }
    }


    /**
     * 函数2：读取短信，按【时间区间+最大读取条数】读取，倒序（最新短信优先）
     *
     * @param context 上下文，需要READ_SMS权限
     * @param maxReadCount 最多读取多少条，例如300；传<=0代表不限制条数
     * @param startTimeTs 开始时间戳(毫秒)；传null：不限制开始，从现在往前读
     * @param endTimeTs   结束时间戳(毫秒)；传null：不限制结束
     * @return 原始读取到的短信列表（未过滤，原始全部短信，rule_id/level字段为空）
     */
    public static List<SmsRecordItem> readSmsList(Context context,
                                                   int maxReadCount,
                                                   Long startTimeTs,
                                                   Long endTimeTs) {
        List<SmsRecordItem> result = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        Uri smsUri = Telephony.Sms.CONTENT_URI;

        // 查询字段
        String[] projection = {
                Telephony.Sms.ADDRESS,
                Telephony.Sms.BODY,
                Telephony.Sms.DATE
        };

        // where条件拼装时间过滤
        StringBuilder whereSb = new StringBuilder();
        List<String> whereArgsList = new ArrayList<>();

        if(startTimeTs != null){
            whereSb.append(Telephony.Sms.DATE).append(" >= ? ");
            whereArgsList.add(String.valueOf(startTimeTs));
        }
        if(endTimeTs != null){
            if(whereSb.length()>0){
                whereSb.append(" AND ");
            }
            whereSb.append(Telephony.Sms.DATE).append(" <= ? ");
            whereArgsList.add(String.valueOf(endTimeTs));
        }

        String selection = whereSb.length()>0 ? whereSb.toString() : null;
        String[] selectionArgs = whereArgsList.isEmpty() ? null : whereArgsList.toArray(new String[0]);

        // ORDER BY date DESC：最新短信排在最前面
        String sortOrder = Telephony.Sms.DATE + " DESC";

        Cursor cursor = null;
        try {
            cursor = resolver.query(smsUri, projection, selection, selectionArgs, sortOrder);
            if(cursor == null){
                return result;
            }

            int idxAddr = cursor.getColumnIndex(Telephony.Sms.ADDRESS);
            int idxBody = cursor.getColumnIndex(Telephony.Sms.BODY);
            int idxDate = cursor.getColumnIndex(Telephony.Sms.DATE);

            while (cursor.moveToNext()){
                // 达到最大读取条数直接退出循环
                if(maxReadCount > 0 && result.size() >= maxReadCount){
                    break;
                }

                SmsRecordItem item = new SmsRecordItem();
                item.smsAddress = cursor.getString(idxAddr);
                item.smsBody = cursor.getString(idxBody);
                item.smsDateTs = cursor.getLong(idxDate);
                //原始读取，level、rule_id为空，匹配之后才填充
                item.level = 0;
                item.rule_id = null;
                result.add(item);
            }
        } catch (Exception e) {
            Log.e(TAG,"读取短信数据库异常",e);
        } finally {
            if(cursor != null){
                cursor.close();
            }
        }
        return result;
    }


    /**
     * 附加工具方法：使用【全局GLOBAL_SMS_RULES】对短信做正则匹配过滤
     * 输入原始短信列表，输出命中规则的完整SmsRecordItem（回填rule_id、level）
     * 一条短信命中多条规则，返回多条记录
     * @param rawSmsList readSmsList返回原始短信
     * @return 命中结果列表，每条携带rule_id、level，可直接Gson序列化上传服务器
     */
    public static List<SmsRecordItem> filterSmsByGlobalRule(List<SmsRecordItem> rawSmsList){
        List<SmsRecordItem> out = new ArrayList<>();
        if(rawSmsList == null || rawSmsList.isEmpty()) return out;

        for(SmsRecordItem rawSms : rawSmsList){
            for(SmsRuleItem rule : GLOBAL_SMS_RULES){
                //规则校验：两个列表同时为空，直接跳过该规则
                boolean numListEmpty = (rule.numberRegexList == null || rule.numberRegexList.isEmpty());
                boolean bodyListEmpty = (rule.bodyRegexList == null || rule.bodyRegexList.isEmpty());
                if(numListEmpty && bodyListEmpty){
                    continue;
                }

                boolean matchNumber = false;
                if(!numListEmpty){
                    matchNumber = matchAnyRegex(rawSms.smsAddress, rule.numberRegexList);
                }

                boolean matchBody = false;
                if(!bodyListEmpty){
                    matchBody = matchAnyRegex(rawSms.smsBody, rule.bodyRegexList);
                }

                //核心逻辑：(号码非空且命中) OR (正文非空且命中)
                boolean ruleHit = false;
                if(!numListEmpty && matchNumber){
                    ruleHit = true;
                }
                if(!bodyListEmpty && matchBody){
                    ruleHit = true;
                }

                if(ruleHit){
                    //复制一份对象，回填规则id与等级，不要修改原始对象
                    SmsRecordItem hitItem = new SmsRecordItem();
                    hitItem.smsAddress = rawSms.smsAddress;
                    hitItem.smsBody = rawSms.smsBody;
                    hitItem.smsDateTs = rawSms.smsDateTs;
                    hitItem.rule_id = rule.data_id;
                    hitItem.level = rule.level;
                    out.add(hitItem);
                    //❗不break，允许一条短信命中多条不同规则
                }
            }
        }
        return out;
    }

    /**
     * 工具：判断文本是否匹配列表中**任意一条正则**
     */
    private static boolean matchAnyRegex(String text, List<String> regexList){
        if(text == null || regexList == null || regexList.isEmpty()){
            return false;
        }
        for(String reg : regexList){
            try{
                if(Pattern.compile(reg).matcher(text).find()){
                    return true;
                }
            }catch (Exception e){
                Log.w(TAG,"正则表达式错误："+reg,e);
            }
        }
        return false;
    }

    //=================== 日志占位，你原有writelog保留 ===================
   // private static void writelog(String tag1, String tag2, String msg){
  //      Log.d("writelog", tag1+"|"+tag2+"|"+msg);
    //}

}