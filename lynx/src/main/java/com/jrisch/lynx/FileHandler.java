package com.jrisch.lynx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

public class FileHandler implements IFileHandler {

    final File rootFile;
    private IStorageInfo storageInfo;
    private ILynxCipherHandler cipherHandler = new UnSafeLynxCipherHandler();

    public FileHandler(IStorageInfo storageState) {
        this(storageState, null);
    }

    public FileHandler(IStorageInfo storageInfo, ILynxCipherHandler lynxCipherHandler) {
        if (storageInfo == null) {
            throw new IllegalArgumentException("StorageInfo cannot be null");
        }
        this.storageInfo = storageInfo;
        this.rootFile = storageInfo.getStorageRoot();
        if (lynxCipherHandler != null) {
            this.cipherHandler = lynxCipherHandler;
        }
    }

    @Override
    public File setupFileSystem() {
        // Make sure that the external storage is available
        if (isExternalStorageWritable()) {
            return getHandlerRoot();
        }
        return null;
    }

    @Override
    public File createFile(String subPath, String name, String content) {
        return createFile(subPath, name, content.getBytes());
    }

    @Override
    public File createFile(String subPath, String name, byte[] content) {
        content = cipherHandler.encrypt(content);
        File root = setupFileSystem();
        if (root == null) {
            return null;
        }
        File folder = new File(combinePath(root.getAbsolutePath(), subPath));
        if (!folder.exists()) {
            folder.mkdirs();
        }

        try {
            File file = new File(folder, name);
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(content);
            fos.close();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public byte[] readFile(String dirName, String fileName) throws FileNotFoundException {
        File root = setupFileSystem();
        if (root == null) {
            return null;
        }
        File file = new File(combinePath(root.getAbsolutePath(), dirName), fileName);
        return readFile(new FileInputStream(file));
    }

    @Override
    public boolean createDirectory(String path) {
        File root = setupFileSystem();
        if (root == null) {
            return false;
        }
        return new File(combinePath(root.getAbsolutePath(), path)).mkdirs();
    }

    @Override
    public boolean hasEnoughSpaceFor(long bytes) {
        long freeSpace = rootFile.getFreeSpace();
        return freeSpace > bytes;
    }

    @Override
    public long[] spaceInfo() {
        long[] ret = new long[] {
            rootFile.getFreeSpace(),
            rootFile.getTotalSpace()
        };
        return ret;
    }

    @Override
    public File[] getFiles(String dirName) {
        File rootPath = setupFileSystem();
        if (rootPath != null) {
            if (rootPath.exists()) {
                return new File(combinePath(rootPath.getAbsolutePath(), dirName)).listFiles();
            }
        }
        return null;
    }

    @Override
    public void deleteDirectory(String subPath) {
        File rootPath = setupFileSystem();
        if (rootPath != null) {
            if (subPath != null) {
                removeFilesRecursive(new File(combinePath(rootPath.getAbsolutePath(), subPath)));
            } else {
                removeFilesRecursive(rootPath);
            }
        }
    }

    @Override
    public void deleteFile(String pathForKey, String filenameForKey) {
        File rootPath = setupFileSystem();
        if (rootPath != null) {
            removeFilesRecursive(new File(combinePath(rootPath.getAbsolutePath(), pathForKey), filenameForKey));
        } else {
            throw new ExternalStorageNotWriteableException();
        }
    }

    @Override
    public void deleteFile(String fullPath) {
        File rootPath = setupFileSystem();
        if (rootPath != null) {
            removeFilesRecursive(new File(fullPath));
        } else {
            throw new ExternalStorageNotWriteableException();
        }
    }

    @Override
    public boolean isFileExist(String dirName, String fileName) {
        File rootPath = setupFileSystem();
        if (rootPath == null) {
            return false;
        }
        File file = new File(combinePath(rootPath.getAbsolutePath(), dirName), fileName);
        return file.exists();
    }

    @Override
    public byte[] readFile(File file) {
        InputStream is = null;
        try {
            long length = file.length();
            byte[] fileData = new byte[(int) length];
            byte[] dummy = new byte[1];
            is = new FileInputStream(file);
            is.read(fileData);
            assert (is.read(dummy) == -1);

            return cipherHandler.decrypt(fileData);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if(is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    String combinePath(String... pathSegments) {
        StringBuilder sb = new StringBuilder(pathSegments.length * 20);
        for (String s : pathSegments) {
            sb.append(s);
            sb.append(File.separator);
        }
        return sb.toString();
    }

    void removeFilesRecursive(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                removeFilesRecursive(f);
            }
        }
        file.delete();
    }

    File getHandlerRoot() {
        return rootFile;
    }

    /* Checks if external storage is available for read and write */
    boolean isExternalStorageWritable() {
        return storageInfo == null || storageInfo.isStorageWritable();
    }

    /**
     * Taken from https://github.com/sromku/android-simple-storage
     */
    byte[] readFile(final FileInputStream stream) {
        class Reader extends Thread {

            byte[] array = null;
        }

        Reader reader = new Reader() {
            public void run() {
                LinkedList<LynxPair<byte[], Integer>> chunks = new LinkedList<>();

                // read the file and build chunks
                int size = 0;
                int globalSize = 0;
                do {
                    try {
                        int chunkSize = 8192;
                        // read chunk
                        byte[] buffer = new byte[chunkSize];
                        size = stream.read(buffer, 0, chunkSize);
                        if (size > 0) {
                            globalSize += size;

                            // add chunk to list
                            chunks.add(new LynxPair<>(buffer, size));
                        }
                    } catch (Exception e) {
                        // very bad
                    }
                } while (size > 0);

                try {
                    stream.close();
                } catch (Exception e) {
                    // very bad
                }

                array = new byte[globalSize];

                // append all chunks to one array
                int offset = 0;
                for (LynxPair<byte[], Integer> chunk : chunks) {
                    // flush chunk to array
                    System.arraycopy(chunk.first, 0, array, offset, chunk.second);
                    offset += chunk.second;
                }

                array = cipherHandler.decrypt(array);
            }
        };

        reader.start();
        try {
            reader.join();
        } catch (InterruptedException e) {
            throw new RuntimeException("Failed on reading file from storage while the locking Thread", e);
        }

        return reader.array;
    }
}
