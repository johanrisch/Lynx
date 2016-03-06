package com.jrisch.lynx;

/**
 * Interface to make the cipher handling platform and provider agnostic.
 * If using one of the add-on libraries for android is used then there is a default call that uses spongycastle to supply keys.
 */
public interface ILynxKeySupplierHandler {

    /**
     * @return the supplier name to use when generating a key.
     */
    String getKeySupplierName();

    /**
     * Will be called when the cipher handler is created which allows for registering of a key supplier.
     */
    void registerSupplier();

    /**
     * @return the desired key size during encryption and decryption.
     */
    int getKeySize();


}
