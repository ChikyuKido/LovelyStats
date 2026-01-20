package io.github.chikyukido.lovelystats.handler;

import io.github.chikyukido.lovelystats.save.ItemPlayerStorage;
import io.github.chikyukido.lovelystats.types.ItemPlayer;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ItemPlayerHandler {

    private static final ItemPlayerHandler INSTANCE = new ItemPlayerHandler();
    private final ConcurrentHashMap<UUID, ItemPlayer> players = new ConcurrentHashMap<>();

    private ItemPlayerHandler() {}

    public static ItemPlayerHandler get() {
        return INSTANCE;
    }

    public static void init() {
        try {
            var players = ItemPlayerStorage.INSTANCE.loadAll();
            for (ItemPlayer player : players) {
                INSTANCE.players.put(player.getUuid(), player);
            }
        } catch (IOException _) {

        }
    }

    public void savePlayer(UUID uuid) {
        if (players.containsKey(uuid)) {
            ItemPlayer player = players.get(uuid);
            try {
                ItemPlayerStorage.INSTANCE.store(player);
            } catch (Exception _) {
            }
        }
    }
    public void saveAllPlayers() {
        for (ItemPlayer player : players.values()) {
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
    public ItemPlayer getBlockPlayer(UUID uuid) {
        return players.computeIfAbsent(uuid, ItemPlayer::new);
    }
}

