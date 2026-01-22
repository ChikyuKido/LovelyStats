package io.github.chikyukido.lovelystats.pages;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.chikyukido.lovelystats.handler.ItemStatsHandler;
import io.github.chikyukido.lovelystats.types.ItemStats;
import io.github.chikyukido.lovelystats.util.IdHashMap;

import javax.annotation.Nonnull;
import java.util.*;

public class ItemTabPage extends TabPage{
    private List<ItemTabPage.ItemStatsData> statsList = new ArrayList<>();
    private String currentSort = "name";
    private boolean ascending = true;

    public ItemTabPage(StatsPage parent, UUID playerUUID) {
        super(parent,playerUUID);
    }


    @Override
    public void build(UICommandBuilder cb, UIEventBuilder event) {
        cb.append("#TabPages","items/items_page.ui");
        event.addEventBinding(CustomUIEventBindingType.Activating,"#Name", EventData.of("Button","name"),false);
        event.addEventBinding(CustomUIEventBindingType.Activating,"#Placed", EventData.of("Button","placed"),false);
        event.addEventBinding(CustomUIEventBindingType.Activating,"#Destroyed", EventData.of("Button","destroyed"),false);
        event.addEventBinding(CustomUIEventBindingType.Activating,"#Collected", EventData.of("Button","collected"),false);
        event.addEventBinding(CustomUIEventBindingType.Activating,"#Dropped", EventData.of("Button","dropped"),false);
        event.addEventBinding(CustomUIEventBindingType.Activating,"#Crafted", EventData.of("Button","crafted"),false);


        statsList = aggregate(playerUUID);

        for (int row = 0; row < statsList.size(); row++) {
            ItemTabPage.ItemStatsData stats = statsList.get(row);
            cb.append("#BlockStatsGrid", "items/items_page_entry.ui");
            String base = "#BlockStatsGrid[" + row + "]";
            cb.set(base + " #StatName.Text", IdHashMap.realName(stats.blockId()));
            cb.set(base + " #Placed.Text", "" + stats.placed);
            cb.set(base + " #Destroyed.Text", "" + stats.broken);
            cb.set(base + " #Collected.Text", "" + stats.collected);
            cb.set(base + " #Dropped.Text", "" + stats.dropped);
            cb.set(base + " #Crafted.Text", "" + stats.crafted);
            cb.set(base + " #Image.AssetPath", IdHashMap.realIcon(stats.blockId()));
        }
    }

    @Override
    public void handleEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull StatsPage.Data data) {
        if(data.value != null) {
            sortAndRefreshGrid(data.value,new UICommandBuilder());
        }
    }
    private void sortAndRefreshGrid(String sortBy,UICommandBuilder cb) {
        switch (sortBy) {
            case "placed" -> statsList.sort(Comparator.comparingLong(s -> s.placed));
            case "destroyed" -> statsList.sort(Comparator.comparingLong(s -> s.broken));
            case "collected" -> statsList.sort(Comparator.comparingLong(s -> s.collected));
            case "dropped" -> statsList.sort(Comparator.comparingLong(s -> s.dropped));
            case "crafted" -> statsList.sort(Comparator.comparingLong(s -> s.crafted));
            case "name" -> statsList.sort(Comparator.comparing(s -> IdHashMap.realName(s.blockId())));
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
            ItemTabPage.ItemStatsData stats = statsList.get(row);
            cb.append("#BlockStatsGrid", "items/items_page_entry.ui");

            String base = "#BlockStatsGrid[" + row + "]";
            cb.set(base + " #StatName.Text", IdHashMap.realName(stats.blockId()));
            cb.set(base + " #Placed.Text", "" + stats.placed);
            cb.set(base + " #Destroyed.Text", "" + stats.broken);
            cb.set(base + " #Collected.Text", "" + stats.collected);
            cb.set(base + " #Dropped.Text", "" + stats.dropped);
            cb.set(base + " #Crafted.Text", "" + stats.crafted);
            cb.set(base + " #Image.AssetPath", IdHashMap.realIcon(stats.blockId()));
        }
        parent.sendUpdate(cb);
    }


    public static List<ItemTabPage.ItemStatsData> aggregate(UUID playerUuid) {
        ItemStats itemPlayer = ItemStatsHandler.get().getBlockPlayer(playerUuid);

        Set<Long> allBlockIds = new HashSet<>();


        allBlockIds.addAll(itemPlayer.getBlocksBroken().keySet());
        allBlockIds.addAll(itemPlayer.getBlocksPlaced().keySet());
        allBlockIds.addAll(itemPlayer.getCrafted().keySet());
        allBlockIds.addAll(itemPlayer.getDropped().keySet());
        allBlockIds.addAll(itemPlayer.getCollected().keySet());

        List<ItemTabPage.ItemStatsData> result = new ArrayList<>();
        for (long blockId : allBlockIds) {
            long placed = itemPlayer.getBlocksPlaced().getOrDefault(blockId, 0L);
            long broken = itemPlayer.getBlocksBroken().getOrDefault(blockId, 0L);
            long collected = itemPlayer.getCollected().getOrDefault(blockId, 0L);
            long dropped = itemPlayer.getDropped().getOrDefault(blockId, 0L);
            long crafted = itemPlayer.getCrafted().getOrDefault(blockId, 0L);

            result.add(new ItemTabPage.ItemStatsData(blockId, placed, broken, collected, dropped,crafted));
        }

        return result;
    }


    private record ItemStatsData(long blockId, long placed, long broken, long collected, long dropped, long crafted) {
    }
}
