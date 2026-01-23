package io.github.chikyukido.lovelystats.util;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class OwnFormat {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");

    public static String formatDistance(double meters) {
        if (meters >= 1000) {
            return String.format("%.2f km", meters / 1000.0);
        } else {
            return String.format("%.0f m", meters);
        }
    }

    public static String formatTime(long seconds) {
        if (seconds >= 3600) {
            long hours = seconds / 3600;
            long minutes = (seconds % 3600) / 60;
            return String.format("%dh %02dm", hours, minutes);
        } else {
            long minutes = seconds / 60;
            long secs = seconds % 60;
            return String.format("%dm %02ds", minutes, secs);
        }
    }


    public static String formatDate(long epochSeconds) {
        Date date = new Date(epochSeconds * 1000);
        return DATE_FORMAT.format(date);
    }
    public static String formatDate(long unixSeconds, String pattern) {
        if (unixSeconds <= 0) return "--";
        Instant instant = Instant.ofEpochSecond(unixSeconds);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.systemDefault());
        return formatter.format(instant);
    }
}
