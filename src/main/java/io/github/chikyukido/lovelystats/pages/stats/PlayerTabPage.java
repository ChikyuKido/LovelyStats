package io.github.chikyukido.lovelystats.pages.stats;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.chikyukido.lovelystats.Main;
import io.github.chikyukido.lovelystats.config.PlayerConfig;
import io.github.chikyukido.lovelystats.handler.EntityStatsHandler;
import io.github.chikyukido.lovelystats.handler.ItemStatsHandler;
import io.github.chikyukido.lovelystats.handler.PlayerStatsHandler;
import io.github.chikyukido.lovelystats.handler.PlaytimeStatsHandler;
import io.github.chikyukido.lovelystats.pages.TabPage;
import io.github.chikyukido.lovelystats.types.PlayerStats;
import io.github.chikyukido.lovelystats.types.PlaytimeStats;
import io.github.chikyukido.lovelystats.util.OwnFormat;

import javax.annotation.Nonnull;

public class PlayerTabPage extends TabPage {
    public PlayerTabPage(StatsPage parent,  PlayerRef playerRef) {
        super(parent,playerRef);
    }


    @Override
    public void build(UICommandBuilder cb, UIEventBuilder event) {
        cb.append("#TabPages", "stats/player/player_page.ui");

        var overview = Main.PLAYER_CONFIG.get().getPlayerConfig(playerRef.getUuid()).getOverview();
        for (PlayerConfig.PlayerConfigData.PlayerConfigDataOverviewEntry entry : overview) {
            if(!entry.isActive()) continue;
            switch (entry.getName()) {
                case "Show Entity" -> buildEntityStats(cb);
                case "Show Player" -> buildPlayerStats(cb);
                case "Show Playtime" -> buildPlaytimeStats(cb);
                case "Show Activity" -> buildActivityStats(cb);
                case "Show Items" -> buildItemStats(cb);
            }
        }
    }

    private void buildEntityStats(UICommandBuilder cb) {
        var entityStats = EntityStatsHandler.get().getEntityStatsFor(playerRef.getUuid());

        addSection(cb, "Entities");
        addStat(cb, "Kills:", String.valueOf(entityStats.getTotalKilled()));
        addStat(cb, "Deaths (Entities):", String.valueOf(entityStats.getTotalKilledBy()));
        addStat(cb, "Damage Dealt:", String.format("%.1f", entityStats.getTotalDamageDealt()));
        addStat(cb, "Damage Taken:", String.format("%.1f", entityStats.getTotalDamageReceived()));
    }

    private void buildItemStats(UICommandBuilder cb) {
        var itemStats = ItemStatsHandler.get().getBlockPlayer(playerRef.getUuid());

        addSection(cb, "Items");
        addStat(cb, "Blocks Placed:", String.valueOf(itemStats.getTotalBlocksPlaced()));
        addStat(cb, "Items Collected:", String.valueOf(itemStats.getTotalCollected()));
        addStat(cb, "Items Dropped:", String.valueOf(itemStats.getTotalDropped()));
        addStat(cb, "Items Crafted:", String.valueOf(itemStats.getTotalItemsCrafted()));
    }

    private void buildPlayerStats(UICommandBuilder cb) {
        PlayerStats ps = PlayerStatsHandler.get().getPlayerStats(playerRef.getUuid());

        addSection(cb, "Player");
        addStat(cb, "Deaths:", String.valueOf(ps.getDeaths()));
        addStat(cb, "Jumps:", String.valueOf(ps.getJumps()));
        addStat(cb, "Chat Messages:", String.valueOf(ps.getChatMessages()));
    }

    private void buildPlaytimeStats(UICommandBuilder cb) {
        PlaytimeStats pts = PlaytimeStatsHandler.get().getPlaytimeForPlayer(playerRef.getUuid());

        addSection(cb, "Playtime");
        addStat(cb, "Total Playtime:", OwnFormat.formatTime(pts.getTotalPlaytime()));
        addStat(cb, "Active Time:", OwnFormat.formatTime(pts.getTotalActivePlaytime()));
        addStat(cb, "Idle Time:", OwnFormat.formatTime(pts.getTotalIdlePlaytime()));
    }

    private void buildActivityStats(UICommandBuilder cb) {
        PlayerStats ps = PlayerStatsHandler.get().getPlayerStats(playerRef.getUuid());

        addSection(cb, "Activity");
        addStat(cb, "Walked:", OwnFormat.formatDistance(ps.getDistanceWalked()));
        addStat(cb, "Ran:", OwnFormat.formatDistance(ps.getDistanceRun()));
        addStat(cb, "Swum:", OwnFormat.formatDistance(ps.getDistanceSwam()));
        addStat(cb, "Fallen:", OwnFormat.formatDistance(ps.getDistanceFallen()));
        addStat(cb, "Climbed:", OwnFormat.formatDistance(ps.getDistanceClimbed()));
        addStat(cb, "Sneaked:", OwnFormat.formatDistance(ps.getDistanceSneaked()));
    }

    private void addStat(UICommandBuilder cb, String name, String value) {
        StringBuilder sb = new StringBuilder();

        sb.append("Group { ")
                .append("LayoutMode: Left; ")
                .append("Anchor: (Height: 32); ")
                .append("Padding: (Left: 12, Right: 12); ")

                .append("Label { ")
                .append("Text: \"").append(name).append("\"; ")
                .append("Style: (FontSize: 16, TextColor: #9aa7b4); ")
                .append("Anchor: (Width: 500, Right: 10); ")
                .append("} ")

                .append("Label { ")
                .append("Text: \"").append(value).append("\"; ")
                .append("Style: (FontSize: 16, TextColor: #e6edf5, HorizontalAlignment: End); ")
                .append("Anchor: (Width: 200); ")
                .append("} ")

                .append("}");

        cb.appendInline("#BodyRow", sb.toString());
    }

    private void addSection(UICommandBuilder cb, String title) {
        cb.appendInline("#BodyRow",
                "Label { " +
                        "Text: \"" + title + "\"; " +
                        "Style: (FontSize: 22, TextColor: #7fb2ff); " +
                        "Padding: (Bottom: 8, Left: 6); " +
                        "}"
        );
    }



    @Override
    public void handleEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull String data) {}

}
