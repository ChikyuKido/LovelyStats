package io.github.chikyukido.lovelystats.pages.leaderboard;

import io.github.chikyukido.lovelystats.handler.ItemStatsHandler;
import io.github.chikyukido.lovelystats.handler.RecordedPlayerHandler;
import io.github.chikyukido.lovelystats.pages.UpdateHandler;
import io.github.chikyukido.lovelystats.pages.table.*;
import io.github.chikyukido.lovelystats.types.ItemStats;

import java.util.List;
import java.util.UUID;

public class LeaderboardItemTabPage extends TablePage {

    public LeaderboardItemTabPage(UpdateHandler parent, UUID playerUUID) {
        super(parent, playerUUID, new TablePageConfig("LeaderboardItemTab", 10, false));

        config.getRows().add(new TablePageRow("Name", 120, TablePageRowType.STRING, TablePageRowVisualizeType.STRING));
        config.getRows().add(new TablePageRow("Placed", 120, TablePageRowType.LONG, TablePageRowVisualizeType.STRING));
        config.getRows().add(new TablePageRow("Destroyed", 120, TablePageRowType.LONG, TablePageRowVisualizeType.STRING));
        config.getRows().add(new TablePageRow("Collected", 120, TablePageRowType.LONG, TablePageRowVisualizeType.STRING));
        config.getRows().add(new TablePageRow("Dropped", 120, TablePageRowType.LONG, TablePageRowVisualizeType.STRING));
        config.getRows().add(new TablePageRow("Crafted", 120, TablePageRowType.LONG, TablePageRowVisualizeType.STRING));

        List<UUID> players = RecordedPlayerHandler.get().getPlayers();
        Object[][] values = new Object[players.size()][config.getRows().size()];
        for (int i = 0; i < players.size(); i++) {
            values[i] = aggregate(players.get(i));
        }
        config.setValues(values);
    }

    private Object[] aggregate(UUID playerUuid) {
        ItemStats itemStats = ItemStatsHandler.get().getBlockPlayer(playerUuid);
        Object[] data = new Object[config.getRows().size()];
        data[0] = RecordedPlayerHandler.get().getUsername(playerUuid);
        data[1] = itemStats.getTotalBlocksPlaced();
        data[2] = itemStats.getTotalBlocksBroken();
        data[3] = itemStats.getTotalCollected();
        data[4] = itemStats.getTotalDropped();
        data[5] = itemStats.getTotalItemsCrafted();
        return data;
    }
}
