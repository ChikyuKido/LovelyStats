package io.github.chikyukido.lovelystats.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import fi.sulku.hytale.TinyMsg;
import io.github.chikyukido.lovelystats.handler.ItemStatsHandler;
import io.github.chikyukido.lovelystats.types.ItemStats;
import io.github.chikyukido.lovelystats.util.IdHashMap;

import java.util.*;

public class BlockCommand {

    public static void run(PlayerRef playerRef) {
        ItemStats p = ItemStatsHandler.get().getBlockPlayer(playerRef.getUuid());

        long totalBroken = p.getBlocksBroken().values().stream().mapToLong(Long::longValue).sum();
        long totalPlaced = p.getBlocksPlaced().values().stream().mapToLong(Long::longValue).sum();

        String msg = "<gold><bold>Block Statistics</bold></gold>\n" +
                "<red>Total Broken:</red> <white>" + totalBroken + "</white>\n" +
                "<green>Total Placed:</green> <white>" + totalPlaced + "</white>\n";

        msg += "<aqua>Top 3 Broken Blocks:</aqua>\n";
        msg += formatTop(p.getBlocksBroken());

        msg += "<yellow>Top 3 Placed Blocks:</yellow>\n";
        msg += formatTop(p.getBlocksPlaced());

        playerRef.sendMessage(TinyMsg.parse(msg));
        Map<String, BlockType> allBlocks = BlockType.getAssetMap().getAssetMap();
        for (String id : allBlocks.keySet()) {
            BlockType block = allBlocks.get(id);
            Item item = block.getItem();
            if(item == null) continue;
            playerRef.sendMessage(Message.translation(item.getTranslationKey()));
            break;
        }
    }
    private static String formatTop(Map<Long, Long> map) {
        return map.entrySet().stream()
                .sorted(Collections.reverseOrder(Comparator.comparingLong(Map.Entry::getValue)))
                .limit(3)
                .map(e -> "<white>" + realName(e.getKey()) + "</white>: <green>" + e.getValue() + "</green>\n")
                .reduce("", String::concat);
    }

    private static String realName(long hash) {
        return IdHashMap.realName(hash);
    }

}

