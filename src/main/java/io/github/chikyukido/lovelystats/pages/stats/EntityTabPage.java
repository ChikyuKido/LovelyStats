package io.github.chikyukido.lovelystats.pages.stats;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.chikyukido.lovelystats.handler.EntityStatsHandler;
import io.github.chikyukido.lovelystats.pages.UpdateHandler;
import io.github.chikyukido.lovelystats.pages.table.*;
import io.github.chikyukido.lovelystats.types.EntityStats;
import io.github.chikyukido.lovelystats.util.IdHashMap;

import java.util.List;

public class EntityTabPage extends TablePage {

    public EntityTabPage(UpdateHandler parent, PlayerRef playerRef) {
        super(parent, playerRef, new TablePageConfig("EntityTab", 0, true));
        config.setIconSize(30);
        config.getRows().add(new TablePageRow("Name", 160, TablePageRowType.STRING, TablePageRowVisualizeType.STRING));
        config.getRows().add(new TablePageRow("Killed", 120, TablePageRowType.LONG, TablePageRowVisualizeType.STRING));
        config.getRows().add(new TablePageRow("KilledBy", 120, TablePageRowType.LONG, TablePageRowVisualizeType.STRING));
        config.getRows().add(new TablePageRow("DamageDealt", 140, TablePageRowType.DOUBLE, TablePageRowVisualizeType.STRING));
        config.getRows().add(new TablePageRow("DamageReceived", 140, TablePageRowType.DOUBLE, TablePageRowVisualizeType.STRING));

        EntityStats stats = EntityStatsHandler.get().getEntityStatsFor(playerRef.getUuid());
        List<EntityStats.SingleEntityStats> entities = stats.getEntities().values().stream().toList();
        Object[][] values = new Object[entities.size()][];

        for (int i = 0; i < entities.size(); i++) {
            values[i] = aggregate(entities.get(i));
        }

        config.setValues(values);
    }

    private Object[] aggregate(EntityStats.SingleEntityStats stats) {
        Object[] data = new Object[config.getRows().size()+1];//+1 because icon
        data[0] = IdHashMap.realNameEntityIcon(stats.getEntityID());
        data[1] = IdHashMap.realNameEntity(stats.getEntityID());
        data[2] = stats.getKilled();
        data[3] = stats.getKilledBy();
        data[4] = stats.getDamageDealt();
        data[5] = stats.getDamageReceived();
        return data;
    }
}
