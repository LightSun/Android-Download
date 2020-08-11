package com.heaven7.android.download;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.connection.FileDownloadUrlConnection;

/**
 * download2 which is used when some google-phones, download pending all the time.
 * @since 1.0.4
 * @author heaven7
 */
public final class Downloader2 {

    private static final String TAG = "Downloader2";
    private static boolean sInit = false;
    private final SimpleDownloadCallback callback;

    public Downloader2(SimpleDownloadCallback callback) {
        this.callback = callback;
    }

    public void download(final Context context, DownloadTask dt){
        Log.d(TAG, "------- start to use FileDownloader -------");
        FileDownloader.getImpl().create(dt.getUrl())
                .setPath(dt.getSavePath(), false)
                .setCallbackProgressTimes(300)
                .setMinIntervalUpdateSpeed(400)
                .setTag(dt)
                .setForceReDownload(false)
                .setListener(new FileDownloadSampleListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Log.d(TAG, "pending: " + task.getUrl());
                        DownloadTask dt = (DownloadTask) task.getTag();
                        dt.setStatus(DownloadHelper.STATUS_PENDING);
                        dt.setExtraId(task.getId());
                    }
                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Log.d(TAG, "progress: " + task.getUrl());
                        DownloadTask dt = (DownloadTask) task.getTag();
                        dt.setStatus(DownloadHelper.STATUS_RUNNING);
                        dt.setTotalBytes(totalBytes);
                        dt.setDownloadBytes(soFarBytes);
                        dt.setExtraId(task.getId());
                        callback.onDownloadRunning(context, dt);
                    }
                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        Log.d(TAG, "error: " + task.getUrl());
                        DownloadTask dt = (DownloadTask) task.getTag();
                        dt.setStatus(DownloadHelper.STATUS_FAILED);
                        dt.setExtraId(task.getId());
                        callback.onDownloadFailed(context, dt);
                    }
                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                        Log.d(TAG, "connected: " + task.getUrl());
                    }
                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Log.d(TAG, "paused: " + task.getUrl());
                        DownloadTask dt = (DownloadTask) task.getTag();
                        dt.setStatus(DownloadHelper.STATUS_PAUSED);
                        dt.setExtraId(task.getId());
                        callback.onDownloadPaused(context, dt);
                    }
                    @Override
                    protected void completed(BaseDownloadTask task) {
                        Log.d(TAG, "completed: " + task.getUrl());
                        DownloadTask dt = (DownloadTask) task.getTag();
                        dt.setStatus(DownloadHelper.STATUS_SUCCESSFUL);
                        dt.setExtraId(task.getId());
                        callback.onDownloadSuccess(context, dt);
                    }
                    @Override
                    protected void warn(BaseDownloadTask task) {
                    }
                }).start();
    }
    public static void pause(int id){
        FileDownloader.getImpl().pause(id);
    }

    public static void init(Context context){
        if(sInit){
            return;
        }
        sInit = true;
        FileDownloader.setupOnApplicationOnCreate((Application) context.getApplicationContext())
                .connectionCreator(new FileDownloadUrlConnection
                        .Creator(new FileDownloadUrlConnection.Configuration()
                        .connectTimeout(15_000) // set connection timeout.
                        .readTimeout(600_000) // set read timeout.
                ))
                .commit();
    }
}
