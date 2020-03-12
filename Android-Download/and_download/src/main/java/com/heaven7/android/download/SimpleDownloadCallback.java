package com.heaven7.android.download;

import android.app.DownloadManager;
import android.content.Context;

public abstract class SimpleDownloadCallback implements IDownloadCallback {

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

    }

    @Override
    public void onNotificationClicked(Context context, DownloadTask task) {

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
                onDownloadSuccess(context, task);
                break;
            case DownloadHelper.STATUS_FAILED:
                onDownloadFailed(context, task);
                break;
        }
    }
    protected void onDownloadPending(Context context, DownloadTask task) {

    }
    protected void onDownloadSuccess(Context context, DownloadTask task) {

    }
    protected void onDownloadFailed(Context context, DownloadTask task) {

    }
    protected void onDownloadPaused(Context context, DownloadTask task) {

    }
    protected void onDownloadRunning(Context context, DownloadTask task) {

    }
}
