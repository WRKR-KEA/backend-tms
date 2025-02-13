package com.wrkr.tickety.global.utils.date;

import java.time.LocalDateTime;

public class DateTimeFormatter {

    public static String yyyyMMddHHmm(LocalDateTime dateTime) {
        return dateTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

}
