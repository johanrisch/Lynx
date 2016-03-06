package com.jrisch.lynxsample;

import android.util.Base64;
import com.jrisch.lynx.ILynxBase64;
import java.io.UnsupportedEncodingException;

/**
 * TODO - add description
 */
public class ILynxBase64Impl implements ILynxBase64 {

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
