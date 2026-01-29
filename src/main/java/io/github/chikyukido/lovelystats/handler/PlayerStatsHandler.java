package io.github.chikyukido.lovelystats.handler;

import io.github.chikyukido.lovelystats.save.PlayerStatsStorage;
import io.github.chikyukido.lovelystats.types.PlayerStats;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerStatsHandler {

    private static final PlayerStatsHandler INSTANCE = new PlayerStatsHandler();
    private final ConcurrentHashMap<UUID, PlayerStats> players = new ConcurrentHashMap<>();

    private PlayerStatsHandler() {}

    public static PlayerStatsHandler get() {
        return INSTANCE;
    }

    public static void init() {
        try {
            var loadedPlayers = PlayerStatsStorage.INSTANCE.loadAll();
            for (PlayerStats player : loadedPlayers) {
                INSTANCE.players.put(player.getUuid(), player);
            }
        } catch (IOException ignored) {
        }
    }

    public void savePlayer(UUID uuid) {
        PlayerStats player = players.get(uuid);
        if (player != null && player.isDirty()) {
            try {
                PlayerStatsStorage.INSTANCE.store(player);
                player.clearDirty();
            } catch (IOException ignored) {
            }
        }
    }

    public void saveAllPlayers() {
        for (PlayerStats player : players.values()) {
            if (player.isDirty()) {
                try {
                    PlayerStatsStorage.INSTANCE.store(player);
                    player.clearDirty();
                } catch (IOException ignored) {
                }
            }
        }
    }

    public void addDistanceWalked(UUID uuid, double amount) {
        PlayerStats ps = getPlayerStats(uuid);
        ps.addDistanceWalked(amount);
        ps.markDirty();
    }

    public void addDistanceRun(UUID uuid, double amount) {
        PlayerStats ps = getPlayerStats(uuid);
        ps.addDistanceRun(amount);
        ps.markDirty();
    }

    public void addDistanceSwam(UUID uuid, double amount) {
        PlayerStats ps = getPlayerStats(uuid);
        ps.addDistanceSwam(amount);
        ps.markDirty();
    }

    public void addDistanceFallen(UUID uuid, double amount) {
        PlayerStats ps = getPlayerStats(uuid);
        ps.addDistanceFallen(amount);
        ps.markDirty();
    }

    public void addDistanceClimbed(UUID uuid, double amount) {
        PlayerStats ps = getPlayerStats(uuid);
        ps.addDistanceClimbed(amount);
        ps.markDirty();
    }

    public void addDistanceSneaked(UUID uuid, double amount) {
        PlayerStats ps = getPlayerStats(uuid);
        ps.addDistanceSneaked(amount);
        ps.markDirty();
    }

    public void addElevationUp(UUID uuid, double amount) {
        PlayerStats ps = getPlayerStats(uuid);
        ps.addElevationUp(amount);
        ps.markDirty();
    }

    public void addElevationDown(UUID uuid, double amount) {
        PlayerStats ps = getPlayerStats(uuid);
        ps.addElevationDown(amount);
        ps.markDirty();
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

    public void incrementJumps(UUID uuid) {
        PlayerStats ps = getPlayerStats(uuid);
        ps.incrementJumps();
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
        return players.computeIfAbsent(uuid, PlayerStats::new);
    }
}