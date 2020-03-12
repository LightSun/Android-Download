package com.heaven7.android.download;

import android.content.Context;

import com.heaven7.java.base.util.Disposable;
import com.heaven7.java.base.util.Scheduler;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * downloader just wrap the {@linkplain DownloadHelper} to use easily.
 * @author heaven7
 */
public final class Downloader {

    private static class Creator{
        static final Downloader INSTANCE = new Downloader();
    }
    private Downloader(){}

    private DownloadHelper mDH;
    /**
     * init the downloader.
     * @param context the context
     */
    public static void init(Context context){
        Creator.INSTANCE.mDH = new DownloadHelper(context);
        getDownloadHelper().registerDownloadReceiver();
    }

    /**
     * get download helper
     * @return the download helper.
     */
    public static DownloadHelper getDownloadHelper(){
        return Creator.INSTANCE.mDH;
    }
    public static Downloader get(){
        return Creator.INSTANCE;
    }

    /**
     * observe a download task at fixed period.
     * @param id the id of download task
     * @param scheduler the scheduler
     * @param periodMills the period in mills
     * @return the disposable
     */
    public Disposable observe(final long id, Scheduler scheduler, long periodMills){
        return scheduler.newWorker().schedulePeriodically(new Runnable() {
            @Override
            public void run() {
                if(!mDH.query(id, null)){
                    System.err.println("query download failed. id = " + id);
                }
            }
        }, 0, periodMills, TimeUnit.MILLISECONDS);
    }

    /**
     * observe all download tasks at fixed period.
     * @param scheduler the scheduler
     * @param periodMills the period in mills
     * @return the disposable
     */
    public Disposable observeAll(Scheduler scheduler, long periodMills){
        return scheduler.newWorker().schedulePeriodically(new Runnable() {
            @Override
            public void run() {
                mDH.queryAll();
            }
        }, 0, periodMills, TimeUnit.MILLISECONDS);
    }
    /**
     * observe all download tasks at fixed period.
     * @param ids the task ids to download.
     * @param scheduler the scheduler
     * @param periodMills the period in mills
     * @return the disposable
     */
    public Disposable observeAll(final List<Long> ids, Scheduler scheduler, long periodMills){
        return scheduler.newWorker().schedulePeriodically(new Runnable() {
            @Override
            public void run() {
                mDH.queryAll(ids);
            }
        }, 0, periodMills, TimeUnit.MILLISECONDS);
    }
}
