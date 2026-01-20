package io.github.chikyukido.lovelystats;

import com.hypixel.hytale.builtin.crafting.commands.RecipeCommand;
import com.hypixel.hytale.protocol.ItemQuantity;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.packets.interface_.Notification;
import com.hypixel.hytale.protocol.packets.inventory.UpdatePlayerInventory;
import com.hypixel.hytale.protocol.packets.window.CraftItemAction;
import com.hypixel.hytale.protocol.packets.window.CraftRecipeAction;
import com.hypixel.hytale.protocol.packets.window.SendWindowAction;
import com.hypixel.hytale.protocol.packets.window.UpdateWindow;
import com.hypixel.hytale.protocol.packets.world.PlaySoundEvent2D;
import com.hypixel.hytale.protocol.packets.world.ServerSetBlocks;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.io.PacketHandler;
import com.hypixel.hytale.server.core.io.adapter.PacketAdapters;
import com.hypixel.hytale.server.core.io.adapter.PlayerPacketWatcher;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.chikyukido.lovelystats.commands.StatsCommand;
import io.github.chikyukido.lovelystats.handler.ItemPlayerHandler;
import io.github.chikyukido.lovelystats.handler.PlaytimePlayerHandler;
import io.github.chikyukido.lovelystats.systems.LastInteractionSystem;
import io.github.chikyukido.lovelystats.systems.item.*;
import io.github.chikyukido.lovelystats.systems.playtime.PlaytimePlayerSystem;
import io.github.chikyukido.lovelystats.systems.SaveSystem;
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
        PacketAdapters.registerOutbound((PacketHandler handler, Packet packet) -> {
            var handlerName = handler.getClass().getSimpleName();
            var packetName = packet.getClass().getSimpleName();
            // some common packets excluded to reduce spam
            if (!"EntityUpdates".equals(packetName) && !"CachedPacket".equals(packetName) && !"PlayInteractionFor".equals(packetName)) {
                getLogger().atInfo()
                        .log("[" + handlerName + "] Sent packet id=" + packet.getId() + ": " + packetName);
            }
            if (packet instanceof PlaySoundEvent2D inv){
                getLogger().atInfo().log(String.valueOf(inv.soundEventIndex));
            }
        });
        PacketAdapters.registerInbound((PlayerPacketWatcher) (PlayerRef handler, Packet packet) -> {
            var handlerName = handler.getClass().getSimpleName();
            var packetName = packet.getClass().getSimpleName();
            // some common packets excluded to reduce spam
            if (!"ClientMovement".equals(packetName) && !"CachedPacket".equals(packetName) && !"PlayInteractionFor".equals(packetName)) {
                handler.sendMessage(Message.raw("[" + handlerName + "] Sent packet id=" + packet.getId() + ": " + packetName));
            }
            if (packet instanceof SendWindowAction inv){
                if(inv.action.getTypeId() == 0) {
                    CraftRecipeAction action = (CraftRecipeAction) inv.action;
                    handler.sendMessage(Message.raw(action.recipeId));
                }

            }
        });
        this.getEntityStoreRegistry().registerSystem(new BlockBreakSystem());
        this.getEntityStoreRegistry().registerSystem(new BlockPlacedSystem());
        this.getEntityStoreRegistry().registerSystem(new ItemDroppedSystem());

        this.getCommandRegistry().registerCommand(new StatsCommand());

        ItemPlayerHandler.init();
        PlaytimePlayerHandler.init();

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
