package io.github.chikyukido.lovelystats.handler;

import com.hypixel.hytale.logger.HytaleLogger;
import io.github.chikyukido.lovelystats.save.ItemStatsStorage;
import io.github.chikyukido.lovelystats.types.ItemStats;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ItemStatsHandler {
    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final ItemStatsHandler INSTANCE = new ItemStatsHandler();
    private final ConcurrentHashMap<UUID, ItemStats> players = new ConcurrentHashMap<>();

    private ItemStatsHandler() {}

    public static ItemStatsHandler get() {
        return INSTANCE;
    }

    public static void init() {
        var loaded = ItemStatsStorage.INSTANCE.loadAll();
        for (ItemStats stats : loaded) {
            INSTANCE.players.put(stats.getUuid(), stats);
        }
    }

    public void savePlayer(UUID uuid) {
        ItemStats stats = players.get(uuid);
        if (stats != null && stats.isDirty()) {
            try {
                ItemStatsStorage.INSTANCE.store(stats);
                stats.clearDirty();
            }catch (IOException e) {
                LOGGER.atWarning().withCause(e).log("Failed to save item stats for player %s", stats.getUuid());
            }
        }
    }

    public void saveAllPlayers() {
        for (ItemStats stats : players.values()) {
            if (stats.isDirty()) {
                try {
                    ItemStatsStorage.INSTANCE.store(stats);
                    stats.clearDirty();
                } catch (Exception ignored) {
                }
            }
        }
    }

    public void increaseBlockBreak(UUID uuid, long blockId) {
        ItemStats is = getBlockPlayer(uuid);
        is.increaseBlockBreak(blockId);
        is.markDirty();
    }

    public void increaseBlockPlace(UUID uuid, long blockId) {
        ItemStats is = getBlockPlayer(uuid);
        is.increaseBlockPlace(blockId);
        is.markDirty();
    }

    public void increaseCollected(UUID uuid, long itemId, long quantity) {
        ItemStats is = getBlockPlayer(uuid);
        is.increaseCollected(itemId, quantity);
        is.markDirty();
    }

    public void increaseDropped(UUID uuid, long itemId, long quantity) {
        ItemStats is = getBlockPlayer(uuid);
        is.increaseDropped(itemId, quantity);
        is.markDirty();
    }

    public void increaseUsed(UUID uuid, long itemId, long quantity) {
        ItemStats is = getBlockPlayer(uuid);
        is.increaseUsed(itemId, quantity);
        is.markDirty();
    }

    public void increaseCrafted(UUID uuid, long itemId, long quantity) {
        ItemStats is = getBlockPlayer(uuid);
        is.increaseCrafted(itemId, quantity);
        is.markDirty();
    }

    public void increaseToolBroken(UUID uuid, long toolId) {
        ItemStats is = getBlockPlayer(uuid);
        is.increaseToolBroken(toolId);
        is.markDirty();
    }

    public ItemStats getBlockPlayer(UUID uuid) {
        return players.computeIfAbsent(uuid, ItemStats::new);
    }
}
