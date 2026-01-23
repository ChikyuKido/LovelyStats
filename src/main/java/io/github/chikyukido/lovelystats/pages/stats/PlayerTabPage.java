package io.github.chikyukido.lovelystats.pages.stats;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.chikyukido.lovelystats.handler.EntityStatsHandler;
import io.github.chikyukido.lovelystats.handler.ItemStatsHandler;
import io.github.chikyukido.lovelystats.handler.PlayerStatsHandler;
import io.github.chikyukido.lovelystats.handler.PlaytimeStatsHandler;
import io.github.chikyukido.lovelystats.pages.TabPage;
import io.github.chikyukido.lovelystats.types.PlayerStats;
import io.github.chikyukido.lovelystats.types.PlaytimeStats;
import io.github.chikyukido.lovelystats.util.OwnFormat;

import javax.annotation.Nonnull;
import java.util.UUID;

public class PlayerTabPage extends TabPage {
    public PlayerTabPage(StatsPage parent,  UUID playerUUID) {
        super(parent,playerUUID);
    }


    @Override
    public void build(UICommandBuilder cb, UIEventBuilder event) {
        cb.append("#TabPages", "stats/player/player_page.ui");

        PlayerStats ps = PlayerStatsHandler.get().getPlayerStats(playerUUID);
        PlaytimeStats pts = PlaytimeStatsHandler.get().getPlaytimeForPlayer(playerUUID);

        var entityStats = EntityStatsHandler.get().getEntityStatsFor(playerUUID);

        addSection(cb, "Entities", true);
        addStat(cb, "Kills:", String.valueOf(entityStats.getTotalKilled()));
        addStat(cb, "Deaths (Entities):", String.valueOf(entityStats.getTotalKilledBy()));
        addStat(cb, "Damage Dealt:", String.format("%.1f", entityStats.getTotalDamageDealt()));
        addStat(cb, "Damage Taken:", String.format("%.1f", entityStats.getTotalDamageReceived()));


        var itemStats = ItemStatsHandler.get().getBlockPlayer(playerUUID);

        addSection(cb, "Items",false);
        addStat(cb, "Blocks Placed:", String.valueOf(itemStats.getTotalBlocksPlaced()));
        addStat(cb, "Items Collected:", String.valueOf(itemStats.getTotalCollected()));
        addStat(cb, "Items Dropped:", String.valueOf(itemStats.getTotalDropped()));
        addStat(cb, "Items Crafted:", String.valueOf(itemStats.getTotalItemsCrafted()));

        addSection(cb, "Player", false);
        addStat(cb, "Deaths:", String.valueOf(ps.getDeaths()));
        addStat(cb, "Jumps:", String.valueOf(ps.getJumps()));
        addStat(cb, "Chat Messages:", String.valueOf(ps.getChatMessages()));


        addSection(cb, "Playtime", false);
        addStat(cb, "Total Playtime:", OwnFormat.formatTime(pts.getTotalPlaytime()));
        addStat(cb, "Active Time:", OwnFormat.formatTime(pts.getTotalActivePlaytime()));
        addStat(cb, "Idle Time:", OwnFormat.formatTime(pts.getTotalIdlePlaytime()));


        addSection(cb, "Activity", false);
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

    private void addSection(UICommandBuilder cb, String title, boolean first) {
        cb.appendInline("#BodyRow",
                "Label { " +
                        "Text: \"" + title + "\"; " +
                        "Style: (FontSize: 22, TextColor: #7fb2ff); " +
                        "Padding: (Top: " + (first ? 6 : 18) + ", Bottom: 8, Left: 6); " +
                        "}"
        );
    }



    @Override
    public void handleEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull String data) {}

}
