package com.heaven7.android.download;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.RequiresApi;

/**
 * the install utils used to install apk
 * @author heaven7
 * @since 1.0.2
 */
public final class InstallUtils {

    public static void activeInstall(Context activity, Uri apkUri, int req) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            boolean hasInstallPermission = isHasInstallPermissionWithO(activity);
            if (!hasInstallPermission) {
                startInstallPermissionSettingActivity(activity, req);
                return;
            }
        }
        installAPK(activity, apkUri);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void startInstallPermissionSettingActivity(Context context, int req) {
        if (context == null){
            return;
        }
        Uri uri = Uri.parse("package:"+ context.getPackageName());
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, uri);
        ((Activity)context).startActivityForResult(intent, req);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static boolean isHasInstallPermissionWithO(Context context){
        if (context == null){
            return false;
        }
        return context.getPackageManager().canRequestPackageInstalls();
    }
    public static void installAPK(Context activity, Uri apkUri) {
       // System.out.println("start install apk: " + apkUri);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 24) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        activity.startActivity(intent);
    }
}
