package com.heaven7.android.download;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.Nullable;

import static android.app.Activity.RESULT_OK;

/**
 * the apk installer.
 * @author heaven7
 * @since 1.0.2
 */
public final class ApkInstaller {

    private final Context context;
    private int mReq;
    private Uri mUri;

    public ApkInstaller(Context context) {
        this.context = context;
    }
    public void install(Uri uri, int req){
        mReq = req;
        mUri = uri;
        InstallUtils.activeInstall(context, uri, req);
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        if(resultCode == RESULT_OK){
            if(requestCode == mReq){
                InstallUtils.installAPK(context, mUri);
            }
        }
    }
}
