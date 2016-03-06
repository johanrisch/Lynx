package com.jrisch.lynx;

/**
 * TODO - add description
 */
public class UnSafeLynxCipherHandler implements ILynxCipherHandler {

    @Override
    public byte[] encrypt(byte[] data) {
        return data;
    }

    @Override
    public byte[] decrypt(byte[] cipher) {
        return cipher;
    }
}
