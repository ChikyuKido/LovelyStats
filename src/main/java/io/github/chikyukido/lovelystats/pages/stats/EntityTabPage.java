package io.github.chikyukido.lovelystats.pages.stats;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.chikyukido.lovelystats.handler.EntityStatsHandler;
import io.github.chikyukido.lovelystats.pages.TabPage;
import io.github.chikyukido.lovelystats.types.EntityStats;
import io.github.chikyukido.lovelystats.util.IdHashMap;

import javax.annotation.Nonnull;
import java.util.*;

public class EntityTabPage extends TabPage {
    private List<EntityStats.SingleEntityStats> statsList = new ArrayList<>();
    private String currentSort = "name";
    private boolean ascending = true;

    public EntityTabPage(StatsPage parent, UUID playerUUID) {
        super(parent,playerUUID);
    }

    @Override
    public void build(UICommandBuilder cb, UIEventBuilder event) {
        cb.append("#TabPages","stats/entity/entity_page.ui");
        event.addEventBinding(CustomUIEventBindingType.Activating,"#Name", EventData.of("Button","name"),false);
        event.addEventBinding(CustomUIEventBindingType.Activating,"#Killed", EventData.of("Button","killed"),false);
        event.addEventBinding(CustomUIEventBindingType.Activating,"#KilledBy", EventData.of("Button","killedBy"),false);
        event.addEventBinding(CustomUIEventBindingType.Activating,"#DamageDealt", EventData.of("Button","damageDealt"),false);
        event.addEventBinding(CustomUIEventBindingType.Activating,"#DamageReceived", EventData.of("Button","damageReceived"),false);


        EntityStats statsMap = EntityStatsHandler.get().getEntityStatsFor(playerUUID);
        statsMap.getEntities().forEach((_, singleEntityStats) -> statsList.add(singleEntityStats));

        for (int row = 0; row < statsList.size(); row++) {
            EntityStats.SingleEntityStats stats = statsList.get(row);
            cb.append("#BlockStatsGrid", "stats/entity/entity_page_entry.ui");
            String base = "#BlockStatsGrid[" + row + "]";
            cb.set(base + " #Name.Text", IdHashMap.realNameEntity(stats.getEntityID()));
            cb.set(base + " #Killed.Text", "" + stats.getKilled());
            cb.set(base + " #KilledBy.Text", "" + stats.getKilledBy());
            cb.set(base + " #DamageDealt.Text", "" + stats.getDamageDealt());
            cb.set(base + " #DamageReceived.Text", "" + stats.getDamageReceived());
            cb.set(base + " #Image.AssetPath", IdHashMap.realNameEntityIcon(stats.getEntityID()));
        }
    }

    @Override
    public void handleEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull String data) {

            sortAndRefreshGrid(data,new UICommandBuilder());

    }
    private void sortAndRefreshGrid(String sortBy,UICommandBuilder cb) {
        switch (sortBy) {
            case "killed" -> statsList.sort(Comparator.comparingLong(EntityStats.SingleEntityStats::getKilled));
            case "killedBy" -> statsList.sort(Comparator.comparingLong(EntityStats.SingleEntityStats::getKilledBy));
            case "damageDealt" -> statsList.sort(Comparator.comparingDouble(EntityStats.SingleEntityStats::getDamageDealt));
            case "damageReceived" -> statsList.sort(Comparator.comparingDouble(EntityStats.SingleEntityStats::getDamageReceived));
            case "name" -> statsList.sort(Comparator.comparing(s -> IdHashMap.realNameEntity(s.getEntityID())));
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
            EntityStats.SingleEntityStats stats = statsList.get(row);
            cb.append("#BlockStatsGrid", "stats/entity/entity_page_entry.ui");
            String base = "#BlockStatsGrid[" + row + "]";
            cb.set(base + " #Name.Text", IdHashMap.realNameEntity(stats.getEntityID()));
            cb.set(base + " #Killed.Text", "" + stats.getKilled());
            cb.set(base + " #KilledBy.Text", "" + stats.getKilledBy());
            cb.set(base + " #DamageDealt.Text", "" + stats.getDamageDealt());
            cb.set(base + " #DamageReceived.Text", "" + stats.getDamageReceived());
            cb.set(base + " #Image.AssetPath", IdHashMap.realNameEntityIcon(stats.getEntityID()));
        }
        parent.sendUpdate(cb);
    }



}
