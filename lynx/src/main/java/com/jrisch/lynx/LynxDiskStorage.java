package com.jrisch.lynx;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * The storage class used to fetch and save data on disk.
 *
 * All of the required dependencies are injected using the constructor.
 */
public final class LynxDiskStorage<K, V> {

    static final String SUB_FOLDER = "Lynx";
    static final String DATA_ENDING = "";
    final Class<V> valueClass;
    final Class<K> keyClass;
    final String typeName;
    final ILynxObjectMapper mapper;
    IFileHandler storage;

    /**
     * Constructor used to create a {@link LynxDiskStorage}. Only used internally by {@link Lynx}, which contains a builder interface to create a {@link LynxDiskStorage}
     * @param mapper
     * @param storage
     * @param keyClass
     * @param valueClass
     * @param name
     */
    LynxDiskStorage(ILynxObjectMapper mapper, IFileHandler storage, Class<K> keyClass, Class<V> valueClass, String name) {
        this.mapper = mapper;
        this.storage = storage;
        this.valueClass = valueClass;
        this.keyClass = keyClass;
        this.typeName = name;

        initFileSystem();
    }

    public final V get(K key) {
        try {
            return mapper.deserialize(storage.readFile(getSubPath(typeName), getFilenameForKey(key)), valueClass);
        } catch (IOException e) {
            return null;
        }
    }

    public final void set(K key, V value) {
        if (isCached(key)) {
            remove(key);
        }
        String folder = getSubPath(typeName);
        String file = getFilenameForKey(key);
        storage.createFile(folder, file, mapper.serialize(value));
    }

    public final List<V> getAll() {
        List<V> ret = new LinkedList<>();
        File[] files = storage.getFiles(getSubPath(typeName));
        for (File file : files) {
            byte[] fileData = storage.readFile(file);
            if(fileData != null) {
                ret.add(mapper.deserialize(fileData, valueClass));
            }
        }
        return ret;
    }

    public final boolean isCached(K key) {
        return storage.isFileExist(getSubPath(typeName), getFilenameForKey(key));
    }

    public final void clear() {
        storage.deleteDirectory(getSubPath(typeName));
    }

    public final void remove(K key) {
        storage.deleteFile(getSubPath(typeName), getFilenameForKey(key));
    }

    private void initFileSystem() {
        String path = getSubPath(typeName);
        storage.createDirectory(path);
    }

    private String getFilenameForKey(K key) {
        return key.toString() + DATA_ENDING;
    }

    public final String getSubPath(String name) {
        return String.format("%s/%s/", SUB_FOLDER, name);
    }
}
