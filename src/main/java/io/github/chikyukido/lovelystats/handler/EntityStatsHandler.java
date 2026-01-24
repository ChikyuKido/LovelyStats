package io.github.chikyukido.lovelystats.handler;

import io.github.chikyukido.lovelystats.save.EntityStatsStorage;
import io.github.chikyukido.lovelystats.types.EntityStats;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EntityStatsHandler {

    private static final EntityStatsHandler INSTANCE = new EntityStatsHandler();
    private final ConcurrentHashMap<UUID, EntityStats> players = new ConcurrentHashMap<>();

    private EntityStatsHandler() {}

    public static EntityStatsHandler get() {
        return INSTANCE;
    }

    public static void init() {
        try {
            var loaded = EntityStatsStorage.INSTANCE.loadAll();
            for (EntityStats stats : loaded) {
                INSTANCE.players.put(stats.getUuid(), stats);
            }
        } catch (IOException ignored) {
        }
    }

    public void savePlayer(UUID uuid) {
        EntityStats stats = players.get(uuid);
        if (stats != null && stats.isDirty()) {
            try {
                EntityStatsStorage.INSTANCE.store(stats);
                stats.clearDirty();
            } catch (Exception ignored) {
            }
        }
    }

    public void saveAllPlayers() {
        for (EntityStats stats : players.values()) {
            if (stats.isDirty()) {
                try {
                    EntityStatsStorage.INSTANCE.store(stats);
                    stats.clearDirty();
                } catch (Exception ignored) {
                }
            }
        }
    }

    public void increaseKilled(UUID uuid, long entityId) {
        EntityStats es = getEntityStatsFor(uuid);
        es.getEntityStats(entityId).increaseKilled();
        es.markDirty();
    }

    public void increaseKilledBY(UUID uuid, long entityId) {
        EntityStats es = getEntityStatsFor(uuid);
        es.getEntityStats(entityId).increaseKilledBy();
        es.markDirty();
    }

    public void increaseDamageDealt(UUID uuid, long entityId, double damage) {
        EntityStats es = getEntityStatsFor(uuid);
        es.getEntityStats(entityId).increaseDamageDealt(damage);
        es.markDirty();
    }

    public void increaseDamageReceived(UUID uuid, long entityId, double damage) {
        EntityStats es = getEntityStatsFor(uuid);
        es.getEntityStats(entityId).increaseDamageReceived(damage);
        es.markDirty();
    }

    public EntityStats getEntityStatsFor(UUID uuid) {
        return players.computeIfAbsent(uuid, EntityStats::new);
    }
}
