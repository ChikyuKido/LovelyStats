package io.github.chikyukido.lovelystats;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import io.github.chikyukido.lovelystats.commands.StatsCommand;
import io.github.chikyukido.lovelystats.systems.PlaytimeSystem;

import javax.annotation.Nonnull;

public class Main extends JavaPlugin {

    public Main(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        this.getEventRegistry().registerGlobal(PlayerConnectEvent.class, PlaytimeSystem::onPlayerConnect);
        this.getEventRegistry().registerGlobal(PlayerDisconnectEvent.class, PlaytimeSystem::onPlayerDisconnect);

        this.getCommandRegistry().registerCommand(new StatsCommand());

        new PlaytimeSystem();
    }
}
