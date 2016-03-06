package com.jrisch.lynx;

import java.security.SecureRandom;
import java.security.spec.KeySpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * TODO - add description
 */
public class LynxCipherHandler implements ILynxCipherHandler {

    public static final String KEY_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA1";

    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";

    private static final String DELIMITER = "]";

    private static final int ITERATION_COUNT = 1000;

    private static final int SALT_LENGTH = 8;

    private static SecureRandom random = new SecureRandom();

    private final String supplierName;
    private final ILynxBase64 base64;

    private ILynxPasswordSupplier passwordSupplier;
    private ILynxKeySupplierHandler lynxKeySupplierHandler;

    public LynxCipherHandler(ILynxPasswordSupplier passwordSupplier, ILynxKeySupplierHandler lynxKeySupplierHandler, ILynxBase64 base64) {
        this.passwordSupplier = passwordSupplier;
        this.lynxKeySupplierHandler = lynxKeySupplierHandler;
        this.lynxKeySupplierHandler.registerSupplier();
        this.supplierName = lynxKeySupplierHandler.getKeySupplierName();
        this.base64 = base64;
    }

    public byte[] encrypt(byte[] data) {
        try {
            return encrypt(data, passwordSupplier.getPassword());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] decrypt(byte[] cipherData) {
        try {
            return decrypt(new String(cipherData, "UTF-8"), passwordSupplier.getPassword());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte[] encrypt(byte[] plaintext, String password)
        throws Exception {
        byte[] salt = generateSalt();
        return encrypt(plaintext, getKey(salt, password), salt);
    }

    private byte[] encrypt(byte[] plaintext, SecretKey key, byte[] salt)
        throws Exception {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, supplierName);

            byte[] iv = generateIv(cipher.getBlockSize());
            IvParameterSpec ivParams = new IvParameterSpec(iv);

            cipher.init(Cipher.ENCRYPT_MODE, key, ivParams);
            byte[] cipherText = cipher.doFinal(plaintext);
            String ret = null;
            if (salt != null) {
                ret = String.format("%s%s%s%s%s",
                                     new String(base64.encode(salt)),
                                     DELIMITER,
                                     new String(base64.encode(iv)),
                                     DELIMITER,
                                     new String(base64.encode(cipherText)));
            } else {
                ret = String.format("%s%s%s", new String(base64.encode(iv)),
                                           DELIMITER, new String(base64.encode(cipherText)));
            }
            return ret.getBytes("UTF-8");
        } catch (Throwable e) {
            throw new Exception("Error while encryption", e);
        }
    }

    private byte[] decrypt(String ciphertext, String password)
        throws Exception {
        String[] fields = ciphertext.split(DELIMITER);
        if (fields.length != 3) {
            throw new IllegalArgumentException("Invalid encypted text format");
        }
        try {
            byte[] salt = base64.decode(fields[0]);
            byte[] iv = base64.decode(fields[1]);
            byte[] cipherBytes = base64.decode(fields[2]);

            SecretKey key = getKey(salt, password);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, supplierName);
            IvParameterSpec ivParams = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, key, ivParams);
            byte[] plaintext = cipher.doFinal(cipherBytes);

            return plaintext;
        } catch (Throwable e) {
            e.printStackTrace();
            throw new Exception("Error while decryption", e);
        }
    }

    private SecretKey getKey(byte[] salt, String password)
        throws Exception {
        try {
            KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, lynxKeySupplierHandler.getKeySize());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_DERIVATION_ALGORITHM, supplierName);
            byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();

            return new SecretKeySpec(keyBytes, "AES");
        } catch (Throwable e) {
            throw new Exception("Error while generating key", e);
        }
    }

    private byte[] generateIv(int length) {
        byte[] b = new byte[length];
        random.nextBytes(b);

        return b;
    }

    private byte[] generateSalt() {
        byte[] b = new byte[SALT_LENGTH];
        random.nextBytes(b);

        return b;
    }
}
