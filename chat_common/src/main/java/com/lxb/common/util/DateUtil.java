package com.lxb.common.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static LocalDateTime toLocalDateTime(Long date) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(date), ZoneId.systemDefault());
    }

    public static String formatLocalDateTime(Long dateTime) {
        return toLocalDateTime(dateTime).format(dateTimeFormatter);
    }

}
