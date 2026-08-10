package com.fairyalliance.smartanswer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * 原生登录工具类，HttpURLConnection，仅Gson解析JSON
 * 提供：登录弹窗、登录POST、SP持久化、读取已存储登录凭证、登录状态判断
 */
public class CyberWinLoginHelper {

    private final Gson gson = new Gson();
    private final Context mContext;
    // 修改为你的登录接口地址
    private static  String LOGIN_URL = "http://xxx.xxx/api/login";
    private static final String SP_NAME = "cyberwin_login_sp";

    public CyberWinLoginHelper(Context context,String apiurl) {
        LOGIN_URL =apiurl;
        this.mContext = context.getApplicationContext();
    }

    /**
     * 外部调用：弹出登录弹窗
     */
    public void showLoginDialog() {
        LinearLayout rootLayout = new LinearLayout(mContext);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setPadding(80, 40, 80, 40);

        EditText etUsername = new EditText(mContext);
        etUsername.setHint("用户ID / 账号");

        EditText etPwd = new EditText(mContext);
        etPwd.setHint("密码");
        etPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        rootLayout.addView(etUsername);
        rootLayout.addView(etPwd);

        AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setTitle("账号登录")
                .setView(rootLayout)
                .setPositiveButton("登录", (d, which) -> {
                    String uid = etUsername.getText().toString().trim();
                    String pwd = etPwd.getText().toString().trim();

                    new Thread(() -> {
                        LoginResp resp = doPostLogin(uid, pwd);
                        ((android.app.Activity) mContext).runOnUiThread(() -> {
                            if (resp != null && resp.error == 0) {
                                //登录成功，关闭弹窗，保存到SP
                                d.dismiss();
                                saveLoginInfo(resp);
                            } else {
                                String msg;
                                if (resp == null) {
                                    msg = "网络请求失败";
                                } else {
                                    msg = "登录失败 status=" + resp.error + " " + resp.msg;
                                }
                                new AlertDialog.Builder(mContext)
                                        .setTitle("提示")
                                        .setMessage(msg)
                                        .setPositiveButton("确定", null)
                                        .show();
                            }
                        });
                    }).start();

                })
                .setNegativeButton("取消", null)
                .create();
        dialog.show();
    }


    /**
     * 原生 HttpURLConnection POST 请求登录
     * @return 服务器返回解析后的LoginResp，出错返回null
     */
    private LoginResp doPostLogin(String userId, String password) {
        HttpURLConnection conn = null;
        OutputStream os = null;
        BufferedReader br = null;
        try {
            URL url = new URL(LOGIN_URL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

            LoginReq req = new LoginReq();
            req.account = userId;
            req.pwd = password;
            String jsonBody = gson.toJson(req);

            os = conn.getOutputStream();
            os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            os.flush();

            int code = conn.getResponseCode();
            InputStream is;
            if (code >= 200 && code < 300) {
                is = conn.getInputStream();
            } else {
                is = conn.getErrorStream();
            }

            br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            String responseJson = sb.toString();
            return gson.fromJson(responseJson, LoginResp.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (os != null) os.close();
                if (br != null) br.close();
            } catch (IOException ignored) {
            }
            if (conn != null) conn.disconnect();
        }
    }

    /**
     * 保存登录信息到SharedPreferences
     */
    private void saveLoginInfo(LoginResp resp) {
        SharedPreferences sp = mContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("msg", resp.msg);
        editor.putString("cwpd_session", resp.cwpd_session);
        editor.putInt("login_status", resp.error);
        editor.apply();
    }

    /**
     * 读取本地持久化登录信息，返回实体对象，供其他网络请求拿token/userId
     * @param context 上下文
     * @return LoginSpModel，未登录字段为空，loginStatus=-1
     */
    public static LoginSpModel getStoredLoginInfo(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        LoginSpModel model = new LoginSpModel();
        model.msg = sp.getString("msg", "");
        model.cwpd_session = sp.getString("cwpd_session", "");
        model.loginStatus = sp.getInt("login_status", -1);
        return model;
    }

    /**
     * 判断本地是否登录成功 status ==9
     */
    public static boolean isLogined(Context context) {
        LoginSpModel info = getStoredLoginInfo(context);
        return info.loginStatus == 0;
    }
    
    
      /**
     * 获取登录id
     */
    public static String Loginedsession(Context context) {
        LoginSpModel info = getStoredLoginInfo(context);
        if(info.loginStatus == 0){
           return info.cwpd_session; 
        }else{
            return "没有登录";
        }
         
    }


    // ========== 数据实体 ==========
    public static class LoginReq {
        @SerializedName("account")
        public String account;
        @SerializedName("pwd")
        public String pwd;
    }

    public static class LoginResp {
        @SerializedName("error")
        public int error;
        @SerializedName("msg")
        public String msg;
      
       
        
        @SerializedName("dom_id")
        public String dom_id;
        
        @SerializedName("referer")
        public String referer;
        
        @SerializedName("cwpd_session")
        public String cwpd_session;
        
        
    }

    /**
     * SP读取出来的登录凭证实体，其他接口直接取 userId / token
     */
    public static class LoginSpModel {
        public String msg;
        public String cwpd_session;
        public int loginStatus;
    }
}
