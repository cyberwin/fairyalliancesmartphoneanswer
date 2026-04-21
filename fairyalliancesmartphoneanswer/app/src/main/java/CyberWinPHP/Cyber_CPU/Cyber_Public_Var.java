package CyberWinPHP.Cyber_CPU;


import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.FileUtils;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;

import androidx.annotation.RequiresApi;

import com.ynwlzc.framework.cyberwinosand_ScreenDisplay.FullscreenActivity;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import com.fairyalliance.smartanswer.CyberWinLogToFile;
import CyberWinPHP.Cyber_Server.MyX509TrustManager;

public class Cyber_Public_Var
{
    //未来之窗核心
   // public  static FullscreenActivity m_cpu=null;
    public static Context cyber_main_instance = null;

    //未来之窗核心

  
    //请求的code
    public static final int REQUEST_ENABLE_BT = 1;

    //蓝牙适配器
    public static BluetoothAdapter mBluetoothAdapter;

    //  public  BluetoothAdapter mBluetoothAdapter;
    //蓝牙socket对象
    public static BluetoothSocket mmSocket;
    public  static UUID uuid= UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    //打印的输出流
    public static OutputStream outputStream = null;

    //打印的内容
    public static  String mPrintContent="\n123456789℃＄¤￠‰§№☆★完\n\n\n未来之窗软件服务\n\nsupport.ynwlzc.net\n\n\n";
    public static  String mPrintContent_order_id_loong="20225578";

    public static  String cyber_default_blue_address="DC:0D:30:7E:8E:8D";

    public static  String cyber_default_printer_type="usb";
    public static  boolean cyber_default_printer_print_finish=false;

    public static  String cyber_default_printer_report_width="58";//打印纸张

    public  static String Serial_Port_Paht="/dev/ttyS2";
    public  static int Serial_Port_Port=115200;

    //支付信息
    public  static String pay_face_wx_appid="wx6e4a5cef9bdab82c";
    public  static String pay_face_wx_mch_id="1507462791";
    public  static String pay_face_wx_sub_mch_id="1510181421";
    //    params2.put(PARAMS_SUB_APPID,"wx1eb85e107e36a21d");
    public  static String pay_face_wx_sub_appid="wx1eb85e107e36a21d";
    public  static String pay_face_wx_store_id="wlzc77";
    public  static String pay_face_wx_totalfee="10";
    public  static String pay_face_wx_phone="";

    //public  static String pay_face_wx_store_id="10";

    public  static String pay_face_wx_mc_name="未来之窗自助收银";

    public  static String pay_face_wx_out_trade_no="1234567809";


    public  static String pay_face_alipay_totalfee="0.01";

    //调试模式

    public  static  boolean cwpd_debug_mod=true;

    //未来之窗之窗支付配置
    public static  String cwpd_pay_allparamorder="";//未来之窗支付全能参数合体

    //未来之窗支付合体配置
    public static  String cwpd_pay_wlzc_client_system_wlzc_client_merchant_id="";//未来之窗支付全能参数合体-商户号
    public static  String cwpd_pay_wlzc_client_system_wlzc_client_merchant_platform="";//未来之窗支付全能参数合体-商户平台类型

    //2022-6-28
    public static  String cwpd_pay_wlzc_client_system_wlzc_client_store_id="";//未来之窗支付全能参数合体-商户号

    //支付中
    public static  boolean cwpd_ret_wait_pay_finish=false;

    //微信刷脸模组
    public static  String cwpd_ret_pay_getWxpayfaceRawdata_CACHE20220414="";
    public static boolean cyberwin_wx_face_mHasInited;
    //
    public static String cwpd_ret_pay_getWxpayfaceRawdata_CACHE20220414_lastcreate_time="";
    public static SimpleDateFormat wx_face_sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    //刷脸模组优化
    //mAuthInfo
    public static  String cwpd_ret_pay_get_wxpayface_authinfo_CACHE20220624="";
    public static String cwpd_ret_pay_get_wxpayface_authinfo_CACHE20220624_lastcreate_time="";

    public static  String cwpd_device_sn="";

    //加载未来之窗配置信息
    public static   boolean cwpd_loadMerchant_config_isok=false;

    //未来之窗之窗支付配置
    public static  String cwpd_loadMerchant_config_raw="";//未来之窗支付全能参数商户合体


    //2019-1123
    public static   String Application_StartupPath ="/CyberWinPHP/";

    public static   String Application_StartupPath_Home ="";
    //"https://51.onelink.ynwlzc.cn//o2o/cyberwin_offline_store.php?g=MerchantV6&c=CyberWin_Store&a=Store_cashier_Qujing_android1000x650";
    public  static String Application_POs_dev_sn="";


    public static  String cyberwin_default_printer="";

    public static  String cyberwin_default_printer_net="";
    //cyber_default_printer_typepublic static  String cyberwin_default_printer_type="";

