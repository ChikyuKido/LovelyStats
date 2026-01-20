package io.github.chikyukido.lovelystats.systems.player;

import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.chikyukido.lovelystats.handler.PlayerStatsHandler;

public class ChatSystem {

    public static void onPlayerChatEvent(PlayerChatEvent event) {
        PlayerRef player = event.getSender();
        PlayerStatsHandler.get().incrementChatMessages(player.getUuid());
    }
}
