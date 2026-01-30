package io.github.chikyukido.lovelystats.systems;

import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChains;
import com.hypixel.hytale.protocol.packets.player.ClientMovement;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.io.adapter.PacketAdapters;
import com.hypixel.hytale.server.core.io.adapter.PlayerPacketWatcher;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;

import java.util.UUID;

public class LastInteractionSystem {
    private static final Long2LongMap lastInteraction = new Long2LongOpenHashMap();
    private static final long IDLE_MS = 5 * 60 * 1000;

    static {
        lastInteraction.defaultReturnValue(Long.MAX_VALUE);
    }

    public static void registerLastInteractionSystem() {
        PacketAdapters.registerInbound((PlayerPacketWatcher) (playerRef, packet) -> {
            if (packet instanceof ClientMovement || packet instanceof SyncInteractionChains) {
               long msb = playerRef.getUuid().getMostSignificantBits();
                lastInteraction.put(msb, System.currentTimeMillis());
            }
        });
    }

    public static boolean isPlayerIdle(UUID uuid) {
        long last = lastInteraction.get(uuid.getMostSignificantBits());
        return System.currentTimeMillis() - last > IDLE_MS;
    }

    public static void onPlayerConnect(PlayerConnectEvent event) {
        long msb = event.getPlayerRef().getUuid().getMostSignificantBits();
        lastInteraction.put(msb, System.currentTimeMillis());
    }
}
