package com.jrisch.lynx.util;

import com.jrisch.lynx.ILynxKeySupplierHandler;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * TODO - add description
 */
public class LynxKeySupplier implements ILynxKeySupplierHandler {

    public LynxKeySupplier(){
        Security.addProvider(new BouncyCastleProvider());
    }

    @Override
    public String getKeySupplierName() {
        return "BC";
    }

    @Override
    public void registerSupplier() {

    }

    @Override
    public int getKeySize() {
        return 128;
    }
}
