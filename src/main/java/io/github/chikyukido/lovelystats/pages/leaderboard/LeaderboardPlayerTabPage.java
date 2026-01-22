package io.github.chikyukido.lovelystats.pages.leaderboard;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.chikyukido.lovelystats.handler.PlayerStatsHandler;
import io.github.chikyukido.lovelystats.handler.PlaytimeStatsHandler;
import io.github.chikyukido.lovelystats.handler.RecordedPlayerHandler;
import io.github.chikyukido.lovelystats.pages.TabPage;
import io.github.chikyukido.lovelystats.types.PlayerStats;
import io.github.chikyukido.lovelystats.types.PlaytimeStats;
import io.github.chikyukido.lovelystats.util.Format;

import javax.annotation.Nonnull;
import java.util.*;

public class LeaderboardPlayerTabPage extends TabPage {
    private List<LeaderboardPlayerTabPage.PlayerData> statsList = new ArrayList<>();
    private String currentSort = "name";
    private boolean ascending = true;

    public LeaderboardPlayerTabPage(LeaderboardPage parent, UUID playerUUID) {
        super(parent, playerUUID);
    }


    @Override
    public void build(UICommandBuilder cb, UIEventBuilder event) {
        cb.append("#TabPages","leaderboard/player/player_page.ui");
        event.addEventBinding(CustomUIEventBindingType.Activating,"#Name", EventData.of("Button","name"),false);
        event.addEventBinding(CustomUIEventBindingType.Activating,"#Playtime", EventData.of("Button","playtime"),false);
        event.addEventBinding(CustomUIEventBindingType.Activating,"#Active", EventData.of("Button","active"),false);
        event.addEventBinding(CustomUIEventBindingType.Activating,"#Idle", EventData.of("Button","idle"),false);
        event.addEventBinding(CustomUIEventBindingType.Activating,"#Chat", EventData.of("Button","chat"),false);
        event.addEventBinding(CustomUIEventBindingType.Activating,"#Deaths", EventData.of("Button","deaths"),false);

        for (UUID player : RecordedPlayerHandler.get().getPlayers()) {
            statsList.add(aggregate(player));
        }

        for (int row = 0; row < statsList.size(); row++) {
            LeaderboardPlayerTabPage.PlayerData stats = statsList.get(row);
            cb.append("#BlockStatsGrid", "leaderboard/player/player_page_entry.ui");
            String base = "#BlockStatsGrid[" + row + "]";
            cb.set(base + " #Name.Text", stats.name);
            cb.set(base + " #Playtime.Text", Format.formatTime(stats.playtime));
            cb.set(base + " #Active.Text", Format.formatTime(stats.active));
            cb.set(base + " #Idle.Text", Format.formatTime(stats.idle));
            cb.set(base + " #Chat.Text", "" + stats.chat);
            cb.set(base + " #Deaths.Text", "" + stats.deaths);
        }
    }

    @Override
    public void handleEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull String data) {
        sortAndRefreshGrid(data,new UICommandBuilder());
    }
    private void sortAndRefreshGrid(String sortBy,UICommandBuilder cb) {
        switch (sortBy) {
            case "playtime" -> statsList.sort(Comparator.comparingLong(s -> s.playtime));
            case "active" -> statsList.sort(Comparator.comparingLong(s -> s.active));
            case "idle" -> statsList.sort(Comparator.comparingLong(s -> s.idle));
            case "chat" -> statsList.sort(Comparator.comparingLong(s -> s.chat));
            case "death" -> statsList.sort(Comparator.comparingLong(s -> s.deaths));
            case "name" -> statsList.sort(Comparator.comparing(s -> s.name));
        }

        if (currentSort.equals(sortBy) && ascending) {
            Collections.reverse(statsList);
            ascending = false;
        } else {
            ascending = true;
        }
        currentSort = sortBy;
        rebuildGrid(cb);
    }

    private void rebuildGrid(UICommandBuilder cb) {
        cb.clear("#BlockStatsGrid");

        for (int row = 0; row < statsList.size(); row++) {
            LeaderboardPlayerTabPage.PlayerData stats = statsList.get(row);
            cb.append("#BlockStatsGrid", "leaderboard/player/player_page_entry.ui");

            String base = "#BlockStatsGrid[" + row + "]";
            cb.set(base + " #Name.Text", stats.name);
            cb.set(base + " #Playtime.Text", Format.formatTime(stats.playtime));
            cb.set(base + " #Active.Text", Format.formatTime(stats.active));
            cb.set(base + " #Idle.Text", Format.formatTime(stats.idle));
            cb.set(base + " #Chat.Text", "" + stats.chat);
            cb.set(base + " #Deaths.Text", "" + stats.deaths);
        }
        parent.sendUpdate(cb);
    }


    private static LeaderboardPlayerTabPage.PlayerData aggregate(UUID playerUuid) {
        PlaytimeStats playtimeStats = PlaytimeStatsHandler.get().getPlaytimeForPlayer(playerUuid);
        PlayerStats playerStats = PlayerStatsHandler.get().getPlayerStats(playerUuid);
        PlayerData p = new PlayerData(RecordedPlayerHandler.get().getUsername(playerUuid),
                playtimeStats.getTotalPlaytime(),
                playtimeStats.getTotalActivePlaytime(),
                playtimeStats.getTotalIdlePlaytime(),
                playerStats.getChatMessages(),
                playerStats.getDeaths()
        );
        return p;
    }


    private record PlayerData(String name, long playtime, long active, long idle, long chat, long deaths) {
    }
}