    public  static String Application_Safe_wlzc_authen_pass="";


    public static  String Thread_wait_Result_Str="cyber-norun";

    //未来之窗运作模式
    public static  String App_start_mode="system";


    //未来之窗缓存技术
    public static  String App_localcache_kernel="4";
    public static  String App_localcache_image="0";

    public static  final  String App_localcache_kernel_CACAHE_DIRNAME ="/cyberwinwebcache";

    public static     String WebZoom ="99";

    //2020-7-9 资源缓存技术
    public static    String App_localcache_resource ="0";
    public static    String App_localcache_resource_path ="%CyberWinPHP_root%/CyberCache/";
    //未来之窗app
    public static    String App_local_path ="%CyberWinPHP_root%/CyberWin/CyberAPP/";

    public static    String CyberWin_MicroApp_local_path ="%CyberWinPHP_root%/CyberWin/CyberWin_MicroApp/";

    public static    String App_localcache_DefaultHome_Load_From ="set";
    public static    String NoNetworkDefaultErrorFrom ="set";

    //2022-7-11 刷脸技术
    public static String App_FaceRecognition_path_facedata="/sdcard/CyberWin/FaceRecognition/FaceData/";
    public static String App_FaceRecognition_path_FaceDetect="/sdcard/CyberWin/FaceRecognition/FaceDetect/";

    //2022-7-14 客户端id
    public static String client_sn="";
    public static String start_linkley = "20220714";;

    public static    String CyberWin_智能屏幕路径 ="%CyberWinPHP_root%/CyberWin/smartscreen/";


    /**
     * *读取未来之窗目标路径
     *
     */
    public static String getCyberWinPath(String filein,Context context)  {
        if(filein.startsWith("%CyberWinPHP_root%")==true) {

            File sdDir = null;
            boolean sdCardExist = Environment.getExternalStorageState()
                    .equals(android.os.Environment.MEDIA_MOUNTED);//判断sd卡是否存在
            if (sdCardExist) {
                sdDir = Environment.getExternalStorageDirectory();//获取跟目录
            }
            String CyberWinPHP_Path = sdDir.toString();
            String url2 = filein.replace("%CyberWinPHP_root%",CyberWinPHP_Path);
            return  url2;
        }
        if(filein.startsWith("%CyberWinPHP_Path%")==true){

            //cyber_getFilePath(Context context)
            String CyberWinPHP_Path=  cyber_getFilePath(context);

            String   url2=filein.replace("%CyberWinPHP_Path%",CyberWinPHP_Path);

            //   mediaPlayer.setDataSource(this, Uri.parse("file://"+sdPath+"/dcim/camera/V70720-091144.mp4"));
            //准备(播放内存卡的时候需要准备)
        }
        return filein;

    }
    /**
     * 将String转换成InputStream
     * @param in
     * @return
     * @throws Exception
     */
    public static InputStream StringTOInputStream(String in) throws Exception{

        ByteArrayInputStream is = new ByteArrayInputStream(in.getBytes("ISO-8859-1"));
        return is;
    }
    /*
    private String MD5(String s) {
    try {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] bytes = md.digest(s.getBytes("utf-8"));
        return toHex(bytes);
    }
    catch (Exception e) {
        throw new RuntimeException(e);
    }
}
————————————————
版权声明：本文为CSDN博主「wangfei0904306」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/wangfei0904306/java/article/details/71565968
     */

    //判断文件是否存在
    public static boolean cyber_fileIsExists(String strFile)
    {
        try
        {
            File f=new File(strFile);
            if(!f.exists())
            {
                return false;
            }

        }
        catch (Exception e)
        {
            return false;
        }

        return true;
    }

