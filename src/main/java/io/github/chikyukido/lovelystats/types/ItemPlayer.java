package io.github.chikyukido.lovelystats.types;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ItemPlayer {
    private final UUID uuid;

    private final Map<Long, Long> blocksBroken = new HashMap<>();
    private final Map<Long, Long> blocksPlaced = new HashMap<>();
    private final Map<Long, Long> collected = new HashMap<>();
    private final Map<Long, Long> dropped = new HashMap<>();
    private final Map<Long, Long> used = new HashMap<>();
    private final Map<Long, Long> crafted = new HashMap<>();
    private final Map<Long, Long> toolsBroken = new HashMap<>();

    public ItemPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    public void increaseBlockBreak(long blockId) {
        blocksBroken.merge(blockId, 1L, Long::sum);
    }

    public void increaseBlockPlace(long blockId) {
        blocksPlaced.merge(blockId, 1L, Long::sum);
    }

    public void increaseCollected(long itemId,long quantity) {
        collected.merge(itemId, quantity, Long::sum);
    }

    public void increaseDropped(long itemId,long quantity) {
        dropped.merge(itemId, quantity, Long::sum);
    }

    public void increaseUsed(long itemId,long quantity) {
        used.merge(itemId, quantity, Long::sum);
    }

    public void increaseCrafted(long itemId,long quantity) {
        crafted.merge(itemId, quantity, Long::sum);
    }

    public void increaseToolBroken(long toolId) {
        toolsBroken.merge(toolId, 1L, Long::sum);
    }

    public Map<Long, Long> getBlocksBroken() {
        return blocksBroken;
    }

    public Map<Long, Long> getBlocksPlaced() {
        return blocksPlaced;
    }

    public Map<Long, Long> getCollected() {
        return collected;
    }

    public Map<Long, Long> getDropped() {
        return dropped;
    }

    public Map<Long, Long> getUsed() {
        return used;
    }

    public Map<Long, Long> getCrafted() {
        return crafted;
    }

    public Map<Long, Long> getToolsBroken() {
        return toolsBroken;
    }

    public UUID getUuid() {
        return uuid;
    }
}