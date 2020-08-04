package com.heaven7.android.download;

import android.content.Context;

import com.heaven7.java.base.util.Scheduler;

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
     * @param observe the observe scheduler
     */
    public static void init(Context context, Scheduler observe){
        Creator.INSTANCE.mDH = new DownloadHelper(context, observe);
        getDownloadHelper().registerAll();
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
}
