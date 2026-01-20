package io.github.chikyukido.lovelystats.commands;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import fi.sulku.hytale.TinyMsg;
import io.github.chikyukido.lovelystats.handler.ItemStatsHandler;
import io.github.chikyukido.lovelystats.types.ItemStats;
import io.github.chikyukido.lovelystats.util.IdHashMap;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

public class ItemCommand {

    public static void run(PlayerRef playerRef) {
        ItemStats p = ItemStatsHandler.get().getBlockPlayer(playerRef.getUuid());

        long totalDropped = p.getDropped().values().stream().mapToLong(Long::longValue).sum();
        long totalCollected = p.getCollected().values().stream().mapToLong(Long::longValue).sum();
        long totalCrafted = p.getCrafted().values().stream().mapToLong(Long::longValue).sum();

        String msg = "<gold><bold>Item Statistics</bold></gold>\n" +
                "<red>Total Dropped:</red> <white>" + totalDropped + "</white>\n" +
                "<green>Total Collected:</green> <white>" + totalCollected + "</white>\n" +
                "<aqua>Total Crafted:</aqua> <white>" + totalCrafted + "</white>\n";

        msg += "<aqua>Top 3 Dropped Items:</aqua>\n" + formatTop(p.getDropped());
        msg += "<yellow>Top 3 Collected Items:</yellow>\n" + formatTop(p.getCollected());
        msg += "<light_purple>Top 3 Crafted Items:</light_purple>\n" + formatTop(p.getCrafted());

        playerRef.sendMessage(TinyMsg.parse(msg));
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