    public static void cyber_downloadFile1(String NetPath ,String local_path ,String filenameV) {
        try{
            //下载路径，如果路径无效了，可换成你的下载路径
            String url =NetPath;// "http://c.qijingonline.com/test.mkv";
            //String path =local_path;// Environment.getExternalStorageDirectory().getAbsolutePath();

            final long startTime = System.currentTimeMillis();
            //Log.i("DOWNLOAD","startTime="+startTime);
            //下载函数

            //  String filename=url.substring(url.lastIndexOf("/") + 1);
            if(url.startsWith("http://")==true) {
                //获取文件名
                URL myURL = new URL(url);

                URLConnection conn = myURL.openConnection();
                //取不到大小的话就试试请求加上下面这句
                conn.setRequestProperty("Accept-Encoding", "identity");
                conn.connect();
                InputStream is = conn.getInputStream();
                int fileSize = conn.getContentLength();//根据响应获取文件大小
                if (fileSize <= 0) throw new RuntimeException("无法获知文件大小 ");
                if (is == null) throw new RuntimeException("stream is null");
                File file1 = new File(local_path);
                if (!file1.exists()) {
                    file1.mkdirs();
                }

                //把数据存入路径+文件名
                FileOutputStream fos = new FileOutputStream(local_path + "/" + filenameV);
                //byte buf[] = new byte[524288];
                byte buf[] = new byte[5242880];
                int downLoadFileSize = 0;
                do {
                    //循环读取
                    int numread = is.read(buf);
                    if (numread == -1) {
                        break;
                    }
                    fos.write(buf, 0, numread);
                    downLoadFileSize += numread;
                    //更新进度条
                } while (true);

                // Log.i("DOWNLOAD","download success");
                // Log.i("DOWNLOAD","totalTime="+ (System.currentTimeMillis() - startTime));

                is.close();
            }
            //https
            if(url.startsWith("https://")==true) {
                SSLContext sslContext=SSLContext.getInstance("SSL");
                TrustManager[] tm={new MyX509TrustManager()};
                //初始化
                sslContext.init(null, tm, new java.security.SecureRandom());
                //获取SSLSocketFactory对象
                SSLSocketFactory ssf=sslContext.getSocketFactory();

                //获取文件名
                URL myURL = new URL(url);

                // URLConnection conn = myURL.openConnection();
                HttpsURLConnection conn=(HttpsURLConnection)myURL.openConnection();
                //取不到大小的话就试试请求加上下面这句
                conn.setRequestProperty("Accept-Encoding", "identity");
                conn.setUseCaches(false);
                // conn.setRequestMethod(requestMethod);
                //设置当前实例使用的SSLSoctetFactory
                conn.setSSLSocketFactory(ssf);

                conn.connect();
                InputStream is = conn.getInputStream();
                int fileSize = conn.getContentLength();//根据响应获取文件大小
                if (fileSize <= 0) throw new RuntimeException("无法获知文件大小 ");
                if (is == null) throw new RuntimeException("stream is null");
                File file1 = new File(local_path);
                if (!file1.exists()) {
                    file1.mkdirs();
                }

                //把数据存入路径+文件名
                FileOutputStream fos = new FileOutputStream(local_path + "/" + filenameV);
                byte buf[] = new byte[524288];//[1024];
                int downLoadFileSize = 0;
                do {
                    //循环读取
                    int numread = is.read(buf);
                    if (numread == -1) {
                        break;
                    }
                    fos.write(buf, 0, numread);
                    downLoadFileSize += numread;
                    //更新进度条
                } while (true);

                // Log.i("DOWNLOAD","download success");
                // Log.i("DOWNLOAD","totalTime="+ (System.currentTimeMillis() - startTime));

                is.close();
            }

        } catch (Exception ex) {
            // Log.e("DOWNLOAD", "error: " + ex.getMessage(), ex);
            CyberWinLogToFile.d("资源池子ex", NetPath+""+ex.getMessage());
        }
    }

    public static String cyber_getFileExt(String filepathorname) {
        String fileName=filepathorname;//f.getName();
        String prefix=fileName.substring(fileName.lastIndexOf(".")+1);
        return prefix;
    }

