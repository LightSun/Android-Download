package com.heaven7.android.download.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.heaven7.android.download.DownloadTask;
import com.heaven7.android.download.Downloader;
import com.heaven7.android.download.SimpleDownloadCallback;
import com.heaven7.core.util.Logger;
import com.heaven7.java.base.util.Disposable;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_APP_INSTALL = 1;
    private static Uri sApkUri;
    private Disposable mTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickDownload(View view) {
        String url = "https://d2.aoc5566.com//android2/20190325/360shoujizhushou_ali213.apk";
        DownloadTask task = new DownloadTask();
        task.setUrl(url);
        long id = Downloader.getDownloadHelper().download(task, new SimpleDownloadCallback(Downloader.getDownloadHelper()) {
            @Override
            protected void onDownloadSuccess(Context context, DownloadTask task) {
                setPermission(task.getSavePath());
                Logger.d(TAG, "onDownloadSuccess", task.getUrl());
                Logger.d(TAG, "onDownloadSuccess", task.getSavePath());
                Logger.d(TAG, "onDownloadSuccess", task.getLocalUri().toString());
                Uri uriForFile = FileProviderUtils.getUriForFile(context, task.getSavePath());
                installAPK(MainActivity.this, uriForFile);
            }

            @Override
            protected void onDownloadFailed(Context context, DownloadTask task) {
                Logger.d(TAG, "onDownloadFailed", task.getUrl());
            }

            @Override
            protected void onDownloadRunning(Context context, DownloadTask task) {
                Logger.d(TAG, "onDownloadRunning",  "percent = "
                        + (task.getDownloadBytes() * 1f / task.getTotalBytes()) * 100 + "/100"
                );

            }
        });
    }
    private void setPermission(String absolutePath) {
        String command = "chmod " + "777" + " " + absolutePath;
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void installAPK(Activity activity, Uri apkUri) {
        sApkUri = apkUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            boolean hasInstallPermission = isHasInstallPermissionWithO(activity);
            if (!hasInstallPermission) {
                startInstallPermissionSettingActivity(activity);
                return;
            }
        }
        installAPK0(activity, apkUri);
    }
    /**
     * 开启设置安装未知来源应用权限界面
     * @param context
     */
    @RequiresApi (api = Build.VERSION_CODES.O)
    private static void startInstallPermissionSettingActivity(Context context) {
        if (context == null){
            return;
        }
        Uri uri = Uri.parse("package:"+ context.getPackageName());
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, uri);
        ((Activity)context).startActivityForResult(intent, REQUEST_CODE_APP_INSTALL);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static boolean isHasInstallPermissionWithO(Context context){
        if (context == null){
            return false;
        }
        return context.getPackageManager().canRequestPackageInstalls();
    }
    public static void installAPK0(Activity activity, Uri apkUri) {
        System.out.println("start install apk: " + apkUri);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 24) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        }
        activity.startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == REQUEST_CODE_APP_INSTALL){
                installAPK0(this, sApkUri);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mTask != null){
            mTask.dispose();
            mTask = null;
        }
    }
}
