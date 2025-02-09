package com.wrkr.tickety.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.wrkr.tickety.global.utils.PkCrypto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PkCryptoTest {

    @BeforeAll
    static void init() {
        PkCrypto pkCrypto = new PkCrypto("AES", "1234567890123456");
        pkCrypto.init();
    }

    @DisplayName("PK 암호화 및 복호화 테스트")
    @Test
    void EncryptionAndDecryptionTest() {
        // Given
        Long originalValue = 3L;

        // When
        String encryptedValue = PkCrypto.encrypt(originalValue);
        Long decryptedValue = PkCrypto.decrypt(encryptedValue);

        System.out.println("인코딩 값: " + encryptedValue);
        System.out.println("디코딩 값: " + decryptedValue);

        // Then
        assertNotNull(
            encryptedValue,
            "암호화 된 값은 null이 아니어야 합니다."
        );
        assertEquals(
            originalValue,
            decryptedValue,
            "복호화 된 값은 원래 값과 일치 해야 합니다."
        );

    }
}
