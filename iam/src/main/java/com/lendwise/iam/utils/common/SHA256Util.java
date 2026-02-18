package com.lendwise.iam.utils.common;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class SHA256Util {

    private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

    /**
     * Calculates the HMAC-SHA256 hash of a message using a secret key.
     * Returns the result as a hexadecimal string.
     *
     * @param key The secret key (String).
     * @param data The message or data to be hashed (String).
     * @return The HMAC-SHA256 hash as a hexadecimal string.
     * @throws NoSuchAlgorithmException If the algorithm is not available.
     * @throws InvalidKeyException If the key is invalid.
     */
    public static String calculateHmacSha256Hex(String key, String data)
            throws NoSuchAlgorithmException, InvalidKeyException {

        // 1. Get the bytes of the key and data
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);

        // 2. Create a SecretKeySpec from the key bytes
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, HMAC_SHA256_ALGORITHM);

        // 3. Get an instance of the Mac class with the HmacSHA256 algorithm
        Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);

        // 4. Initialize the Mac instance with the secret key
        mac.init(secretKeySpec);

        // 5. Compute the HMAC hash
        byte[] hmacBytes = mac.doFinal(dataBytes);

        // 6. Convert the resulting byte array to a hexadecimal string
        return bytesToHex(hmacBytes);
    }

    /**
     * Calculates the HMAC-SHA256 hash of a message using a secret key.
     * Returns the result as a Base64 encoded string.
     *
     * @param key The secret key (String).
     * @param data The message or data to be hashed (String).
     * @return The HMAC-SHA256 hash as a Base64 encoded string.
     * @throws NoSuchAlgorithmException If the algorithm is not available.
     * @throws InvalidKeyException If the key is invalid.
     */
    public static String calculateHmacSha256Base64(String key, String data)
            throws NoSuchAlgorithmException, InvalidKeyException {

        // 1. Get the bytes of the key and data
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);

        // 2. Create a SecretKeySpec from the key bytes
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, HMAC_SHA256_ALGORITHM);

        // 3. Get an instance of the Mac class with the HmacSHA256 algorithm
        Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);

        // 4. Initialize the Mac instance with the secret key
        mac.init(secretKeySpec);

        // 5. Compute the HMAC hash
        byte[] hmacBytes = mac.doFinal(dataBytes);

        // 6. Convert the resulting byte array to a Base64 encoded string
        return Base64.getEncoder().encodeToString(hmacBytes);
    }

//    /**
//     * Helper method to convert a byte array to a hexadecimal string.
//     */
//    private static String bytesToHex(byte[] bytes) {
//        StringBuilder sb = new StringBuilder(2 * bytes.length);
//        for (byte b : bytes) {
//            sb.append(String.format("%02x", b));
//        }
//        return sb.toString();
//    }

    /**
     * Generates SHA256 hash and returns it as a hexadecimal string
     * @param input The input string to hash
     * @return SHA256 hash as hex string
     */
    public static String sha256Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    /**
     * Generates SHA256 hash and returns it as a Base64 string
     * @param input The input string to hash
     * @return SHA256 hash as Base64 string
     */
    public static String sha256Base64(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    /**
     * Generates SHA256 hash from byte array and returns it as hexadecimal string
     * @param input The input bytes to hash
     * @return SHA256 hash as hex string
     */
    public static String sha256Hex(byte[] input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input);
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    /**
     * Generates SHA256 hash and returns raw bytes
     * @param input The input string to hash
     * @return SHA256 hash as byte array
     */
    public static byte[] sha256Bytes(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(input.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    /**
     * Converts byte array to hexadecimal string
     * @param bytes The byte array to convert
     * @return Hexadecimal string representation
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString().toUpperCase();
    }

    // Example usage
    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException {

//        System.out.println(calculateHmacSha256Hex("11111E1E57C9FEAF3879D04E6917874BABDE", "10990326260431809101666"));
        System.out.println(calculateHmacSha256Hex("1111B14CA5898A4E4133BBCE2EA2315A1916", "31990309260441119101668"));
//        System.out.println("HEX: "+ );
        
//        System.out.println("Input: " + input);
//        System.out.println("SHA256 (Hex): " + sha256Hex(input));
//        System.out.println("SHA256 (Base64): " + sha256Base64(input));
//
//        // Example with byte array
//        byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
//        System.out.println("SHA256 from bytes (Hex): " + sha256Hex(inputBytes));
    }
}