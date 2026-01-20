package io.github.chikyukido.lovelystats.util;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;

import java.util.HashMap;
import java.util.Map;

public class IdHashMap {

    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();


    private static final Map<Long, String> ITEM_HASHMAP = new HashMap<>();

    public static void init() {
        long startTime = System.nanoTime();
        Map<String, BlockType> allBlocks = BlockType.getAssetMap().getAssetMap();
        for (String id : allBlocks.keySet()) {
            BlockType block = allBlocks.get(id);
            Item item = block.getItem();
            if (item != null) {
                ITEM_HASHMAP.put(
                        Murmur3.hash64(block.getId()),
                        Message.translation(item.getTranslationKey()).getAnsiMessage()
                );
            } else {
                ITEM_HASHMAP.put(
                        Murmur3.hash64(block.getId()),
                        block.getId()
                );
            }
        }

        long endTime = System.nanoTime();
        long durationMs = (endTime - startTime) / 1_000_000;
        LOGGER.atInfo().log("Loaded block names in %dms", durationMs);
    }

    public static String realName(long hash) {
        return ITEM_HASHMAP.getOrDefault(hash, "Unknown Block");
    }
}
