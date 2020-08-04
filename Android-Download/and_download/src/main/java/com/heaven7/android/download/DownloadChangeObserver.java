package com.heaven7.android.download;

import android.database.ContentObserver;
import android.os.Handler;

class DownloadChangeObserver extends ContentObserver {

    private final Callback callback;

    public DownloadChangeObserver(Callback callback) {
        super(new Handler());
        this.callback = callback;
    }

    @Override
    public void onChange(boolean selfChange) {
        callback.onContentChanged(selfChange);
    }

    public interface Callback{
        void onContentChanged(boolean selfChange);
    }
}
