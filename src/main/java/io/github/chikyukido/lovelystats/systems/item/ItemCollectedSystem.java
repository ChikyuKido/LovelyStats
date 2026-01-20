package io.github.chikyukido.lovelystats.systems.item;

import com.hypixel.hytale.protocol.packets.interface_.Notification;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.io.adapter.PacketAdapters;
import com.hypixel.hytale.server.core.io.adapter.PlayerPacketWatcher;
import io.github.chikyukido.lovelystats.handler.ItemStatsHandler;
import io.github.chikyukido.lovelystats.util.Murmur3;

public class ItemCollectedSystem {
    public static void registerItemCollectedSystem() {
        PacketAdapters.registerOutbound((PlayerPacketWatcher) (playerRef, packet) -> {
            if(packet instanceof Notification inv){
                if(inv.item == null) return;
                ItemStatsHandler.get().increaseCollected(
                        playerRef.getUuid(),
                        Murmur3.hash64(inv.item.itemId),
                        inv.item.quantity
                );
                playerRef.sendMessage(Message.raw("item collected"));

            }
        });
    }
}
