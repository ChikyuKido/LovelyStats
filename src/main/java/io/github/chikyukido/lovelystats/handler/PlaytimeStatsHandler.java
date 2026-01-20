package io.github.chikyukido.lovelystats.handler;

import io.github.chikyukido.lovelystats.save.PlaytimeStatsStorage;
import io.github.chikyukido.lovelystats.types.PlaytimeStats;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlaytimeStatsHandler {
    private static final PlaytimeStatsHandler INSTANCE = new PlaytimeStatsHandler();
    private final ConcurrentHashMap<UUID, PlaytimeStats> players = new ConcurrentHashMap<>();

    private PlaytimeStatsHandler() {
    }

    public static PlaytimeStatsHandler get() {
        return INSTANCE;
    }

    public static void init() {
        try {
            var players = PlaytimeStatsStorage.INSTANCE.loadAll();
            for (PlaytimeStats player : players) {
                INSTANCE.players.put(player.getUuid(), player);
            }
        } catch (IOException _) {
        }
    }

    public void savePlayer(UUID uuid) {
        if (players.containsKey(uuid)) {
            PlaytimeStats player = players.get(uuid);
            try {
                PlaytimeStatsStorage.INSTANCE.store(player);
            } catch (Exception _) {
            }
        }
    }

    public void saveAllPlayers() {
        for (PlaytimeStats player : players.values()) {
            savePlayer(player.getUuid());
        }
    }

    public void startPlaytimeSession(UUID uuid) {
        players.computeIfAbsent(uuid, PlaytimeStats::new).startPlaytimeSession();
    }

    public void endPlaytimeSession(UUID uuid) {
        players.computeIfAbsent(uuid, PlaytimeStats::new).endPlaytimeSession();
    }

    public void increaseActivePlaytime(UUID uuid, long playtime) {
        players.computeIfAbsent(uuid, PlaytimeStats::new).increaseActivePlaytime(playtime);
    }

    public void increaseIdlePlaytime(UUID uuid, long playtime) {
        players.computeIfAbsent(uuid, PlaytimeStats::new).increaseIdlePlaytime(playtime);
    }

    public PlaytimeStats getPlaytimeForPlayer(UUID uuid) {
        return players.computeIfAbsent(uuid, PlaytimeStats::new);
    }


}
