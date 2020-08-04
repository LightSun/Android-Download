package com.heaven7.android.download.app;

import android.app.Application;

import com.heaven7.android.download.Downloader;
import com.heaven7.java.pc.schedulers.Schedulers;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Downloader.init(this, Schedulers.io());
    }
}
