package com.heaven7.android.download.app;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.heaven7.android.download.DownloadTask;
import com.heaven7.android.download.Downloader;
import com.heaven7.android.download.SimpleDownloadCallback;
import com.heaven7.core.util.Logger;
import com.heaven7.core.util.Toaster;
import com.heaven7.java.base.util.Disposable;
import com.heaven7.java.base.util.FileUtils;
import com.heaven7.java.pc.schedulers.Schedulers;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Disposable mTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickDownload(View view) {
        String url = "https://d2.aoc5566.com//android2/20190325/360shoujizhushou_ali213.apk";
        File file = new File(getCacheDir(), FileUtils.getSimpleFileName(url));
        if(file.exists()){
            Toaster.show(this, "already exists. " + file.getAbsolutePath());
            return;
        }
        DownloadTask task = new DownloadTask();
        task.setUrl(url);
       // task.setSaveUri(FileProviderUtils.getUriForFile(this, file));
        task.setSavePath(file.getAbsolutePath()); //must be file
        long id = Downloader.getDownloadHelper().download(task, new SimpleDownloadCallback(Downloader.getDownloadHelper()) {
            @Override
            protected void onDownloadSuccess(Context context, DownloadTask task) {
                Logger.d(TAG, "onDownloadSuccess", task.getUrl());
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mTask != null){
            mTask.dispose();
            mTask = null;
        }
    }
}
