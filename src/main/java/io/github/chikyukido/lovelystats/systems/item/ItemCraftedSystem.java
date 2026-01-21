package io.github.chikyukido.lovelystats.systems.item;

import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.ItemWithAllMetadata;
import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChains;
import com.hypixel.hytale.protocol.packets.inventory.UpdatePlayerInventory;
import com.hypixel.hytale.protocol.packets.window.*;
import com.hypixel.hytale.protocol.packets.world.PlaySoundEvent2D;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.item.config.CraftingRecipe;
import com.hypixel.hytale.server.core.asset.type.item.config.FieldcraftCategory;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.MaterialQuantity;
import com.hypixel.hytale.server.core.io.adapter.PacketAdapters;
import com.hypixel.hytale.server.core.io.adapter.PlayerPacketWatcher;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.chikyukido.lovelystats.handler.ItemStatsHandler;
import io.github.chikyukido.lovelystats.util.Murmur3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ItemCraftedSystem {

    private static final Map<UUID, LastRecipe> lastRecipe = new ConcurrentHashMap<>();
    private static final Map<UUID, Boolean> firstUpdate = new ConcurrentHashMap<>();
    private static final Map<UUID, ConversionCrafting> conversionCrafting = new ConcurrentHashMap<>();

    public static void registerItemCraftedSystem() {
        PacketAdapters.registerOutbound((PlayerPacketWatcher) (player, packet) -> {
            // for in inventory crafting
            if (packet instanceof PlaySoundEvent2D p) {
                if (p.soundEventIndex == 682) {
                    increaseCrafting(player);
                }
            } else if (packet instanceof UpdateWindow p) { // check if a progressing craft is finished
                if (firstUpdate.getOrDefault(player.getUuid(), true)) {
                    firstUpdate.put(player.getUuid(), false);
                    return;
                }
                String json = p.windowData;
                if (json == null) return;
                int idx = json.indexOf("\"progress\":");
                if (idx == -1) return;
                boolean done = json.startsWith("1", idx + 11);
                if (done) {
                    increaseCrafting(player);
                }
            }else if(packet instanceof OpenWindow) { // these below are for instant conversion in crafting benches
                conversionCrafting.put(player.getUuid(), new ConversionCrafting(new HashMap<>(), ConversionState.OPENED));
            }else if(packet instanceof UpdatePlayerInventory inv){
                if(conversionCrafting.containsKey(player.getUuid())){
                    ConversionCrafting crafting = conversionCrafting.get(player.getUuid());
                    if(crafting.state == ConversionState.CONVERTED) {
                        Map<Integer, ItemWithAllMetadata> currentInventory = new HashMap<>();
                        if (inv.hotbar != null && inv.hotbar.items != null)
                            currentInventory.putAll(inv.hotbar.items);
                        if (inv.storage != null && inv.storage.items != null)
                            currentInventory.putAll(inv.storage.items);
                        increaseConversion(crafting.lastInventory,currentInventory,player);
                        crafting.state = ConversionState.OPENED;
                    }
                    crafting.lastInventory.clear();
                    if (inv.hotbar != null && inv.hotbar.items != null)
                        crafting.lastInventory.putAll(inv.hotbar.items);
                    if (inv.storage != null && inv.storage.items != null)
                        crafting.lastInventory.putAll(inv.storage.items);

                }
            }else if(packet instanceof CloseWindow) {
                conversionCrafting.remove(player.getUuid());
            }
        });
        PacketAdapters.registerInbound((PlayerPacketWatcher) (player, packet) -> {
            if (packet instanceof SendWindowAction inv) {
                if (inv.action.getTypeId() == 0) {
                    CraftRecipeAction action = (CraftRecipeAction) inv.action;
                    if (action.recipeId == null) {
                        if(conversionCrafting.containsKey(player.getUuid())) {
                            conversionCrafting.get(player.getUuid()).state = ConversionState.CONVERTED;
                        }
                        return;
                    }
                    CraftingRecipe cr = CraftingRecipe.getAssetMap().getAsset(action.recipeId);
                    if (cr == null) return;
                    if(cr.getTimeSeconds() == 0.0) {
                        lastRecipe.put(player.getUuid(), new LastRecipe(action.recipeId, 1));
                        increaseCrafting(player);
                        return;
                    }
                    if(lastRecipe.containsKey(player.getUuid()) && lastRecipe.get(player.getUuid()).recipeId.equals(action.recipeId)) {
                        lastRecipe.get(player.getUuid()).quantity+=1;
                    }else {
                        lastRecipe.put(player.getUuid(), new LastRecipe(action.recipeId, 1));
                    }
                    firstUpdate.put(player.getUuid(), true);
                }
            }
        });
    }

    private static void increaseCrafting(PlayerRef player) {
        LastRecipe recipe = lastRecipe.getOrDefault(player.getUuid(), null);
        if (recipe == null) return;
        if (recipe.quantity == 0) {
            lastRecipe.remove(player.getUuid());
            return;
        }
        CraftingRecipe cr = CraftingRecipe.getAssetMap().getAsset(recipe.recipeId);
        if (cr == null) return;
        MaterialQuantity[] outputs = cr.getOutputs();
        if (outputs == null || outputs.length == 0) return;
        for (MaterialQuantity output : cr.getOutputs()) {
            if (output.getItemId() == null) return;
            ItemStatsHandler.get().increaseCrafted(player.getUuid(), Murmur3.hash64(output.getItemId()), output.getQuantity());
        }
        recipe.quantity-=1;
        if(recipe.quantity==0) {
            lastRecipe.remove(player.getUuid());
        }
    }

    private static void increaseConversion(Map<Integer, ItemWithAllMetadata> lastInventory,Map<Integer, ItemWithAllMetadata> currentInventory,PlayerRef player){
        for (ItemWithAllMetadata currentItem : currentInventory.values()) {
            String id = currentItem.itemId;
            int currentQty = currentItem.quantity;

            int lastQty = lastInventory.values().stream()
                    .filter(item -> item.itemId.equals(id))
                    .mapToInt(item -> item.quantity)
                    .sum();

            if (currentQty > lastQty) {
                ItemStatsHandler.get().increaseCrafted(player.getUuid(), Murmur3.hash64(id), (currentQty - lastQty));
            }
        }
    }

    private enum ConversionState {
        OPENED,
        CONVERTED,
    }

    private static class ConversionCrafting {
        private final Map<Integer, ItemWithAllMetadata> lastInventory;
        private ConversionState state;

        public ConversionCrafting(Map<Integer, ItemWithAllMetadata> lastInventory, ConversionState state) {
            this.lastInventory = lastInventory;
            this.state = state;
        }
    }
    private static class LastRecipe {
        private String recipeId;
        private int quantity;

        public LastRecipe(String recipeId, int quantity) {
            this.recipeId = recipeId;
            this.quantity = quantity;
        }
    }

}
