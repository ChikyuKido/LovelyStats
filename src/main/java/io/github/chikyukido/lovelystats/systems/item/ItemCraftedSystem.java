package io.github.chikyukido.lovelystats.systems.item;

import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.packets.window.CraftRecipeAction;
import com.hypixel.hytale.protocol.packets.window.SendWindowAction;
import com.hypixel.hytale.protocol.packets.window.UpdateWindow;
import com.hypixel.hytale.protocol.packets.world.PlaySoundEvent2D;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.item.config.CraftingRecipe;
import com.hypixel.hytale.server.core.inventory.MaterialQuantity;
import com.hypixel.hytale.server.core.io.PacketHandler;
import com.hypixel.hytale.server.core.io.adapter.PacketAdapters;
import com.hypixel.hytale.server.core.io.adapter.PlayerPacketWatcher;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.chikyukido.lovelystats.handler.ItemPlayerHandler;
import io.github.chikyukido.lovelystats.util.Murmur3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ItemCraftedSystem {

    private static final Map<UUID,String> lastRecipe = new HashMap<>();
    private static final Map<UUID,Boolean> firstUpdate = new HashMap<>();

    public static void registerItemCraftedSystem() {
        PacketAdapters.registerOutbound((PlayerPacketWatcher) ( player, packet) -> {
            if (packet instanceof PlaySoundEvent2D p) {
                if (p.soundEventIndex == 682) {
                    increaseCrafting(player);
                }
            }else if(packet instanceof UpdateWindow p) {
                if(firstUpdate.getOrDefault(player.getUuid(),true)) {
                    firstUpdate.put(player.getUuid(),false);
                    return;
                }
                String json = p.windowData;
                if(json == null) return;
                int idx = json.indexOf("\"progress\":");
                if (idx == -1) return;
                boolean done = json.startsWith("1", idx + 11);
                if(done) {
                    increaseCrafting(player);
                }

            }
        });
        PacketAdapters.registerInbound((PlayerPacketWatcher) ( player,  packet) -> {
            if (packet instanceof SendWindowAction inv) {
                if (inv.action.getTypeId() == 0) {
                    CraftRecipeAction action = (CraftRecipeAction) inv.action;
                    lastRecipe.put(player.getUuid(), action.recipeId);
                    firstUpdate.put(player.getUuid(), true);
                }
            }
        });
    }

    private static void increaseCrafting(PlayerRef player) {
        String recipeId = lastRecipe.getOrDefault(player.getUuid(),"");
        if(recipeId.isEmpty()) return;
        CraftingRecipe cr = CraftingRecipe.getAssetMap().getAsset(recipeId);
        if(cr == null) return;
        MaterialQuantity[] outputs = cr.getOutputs();
        if(outputs == null || outputs.length == 0) return;
        for (MaterialQuantity output : cr.getOutputs()) {
            if(output.getItemId() == null) return;
            ItemPlayerHandler.get().increaseCrafted(player.getUuid(), Murmur3.hash64(output.getItemId()),output.getQuantity());
        }
        lastRecipe.remove(player.getUuid());
    }

}
