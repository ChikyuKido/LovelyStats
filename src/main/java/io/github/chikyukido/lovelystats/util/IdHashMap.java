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
    private static final Map<Long, String> ITEM_ICON_HASHMAP = new HashMap<>();

    public static void init() {
        long startTime = System.nanoTime();
        Map<String, BlockType> allBlocks = BlockType.getAssetMap().getAssetMap();
        Map<String, Item> allItems =  Item.getAssetMap().getAssetMap();
        for (String id : allBlocks.keySet()) {
            BlockType block = allBlocks.get(id);
            Item item = block.getItem();
            if (item != null) {
                ITEM_HASHMAP.put(
                        Murmur3.hash64(block.getId()),
                        Message.translation(item.getTranslationKey()).getAnsiMessage()
                );
                ITEM_ICON_HASHMAP.put(Murmur3.hash64(block.getId()),item.getIcon());
            }else {
                ITEM_HASHMAP.put(
                        Murmur3.hash64(block.getId()),
                        id
                );
            }
        }
        for (String id : allItems.keySet()) {
            if(ITEM_HASHMAP.containsKey(Murmur3.hash64(id))) continue;
            Item item = allItems.get(id);
            ITEM_HASHMAP.put(
                    Murmur3.hash64(item.getId()),
                    Message.translation(item.getTranslationKey()).getAnsiMessage()
            );
            ITEM_ICON_HASHMAP.put(Murmur3.hash64(item.getId()),item.getIcon());
        }

        long endTime = System.nanoTime();
        long durationMs = (endTime - startTime) / 1_000_000;
        LOGGER.atInfo().log("Loaded %d block names in %dms",ITEM_HASHMAP.size(), durationMs);
    }

    public static String realName(long hash) {
        return ITEM_HASHMAP.getOrDefault(hash, "Unknown Block");
    }
    public static String realIcon(long hash) {
        return ITEM_ICON_HASHMAP.getOrDefault(hash, "Unknown Block");
    }
}
