package io.github.chikyukido.lovelystats.pages;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import java.util.UUID;

public abstract class TabPage {
    protected StatsPage parent;
    protected final UUID playerUUID;
    public TabPage(StatsPage parent, UUID playerUUID) {
        this.parent = parent;
        this.playerUUID = playerUUID;
    }


    public abstract void build(UICommandBuilder cb, UIEventBuilder event);
    public abstract void handleEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull StatsPage.Data data);
}
