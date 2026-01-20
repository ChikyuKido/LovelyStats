package io.github.chikyukido.lovelystats.types;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BlockPlayer {
    private final UUID uuid;
    private final Map<Long,Long> blocksBroken = new HashMap<>();
    private final Map<Long,Long> blocksPlaced = new HashMap<>();

    public BlockPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    public void increaseBlockBreak(long blockId) {
        blocksBroken.put(blockId, blocksBroken.getOrDefault(blockId, 0L) + 1);
    }
    public void increaseBlockPlace(long blockId) {
        blocksPlaced.put(blockId, blocksPlaced.getOrDefault(blockId, 0L) + 1);
    }

    public Map<Long,Long> getBlocksBroken() {
        return blocksBroken;
    }
    public Map<Long,Long> getBlocksPlaced() {
        return blocksPlaced;
    }

    public UUID getUuid() {
        return uuid;
    }
}
