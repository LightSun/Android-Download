package com.heaven7.android.download;

import android.app.DownloadManager;
import android.content.Context;

public interface IDownloadCallback {

    void onPreDownload(Context context, DownloadTask task, DownloadManager.Request request);

    void onQueryResult(Context context, DownloadTask task);

    void onNotificationClicked(Context context, DownloadTask task);

    void startViewDownload(Context context, DownloadTask task);
}

 /*switch (status) {
         case DownloadManager.STATUS_PAUSED:
         break;
         case DownloadManager.STATUS_PENDING:
         break;
         case DownloadManager.STATUS_RUNNING:
         break;
         case DownloadManager.STATUS_SUCCESSFUL:
         break;
         case DownloadManager.STATUS_FAILED:
         break;
         }*/