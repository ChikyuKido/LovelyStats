package io.github.chikyukido.lovelystats;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.packets.window.CraftRecipeAction;
import com.hypixel.hytale.protocol.packets.window.SendWindowAction;
import com.hypixel.hytale.protocol.packets.window.UpdateWindow;
import com.hypixel.hytale.protocol.packets.world.PlaySoundEvent2D;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.io.PacketHandler;
import com.hypixel.hytale.server.core.io.adapter.PacketAdapters;
import com.hypixel.hytale.server.core.io.adapter.PlayerPacketWatcher;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.chikyukido.lovelystats.commands.StatsCommand;
import io.github.chikyukido.lovelystats.handler.ItemStatsHandler;
import io.github.chikyukido.lovelystats.handler.PlayerStatsHandler;
import io.github.chikyukido.lovelystats.handler.PlaytimeStatsHandler;
import io.github.chikyukido.lovelystats.systems.LastInteractionSystem;
import io.github.chikyukido.lovelystats.systems.SaveSystem;
import io.github.chikyukido.lovelystats.systems.item.*;
import io.github.chikyukido.lovelystats.systems.player.ChatSystem;
import io.github.chikyukido.lovelystats.systems.player.DeathSystem;
import io.github.chikyukido.lovelystats.systems.player.TravelSystem;
import io.github.chikyukido.lovelystats.systems.playtime.PlaytimePlayerSystem;
import io.github.chikyukido.lovelystats.util.IdHashMap;

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
        this.getEventRegistry().registerGlobal(PlayerChatEvent.class, ChatSystem::onPlayerChatEvent);

        this.getEntityStoreRegistry().registerSystem(new BlockBreakSystem());
        this.getEntityStoreRegistry().registerSystem(new BlockPlacedSystem());
        this.getEntityStoreRegistry().registerSystem(new ItemDroppedSystem());
        this.getEntityStoreRegistry().registerSystem(new DeathSystem());

        this.getCommandRegistry().registerCommand(new StatsCommand());

        this.getEntityStoreRegistry().registerSystem(new TravelSystem());


        ItemStatsHandler.init();
        PlaytimeStatsHandler.init();
        PlayerStatsHandler.init();

        PlaytimePlayerSystem.registerPlaytimeSystem();
        LastInteractionSystem.registerLastInteractionSystem();
        ItemCollectedSystem.registerItemCollectedSystem();
        ItemCraftedSystem.registerItemCraftedSystem();

        SaveSystem.run();
    }

    @Override
    protected void start() {
        IdHashMap.init();
    }

}
