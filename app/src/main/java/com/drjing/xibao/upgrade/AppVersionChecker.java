package com.drjing.xibao.upgrade;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.kristain.common.utils.DateTimeUtils;
import com.kristain.common.utils.FileUtils;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * APP版本检测/更新
 * Created by kristain on 15/12/19.
 */
public class AppVersionChecker {

    public static final String SERVER_VERSION = "version";
    public static final String CHANGE_LOG = "changeLog";
    public static final String APK_URL = "apkURL";
    public final static String SD_FOLDER = FileUtils.gainSDCardPath() + "/AppVersionChecker/";
    private static AppVersionChecker mChecker = new AppVersionChecker();
    private static final String TAG = AppVersionChecker.class.getSimpleName();

    public static void requestCheck(Context mContext, String strURL) {
        mChecker.getLatestVersion(mContext, strURL);
    }

    /**
     * 获取最新版本
     *
     * @param mContext
     * @param strURL
     * @return
     */
    private JSONObject getLatestVersion(final Context mContext, String strURL) {
       /* ToolHTTP.get(strURL, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, final JSONObject response) {
                if (getLocalVersion(mContext) >= response.optDouble(SERVER_VERSION, 0.0)) {
                    ToolAlert.toastShort(mContext, "已是最新版本");
                } else {
                    ToolAlert.dialog(mContext, "版本升级", "检测到有新版本，是否马上更新？",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        downLoadApk(mContext, response.getString("apkURL"));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });*/
        return null;
    }

    /**
     * 从服务器中下载APK
     */
    public static void downLoadApk(final Context mContext, final String downURL) {
        final ProgressDialog pd; // 进度条对话框
        pd = new ProgressDialog(mContext);
        pd.setCancelable(false);// 必须一直下载完，不可取消
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("正在下载");
        pd.show();
        new Thread() {
            @Override
            public void run() {
                try {
                    File file = downloadFile(downURL, pd);
                    sleep(3000);
                    installApk(mContext, file);
                    // 结束掉进度条对话框
                    pd.dismiss();
                } catch (Exception e) {
                    pd.dismiss();
                    Log.e(TAG, "下载新版本失败，原因：" + e.getMessage());
                }
            }
        }.start();
    }

    /**
     * 从服务器下载最新更新文件
     *
     * @param path 下载路径
     * @param pd   进度条
     * @return
     * @throws Exception
     */
    private static File downloadFile(String path, ProgressDialog pd) throws Exception {
        // 如果相等的话表示当前的sdcard挂载在手机上并且是可用的
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            // 获取到文件的大小
            pd.setMax(conn.getContentLength());
            InputStream is = conn.getInputStream();
            String fileName = SD_FOLDER + DateTimeUtils.formatDateTime(new java.util.Date(), "yyyyMMddHHmm") + ".apk";
            File file = new File(fileName);
            // 目录不存在创建目录
            if (!file.getParentFile().exists())
                file.getParentFile().mkdirs();
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len;
            int total = 0;
            while ((len = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                total += len;
                // 获取当前下载量
                pd.setProgress(total);
            }
            fos.close();
            bis.close();
            is.close();
            return file;
        } else {
            throw new IOException("未发现有SD卡");
        }
    }

    /**
     * 安装apk
     */
    private static void installApk(Context mContext, File file) {
        Uri fileUri = Uri.fromFile(file);
        Intent it = new Intent();
        it.setAction(Intent.ACTION_VIEW);
        it.setDataAndType(fileUri, "application/vnd.android.package-archive");
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 防止打不开应用
        mContext.startActivity(it);
    }

    /**
     * 获取应用程序版本（versionName）
     *
     * @return 当前应用的版本号
     */
    public static  int getLocalVersion(Context context) {
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            Log.e(TAG, "获取应用程序版本失败，原因：" + e.getMessage());
            return 1;
        }
        Log.e(TAG, "获取应用程序版本：" + info.versionCode+ info.packageName+info.versionName);
        return Integer.valueOf(info.versionCode);
    }
}
