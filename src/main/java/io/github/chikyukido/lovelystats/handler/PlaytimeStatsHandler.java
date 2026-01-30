package io.github.chikyukido.lovelystats.handler;

import com.hypixel.hytale.logger.HytaleLogger;
import io.github.chikyukido.lovelystats.save.PlaytimeStatsStorage;
import io.github.chikyukido.lovelystats.types.PlaytimeStats;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlaytimeStatsHandler {
    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final PlaytimeStatsHandler INSTANCE = new PlaytimeStatsHandler();
    private final ConcurrentHashMap<UUID, PlaytimeStats> players = new ConcurrentHashMap<>();

    private PlaytimeStatsHandler() {}

    public static PlaytimeStatsHandler get() {
        return INSTANCE;
    }

    public static void init() {
        var loaded = PlaytimeStatsStorage.INSTANCE.loadAll();
        for (PlaytimeStats stats : loaded) {
            INSTANCE.players.put(stats.getUuid(), stats);
        }
    }


    public void saveAllPlayers() {
        for (PlaytimeStats stats : players.values()) {
            if (stats.isDirty()) {
                try {
                    PlaytimeStatsStorage.INSTANCE.store(stats);
                    stats.clearDirty();
                } catch (IOException e) {
                    LOGGER.atWarning().withCause(e).log("Failed to save playtime stats for player %s", stats.getUuid());
                }
            }
        }
    }

    public void startPlaytimeSession(UUID uuid) {
        PlaytimeStats stats = players.computeIfAbsent(uuid, PlaytimeStats::new);
        stats.startPlaytimeSession();
        stats.markDirty();
    }

    public void endPlaytimeSession(UUID uuid) {
        PlaytimeStats stats = players.computeIfAbsent(uuid, PlaytimeStats::new);
        stats.endPlaytimeSession();
        stats.markDirty();
    }

    public void increaseActivePlaytime(UUID uuid, long playtime) {
        PlaytimeStats stats = players.computeIfAbsent(uuid, PlaytimeStats::new);
        stats.increaseActivePlaytime(playtime);
        stats.markDirty();
    }

    public void increaseIdlePlaytime(UUID uuid, long playtime) {
        PlaytimeStats stats = players.computeIfAbsent(uuid, PlaytimeStats::new);
        stats.increaseIdlePlaytime(playtime);
        stats.markDirty();
    }

    public PlaytimeStats getPlaytimeForPlayer(UUID uuid) {
        return players.computeIfAbsent(uuid, PlaytimeStats::new);
    }
}
