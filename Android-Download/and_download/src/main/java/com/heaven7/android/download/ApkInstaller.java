package com.heaven7.android.download;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;

import static android.app.Activity.RESULT_OK;

/**
 * the apk installer.
 * @author heaven7
 * @since 1.0.2
 */
public class ApkInstaller {
    public static final int REQ_INSTALL_APK = 8424;

    private final Context context;
    private final Class<?> mEntryClass;
    private UpdateReceiver mReceiver;
    private int mReq;
    private Uri mUri;

    public ApkInstaller(Context context, Class<?> entry) {
        this.context = context;
        this.mEntryClass = entry;
    }
    public void install(Uri uri, int req){
        if(mReceiver == null){
            mReceiver = new UpdateReceiver();
            IntentFilter filter = new IntentFilter(Intent.ACTION_MY_PACKAGE_REPLACED);
            filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
            context.registerReceiver(mReceiver, filter);
        }
        mReq = req;
        mUri = uri;
        InstallUtils.activeInstall(context, uri, req);
    }

    public void install(Uri uri){
       install(uri, REQ_INSTALL_APK);
    }

    public void onDestroy(){
       if(mReceiver != null){
           try {
               context.unregisterReceiver(mReceiver);
           }catch (Exception e){

           }
           mReceiver = null;
       }
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        if(resultCode == RESULT_OK){
            if(requestCode == mReq){
                InstallUtils.installAPK(context, mUri);
            }
        }
    }
    protected void onUpdateSuccess(){
        Intent intent1 = new Intent(context, mEntryClass);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);
    }

    private class UpdateReceiver extends BroadcastReceiver {

        //private static final String TAG = "UpdateReceiver";

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_PACKAGE_REPLACED.equals(action) || Intent.ACTION_MY_PACKAGE_REPLACED.equals(action)) {
                try {
                    String scheme = intent.getScheme();
                    String packageName = intent.getData().getSchemeSpecificPart();
                    Log.d("UpdateReceiver", "installed: scheme = " + scheme + " ,packageName = " + packageName);
                    if (context.getPackageName().equals(packageName)) {
                        onUpdateSuccess();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
