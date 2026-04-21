package com.fairyalliance.smartanswer;


import android.content.Context;
import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import CyberWinPHP.Cyber_CPU.Cyber_Public_Var;

public class CyberWinLogToFile {

    private static String TAG = "LogToFile";

    private static String logPath = null;//log日志存放路径

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US);//日期格式;

    private static Date date = new Date();//因为log日志是使用日期命名的，使用静态成员变量主要是为了在整个程序运行期间只存在一个.log文件中;

    /**
     * 初始化，须在使用之前设置，最好在Application创建时调用
     *
     * @param context
     */
    public static void init(Context context) {
        logPath = getFilePath(context) + "/Logs";//获得文件储存路径,在后面加"/Logs"建立子文件夹
    }

    /**
     * 获得文件存储路径
     *
     * @return
     */
    public static String getFilePath(Context context) {

        if (Environment.MEDIA_MOUNTED.equals(Environment.MEDIA_MOUNTED) || !Environment.isExternalStorageRemovable()) {//如果外部储存可用
            return context.getExternalFilesDir(null).getPath();//获得外部存储路径,默认路径为 /storage/emulated/0/Android/data/com.waka.workspace.logtofile/files/Logs/log_2016-03-14_16-15-09.log
        } else {
            return context.getFilesDir().getPath();//直接存在/data/data里，非root手机是看不到的
        }
    }

    private static final char VERBOSE = 'v';

    private static final char DEBUG = 'd';

    private static final char INFO = 'i';

    private static final char WARN = 'w';

    private static final char ERROR = 'e';

    public static void v(String tag, String msg) {
        writeToFile("VERBOSE", tag, msg);
    }

    public static void deleteAllLog( Context context){
        String pPath = getFilePath(context) + "/Logs";//获得文件储存路径,在后面加"/Logs"建立子文件夹

        File dir = new File(pPath);
        deleteDirWihtFile(dir);
    }
    //删除文件
    public static void deleteDirWihtFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDirWihtFile(file); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }

    public static void d(String tag, String msg) {

        if(Cyber_Public_Var.cwpd_debug_mod==true) {
            //调试模式下面
            writeToFile("debug", tag, msg);
        }
    }

    public static void d_windows(String capturetype, String type, String s ){
        //如果父路径不存在
        writeToFile(capturetype, type, s);
    }
    // writeToFileInner(type,tag,msg,true);
    public static void dOver(String tag, String msg) {

        if(Cyber_Public_Var.cwpd_debug_mod==true) {
            //调试模式下面
            writeToFileInner("debug",tag,msg,false,false);
        }
    }

    public static void i(String tag, String msg) {
        writeToFile("info", tag, msg);
    }

    public static void w(String tag, String msg) {
        writeToFile("war", tag, msg);
    }

    public static void e(String tag, String msg) {
        writeToFile("error", tag, msg);
    }

    /**
     * 将log信息写入文件中
     *
     * @param type
     * @param tag
     * @param msg
     */
    private static void writeToFile(String type, String tag, String msg) {
        writeToFileInner(type,tag,msg,true,true);
    }
    private static void writeToFileInner(String type, String tag, String msg ,boolean isAppend,boolean isFileNameAppenDate) {
        if(Cyber_Public_Var.cwpd_debug_mod==false) {
            //不写日志
            return;

        }
        if (null == logPath) {
            //Log.e(TAG, "logPath == null ，未初始化LogToFile");
            return;
        }

        //2019-10-22
        long time=System.currentTimeMillis();//long now = android.os.SystemClock.uptimeMillis();
        SimpleDateFormat format_old=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH");
        Date d1=new Date(time);
        String t1=format.format(d1);

        if(isFileNameAppenDate==false){
            t1="";//不加时间的文件名
        }


        //String fileName = logPath + "/log_" + tag+dateFormat.format(new Date()) + ".log";//log日志名，使用时间命名，保证不重复

        //如果父路径不存在
        File file = new File(logPath);
        if (!file.exists()) {
            file.mkdirs();//创建父路径
        }

        String logPath2022=logPath+"/"+type+"/";

        file = new File(logPath2022);
        if (!file.exists()) {
            file.mkdirs();//创建父路径
        }



        String fileName = logPath2022 + "/" + tag+t1 + ".log";//log日志名，使用时间命名，保证不重复
        String log = dateFormat.format(date) + " " + type + "\n " + tag+"\n========================================================="
                + "\n " + msg + "\n";//log日志内容，可以自行定制


        FileOutputStream fos = null;//FileOutputStream会自动调用底层的close()方法，不用关闭
        BufferedWriter bw = null;
        try {



            fos = new FileOutputStream(fileName, isAppend);//这里的第二个参数代表追加还是覆盖，true为追加，flase为覆盖
            bw = new BufferedWriter(new OutputStreamWriter(fos));
            bw.newLine();
            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
            //  time1.setText("Date获取当前日期时间"+simpleDateFormat.format(date));
            bw.write(simpleDateFormat.format(date));
            bw.newLine();
            bw.write(log);
            bw.newLine();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.close();//关闭缓冲流
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
