package io.github.chikyukido.lovelystats.pages.leaderboard;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.chikyukido.lovelystats.handler.EntityStatsHandler;
import io.github.chikyukido.lovelystats.handler.RecordedPlayerHandler;
import io.github.chikyukido.lovelystats.pages.TabPage;
import io.github.chikyukido.lovelystats.types.EntityStats;

import javax.annotation.Nonnull;
import java.util.*;

public class LeaderboardEntityTabPage extends TabPage {
    private List<EntityStatsData> statsList = new ArrayList<>();
    private String currentSort = "name";
    private boolean ascending = true;

    public LeaderboardEntityTabPage(LeaderboardPage parent, UUID playerUUID) {
        super(parent,playerUUID);
    }

    @Override
    public void build(UICommandBuilder cb, UIEventBuilder event) {
        cb.append("#TabPages","leaderboard/entity/entity_page.ui");
        event.addEventBinding(CustomUIEventBindingType.Activating,"#Name", EventData.of("Button","name"),false);
        event.addEventBinding(CustomUIEventBindingType.Activating,"#Killed", EventData.of("Button","killed"),false);
        event.addEventBinding(CustomUIEventBindingType.Activating,"#KilledBy", EventData.of("Button","killedBy"),false);
        event.addEventBinding(CustomUIEventBindingType.Activating,"#DamageDealt", EventData.of("Button","damageDealt"),false);
        event.addEventBinding(CustomUIEventBindingType.Activating,"#DamageReceived", EventData.of("Button","damageReceived"),false);


        for (UUID player : RecordedPlayerHandler.get().getPlayers()) {
            statsList.add(aggregate(player));
        }
        for (int row = 0; row < statsList.size(); row++) {
            EntityStatsData stats = statsList.get(row);
            cb.append("#BlockStatsGrid", "leaderboard/entity/entity_page_entry.ui");
            String base = "#BlockStatsGrid[" + row + "]";
            cb.set(base + " #Name.Text", stats.username);
            cb.set(base + " #Killed.Text", "" + stats.killed);
            cb.set(base + " #KilledBy.Text", "" + stats.killedBy);
            cb.set(base + " #DamageDealt.Text", "" + stats.dealt);
            cb.set(base + " #DamageReceived.Text", "" + stats.received);
        }
    }

    @Override
    public void handleEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull String data) {

            sortAndRefreshGrid(data,new UICommandBuilder());

    }
    private void sortAndRefreshGrid(String sortBy,UICommandBuilder cb) {
        switch (sortBy) {
            case "killed" -> statsList.sort(Comparator.comparingLong(EntityStatsData::killed));
            case "killedBy" -> statsList.sort(Comparator.comparingLong(EntityStatsData::killedBy));
            case "damageDealt" -> statsList.sort(Comparator.comparingDouble(EntityStatsData::dealt));
            case "damageReceived" -> statsList.sort(Comparator.comparingDouble(EntityStatsData::received));
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
            EntityStatsData stats = statsList.get(row);
            cb.append("#BlockStatsGrid", "leaderboard/entity/entity_page_entry.ui");
            String base = "#BlockStatsGrid[" + row + "]";
            cb.set(base + " #Name.Text", stats.username);
            cb.set(base + " #Killed.Text", "" + stats.killed);
            cb.set(base + " #KilledBy.Text", "" + stats.killedBy);
            cb.set(base + " #DamageDealt.Text", "" + stats.dealt);
            cb.set(base + " #DamageReceived.Text", "" + stats.received);
        }
        parent.sendUpdate(cb);
    }
    private EntityStatsData aggregate(UUID playerUuid) {
        EntityStats entity = EntityStatsHandler.get().getEntityStatsFor(playerUuid);
        return new EntityStatsData(RecordedPlayerHandler.get().getUsername(playerUuid),
                entity.getTotalKilled(),
                entity.getTotalKilledBy(),
                entity.getTotalDamageDealt(),
                entity.getTotalDamageReceived());
    }


    private record EntityStatsData(String username, long killed, long killedBy, double dealt, double received) {
    }


}
