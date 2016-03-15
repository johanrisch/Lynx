package com.jrisch.lynx_android.cipher;

import com.jrisch.lynx.ILynxKeySupplierHandler;
import java.security.Security;

/**
 * Default implementation of {@link ILynxKeySupplierHandler} which uses spongycastle to create the keys.
 */
public class LynxBouncyCastleProvider implements ILynxKeySupplierHandler {
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