    public static String cyber_getFilePath(Context context) {

        if (Environment.MEDIA_MOUNTED.equals(Environment.MEDIA_MOUNTED) || !Environment.isExternalStorageRemovable()) {//如果外部储存可用
            return context.getExternalFilesDir(null).getPath();//获得外部存储路径,默认路径为 /storage/emulated/0/Android/data/com.waka.workspace.logtofile/files/Logs/log_2016-03-14_16-15-09.log
        } else {
            return context.getFilesDir().getPath();//直接存在/data/data里，非root手机是看不到的
        }
    }
    public static String cyber_md5(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(s.getBytes("utf-8"));
            return toHex(bytes);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public  static String MD5(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(s.getBytes("utf-8"));
            return toHex(bytes);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String toHex(byte[] bytes) {

        final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
        StringBuilder ret = new StringBuilder(bytes.length * 2);
        for (int i=0; i<bytes.length; i++) {
            ret.append(HEX_DIGITS[(bytes[i] >> 4) & 0x0f]);
            ret.append(HEX_DIGITS[bytes[i] & 0x0f]);
        }
        return ret.toString();
    }

    //2022-6-24
    //计算时差
    public static long getTimeMillis(String strTime) {
        long returnMillis = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        Date d = null;
        try {
            d = sdf.parse(strTime);
            returnMillis = d.getTime();
        } catch (ParseException e) {
            //   Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
        return returnMillis;
    }
    public static  String getTimeExpend(String startTime, String endTime){
        //传入字串类型 2016/06/28 08:30
        long longStart = getTimeMillis(startTime); //获取开始时间毫秒数
        long longEnd = getTimeMillis(endTime);  //获取结束时间毫秒数
        long longExpend = longEnd - longStart;  //获取时间差

        long longHours = longExpend / (60 * 60 * 1000); //根据时间差来计算小时数
        long longMinutes = (longExpend - longHours * (60 * 60 * 1000)) / (60 * 1000);   //根据时间差来计算分钟数

        return longHours + ":" + longMinutes;
    }

    public static  long getTimeExpendMinute(String startTime, String endTime){
        //传入字串类型 2016/06/28 08:30
        long longStart = getTimeMillis(startTime); //获取开始时间毫秒数
        long longEnd = getTimeMillis(endTime);  //获取结束时间毫秒数
        long longExpend = longEnd - longStart;  //获取时间差


        long longMinutes = (longExpend ) / (60 * 1000);   //根据时间差来计算分钟数

        return  longMinutes;
    }

    //2022-7-27 WIFI判断
    /**
     * 检查网络是否可用
     *
     * @param paramContext
     * @return
     */
    public static boolean checkEnableWifi(Context paramContext) {
        @SuppressLint("WrongConstant") NetworkInfo localNetworkInfo = ((ConnectivityManager) paramContext.getSystemService("connectivity")).getActiveNetworkInfo();
        if ((localNetworkInfo != null) && (localNetworkInfo.isAvailable()))
            return true;
        return false;
    }

    /**
     * 获取当前ip地址
     *
     * @param context
     * @return
     */
    public static String getLocalIpAddress(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int i = wifiInfo.getIpAddress();
            return int2ip(i);
        } catch (Exception ex) {
            return "0";
        }
    }

    //
    public static String getLocalIpMask(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            //  WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();


            return FormatString(dhcpInfo.netmask) ;
        } catch (Exception ex) {
            return "0";
        }
    }
    public static String FormatString(int value){
        String strValue="";
        byte[] ary = intToByteArray(value);
        for(int i=ary.length-1;i>=0;i--){
            strValue += (ary[i] & 0xFF);
            if(i>0){
                strValue+=".";
            }
        }
        return strValue;
    }
    public  static byte[] intToByteArray(int value){
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++){
            int offset = (b.length - 1 - i) * 8;
            b[i] = (byte) ((value >>> offset) & 0xFF);
        }
        return b;
    }

    /**
     * 将ip的整数形式转换成ip形式
     *
     * @param ipInt
     * @return
     */
    public static String int2ip(int ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }




    /**
     * 根据Uri获取文件绝对路径，解决Android4.4以上版本Uri转换 兼容Android 10
     *
     * @param context
     * @param imageUri
     */
    public static String getFileAbsolutePath(Context context, Uri imageUri) {
        if (context == null || imageUri == null) {
            return null;
        }

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT) {
            return getRealFilePath(context, imageUri);
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && DocumentsContract.isDocumentUri(context, imageUri)) {
            if (isExternalStorageDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(imageUri)) {
                String id = DocumentsContract.getDocumentId(imageUri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            return uriToFileApiQ(context,imageUri);
        }
        else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(imageUri)) {
                return imageUri.getLastPathSegment();
            }
            return getDataColumn(context, imageUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
            return imageUri.getPath();
        }
        return null;
    }

    //此方法 只能用于4.4以下的版本
    private static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) {
            return null;
        }
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            String[] projection = {MediaStore.Images.ImageColumns.DATA};
            Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

//            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


    /**
     * Android 10 以上适配 另一种写法
     * @param context
     * @param uri
     * @return
     */
    private static String getFileFromContentUri(Context context, Uri uri) {
        if (uri == null) {
            return null;
        }
        String filePath;
        String[] filePathColumn = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME};
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(uri, filePathColumn, null,
                null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            try {
                filePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
                return filePath;
            } catch (Exception e) {
            } finally {
                cursor.close();
            }
        }
        return "";
    }

    /**
     * Android 10 以上适配
     * @param context
     * @param uri
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private static String uriToFileApiQ(Context context, Uri uri) {
        File file = null;
        //android10以上转换
        if (uri.getScheme().equals(ContentResolver.SCHEME_FILE)) {
            file = new File(uri.getPath());
        } else if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //把文件复制到沙盒目录
            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = contentResolver.query(uri, null, null, null, null);
            if (cursor.moveToFirst()) {
                String displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                try {
                    InputStream is = contentResolver.openInputStream(uri);
                    File cache = new File(context.getExternalCacheDir().getAbsolutePath(), Math.round((Math.random() + 1) * 1000) + displayName);
                    FileOutputStream fos = new FileOutputStream(cache);
                    FileUtils.copy(is, fos);
                    file = cache;
                    fos.close();
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file.getAbsolutePath();
    }


    /**
     * 通过文件路径 uri的转字符也可以
     * @param filePath
     * @return
     */
    public static String getMimeType(String filePath) {
        String ext = MimeTypeMap.getFileExtensionFromUrl(filePath);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
    }



}
