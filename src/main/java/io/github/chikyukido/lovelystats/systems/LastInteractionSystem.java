package io.github.chikyukido.lovelystats.systems;

import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChains;
import com.hypixel.hytale.protocol.packets.player.ClientMovement;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.io.adapter.PacketAdapters;
import com.hypixel.hytale.server.core.io.adapter.PlayerPacketWatcher;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LastInteractionSystem {
    private static final Map<UUID, Long> lastInteraction = new ConcurrentHashMap<>();
    private static final long IDLE_MS = 5 * 60 * 1000;

    public static void registerLastInteractionSystem() {
        PacketAdapters.registerInbound((PlayerPacketWatcher) (playerRef, packet) -> {
            if (packet instanceof ClientMovement || packet instanceof SyncInteractionChains) {
                lastInteraction.put(playerRef.getUuid(), System.currentTimeMillis());
            }
        });
    }

    public static boolean isPlayerIdle(UUID uuid) {
        long last = lastInteraction.getOrDefault(uuid, Long.MAX_VALUE);
        return System.currentTimeMillis() - last > IDLE_MS;
    }

    public static void onPlayerConnect(PlayerConnectEvent event) {
        lastInteraction.put(event.getPlayerRef().getUuid(), System.currentTimeMillis());
    }
}
