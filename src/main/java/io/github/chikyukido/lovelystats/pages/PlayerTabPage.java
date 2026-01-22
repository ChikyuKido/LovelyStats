package io.github.chikyukido.lovelystats.pages;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.chikyukido.lovelystats.handler.PlayerStatsHandler;
import io.github.chikyukido.lovelystats.handler.PlaytimeStatsHandler;
import io.github.chikyukido.lovelystats.types.PlayerStats;
import io.github.chikyukido.lovelystats.types.PlaytimeSession;
import io.github.chikyukido.lovelystats.types.PlaytimeStats;

import javax.annotation.Nonnull;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PlayerTabPage extends TabPage{
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");

    public PlayerTabPage(StatsPage parent,  UUID playerUUID) {
        super(parent,playerUUID);
    }


    @Override
    public void build(UICommandBuilder cb, UIEventBuilder event) {
        cb.append("#TabPages","player/player_page.ui");

        PlayerStats playerStats = PlayerStatsHandler.get().getPlayerStats(playerUUID);
        PlaytimeStats playtimeStats = PlaytimeStatsHandler.get().getPlaytimeForPlayer(playerUUID);

        cb.set("#LeftStats #DistanceWalked.Text", formatDistance("Walked", playerStats.getDistanceWalked()));
        cb.set("#LeftStats #DistanceRun.Text", formatDistance("Run", playerStats.getDistanceRun()));
        cb.set("#LeftStats #DistanceSwam.Text", formatDistance("Swam", playerStats.getDistanceSwam()));
        cb.set("#LeftStats #DistanceFallen.Text", formatDistance("Fallen", playerStats.getDistanceFallen()));
        cb.set("#LeftStats #DistanceClimbed.Text", formatDistance("Climbed", playerStats.getDistanceClimbed()));
        cb.set("#LeftStats #DistanceSneaked.Text", formatDistance("Sneaked", playerStats.getDistanceSneaked()));
        cb.set("#LeftStats #ElevationUp.Text", formatDistance("Elevation Up", playerStats.getElevationUp()));
        cb.set("#LeftStats #ElevationDown.Text", formatDistance("Elevation Down", playerStats.getElevationDown()));

        cb.set("#LeftStats #Deaths.Text", "Deaths: " + playerStats.getDeaths());
        cb.set("#LeftStats #ChatMessages.Text", "Chat Messages: " + playerStats.getChatMessages());
        cb.set("#LeftStats #Jumps.Text", "Jumps: " + playerStats.getJumps());

        long totalActive = 0;
        long totalIdle = 0;
        long totalPlaytime = 0;

        List<PlaytimeSession> sessions = playtimeStats.getSessions();
        for (PlaytimeSession s : sessions) {
            totalActive += s.getActiveTime();
            totalIdle += s.getIdleTime();
            totalPlaytime += s.getActiveTime() + s.getIdleTime();
        }

        PlaytimeSession current = playtimeStats.getCurrentSession();
        if (current != null) {
            totalActive += current.getActiveTime();
            totalIdle += current.getIdleTime();
            totalPlaytime += current.getActiveTime() + current.getIdleTime();
        }

        cb.set("#RightStats #TotalPlaytime.Text", "Playtime: " + formatTime(totalPlaytime));
        cb.set("#RightStats #TotalActiveTime.Text", "Active Time: " + formatTime(totalActive));
        cb.set("#RightStats #TotalIdleTime.Text", "Idle Time: " + formatTime(totalIdle));
        cb.set("#RightStats #TotalSessions.Text", "Sessions: " + (sessions.size() + (current != null ? 1 : 0)));

        if (current != null) {
            cb.set("#RightStats #CurrentStarted.Text", "Started: " + formatDate(current.getStartTime()));
            cb.set("#RightStats #CurrentPlaytime.Text", "Playtime: " + formatTime(current.getActiveTime() + current.getIdleTime()));
            cb.set("#RightStats #CurrentActive.Text", "Active: " + formatTime(current.getActiveTime()));
            cb.set("#RightStats #CurrentIdle.Text", "Idle: " + formatTime(current.getIdleTime()));
        } else {
            cb.set("#RightStats #CurrentStarted.Text", "Started: --");
            cb.set("#RightStats #CurrentPlaytime.Text", "Playtime: --");
            cb.set("#RightStats #CurrentActive.Text", "Active: --");
            cb.set("#RightStats #CurrentIdle.Text", "Idle: --");
        }

        PlaytimeSession longest = null;
        long longestDuration = 0;
        for (PlaytimeSession s : sessions) {
            long duration = s.getActiveTime() + s.getIdleTime();
            if (duration > longestDuration) {
                longestDuration = duration;
                longest = s;
            }
        }

        if (longest != null) {
            cb.set("#RightStats #LongestStarted.Text", "Started: " + formatDate(longest.getStartTime()));
            cb.set("#RightStats #LongestEnded.Text", "Ended: " + (longest.getStopTime() > 0 ? formatDate(longest.getStopTime()) : "--"));
            cb.set("#RightStats #LongestPlaytime.Text", "Playtime: " + formatTime(longest.getActiveTime() + longest.getIdleTime()));
            cb.set("#RightStats #LongestActive.Text", "Active: " + formatTime(longest.getActiveTime()));
            cb.set("#RightStats #LongestIdle.Text", "Idle: " + formatTime(longest.getIdleTime()));
        } else {
            cb.set("#RightStats #LongestStarted.Text", "Started: --");
            cb.set("#RightStats #LongestEnded.Text", "Ended: --");
            cb.set("#RightStats #LongestPlaytime.Text", "Playtime: --");
            cb.set("#RightStats #LongestActive.Text", "Active: --");
            cb.set("#RightStats #LongestIdle.Text", "Idle: --");
        }
    }

    @Override
    public void handleEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull StatsPage.Data data) {}

    private String formatDistance(String label, double meters) {
        if (meters >= 1000) {
            return String.format("%s: %.2f km", label, meters / 1000.0);
        } else {
            return String.format("%s: %.0f m", label, meters);
        }
    }

    private String formatTime(long seconds) {
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

    private String formatDate(long epochSeconds) {
        Date date = new Date(epochSeconds * 1000);
        return DATE_FORMAT.format(date);
    }
}
