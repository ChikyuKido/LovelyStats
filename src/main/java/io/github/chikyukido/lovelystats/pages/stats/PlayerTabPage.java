package io.github.chikyukido.lovelystats.pages.stats;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.chikyukido.lovelystats.handler.PlayerStatsHandler;
import io.github.chikyukido.lovelystats.handler.PlaytimeStatsHandler;
import io.github.chikyukido.lovelystats.pages.TabPage;
import io.github.chikyukido.lovelystats.types.PlayerStats;
import io.github.chikyukido.lovelystats.types.PlaytimeSession;
import io.github.chikyukido.lovelystats.types.PlaytimeStats;
import io.github.chikyukido.lovelystats.util.Format;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public class PlayerTabPage extends TabPage {
    public PlayerTabPage(StatsPage parent,  UUID playerUUID) {
        super(parent,playerUUID);
    }


    @Override
    public void build(UICommandBuilder cb, UIEventBuilder event) {
        cb.append("#TabPages","stats/player/player_page.ui");

        PlayerStats playerStats = PlayerStatsHandler.get().getPlayerStats(playerUUID);
        PlaytimeStats playtimeStats = PlaytimeStatsHandler.get().getPlaytimeForPlayer(playerUUID);

        cb.set("#LeftStats #DistanceWalked.Text", "Walked: "+Format.formatDistance(playerStats.getDistanceWalked()));
        cb.set("#LeftStats #DistanceRun.Text", "Run: "+Format.formatDistance( playerStats.getDistanceRun()));
        cb.set("#LeftStats #DistanceSwam.Text", "Swam: "+Format.formatDistance( playerStats.getDistanceSwam()));
        cb.set("#LeftStats #DistanceFallen.Text", "Fallen: "+Format.formatDistance(playerStats.getDistanceFallen()));
        cb.set("#LeftStats #DistanceClimbed.Text", "Climbed: "+Format.formatDistance(playerStats.getDistanceClimbed()));
        cb.set("#LeftStats #DistanceSneaked.Text", "Sneaked: "+Format.formatDistance(playerStats.getDistanceSneaked()));
        cb.set("#LeftStats #ElevationUp.Text", "Elevation Up: " +Format.formatDistance(playerStats.getElevationUp()));
        cb.set("#LeftStats #ElevationDown.Text","Elevation Down: " +Format.formatDistance( playerStats.getElevationDown()));

        cb.set("#LeftStats #Deaths.Text", "Deaths: " + playerStats.getDeaths());
        cb.set("#LeftStats #ChatMessages.Text", "Chat Messages: " + playerStats.getChatMessages());
        cb.set("#LeftStats #Jumps.Text", "Jumps: " + playerStats.getJumps());

        long totalActive = playtimeStats.getTotalActivePlaytime();
        long totalIdle = playtimeStats.getTotalIdlePlaytime();
        long totalPlaytime = playtimeStats.getTotalPlaytime();

        List<PlaytimeSession> sessions = playtimeStats.getSessions();
        PlaytimeSession current = playtimeStats.getCurrentSession();

        cb.set("#RightStats #TotalPlaytime.Text", "Playtime: " + Format.formatTime(totalPlaytime));
        cb.set("#RightStats #TotalActiveTime.Text", "Active Time: " + Format.formatTime(totalActive));
        cb.set("#RightStats #TotalIdleTime.Text", "Idle Time: " + Format.formatTime(totalIdle));
        cb.set("#RightStats #TotalSessions.Text", "Sessions: " + (sessions.size() + (current != null ? 1 : 0)));

        if (current != null) {
            cb.set("#RightStats #CurrentStarted.Text", "Started: " + Format.formatDate(current.getStartTime()));
            cb.set("#RightStats #CurrentPlaytime.Text", "Playtime: " + Format.formatTime(current.getActiveTime() + current.getIdleTime()));
            cb.set("#RightStats #CurrentActive.Text", "Active: " + Format.formatTime(current.getActiveTime()));
            cb.set("#RightStats #CurrentIdle.Text", "Idle: " + Format.formatTime(current.getIdleTime()));
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
            cb.set("#RightStats #LongestStarted.Text", "Started: " + Format.formatDate(longest.getStartTime()));
            cb.set("#RightStats #LongestEnded.Text", "Ended: " + (longest.getStopTime() > 0 ? Format.formatDate(longest.getStopTime()) : "--"));
            cb.set("#RightStats #LongestPlaytime.Text", "Playtime: " + Format.formatTime(longest.getActiveTime() + longest.getIdleTime()));
            cb.set("#RightStats #LongestActive.Text", "Active: " + Format.formatTime(longest.getActiveTime()));
            cb.set("#RightStats #LongestIdle.Text", "Idle: " + Format.formatTime(longest.getIdleTime()));
        } else {
            cb.set("#RightStats #LongestStarted.Text", "Started: --");
            cb.set("#RightStats #LongestEnded.Text", "Ended: --");
            cb.set("#RightStats #LongestPlaytime.Text", "Playtime: --");
            cb.set("#RightStats #LongestActive.Text", "Active: --");
            cb.set("#RightStats #LongestIdle.Text", "Idle: --");
        }
    }

    @Override
    public void handleEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull String data) {}

}
