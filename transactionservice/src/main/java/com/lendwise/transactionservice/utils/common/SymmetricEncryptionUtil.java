package com.lendwise.transactionservice.utils.common;

import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/*
    @created 12/09/2024 4:52 PM
    @project 2.0
    @author biplaw.chaudhary
*/
@Component
public class SymmetricEncryptionUtil {

    public enum EncryptionAlgorithm {
        AES_ECB,
        AES_CBC,
        AES_GCM
    }

    private static final int AES_KEY_SIZE = 256;
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;

    /**
     * Generates a secret key for the specified algorithm.
     *
     * @param algorithm The encryption algorithm
     * @return Base64 encoded secret key
     * @throws NoSuchAlgorithmException if the algorithm is not available
     */
    public String generateKey(EncryptionAlgorithm algorithm) throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(AES_KEY_SIZE);
        SecretKey key = keyGen.generateKey();
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    /**
     * Encrypts the plaintext using the specified algorithm and key.
     *
     * @param plaintext The text to encrypt
     * @param key       Base64 encoded secret key
     * @param algorithm The encryption algorithm to use
     * @return Base64 encoded ciphertext
     * @throws Exception if encryption fails
     */
    public String encrypt(String plaintext, String key, EncryptionAlgorithm algorithm) throws Exception {
        SecretKey secretKey = new SecretKeySpec(Base64.getDecoder().decode(key), "AES");
        byte[] cipherText;

        switch (algorithm) {
            case AES_ECB:
                cipherText = encryptAesEcb(plaintext, secretKey);
                break;
            case AES_CBC:
                cipherText = encryptAesCbc(plaintext, secretKey);
                break;
            case AES_GCM:
                cipherText = encryptAesGcm(plaintext, secretKey);
                break;
            default:
                throw new IllegalArgumentException("Unsupported encryption algorithm");
        }

        return Base64.getEncoder().encodeToString(cipherText);
    }

    /**
     * Decrypts the ciphertext using the specified algorithm and key.
     *
     * @param ciphertext Base64 encoded ciphertext
     * @param key        Base64 encoded secret key
     * @param algorithm  The encryption algorithm to use
     * @return Decrypted plaintext
     * @throws Exception if decryption fails
     */
    public String decrypt(String ciphertext, String key, EncryptionAlgorithm algorithm) throws Exception {
        SecretKey secretKey = new SecretKeySpec(Base64.getDecoder().decode(key), "AES");
        byte[] plainText;

        switch (algorithm) {
            case AES_ECB:
                plainText = decryptAesEcb(Base64.getDecoder().decode(ciphertext), secretKey);
                break;
            case AES_CBC:
                plainText = decryptAesCbc(Base64.getDecoder().decode(ciphertext), secretKey);
                break;
            case AES_GCM:
                plainText = decryptAesGcm(Base64.getDecoder().decode(ciphertext), secretKey);
                break;
            default:
                throw new IllegalArgumentException("Unsupported encryption algorithm");
        }

        return new String(plainText, StandardCharsets.UTF_8);
    }

    private byte[] encryptAesEcb(String plaintext, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
    }

    private byte[] decryptAesEcb(byte[] ciphertext, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(ciphertext);
    }

    private byte[] encryptAesCbc(String plaintext, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        byte[] cipherTextWithIv = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, cipherTextWithIv, 0, iv.length);
        System.arraycopy(encrypted, 0, cipherTextWithIv, iv.length, encrypted.length);
        return cipherTextWithIv;
    }

    private byte[] decryptAesCbc(byte[] ciphertext, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] iv = new byte[16];
        System.arraycopy(ciphertext, 0, iv, 0, iv.length);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        return cipher.doFinal(ciphertext, iv.length, ciphertext.length - iv.length);
    }

    private byte[] encryptAesGcm(String plaintext, SecretKey key) throws Exception {
        byte[] iv = new byte[GCM_IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, gcmParameterSpec);
        byte[] cipherText = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        byte[] cipherTextWithIv = new byte[iv.length + cipherText.length];
        System.arraycopy(iv, 0, cipherTextWithIv, 0, iv.length);
        System.arraycopy(cipherText, 0, cipherTextWithIv, iv.length, cipherText.length);
        return cipherTextWithIv;
    }

    private byte[] decryptAesGcm(byte[] ciphertext, SecretKey key) throws Exception {
        byte[] iv = new byte[GCM_IV_LENGTH];
        System.arraycopy(ciphertext, 0, iv, 0, iv.length);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, gcmParameterSpec);
        return cipher.doFinal(ciphertext, GCM_IV_LENGTH, ciphertext.length - GCM_IV_LENGTH);
    }
}
