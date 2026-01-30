package io.github.chikyukido.lovelystats.handler;

import com.hypixel.hytale.logger.HytaleLogger;
import io.github.chikyukido.lovelystats.save.EntityStatsStorage;
import io.github.chikyukido.lovelystats.types.EntityStats;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EntityStatsHandler {
    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final EntityStatsHandler INSTANCE = new EntityStatsHandler();
    private final ConcurrentHashMap<UUID, EntityStats> players = new ConcurrentHashMap<>();

    private EntityStatsHandler() {}

    public static EntityStatsHandler get() {
        return INSTANCE;
    }

    public static void init() {
        var loaded = EntityStatsStorage.INSTANCE.loadAll();
        for (EntityStats stats : loaded) {
            INSTANCE.players.put(stats.getUuid(), stats);
        }
    }

    public void saveAllPlayers() {
        for (EntityStats stats : players.values()) {
            if (stats.isDirty()) {
                try {
                    EntityStatsStorage.INSTANCE.store(stats);
                    stats.clearDirty();
                } catch (IOException e) {
                    LOGGER.atWarning().withCause(e).log("Failed to save entity stats for player %s", stats.getUuid());
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
