package io.github.chikyukido.lovelystats.commands;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import fi.sulku.hytale.TinyMsg;
import io.github.chikyukido.lovelystats.types.PlaytimeStats;
import io.github.chikyukido.lovelystats.handler.PlaytimeStatsHandler;
import io.github.chikyukido.lovelystats.types.PlaytimeSession;

public class PlaytimeCommand {
    public static void run(PlayerRef playerRef) {
        PlaytimeStats p = PlaytimeStatsHandler.get()
                .getPlaytimeForPlayer(playerRef.getUuid());

        long totalActive = 0;
        long totalIdle = 0;
        long longestSession = 0;

        for (PlaytimeSession s : p.getSessions()) {
            totalActive += s.getActiveTime();
            totalIdle += s.getIdleTime();
            longestSession = Math.max(longestSession, sessionDuration(s));
        }

        PlaytimeSession current = p.getCurrentSession();
        if (current != null) {
            totalActive += current.getActiveTime();
            totalIdle += current.getIdleTime();
            longestSession = Math.max(longestSession, sessionDuration(current));
        }

        long totalTime = totalActive + totalIdle;

        String msg =
                "<gold><bold>Playtime Statistics</bold></gold>\n" +

                        "<yellow>Total Time:</yellow> <white>" + formatDuration(totalTime) + "</white>\n" +
                        "<green>Active:</green> <white>" + formatDuration(totalActive) + "</white>\n" +
                        "<gray>Idle:</gray> <white>" + formatDuration(totalIdle) + "</white>\n" +
                        "<aqua>Sessions:</aqua> <white>" + p.getSessions().size() + "</white>\n" +
                        "<purple>Longest Session:</purple> <white>" + formatDuration(longestSession) + "</white>\n";

        if (current != null) {
            msg +=
                    "<gold>Current Session</gold>\n" +
                            "<yellow>Started:</yellow> <white>" + current.getStartTime() + "</white>\n" +
                            "<green>Active:</green> <white>" + formatDuration(current.getActiveTime()) + "</white>\n" +
                            "<gray>Idle:</gray> <white>" + formatDuration(current.getIdleTime()) + "</white>\n";
        }

        playerRef.sendMessage(TinyMsg.parse(msg));
    }


    private static String formatDuration(long seconds) {
        long h = seconds / 3600;
        long m = (seconds % 3600) / 60;
        long s = seconds % 60;
        return h + "h " + m + "m " + s + "s";
    }

    private static long sessionDuration(PlaytimeSession s) {
        long end = s.getStopTime() > 0 ? s.getStopTime() : System.currentTimeMillis() / 1000;
        return end - s.getStartTime();
    }

}
