package io.github.chikyukido.lovelystats;

import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import io.github.chikyukido.lovelystats.commands.LeaderboardCommand;
import io.github.chikyukido.lovelystats.commands.StatsCommand;
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


        ItemStatsHandler.init();
        PlaytimeStatsHandler.init();
        PlayerStatsHandler.init();
        EntityStatsHandler.init();
        RecordedPlayerHandler.init();

        PlaytimePlayerSystem.registerPlaytimeSystem();
        LastInteractionSystem.registerLastInteractionSystem();
        ItemCollectedSystem.registerItemCollectedSystem();
        ItemCraftedSystem.registerItemCraftedSystem();

        SaveSystem.run();
    }

    @Override
    protected void shutdown() {
        SaveSystem.save();
    }

    @Override
    protected void start() {
        IdHashMap.init();
//        List<UUID> uuids = new ArrayList<>();
//        for (int i = 0; i <2000; i++) {
//            uuids.add(UUID.randomUUID());
//        }
//        List<PlayerStats> playerStats = GenerateData.generateRandomPlayerStats(uuids);
//        List<ItemStats> itemStats = GenerateData.generateRandomItemStats(uuids);
//        List<EntityStats> entityStats = GenerateData.generateRandomEntityStats(uuids);
//        List<PlaytimeStats> playtimeStats = GenerateData.generateRandomStats(uuids);
//        Map<UUID, String> playerNames = GenerateData.generateRandomNames(uuids);
//
//        playerStats.forEach(p -> PlayerStatsHandler.get().getPlayers().put(p.getUuid(),p));
//        itemStats.forEach(p -> ItemStatsHandler.get().getPlayers().put(p.getUuid(),p));
//        entityStats.forEach(p -> EntityStatsHandler.get().getPlayers().put(p.getUuid(),p));
//        playtimeStats.forEach(p -> PlaytimeStatsHandler.get().getPlayers().put(p.getUuid(),p));
//        playerNames.forEach((uuid, s) -> RecordedPlayerHandler.get().getPlayers().put(uuid,s));
    }

}
