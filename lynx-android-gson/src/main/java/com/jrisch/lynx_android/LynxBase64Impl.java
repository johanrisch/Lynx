package com.jrisch.lynx_android;

import android.util.Base64;
import com.jrisch.lynx.ILynxBase64;
import java.io.UnsupportedEncodingException;

/**
 * Base implementation of {@link ILynxBase64} which uses the android Base64.
 */
public class LynxBase64Impl implements ILynxBase64 {

    @Override
    public byte[] encode(byte[] in) {
        return Base64.encode(in, Base64.NO_WRAP);
    }

    @Override
    public byte[] decode(byte[] in) {

        return Base64.decode(in, Base64.NO_WRAP);
    }

    @Override
    public byte[] encode(String in) {
        try {
            return Base64.encode(in.getBytes("UTF-8"), Base64.NO_WRAP);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public byte[] decode(String in) {
        try {
            return Base64.decode(in.getBytes("UTF-8"), Base64.NO_WRAP);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
