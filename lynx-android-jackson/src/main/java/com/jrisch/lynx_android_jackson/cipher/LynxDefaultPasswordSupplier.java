package com.jrisch.lynx_android_jackson.cipher;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import com.jrisch.lynx.ILynxCipherHandler;
import java.security.SecureRandom;

/**
 * TODO - add description
 */
public class LynxDefaultPasswordSupplier implements ILynxCipherHandler.ILynxPasswordSupplier {

    private static final String PREFS_NAME = "com.jrisch.lynx.passwordSupplier";
    private static final String KEY_PASSWORD = "com.jrisch.lynx.passwordSupplier.keyPassword";
    private final SharedPreferences prefs;

    public LynxDefaultPasswordSupplier(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (prefs.getString(KEY_PASSWORD, null) == null) {
            SecureRandom sr = new SecureRandom();
            byte[] bytes = new byte[128];
            sr.nextBytes(bytes);
            prefs.edit().putString(KEY_PASSWORD, new String(Base64.encode(bytes, Base64.NO_WRAP))).commit();
        }

    }

    @Override
    public String getPassword() {
        return prefs.getString(KEY_PASSWORD, null);
    }
}
