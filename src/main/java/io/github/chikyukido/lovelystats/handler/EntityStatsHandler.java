package io.github.chikyukido.lovelystats.handler;


import io.github.chikyukido.lovelystats.save.EntityStatsStorage;
import io.github.chikyukido.lovelystats.types.EntityStats;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EntityStatsHandler {

    private static final EntityStatsHandler INSTANCE = new EntityStatsHandler();
    private final ConcurrentHashMap<UUID, EntityStats> players = new ConcurrentHashMap<>();

    private EntityStatsHandler() {
    }

    public static EntityStatsHandler get() {
        return INSTANCE;
    }

    public static void init() {
        try {
            var players = EntityStatsStorage.INSTANCE.loadAll();
            for (EntityStats player : players) {
                INSTANCE.players.put(player.getUuid(), player);
            }
        } catch (IOException _) {

        }
    }

    public void savePlayer(UUID uuid) {
        if (players.containsKey(uuid)) {
            EntityStats player = players.get(uuid);
            try {
                EntityStatsStorage.INSTANCE.store(player);
            } catch (Exception _) {
            }
        }
    }

    public void saveAllPlayers() {
        for (EntityStats player : players.values()) {
            savePlayer(player.getUuid());
        }
    }

    public void increaseKilled(UUID uuid, long entityId) {
        getEntityStatsFor(uuid).getEntityStats(entityId).increaseKilled();
    }
    public void increaseKilledBY(UUID uuid, long entityId) {
        getEntityStatsFor(uuid).getEntityStats(entityId).increaseKilledBy();
    }
    public void increaseDamageDealt(UUID uuid, long entityId,double damage) {
        getEntityStatsFor(uuid).getEntityStats(entityId).increaseDamageDealt(damage);
    }
    public void increaseDamageReceived(UUID uuid, long entityId,double damage) {
        getEntityStatsFor(uuid).getEntityStats(entityId).increaseDamageReceived(damage);
    }

    public EntityStats getEntityStatsFor(UUID uuid) {
        return players.computeIfAbsent(uuid, EntityStats::new);
    }
}

