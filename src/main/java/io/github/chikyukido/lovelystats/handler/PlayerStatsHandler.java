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
                INSTANCE.players.put(UUID.fromString(player.getUuid()), player);
            }
        } catch (IOException e) {}
    }

    public void savePlayer(UUID uuid) {
        PlayerStats player = players.get(uuid);
        if (player != null) {
            try {
                PlayerStatsStorage.INSTANCE.store(player);
            } catch (IOException e) {}
        }
    }

    public void saveAllPlayers() {
        for (PlayerStats player : players.values()) {
            savePlayer(UUID.fromString(player.getUuid()));
        }
    }

    public void addDistanceWalked(UUID uuid, double amount) {
        getPlayerStats(uuid).addDistanceWalked(amount);
    }

    public void addDistanceRun(UUID uuid, double amount) {
        getPlayerStats(uuid).addDistanceRun(amount);
    }

    public void addDistanceSwam(UUID uuid, double amount) {
        getPlayerStats(uuid).addDistanceSwam(amount);
    }

    public void addDistanceFallen(UUID uuid, double amount) {
        getPlayerStats(uuid).addDistanceFallen(amount);
    }

    public void addDistanceClimbed(UUID uuid, double amount) {
        getPlayerStats(uuid).addDistanceClimbed(amount);
    }

    public void addDistanceSneaked(UUID uuid, double amount) {
        getPlayerStats(uuid).addDistanceSneaked(amount);
    }

    public void addElevationUp(UUID uuid, double amount) {
        getPlayerStats(uuid).addElevationUp(amount);
    }

    public void addElevationDown(UUID uuid, double amount) {
        getPlayerStats(uuid).addElevationDown(amount);
    }

    public void incrementChatMessages(UUID uuid) {
        getPlayerStats(uuid).incrementChatMessages();
    }

    public void incrementDeaths(UUID uuid) {
        getPlayerStats(uuid).incrementDeaths();
    }

    public PlayerStats getPlayerStats(UUID uuid) {
        return players.computeIfAbsent(uuid, id -> new PlayerStats(id.toString()));
    }
}
