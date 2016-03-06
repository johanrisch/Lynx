package com.jrisch.lynx;

/**
 * Interface for doing Base64 encoding and decoding. For now it <b>is required</b> that the encoding does not add new lines to the encoded string.
 *
 * If using one of the app-on libraries for android there is a default implementation that wraps the android Base64 implementation.
 */
public interface ILynxBase64 {

    byte[] encode(byte[] in);

    byte[] decode(byte[] in);

    byte[] encode(String in);

    byte[] decode(String in);
}
