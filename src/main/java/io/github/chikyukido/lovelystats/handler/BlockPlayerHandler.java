package io.github.chikyukido.lovelystats.handler;

import io.github.chikyukido.lovelystats.save.BlockPlayerStorage;
import io.github.chikyukido.lovelystats.types.BlockPlayer;
import io.github.chikyukido.lovelystats.types.PlaytimePlayer;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BlockPlayerHandler {

    private static final BlockPlayerHandler INSTANCE = new BlockPlayerHandler();
    private final ConcurrentHashMap<UUID, BlockPlayer> players = new ConcurrentHashMap<>();

    private BlockPlayerHandler() {}

    public static BlockPlayerHandler get() {
        return INSTANCE;
    }

    public static void init() {
        try {
            var players = BlockPlayerStorage.INSTANCE.loadAll();
            for (BlockPlayer player : players) {
                INSTANCE.players.put(player.getUuid(), player);
            }
        } catch (IOException _) {

        }
    }

    public void savePlayer(UUID uuid) {
        if (players.containsKey(uuid)) {
            BlockPlayer player = players.get(uuid);
            try {
                BlockPlayerStorage.INSTANCE.store(player);
            } catch (Exception _) {
            }
        }
    }
    public void saveAllPlayers() {
        for (BlockPlayer player : players.values()) {
            savePlayer(player.getUuid());
        }
    }

    public void increaseBlockBreak(UUID uuid, long blockId) {
        getBlockPlayer(uuid).increaseBlockBreak(blockId);
    }
    public void increaseBlockPlace(UUID uuid, long blockId) {
        getBlockPlayer(uuid).increaseBlockPlace(blockId);
    }

    public BlockPlayer getBlockPlayer(UUID uuid) {
        return players.computeIfAbsent(uuid, BlockPlayer::new);
    }
}

