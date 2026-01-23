package io.github.chikyukido.lovelystats.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Format {
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
            long days = TimeUnit.SECONDS.toDays(seconds);
            long hours = TimeUnit.SECONDS.toHours(seconds) % 24;
            long minutes = TimeUnit.SECONDS.toMinutes(seconds) % 60;
            long secs = seconds % 60;
            if (days > 0) {
                return String.format("%dd %02dh %02dm %02ds", days, hours, minutes, secs);
            } else {
                return String.format("%02dh %02dm %02ds", hours, minutes, secs);
            }
        } else if (seconds >= 60) {
            long minutes = seconds / 60;
            long secs = seconds % 60;
            return String.format("%02dm %02ds", minutes, secs);
        } else {
            return String.format("%02ds", seconds);
        }
    }

    public static String formatDate(long epochSeconds) {
        Date date = new Date(epochSeconds * 1000);
        return DATE_FORMAT.format(date);
    }
}
