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
    public void init() {
        this.secretKey = generateKey();
        instance = this;
    }

    /**
     * 테스트 환경에서만 사용해야 합니다.
     * <p>
     * 프로덕션 환경에서 호출하지 마세요.
     * Mock 객체를 싱글턴으로 설정하기 위해 제공됩니다.
     */
    @Deprecated
    public static void setInstance(PkCrypto mockInstance) {
        instance = mockInstance;
    }
    private SecretKey generateKey() {
        byte[] keyBytes = secret.getBytes();
        return new SecretKeySpec(keyBytes, algorithm);
    }

    public static PkCrypto getInstance() {
        if (instance == null) {
            throw new IllegalStateException("PkCrypto가 초기화되지 않았습니다");
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
            throw new IllegalStateException("암호화 실패", e);
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
            throw new IllegalStateException("복호화 실패", e);
        }
    }

    public static String encrypt(Long value) {
        return getInstance().encryptValue(value);
    }

    public static Long decrypt(String encryptedValue) {
        return getInstance().decryptValue(encryptedValue);
    }


}
