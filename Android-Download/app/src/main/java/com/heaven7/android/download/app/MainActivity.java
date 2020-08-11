package com.heaven7.android.download.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.heaven7.android.download.ApkInstaller;
import com.heaven7.android.download.DownloadTask;
import com.heaven7.android.download.Downloader;
import com.heaven7.android.download.SimpleDownloadCallback;
import com.heaven7.core.util.Logger;
import com.heaven7.java.base.util.Disposable;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private final ApkInstaller mInstaller = new ApkInstaller(this, MainActivity.class);
    private Disposable mTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickDownload(View view) {
        //String url = "https://d2.aoc5566.com//android2/20190325/360shoujizhushou_ali213.apk";
        String url = "https://common-dev.oss-cn-beijing.aliyuncs.com/shipper-debug.apk";
        DownloadTask task = new DownloadTask();
        task.setUrl(url);
         Downloader.getDownloadHelper().download(task, new SimpleDownloadCallback(Downloader.getDownloadHelper()) {
            @Override
            protected void onDownloadSuccess(Context context, DownloadTask task) {
                setPermission(task.getSavePath());
                Logger.d(TAG, "onDownloadSuccess", task.getUrl());
                Logger.d(TAG, "onDownloadSuccess", task.getSavePath());
                Logger.d(TAG, "onDownloadSuccess", task.getLocalUri().toString());
                Uri uriForFile = FileProviderUtils.getUriForFile(context, task.getSavePath());
                mInstaller.install(uriForFile);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mInstaller.onActivityResult(requestCode, resultCode, data);
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
