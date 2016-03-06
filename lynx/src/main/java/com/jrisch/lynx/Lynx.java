package com.jrisch.lynx;

/**
 * Main entry point for creating a {@link LynxDiskStorage}
 * @param <K> the key class for the {@link LynxDiskStorage}. Objects will be saved using the key objects {@link Object#toString()}
 * @param <V> the value class for the {@link LynxDiskStorage}.
 */
public class Lynx<K, V> {

    /**
     * Created a Lynx instance.
     *
     * You must at least call {@link Lynx#withMapper(ILynxObjectMapper)} before calling {@link Lynx#build()}
     *
     * It is highly recommended to at least call the {@link Lynx#named(String)} method before building the {@link LynxDiskStorage} in order to avoid accidental overwriting
     * of different storages.
     *
     * @param storageInfo a {@link IStorageInfo} implementation which supplies Lynx with storage status and directories. If one of the addon libraries for android is used there
     * is two default {@link IStorageInfo}  LynxExternalStorageInfo and LynxInternalStorageInfo.
     * @param keyClass class for the object to be used as key. {@link LynxDiskStorage} will use the instances toString() method when mapping the key object to a value object
     * @param valueClass class for the object to be stored.
     * @return an Instance of {@link Lynx} which supplies builder methods for creating a {@link LynxDiskStorage}.
     */
    public static <K, V> Lynx<K, V> create(IStorageInfo storageInfo, Class<K> keyClass, Class<V> valueClass) {

        return new Lynx<>(storageInfo, keyClass, valueClass);
    }

    final Class<K> keyClass;
    final Class<V> valueClass;
    String name;
    ILynxObjectMapper mapper;
    IFileHandler fileHandler;
    ILynxCipherHandler cipherHandler;
    IStorageInfo storageInfo;

    private Lynx(IStorageInfo storageInfo, Class<K> keyClass, Class<V> valueClass) {
        if (storageInfo == null || keyClass == null || valueClass == null) {
            throw new IllegalArgumentException("neither storageInfo, keyClass nor valueClass may be null");
        }
        this.storageInfo = storageInfo;
        this.keyClass = keyClass;
        this.valueClass = valueClass;
    }

    /**
     * Name the storage being instantiated.
     * @param name the name of the instance
     * @return {@link Lynx} with the name set for the instance being built.
     */
    public Lynx<K, V> named(String name) {
        this.name = name;
        return this;
    }

    /**
     * Supply an object mapper that will convert objects to and from bytes to be written to disk. If either lynx-android-jackson or lynx-android-gson is
     * used there is a default implementation LynxObjectMapper using either jackson or gson to serialize the objects. You are, of course, free to use your own implementation
     * as well
     * @param mapper a {@link ILynxObjectMapper} implementation used to serialize and deserialize object
     * @return {@link Lynx} with the {@link ILynxObjectMapper} set for the instance being built.
     */
    public Lynx<K, V> withMapper(ILynxObjectMapper mapper) {
        this.mapper = mapper;
        return this;
    }

    /**
     * Optionally supply your own implementation of a {@link IFileHandler}.
     * @param fileHandler an implementation of {@link IFileHandler} to use to read and write the files for the {@link LynxDiskStorage}
     * @return {@link Lynx} with the {@link IFileHandler} set for the instance being built.
     */
    public Lynx<K, V> withFileHandler(IFileHandler fileHandler) {
        this.fileHandler = fileHandler;
        return this;
    }

    /**
     * Optionally supply a {@link ILynxCipherHandler} to be used by the default {@link IFileHandler}. If you supply your own {@link IFileHandler} this method will have no effect
     * on the created {@link LynxDiskStorage}
     * @param cipherHandler an implementation of {@link ILynxCipherHandler}.
     * @return {@link Lynx} with the {@link ILynxCipherHandler} set for the instance being built.
     */
    public Lynx<K, V> withCipherHandler(ILynxCipherHandler cipherHandler) {
        this.cipherHandler = cipherHandler;
        return this;
    }

    /**
     * Build the {@link LynxDiskStorage}.
     * @return {@link LynxDiskStorage} with the parameters set while building the instance.
     */
    public LynxDiskStorage<K, V> build() {
        if (storageInfo == null) {
            throw new IllegalArgumentException("storageInfo may not be null");
        }
        if (mapper == null) {
            throw new IllegalArgumentException("Mapper may not be null");
        }
        if (name == null) {
            name = keyClass.getName() + "-" + valueClass.getName();
        }
        if (cipherHandler == null) {
            cipherHandler = new UnSafeLynxCipherHandler();
        }
        if (fileHandler == null) {
            fileHandler = new FileHandler(storageInfo, cipherHandler);
        }
        return new LynxDiskStorage<>(mapper, fileHandler, keyClass, valueClass, name);
    }
}