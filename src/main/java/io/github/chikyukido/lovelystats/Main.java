package io.github.chikyukido.lovelystats;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import io.github.chikyukido.lovelystats.commands.StatsCommand;
import io.github.chikyukido.lovelystats.handler.PlaytimePlayerHandler;
import io.github.chikyukido.lovelystats.systems.LastInteractionSystem;
import io.github.chikyukido.lovelystats.systems.PlaytimePlayerSystem;
import io.github.chikyukido.lovelystats.systems.block.BlockBreakSystem;
import io.github.chikyukido.lovelystats.systems.block.BlockPlacedSystem;

import javax.annotation.Nonnull;

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

        PlaytimePlayerHandler.init();

        PlaytimePlayerSystem.registerPlaytimeSystem();
        LastInteractionSystem.registerLastInteractionSystem();
    }
}
