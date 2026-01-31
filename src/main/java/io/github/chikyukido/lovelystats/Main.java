package io.github.chikyukido.lovelystats;

import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.util.Config;
import io.github.chikyukido.lovelystats.commands.LeaderboardCommand;
import io.github.chikyukido.lovelystats.commands.StatsCommand;
import io.github.chikyukido.lovelystats.config.PlayerConfig;
import io.github.chikyukido.lovelystats.handler.*;
import io.github.chikyukido.lovelystats.systems.LastInteractionSystem;
import io.github.chikyukido.lovelystats.systems.SaveSystem;
import io.github.chikyukido.lovelystats.systems.entity.EntityDamageSystem;
import io.github.chikyukido.lovelystats.systems.item.*;
import io.github.chikyukido.lovelystats.systems.player.ChatSystem;
import io.github.chikyukido.lovelystats.systems.player.DeathSystem;
import io.github.chikyukido.lovelystats.systems.player.TravelSystem;
import io.github.chikyukido.lovelystats.systems.playtime.PlaytimePlayerSystem;
import io.github.chikyukido.lovelystats.util.IdHashMap;
import io.github.chikyukido.lovelystats.util.Instrumenter;
import io.github.chikyukido.lovelystats.util.NPCRoles;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Main extends JavaPlugin {
    public static Config<PlayerConfig> PLAYER_CONFIG;
    public Main(@Nonnull JavaPluginInit init) {
        super(init);
        PLAYER_CONFIG = this.withConfig("PlayerConfig", PlayerConfig.CODEC);
    }

    @Override
    protected void setup() {
        this.getEventRegistry().registerGlobal(PlayerConnectEvent.class, PlaytimePlayerSystem::onPlayerConnect);
        this.getEventRegistry().registerGlobal(PlayerDisconnectEvent.class, PlaytimePlayerSystem::onPlayerDisconnect);
        this.getEventRegistry().registerGlobal(PlayerConnectEvent.class, LastInteractionSystem::onPlayerConnect);
        this.getEventRegistry().registerGlobal(PlayerConnectEvent.class, RecordedPlayerHandler::onPlayerConnect);
        this.getEventRegistry().registerGlobal(PlayerChatEvent.class, ChatSystem::onPlayerChatEvent);

        this.getEntityStoreRegistry().registerSystem(new BlockBreakSystem());
        this.getEntityStoreRegistry().registerSystem(new BlockPlacedSystem());
        this.getEntityStoreRegistry().registerSystem(new ItemDroppedSystem());
        this.getEntityStoreRegistry().registerSystem(new DeathSystem());
        this.getEntityStoreRegistry().registerSystem(new EntityDamageSystem());

        this.getCommandRegistry().registerCommand(new StatsCommand());
        this.getCommandRegistry().registerCommand(new LeaderboardCommand());

        this.getEntityStoreRegistry().registerSystem(new TravelSystem());

        PlaytimePlayerSystem.registerPlaytimeSystem();
        LastInteractionSystem.registerLastInteractionSystem();
        ItemCollectedSystem.registerItemCollectedSystem();
        ItemCraftedSystem.registerItemCraftedSystem();

        SaveSystem.run();
        if(Instrumenter.ENABLED) {
            HytaleServer.SCHEDULED_EXECUTOR.scheduleAtFixedRate(() -> {
                System.out.println(Instrumenter.get());
            }, 1, 1000, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    protected void shutdown() {
        SaveSystem.save();
    }

    @Override
    protected void start() {
        IdHashMap.init();
        try {
            NPCRoles.init();
        }catch (IOException e) {
            getLogger().atWarning().withCause(e).log("Failed to load NPC roles");
        }
        ItemStatsHandler.init();
        PlaytimeStatsHandler.init();
        PlayerStatsHandler.init();
        EntityStatsHandler.init();
        RecordedPlayerHandler.init();
    }

}
