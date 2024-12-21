package com.example.weather_forecast.domains;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtil {
    public static LocalDateTime getLocalDateTimeFromTimestamp(long timestamp){
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());
    }

    public static String getDateFromTimestamp(long timestamp) {
        Date date = new Date(timestamp * 1000); // Chuyển từ giây sang millisecond
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()); // Chỉ định dạng ngày
        return sdf.format(date);
    }

    // Tách giờ từ timestamp
    public static String getTimeFromTimestamp(long timestamp) {
        Date date = new Date(timestamp * 1000); // Chuyển từ giây sang millisecond
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm a", Locale.getDefault()); // Chỉ định dạng giờ
        return sdf.format(date);
    }
}

