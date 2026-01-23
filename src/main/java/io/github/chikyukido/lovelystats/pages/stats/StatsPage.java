package io.github.chikyukido.lovelystats.pages.stats;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.chikyukido.lovelystats.pages.TabPage;
import io.github.chikyukido.lovelystats.pages.UpdateHandler;

import javax.annotation.Nonnull;
import java.util.UUID;

public class StatsPage extends InteractiveCustomUIPage<StatsPage.Data> implements UpdateHandler {
    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private TabPage currentPage;
    private String currentPageName = "player";
    private final UUID playerUUID;

    public StatsPage(@Nonnull PlayerRef playerRef, UUID playerUUID) {
        super(playerRef, CustomPageLifetime.CanDismiss,Data.CODEX);
        this.playerUUID = playerUUID;
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder cb, @Nonnull UIEventBuilder event, @Nonnull Store<EntityStore> store) {
        cb.append("stats/base_page.ui");
        event.addEventBinding(CustomUIEventBindingType.Activating,"#PlayersTab", EventData.of("Button","player"),false);
        event.addEventBinding(CustomUIEventBindingType.Activating,"#PlaytimeTab", EventData.of("Button","playtime"),false);
        event.addEventBinding(CustomUIEventBindingType.Activating,"#BlocksTab", EventData.of("Button","block"),false);
        event.addEventBinding(CustomUIEventBindingType.Activating,"#EntityTab", EventData.of("Button","entity"),false);
        currentPage = new PlayerTabPage(this,playerUUID);
        currentPage.build(cb,event);
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull Data data) {
        super.handleDataEvent(ref, store, data);

        if(data.value.equals("player") || data.value.equals("block") || data.value.equals("entity") || data.value.equals("playtime")) {
            rebuild(data.value);
            return;
        }

        if(currentPage != null) currentPage.handleEvent(ref,store,data.value);
    }

    private void rebuild(String page) {
        long start = System.currentTimeMillis();
        if(currentPageName.equals(page)) return;
        currentPageName = page;
        switch (page) {
            case "player" -> currentPage = new PlayerTabPage(this,playerUUID);
            case "block" -> currentPage = new ItemTabPage(this,playerUUID);
            case "entity" -> currentPage = new EntityTabPage(this,playerUUID);
            case "playtime" -> currentPage = new PlaytimeTabPage(this,playerUUID);
        }
        UICommandBuilder cb = new UICommandBuilder();
        cb.set("#EntityTabImage.Background","entity.png");
        cb.set("#BlocksTabImage.Background","blocks.png");
        cb.set("#PlayersTabImage.Background","player.png");
        cb.set("#PlaytimeTabImage.Background","time.png");
        switch (page) {
            case "player" -> cb.set("#PlayersTabImage.Background","player_selected.png");
            case "block" -> cb.set("#BlocksTabImage.Background","blocks_selected.png");
            case "entity" -> cb.set("#EntityTabImage.Background","entity_selected.png");
            case "playtime" -> cb.set("#PlaytimeTabImage.Background","time_selected.png");
        }
        UIEventBuilder event = new UIEventBuilder();
        cb.clear("#TabPages");

        currentPage.build(cb,event);
        super.sendUpdate(cb,event,false);
        long end = System.currentTimeMillis();
        LOGGER.atInfo().log("Rebuild Stats page in %dms", end-start);
    }
    @Override
    public void sendUpdate(UICommandBuilder cb) {
        super.sendUpdate(cb);
    }

    public static class Data {
        public static final BuilderCodec<StatsPage.Data> CODEX = BuilderCodec.builder(StatsPage.Data.class, StatsPage.Data::new)
                .append(new KeyedCodec<>("Button", Codec.STRING),(data, s) -> data.value = s, data -> data.value).add()
                .build();
        public String value;
    }
}
