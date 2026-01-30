package io.github.chikyukido.lovelystats.handler;

import com.hypixel.hytale.logger.HytaleLogger;
import io.github.chikyukido.lovelystats.save.PlayerStatsStorage;
import io.github.chikyukido.lovelystats.types.PlayerStats;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerStatsHandler {
    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final PlayerStatsHandler INSTANCE = new PlayerStatsHandler();
    private final ConcurrentHashMap<UUID, PlayerStats> players = new ConcurrentHashMap<>();
    private final Long2ObjectMap<PlayerStats> playersFast = new Long2ObjectOpenHashMap<>();

    private PlayerStatsHandler() {}

    public static PlayerStatsHandler get() {
        return INSTANCE;
    }

    public static void init() {

        var loadedPlayers = PlayerStatsStorage.INSTANCE.loadAll();
        for (PlayerStats player : loadedPlayers) {
            INSTANCE.players.put(player.getUuid(), player);
            INSTANCE.playersFast.put(player.getUuid().getMostSignificantBits(), player);
        }

    }


    public void saveAllPlayers() {
        for (PlayerStats player : players.values()) {
            if (player.isDirty()) {
                try {
                    PlayerStatsStorage.INSTANCE.store(player);
                    player.clearDirty();
                } catch (IOException e) {
                    LOGGER.atWarning().withCause(e).log("Failed to save entity stats for player %s", player.getUuid());
                }
            }
        }
    }



    public void incrementChatMessages(UUID uuid) {
        PlayerStats ps = getPlayerStats(uuid);
        ps.incrementChatMessages();
        ps.markDirty();
    }

    public void incrementDeaths(UUID uuid) {
        PlayerStats ps = getPlayerStats(uuid);
        ps.incrementDeaths();
        ps.markDirty();
    }

    public void incrementPlayerDeaths(UUID uuid) {
        PlayerStats ps = getPlayerStats(uuid);
        ps.incrementPlayerDeaths();
        ps.markDirty();
    }

    public void incrementPlayerKills(UUID uuid) {
        PlayerStats ps = getPlayerStats(uuid);
        ps.incrementPlayerKills();
        ps.markDirty();
    }

    public void addPlayerDamageDealt(UUID uuid, double amount) {
        PlayerStats ps = getPlayerStats(uuid);
        ps.addPlayerDamageDealt(amount);
        ps.markDirty();
    }

    public void addPlayerDamageReceived(UUID uuid, double amount) {
        PlayerStats ps = getPlayerStats(uuid);
        ps.addPlayerDamageReceived(amount);
        ps.markDirty();
    }


    public PlayerStats getPlayerStats(UUID uuid) {
        long key = uuid.getMostSignificantBits();
        PlayerStats stats = playersFast.get(key);
        if (stats != null) return stats;

        stats = new PlayerStats(uuid);
        players.putIfAbsent(uuid, stats);
        playersFast.put(key, stats);
        return stats;
    }

}