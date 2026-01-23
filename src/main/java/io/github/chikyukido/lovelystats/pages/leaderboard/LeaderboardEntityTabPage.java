package io.github.chikyukido.lovelystats.pages.leaderboard;

import io.github.chikyukido.lovelystats.handler.EntityStatsHandler;
import io.github.chikyukido.lovelystats.handler.RecordedPlayerHandler;
import io.github.chikyukido.lovelystats.pages.UpdateHandler;
import io.github.chikyukido.lovelystats.pages.table.*;
import io.github.chikyukido.lovelystats.types.EntityStats;

import java.util.List;
import java.util.UUID;

public class LeaderboardEntityTabPage extends TablePage {

    public LeaderboardEntityTabPage(UpdateHandler parent, UUID playerUUID) {
        super(parent, playerUUID, new TablePageConfig("LeaderboardEntityTab", 10, false));

        config.getRows().add(new TablePageRow("Name", 120, TablePageRowType.STRING, TablePageRowVisualizeType.STRING));
        config.getRows().add(new TablePageRow("Killed", 120, TablePageRowType.LONG, TablePageRowVisualizeType.STRING));
        config.getRows().add(new TablePageRow("KilledBy", 120, TablePageRowType.LONG, TablePageRowVisualizeType.STRING));
        config.getRows().add(new TablePageRow("DamageDealt", 120, TablePageRowType.DOUBLE, TablePageRowVisualizeType.STRING));
        config.getRows().add(new TablePageRow("DamageReceived", 120, TablePageRowType.DOUBLE, TablePageRowVisualizeType.STRING));

        List<UUID> players = RecordedPlayerHandler.get().getPlayers();
        Object[][] values = new Object[players.size()][config.getRows().size()];
        for (int i = 0; i < players.size(); i++) {
            values[i] = aggregate(players.get(i));
        }
        config.setValues(values);
    }

    private Object[] aggregate(UUID playerUuid) {
        EntityStats entity = EntityStatsHandler.get().getEntityStatsFor(playerUuid);
        Object[] data = new Object[config.getRows().size()];
        data[0] = RecordedPlayerHandler.get().getUsername(playerUuid);
        data[1] = entity.getTotalKilled();
        data[2] = entity.getTotalKilledBy();
        data[3] = entity.getTotalDamageDealt();
        data[4] = entity.getTotalDamageReceived();
        return data;
    }
}
