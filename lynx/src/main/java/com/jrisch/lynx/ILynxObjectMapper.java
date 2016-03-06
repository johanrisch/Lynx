package com.jrisch.lynx;

import java.io.InputStream;

/**
 * Interface used to serialize and deserialize objects when storing data.
 */
public interface ILynxObjectMapper {

    /**
     * Serialize an object
     * @param object the object to be serialized
     * @return the raw byte data representing the object.
     */
    <V> byte[] serialize(V object);

    /**
     * Deserialize an object
     * @param data the raw data to be deserialized into an object.
     * @param cls the class of the object that data represents
     * @return an instance of cls represented by data
     */
    <V> V deserialize(byte[] data, Class<V> cls);

    /**
     * Same as {@link ILynxObjectMapper#deserialize(byte[], Class)} but with an input stream instead of a byte array
     */
    <V> V deserialize(InputStream inputStream, Class<V> cls);
}