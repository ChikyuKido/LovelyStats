package io.github.chikyukido.lovelystats.pages;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

public abstract class TabPage {
    protected UpdateHandler parent;
    protected final PlayerRef playerRef;
    public TabPage(UpdateHandler parent, PlayerRef playerRef) {
        this.parent = parent;
        this.playerRef = playerRef;
    }


    public abstract void build(UICommandBuilder cb, UIEventBuilder event);
    public abstract void handleEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull String data);
}
