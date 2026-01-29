package io.github.chikyukido.lovelystats.pages.stats;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.chikyukido.lovelystats.handler.ItemStatsHandler;
import io.github.chikyukido.lovelystats.pages.UpdateHandler;
import io.github.chikyukido.lovelystats.pages.table.*;
import io.github.chikyukido.lovelystats.types.ItemStats;
import io.github.chikyukido.lovelystats.util.IdHashMap;

import java.util.*;

public class ItemTabPage extends TablePage {

    public ItemTabPage(UpdateHandler parent, PlayerRef playerRef) {
        super(parent, playerRef, new TablePageConfig("ItemTab", 0, true));
        config.setIconSize(30);

        config.getRows().add(new TablePageRow("Name", 160, TablePageRowType.STRING, TablePageRowVisualizeType.STRING));
        config.getRows().add(new TablePageRow("Placed", 120, TablePageRowType.LONG, TablePageRowVisualizeType.STRING));
        config.getRows().add(new TablePageRow("Destroyed", 120, TablePageRowType.LONG, TablePageRowVisualizeType.STRING));
        config.getRows().add(new TablePageRow("Collected", 120, TablePageRowType.LONG, TablePageRowVisualizeType.STRING));
        config.getRows().add(new TablePageRow("Dropped", 120, TablePageRowType.LONG, TablePageRowVisualizeType.STRING));
        config.getRows().add(new TablePageRow("Crafted", 120, TablePageRowType.LONG, TablePageRowVisualizeType.STRING));

        List<ItemStatsData> statsList = aggregate(playerRef.getUuid());
        Object[][] values = new Object[statsList.size()][];

        for (int i = 0; i < statsList.size(); i++) {
            values[i] = aggregateRow(statsList.get(i));
        }

        config.setValues(values);
    }

    private Object[] aggregateRow(ItemStatsData stats) {
        Object[] data = new Object[config.getRows().size() + 1];  // +1 for icon
        data[0] = IdHashMap.realIcon(stats.blockId());
        data[1] = IdHashMap.realName(stats.blockId());
        data[2] = stats.placed;
        data[3] = stats.broken;
        data[4] = stats.collected;
        data[5] = stats.dropped;
        data[6] = stats.crafted;
        return data;
    }

    private List<ItemStatsData> aggregate(UUID playerUuid) {
        ItemStats itemPlayer = ItemStatsHandler.get().getBlockPlayer(playerUuid);

        Set<Long> allBlockIds = new HashSet<>();
        allBlockIds.addAll(itemPlayer.getBlocksBroken().keySet());
        allBlockIds.addAll(itemPlayer.getBlocksPlaced().keySet());
        allBlockIds.addAll(itemPlayer.getCrafted().keySet());
        allBlockIds.addAll(itemPlayer.getDropped().keySet());
        allBlockIds.addAll(itemPlayer.getCollected().keySet());

        List<ItemStatsData> result = new ArrayList<>();
        for (long blockId : allBlockIds) {
            long placed = itemPlayer.getBlocksPlaced().getOrDefault(blockId, 0L);
            long broken = itemPlayer.getBlocksBroken().getOrDefault(blockId, 0L);
            long collected = itemPlayer.getCollected().getOrDefault(blockId, 0L);
            long dropped = itemPlayer.getDropped().getOrDefault(blockId, 0L);
            long crafted = itemPlayer.getCrafted().getOrDefault(blockId, 0L);

            result.add(new ItemStatsData(blockId, placed, broken, collected, dropped, crafted));
        }
        return result;
    }
    private record ItemStatsData(long blockId, long placed, long broken, long collected, long dropped, long crafted) { }
}
