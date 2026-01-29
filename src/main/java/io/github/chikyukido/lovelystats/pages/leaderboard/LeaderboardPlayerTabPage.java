package io.github.chikyukido.lovelystats.pages.leaderboard;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.chikyukido.lovelystats.handler.PlayerStatsHandler;
import io.github.chikyukido.lovelystats.handler.PlaytimeStatsHandler;
import io.github.chikyukido.lovelystats.handler.RecordedPlayerHandler;
import io.github.chikyukido.lovelystats.pages.UpdateHandler;
import io.github.chikyukido.lovelystats.pages.table.*;
import io.github.chikyukido.lovelystats.types.PlayerStats;
import io.github.chikyukido.lovelystats.types.PlaytimeStats;

import java.util.List;
import java.util.UUID;

public class LeaderboardPlayerTabPage extends TablePage {


    public LeaderboardPlayerTabPage(UpdateHandler parent, PlayerRef playerRef) {
        super(parent, playerRef, new TablePageConfig("LeaderboardPlayerTab",10,false));
        config.getRows().add(new TablePageRow("Name",140, TablePageRowType.STRING, TablePageRowVisualizeType.STRING));
        config.getRows().add(new TablePageRow("Playtime",130, TablePageRowType.LONG,TablePageRowVisualizeType.TIME));
        config.getRows().add(new TablePageRow("Active",130, TablePageRowType.LONG,TablePageRowVisualizeType.TIME));
        config.getRows().add(new TablePageRow("Idle",130, TablePageRowType.LONG,TablePageRowVisualizeType.TIME));
        config.getRows().add(new TablePageRow("Chat Msg",120, TablePageRowType.LONG,TablePageRowVisualizeType.STRING));
        config.getRows().add(new TablePageRow("Deaths",120, TablePageRowType.LONG,TablePageRowVisualizeType.STRING));
        List<UUID> players = RecordedPlayerHandler.get().getPlayersList();
        Object[][] values = new Object[players.size()][config.getRows().size()];
        for (int i = 0; i < players.size(); i++) {
            values[i] = aggregate(players.get(i));
        }
        config.setValues(values);
    }
    private Object[] aggregate(UUID playerUuid) {
        PlaytimeStats playtimeStats = PlaytimeStatsHandler.get().getPlaytimeForPlayer(playerUuid);
        PlayerStats playerStats = PlayerStatsHandler.get().getPlayerStats(playerUuid);
        Object[] p = new Object[config.getRows().size()];
        p[0] = RecordedPlayerHandler.get().getUsername(playerUuid);
        p[1] = playtimeStats.getTotalPlaytime();
        p[2] = playtimeStats.getTotalActivePlaytime();
        p[3] = playtimeStats.getTotalIdlePlaytime();
        p[4] = playerStats.getChatMessages();
        p[5] = playerStats.getDeaths();
        return p;
    }
}
