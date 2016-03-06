package com.jrisch.lynx;

/**
 * Interface representing the encryption and decryption of the data being stored.
 *
 * If one of the add-on libraries are being used there is a default {@link ILynxPasswordSupplier}
 */
public interface ILynxCipherHandler {
    byte[] encrypt(byte[] data);
    byte[] decrypt(byte[] cipher);


    interface ILynxPasswordSupplier {
        String getPassword();
    }

}
