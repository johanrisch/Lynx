package com.jrisch.lynx;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public interface IFileHandler {

    File setupFileSystem();

    File createFile(String subPath, String name, String content);

    File createFile(String subPath, String name, byte[] content);

    byte[] readFile(String dirName, String fileName) throws FileNotFoundException;

    boolean createDirectory(String path);

    boolean hasEnoughSpaceFor(long bytes);

    long[] spaceInfo();

    File[] getFiles(String dirName);

    void deleteDirectory(String pathForKey);

    void deleteFile(String pathForKey, String filenameForKey);

    void deleteFile(String fullPath);

    boolean isFileExist(String dirName, String fileName);

    byte[] readFile(File file);

    class ExternalStorageNotWriteableException extends RuntimeException{

    }
}
