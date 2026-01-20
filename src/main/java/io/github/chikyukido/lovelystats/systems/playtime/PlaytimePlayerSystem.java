package io.github.chikyukido.lovelystats.systems.playtime;

import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import io.github.chikyukido.lovelystats.handler.PlaytimeStatsHandler;
import io.github.chikyukido.lovelystats.systems.LastInteractionSystem;

import java.util.UUID;
import java.util.concurrent.TimeUnit;


public class PlaytimePlayerSystem {
    public static void registerPlaytimeSystem() {
        HytaleServer.SCHEDULED_EXECUTOR.scheduleAtFixedRate(() -> {
            var players = Universe.get().getPlayers();
            for (PlayerRef player : players) {
                if (LastInteractionSystem.isPlayerIdle(player.getUuid())) {
                    PlaytimeStatsHandler.get().increaseIdlePlaytime(player.getUuid(), 1);
                } else {
                    PlaytimeStatsHandler.get().increaseActivePlaytime(player.getUuid(), 1);
                }
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    public static void onPlayerConnect(PlayerConnectEvent event) {
        PlayerRef player = event.getPlayerRef();
        UUID uuid = player.getUuid();
        PlaytimeStatsHandler.get().startPlaytimeSession(uuid);
    }

    public static void onPlayerDisconnect(PlayerDisconnectEvent event) {
        PlayerRef player = event.getPlayerRef();
        UUID uuid = player.getUuid();
        PlaytimeStatsHandler.get().endPlaytimeSession(uuid);
    }

}

