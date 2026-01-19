package io.github.chikyukido.lovelystats.systems;

import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.system.DelayedSystem;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.chikyukido.lovelystats.stats.StatsHandler;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


public class PlaytimeSystem {
    public PlaytimeSystem() {
        HytaleServer.SCHEDULED_EXECUTOR.scheduleAtFixedRate(() -> {
            var players = Universe.get().getPlayers();
            for (PlayerRef player : players) {
                //TODO: Check for idling
                StatsHandler.get().increasePlaytime(player.getUuid().toString(),1);
            }
        },1,1, TimeUnit.SECONDS);
    }

    public static void onPlayerConnect(PlayerConnectEvent event) {
        PlayerRef player = event.getPlayerRef();
        UUID uuid = player.getUuid();
        StatsHandler.get().startPlaytimeSession(uuid.toString());
    }
    public static void onPlayerDisconnect(PlayerDisconnectEvent event) {
        PlayerRef player = event.getPlayerRef();
        UUID uuid = player.getUuid();
        StatsHandler.get().endPlaytimeSession(uuid.toString());
        StatsHandler.get().savePlayer(uuid.toString());
    }

}

