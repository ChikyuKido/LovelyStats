package io.github.chikyukido.lovelystats;

import com.hypixel.hytale.assetstore.event.LoadedAssetsEvent;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import io.github.chikyukido.lovelystats.commands.StatsCommand;
import io.github.chikyukido.lovelystats.handler.BlockPlayerHandler;
import io.github.chikyukido.lovelystats.handler.PlaytimePlayerHandler;
import io.github.chikyukido.lovelystats.systems.LastInteractionSystem;
import io.github.chikyukido.lovelystats.systems.PlaytimePlayerSystem;
import io.github.chikyukido.lovelystats.systems.SaveSystem;
import io.github.chikyukido.lovelystats.systems.block.BlockBreakSystem;
import io.github.chikyukido.lovelystats.systems.block.BlockPlacedSystem;
import io.github.chikyukido.lovelystats.util.IdHashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class Main extends JavaPlugin {

    public Main(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        this.getEventRegistry().registerGlobal(PlayerConnectEvent.class, PlaytimePlayerSystem::onPlayerConnect);
        this.getEventRegistry().registerGlobal(PlayerDisconnectEvent.class, PlaytimePlayerSystem::onPlayerDisconnect);

        this.getEventRegistry().registerGlobal(PlayerConnectEvent.class, LastInteractionSystem::onPlayerConnect);

        this.getEntityStoreRegistry().registerSystem(new BlockBreakSystem());
        this.getEntityStoreRegistry().registerSystem(new BlockPlacedSystem());

        this.getCommandRegistry().registerCommand(new StatsCommand());

        BlockPlayerHandler.init();
        PlaytimePlayerHandler.init();

        PlaytimePlayerSystem.registerPlaytimeSystem();
        LastInteractionSystem.registerLastInteractionSystem();

        SaveSystem.run();
    }

    @Override
    protected void start() {
        IdHashMap.init();
    }
}
