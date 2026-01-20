package io.github.chikyukido.lovelystats.systems.item;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.event.events.ecs.DropItemEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.chikyukido.lovelystats.handler.ItemStatsHandler;
import io.github.chikyukido.lovelystats.util.Murmur3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemDroppedSystem extends EntityEventSystem<EntityStore, DropItemEvent.Drop> {
    public ItemDroppedSystem() {
        super(DropItemEvent.Drop.class);
    }

    @Override
    public void handle(int i, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull DropItemEvent.Drop event) {
        ItemStack is = event.getItemStack();
        Item item = is.getItem();
        if (item == Item.UNKNOWN) return;
        PlayerRef player = archetypeChunk.getComponent(i, PlayerRef.getComponentType());
        if (player == null) return;
        ItemStatsHandler.get().increaseDropped(player.getUuid(), Murmur3.hash64(item.getBlockId()), is.getQuantity());
    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return Archetype.empty();
    }
}
