package com.jrisch.lynx;

import com.jrisch.lynx.util.LynxKeySupplier;
import org.bouncycastle.util.encoders.Base64;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * TODO - add description
 */
public class LynxCipherHandlerTest {

    private LynxCipherHandler lynxCipherHandler;

    @Before
    public void setUp() throws Exception {
        lynxCipherHandler = new LynxCipherHandler(new ILynxCipherHandler.ILynxPasswordSupplier() {
            @Override
            public String getPassword() {
                return "abc12323";
            }
        }, new LynxKeySupplier(), new ILynxBase64() {

            @Override
            public byte[] encode(byte[] in) {
                return Base64.encode(in);
            }

            @Override
            public byte[] decode(byte[] in) {
                return Base64.decode(in);
            }

            @Override
            public byte[] encode(String in) {
                return Base64.encode(in.getBytes());
            }

            @Override
            public byte[] decode(String in) {
                return Base64.decode(in);
            }
        });
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testEncrypt() throws Exception {
        byte[] plainText = "test".getBytes();
        byte[] data = lynxCipherHandler.encrypt(plainText);

        boolean same = true;
        for (int i = 0; i < plainText.length || i < data.length; i++) {
            if (plainText[i] != data[i]) {
                same = false;
                break;
            }
        }
        assertFalse(same);
    }

    @Test
    public void testDecrypt() throws Exception {
        String text = "this is a test";

        assertTrue(new String(lynxCipherHandler.decrypt(lynxCipherHandler.encrypt(text.getBytes()))).equalsIgnoreCase(text));
    }
}