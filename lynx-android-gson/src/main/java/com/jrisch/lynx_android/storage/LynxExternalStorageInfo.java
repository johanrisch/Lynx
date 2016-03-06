package com.jrisch.lynx_android.storage;

import android.content.Context;
import android.os.Environment;
import com.jrisch.lynx.IStorageInfo;
import java.io.File;

/**
 * Default implementation of {@link IStorageInfo} that will store the data in the data
 * folder for the application on the external storage.
 */
public class LynxExternalStorageInfo implements IStorageInfo {

    private Context context;

    public LynxExternalStorageInfo(Context context){

        this.context = context;
    }

    @Override
    public File getStorageRoot() {
        return context.getExternalFilesDir(null);
    }

    @Override
    public boolean isStorageWritable() {
        return Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED);
    }
}
