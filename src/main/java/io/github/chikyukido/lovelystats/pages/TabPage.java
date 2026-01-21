package io.github.chikyukido.lovelystats.pages;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

public abstract class TabPage {
    protected PlayerRef playerRef;
    protected StatsPage parent;
    public TabPage(StatsPage parent, PlayerRef playerRef) {
        this.playerRef = playerRef;
        this.parent = parent;
    }
    public abstract void build(UICommandBuilder cb, UIEventBuilder event);
    public abstract void handleEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull StatsPage.Data data);
}
