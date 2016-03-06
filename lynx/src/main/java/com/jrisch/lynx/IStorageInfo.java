package com.jrisch.lynx;

import java.io.File;

/**
 * Interface used to decide if it is ok to write to the storage and to supply the root directory to be used when storing data.
 */
public interface IStorageInfo {

    /**
     * @return the root folder to be used when storing data
     */
    File getStorageRoot();

    /**
     * @return true if the storage is available for writing, false otherwise.
     */
    boolean isStorageWritable();
}
