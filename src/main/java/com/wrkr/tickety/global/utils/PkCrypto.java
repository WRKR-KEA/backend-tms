package com.wrkr.tickety.global.utils;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.util.Base64;

@Component
public class PkCrypto {
    private static PkCrypto instance;

    private final String algorithm;
    private final String secret;

    private SecretKey secretKey;

    public PkCrypto(
            @Value("${crypto.algorithm}") String algorithm,
            @Value("${crypto.secret}") String secret) {
        this.algorithm = algorithm;
        this.secret = secret;
    }
    @PostConstruct
    private void init() {
        this.secretKey = generateKey();
        instance = this;
    }

    private SecretKey generateKey() {
        byte[] keyBytes = secret.getBytes();
        return new SecretKeySpec(keyBytes, algorithm);
    }

    public static PkCrypto getInstance() {
        if (instance == null) {
            throw new IllegalStateException("PKCrypto is not initialized");
        }
        return instance;
    }

    public String encryptValue(Long value) {
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] valueBytes = ByteBuffer.allocate(Long.BYTES).putLong(value).array();
            byte[] encrypted = cipher.doFinal(valueBytes);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(encrypted);
        } catch (Exception e) {
            throw new IllegalStateException("Encryption failed", e);
        }
    }

    public Long decryptValue(String encryptedValue) {
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] encryptedBytes = Base64.getUrlDecoder().decode(encryptedValue);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return ByteBuffer.wrap(decryptedBytes).getLong();
        } catch (Exception e) {
            throw new IllegalStateException("Decryption failed", e);
        }
    }

    public static String encrypt(Long value) {return getInstance().encryptValue(value);}

    public static Long decrypt(String encryptedValue) {
        return getInstance().decryptValue(encryptedValue);
    }


}
