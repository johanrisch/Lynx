package com.jrisch.lynx_android_jackson.cipher;

import com.jrisch.lynx.ILynxKeySupplierHandler;
import java.security.Security;

/**
 * TODO - add description
 */
public class ILynxBouncyCastleProvider implements ILynxKeySupplierHandler {
    static {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);

    }

    @Override
    public String getKeySupplierName() {
        return "SC";
    }

    @Override
    public void registerSupplier() {

    }

    @Override
    public int getKeySize() {
        return 128;
    }
}
