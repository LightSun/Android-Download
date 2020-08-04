package com.heaven7.android.download.app;

import android.content.Context;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import java.io.File;

/**
 * @author heaven7
 */
public final class FileProviderUtils {

    public static Uri getUriForFile(Context context, String file) {
        return getUriForFile(context, new File(file));
    }

    public static Uri getUriForFile(Context context, File file) {
        if (file == null) {
            throw new NullPointerException();
        }
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(context, getAuthority(context), file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

    public static String getAuthority(Context activity){
        return activity.getPackageName() + ".AppFileProvider";
    }

}
