package io.github.chikyukido.lovelystats.handler;

import io.github.chikyukido.lovelystats.save.ItemStatsStorage;
import io.github.chikyukido.lovelystats.types.ItemStats;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ItemStatsHandler {

    private static final ItemStatsHandler INSTANCE = new ItemStatsHandler();
    private final ConcurrentHashMap<UUID, ItemStats> players = new ConcurrentHashMap<>();

    private ItemStatsHandler() {}

    public static ItemStatsHandler get() {
        return INSTANCE;
    }

    public static void init() {
        try {
            var players = ItemStatsStorage.INSTANCE.loadAll();
            for (ItemStats player : players) {
                INSTANCE.players.put(player.getUuid(), player);
            }
        } catch (IOException _) {

        }
    }

    public void savePlayer(UUID uuid) {
        if (players.containsKey(uuid)) {
            ItemStats player = players.get(uuid);
            try {
                ItemStatsStorage.INSTANCE.store(player);
            } catch (Exception _) {
            }
        }
    }
    public void saveAllPlayers() {
        for (ItemStats player : players.values()) {
            savePlayer(player.getUuid());
        }
    }

    public void increaseBlockBreak(UUID uuid, long blockId) {
        getBlockPlayer(uuid).increaseBlockBreak(blockId);
    }
    public void increaseBlockPlace(UUID uuid, long blockId) {
        getBlockPlayer(uuid).increaseBlockPlace(blockId);
    }
    public void increaseCollected(UUID uuid, long itemId,long quantity) {
        getBlockPlayer(uuid).increaseCollected(itemId,quantity);
    }

    public void increaseDropped(UUID uuid, long itemId,long quantity) {
        getBlockPlayer(uuid).increaseDropped(itemId,quantity);
    }

    public void increaseUsed(UUID uuid, long itemId,long quantity) {
        getBlockPlayer(uuid).increaseUsed(itemId,quantity);
    }

    public void increaseCrafted(UUID uuid, long itemId,long quantity) {
        getBlockPlayer(uuid).increaseCrafted(itemId,quantity);
    }

    public void increaseToolBroken(UUID uuid, long toolId) {
        getBlockPlayer(uuid).increaseToolBroken(toolId);
    }
    public ItemStats getBlockPlayer(UUID uuid) {
        return players.computeIfAbsent(uuid, ItemStats::new);
    }
}

