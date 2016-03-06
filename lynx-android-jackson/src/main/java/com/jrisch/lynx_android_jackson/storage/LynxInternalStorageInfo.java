package com.jrisch.lynx_android_jackson.storage;

import android.content.Context;
import com.jrisch.lynx.IStorageInfo;
import java.io.File;

/**
 * Default implementation of {@link IStorageInfo} that will store the data in the private data folder for the application.
 */
public class LynxInternalStorageInfo implements IStorageInfo {

    private Context context;

    public LynxInternalStorageInfo(Context context) {

        this.context = context;
    }

    @Override
    public File getStorageRoot() {
        return context.getFilesDir();
    }

    @Override
    public boolean isStorageWritable() {
        return true;
    }
}
