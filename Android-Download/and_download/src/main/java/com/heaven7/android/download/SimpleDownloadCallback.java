package com.heaven7.android.download;

import android.app.Application;
import android.app.DownloadManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.connection.FileDownloadUrlConnection;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public abstract class SimpleDownloadCallback implements IDownloadCallback {

    private static final String TAG = "SimpleDownloadCallback";
    private final AtomicBoolean mSuccessed = new AtomicBoolean();
    private final DownloadHelper dh;

    public SimpleDownloadCallback(DownloadHelper dh) {
        this.dh = dh;
    }

    public DownloadHelper getDownloadHelper() {
        return dh;
    }

    @Override
    public void onPreDownload(Context mContext, DownloadTask task, DownloadManager.Request request) {
        //移动网络情况下是否允许漫游
        request.setAllowedOverRoaming(false);
        //display notification
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setVisibleInDownloadsUi(true);

        //request.setTitle("通知标题，随意修改");
        //request.setDescription("新版***下载中...");
    }
    @Override
    public void startViewDownload(Context context, DownloadTask task) {
        System.out.println("startViewDownload");
    }

    @Override
    public void onNotificationClicked(Context context, DownloadTask task) {
        System.out.println("onNotificationClicked");
    }

    @Override
    public void onQueryResult(Context context, DownloadTask task) {
        switch (task.getStatus()) {
            case DownloadHelper.STATUS_PAUSED:
                onDownloadPaused(context, task);
                break;
            case DownloadHelper.STATUS_PENDING:
                onDownloadPending(context, task);
                break;
            case DownloadHelper.STATUS_RUNNING:
                onDownloadRunning(context, task);
                break;
            case DownloadHelper.STATUS_SUCCESSFUL:
                if(mSuccessed.compareAndSet(false, true)){
                    onDownloadSuccess(context, task);
                }
                break;
            case DownloadHelper.STATUS_FAILED:
                onDownloadFailed(context, task);
                break;

            default:
                System.err.println("download known error: " + task.getStatus());
                onUnknownError(context, task);
                break;
        }
    }

    protected void onUnknownError(Context context, DownloadTask task) {

    }

    protected void onDownloadPending(final Context context, final DownloadTask task) {
        //if pending 5 seconds. we use another to download.
        scheduleDelay(new Runnable() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Downloader2.init(context);
                        Downloader2 d = new Downloader2(SimpleDownloadCallback.this);
                        d.download(context, task);
                    }
                });
            }
        }, 5000);
    }
    protected void onDownloadSuccess(Context context, DownloadTask task) {

    }
    protected void onDownloadFailed(Context context, DownloadTask task) {

    }
    protected void onDownloadPaused(Context context, DownloadTask task) {

    }
    protected void onDownloadRunning(Context context, DownloadTask task) {

    }

    /**
     * schedule the download task delay
     * @param task the task
     * @param delay the delay
     * @since 1.0.4
     */
    protected void scheduleDelay(Runnable task, long delay){

    }
}
