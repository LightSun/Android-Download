package com.heaven7.android.download;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

import androidx.collection.LongSparseArray;

import com.heaven7.java.base.util.Disposable;
import com.heaven7.java.base.util.FileUtils;
import com.heaven7.java.base.util.Predicates;
import com.heaven7.java.base.util.Scheduler;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * the download helper use the system download manager to download
 * @author heaven7
 */
public final class DownloadHelper implements DownloadChangeObserver.Callback {

    public final static int STATUS_PENDING = DownloadManager.STATUS_PENDING;
    public final static int STATUS_RUNNING = DownloadManager.STATUS_RUNNING;
    public final static int STATUS_PAUSED = DownloadManager.STATUS_PAUSED;
    public final static int STATUS_SUCCESSFUL = DownloadManager.STATUS_SUCCESSFUL;
    public final static int STATUS_FAILED = DownloadManager.STATUS_FAILED;

    private final LongSparseArray<Params> mCallbacks = new LongSparseArray<>();
    private final DownloadChangeObserver mObserver = new DownloadChangeObserver(this);
    private final DownloadManager mDM;
    private final Context mContext;
    private final Scheduler mScheduler;
    private Disposable mTask;

    public DownloadHelper(Context context, Scheduler mScheduler) {
        this.mContext = context.getApplicationContext();
        this.mScheduler = mScheduler;
        this.mDM = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    /**
     * get scheduler
     * @since 1.0.4
     * @return the scheduler
     */
    public Scheduler getScheduler() {
        return mScheduler;
    }
    /**
     * download the url
     * @param url the url
     * @param callback the download callback
     * @return the download id
     * @since 1.0.3
     */
    public long download(String url, IDownloadCallback callback) {
        return download(new DownloadTask.Builder().setUrl(url).build(), callback);
    }

    /**
     * do download
     * @param task the download task
     * @param callback the callback
     * @return the download id
     */
    public long download(DownloadTask task, IDownloadCallback callback) {
        File file = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                FileUtils.getSimpleFileName(task.getUrl()));

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(task.getUrl()));
        request.setDestinationUri(Uri.fromFile(file));
        task.setSavePath(file.getAbsolutePath());

        callback.onPreDownload(mContext, task, request);

        long id = mDM.enqueue(request);
        task.setId(id);
        mCallbacks.put(id, new Params(task, callback));
        return id;
    }

    /**
     * cancel download
     * @param id the download id
     * @return true if canceled
     */
    public boolean cancel(long id){
        mCallbacks.remove(id);
        return mDM.remove(id) > 0;
    }
    public void removeCallback(long id){
        mCallbacks.remove(id);
    }
    public void registerAll(){
        registerDownloadReceiver();
        registerDownloadObserver();
    }
    public void unregisterAll(){
        unregisterDownloadReceiver();
        unregisterDownloadObserver();
    }
    public void registerDownloadReceiver(){
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        filter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
        filter.addAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
        mContext.registerReceiver(mReceiver, filter);
    }
    public void registerDownloadObserver(){
        mContext.getContentResolver().registerContentObserver(Uri.parse("content://downloads/my_downloads"),
                true, mObserver);
    }
    public void unregisterDownloadReceiver(){
        try {
            mContext.unregisterReceiver(mReceiver);
        }catch (Exception e){
            //ignore
        }
    }
    public void unregisterDownloadObserver(){
        try {
            mContext.getContentResolver().unregisterContentObserver(mObserver);
        }catch (Exception e){
            //ignore
        }
    }
    public void cancelObserve(){
        if(mTask != null){
            mTask.dispose();
            mTask = null;
        }
    }
    public List<Long> getDownloadIds(){
        int size = mCallbacks.size();
        if(size == 0){
            return Collections.emptyList();
        }
        List<Long> keys = new ArrayList<>();
        for (int i = 0 ; i < size ; i ++){
            keys.add(mCallbacks.keyAt(i));
        }
        return keys;
    }

    public void queryAll() {
        List<Long> list = getDownloadIds();
        if(!Predicates.isEmpty(list)){
            queryAll(list);
        }
    }
    public void queryAll(List<Long> ids) {
        for (Long id: ids){
            if(!query(id, null)){
                System.out.println("Download : >> query download failed. id = " + id);
            }
        }
    }
    public boolean query(long id, Integer downloadFlags){
        return query(id, downloadFlags, true);
    }
    public DownloadTask queryAny(long id, Integer downloadFlags){
        DownloadTask task = new DownloadTask();
        task.setId(id);
        if(query0(id, downloadFlags, task)){
            return task;
        }
        return null;
    }
    private boolean query(long id, Integer downloadFlags, boolean callback){
        Params params = mCallbacks.get(id);
        if(params == null){
            return false;
        }
        if(!query0(id, downloadFlags, params.task)){
            params.task.setStatus(DownloadHelper.STATUS_FAILED);
        }
        if(callback){
            params.callback.onQueryResult(mContext,  params.task);
        }
        return true;
    }
    private boolean query0(long id, Integer downloadFlags, DownloadTask task){
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(id);
        if(downloadFlags != null){
            query.setFilterByStatus(downloadFlags);
        }
        Cursor cursor = mDM.query(query);
        try {
            if (cursor.moveToFirst()) {
                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                long totalBytes = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                long alreadyBytes = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                String reason = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_REASON));
                long lastModifyTime = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP));
                String uriStr = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

                task.setTotalBytes(totalBytes);
                task.setDownloadBytes(alreadyBytes);
                task.setLastModifyTime(lastModifyTime);
                task.setReason(reason);
                task.setStatus(status);
                if(uriStr != null){
                    task.setLocalUri(Uri.parse(uriStr));
                }
                return true;
            }else {
                System.err.println("no download state for: " + task.getUrl());
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        finally {
            cursor.close();
        }
        return true;
    }
    private void dispatchNotificationClicked(long id){
        Params params = mCallbacks.get(id);
        if(params == null){
            return;
        }
        query(id, null, false);
        params.callback.onNotificationClicked(mContext, params.task);
    }
    private void dispatchViewDownload(long id){
        Params params = mCallbacks.get(id);
        if(params == null){
            return;
        }
        params.callback.startViewDownload(mContext, params.task);
    }
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
            if(DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())){
                query(id, null);
            }else if(DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(intent.getAction())){
                long[] ids = intent.getLongArrayExtra(DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS);
                if(ids != null){
                    for (long lid : ids){
                        dispatchNotificationClicked(lid);
                    }
                }else {
                    dispatchNotificationClicked(id);
                }
            }else if(DownloadManager.ACTION_VIEW_DOWNLOADS.equals(intent.getAction())){
                dispatchViewDownload(id);
            }
        }
    };

    @Override
    public void onContentChanged(boolean selfChange) {
        //Log.d("DH", "onContentChanged: selfChange = " + selfChange);
        mTask = mScheduler.newWorker().schedule(new Runnable() {
            @Override
            public void run() {
                queryAll();
            }
        });
    }

    private static class Params{
        final DownloadTask task;
        final IDownloadCallback callback;

        Params(DownloadTask task, IDownloadCallback callback) {
            this.task = task;
            this.callback = callback;
        }
    }
}