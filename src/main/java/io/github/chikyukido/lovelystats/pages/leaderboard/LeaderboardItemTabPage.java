package io.github.chikyukido.lovelystats.pages.leaderboard;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.chikyukido.lovelystats.handler.ItemStatsHandler;
import io.github.chikyukido.lovelystats.handler.RecordedPlayerHandler;
import io.github.chikyukido.lovelystats.pages.TabPage;
import io.github.chikyukido.lovelystats.types.ItemStats;

import javax.annotation.Nonnull;
import java.util.*;

public class LeaderboardItemTabPage extends TabPage {
    private List<ItemStatsData> statsList = new ArrayList<>();
    private String currentSort = "name";
    private boolean ascending = true;

    public LeaderboardItemTabPage(LeaderboardPage parent, UUID playerUUID) {
        super(parent,playerUUID);
    }

    @Override
    public void build(UICommandBuilder cb, UIEventBuilder event) {
        cb.append("#TabPages","leaderboard/items/items_page.ui");
        event.addEventBinding(CustomUIEventBindingType.Activating,"#Name", EventData.of("Button","name"),false);
        event.addEventBinding(CustomUIEventBindingType.Activating,"#Placed", EventData.of("Button","placed"),false);
        event.addEventBinding(CustomUIEventBindingType.Activating,"#Destroyed", EventData.of("Button","destroyed"),false);
        event.addEventBinding(CustomUIEventBindingType.Activating,"#Collected", EventData.of("Button","collected"),false);
        event.addEventBinding(CustomUIEventBindingType.Activating,"#Dropped", EventData.of("Button","dropped"),false);
        event.addEventBinding(CustomUIEventBindingType.Activating,"#Crafted", EventData.of("Button","crafted"),false);


        for (UUID player : RecordedPlayerHandler.get().getPlayers()) {
            statsList.add(aggregate(player));
        }

        for (int row = 0; row < statsList.size(); row++) {
            ItemStatsData stats = statsList.get(row);
            cb.append("#BlockStatsGrid", "leaderboard/items/items_page_entry.ui");
            String base = "#BlockStatsGrid[" + row + "]";
            cb.set(base + " #Name.Text", stats.username);
            cb.set(base + " #Placed.Text", "" + stats.placed);
            cb.set(base + " #Destroyed.Text", "" + stats.broken);
            cb.set(base + " #Collected.Text", "" + stats.collected);
            cb.set(base + " #Dropped.Text", "" + stats.dropped);
            cb.set(base + " #Crafted.Text", "" + stats.crafted);
        }
    }

    @Override
    public void handleEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull String data) {
        sortAndRefreshGrid(data,new UICommandBuilder());
    }
    private void sortAndRefreshGrid(String sortBy,UICommandBuilder cb) {
        switch (sortBy) {
            case "placed" -> statsList.sort(Comparator.comparingLong(s -> s.placed));
            case "destroyed" -> statsList.sort(Comparator.comparingLong(s -> s.broken));
            case "collected" -> statsList.sort(Comparator.comparingLong(s -> s.collected));
            case "dropped" -> statsList.sort(Comparator.comparingLong(s -> s.dropped));
            case "crafted" -> statsList.sort(Comparator.comparingLong(s -> s.crafted));
            case "name" -> statsList.sort(Comparator.comparing(s -> s.username));
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
            ItemStatsData stats = statsList.get(row);
            cb.append("#BlockStatsGrid", "leaderboard/items/items_page_entry.ui");

            String base = "#BlockStatsGrid[" + row + "]";
            cb.set(base + " #Name.Text", stats.username);
            cb.set(base + " #Placed.Text", "" + stats.placed);
            cb.set(base + " #Destroyed.Text", "" + stats.broken);
            cb.set(base + " #Collected.Text", "" + stats.collected);
            cb.set(base + " #Dropped.Text", "" + stats.dropped);
            cb.set(base + " #Crafted.Text", "" + stats.crafted);
        }
        parent.sendUpdate(cb);
    }


    private ItemStatsData aggregate(UUID playerUuid) {
        ItemStats itemPlayer = ItemStatsHandler.get().getBlockPlayer(playerUuid);
        return new ItemStatsData(RecordedPlayerHandler.get().getUsername(playerUuid),
                itemPlayer.getTotalBlocksPlaced(),
                itemPlayer.getTotalBlocksBroken(),
                itemPlayer.getTotalCollected(),
                itemPlayer.getTotalDropped(),
                itemPlayer.getTotalItemsCrafted());
    }


    private record ItemStatsData(String username, long placed, long broken, long collected, long dropped, long crafted) {
    }
}
