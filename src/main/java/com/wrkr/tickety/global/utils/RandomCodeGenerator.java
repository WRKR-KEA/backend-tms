package com.wrkr.tickety.global.utils;

import java.security.SecureRandom;
import java.util.UUID;

public class RandomCodeGenerator {

    private static final int VERIFY_CODE_LENGTH = 6;
    private static final SecureRandom random = new SecureRandom();

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    public static String generateRandomCode() {
        StringBuilder code = new StringBuilder(VERIFY_CODE_LENGTH);
        for (int i = 0; i < VERIFY_CODE_LENGTH; i++) {
            int digit = random.nextInt(10);
            code.append(digit);
        }
        return code.toString();
    }


}
