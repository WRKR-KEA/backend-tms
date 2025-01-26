package com.wrkr.tickety.global.utils;

import java.util.UUID;

public class UUIDGenerator {

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }
}
