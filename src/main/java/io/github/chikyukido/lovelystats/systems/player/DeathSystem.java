package io.github.chikyukido.lovelystats.systems.player;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.modules.entity.damage.event.KillFeedEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.chikyukido.lovelystats.handler.PlayerStatsHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DeathSystem extends EntityEventSystem<EntityStore, KillFeedEvent.DecedentMessage> {
    protected DeathSystem() {
        super(KillFeedEvent.DecedentMessage.class);
    }

    @Override
    public void handle(int i, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull KillFeedEvent.DecedentMessage decedentMessage) {
        PlayerRef player = archetypeChunk.getComponent(i, PlayerRef.getComponentType());
        if(player == null) {
            return;
        }
        PlayerStatsHandler.get().incrementDeaths(player.getUuid());
    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return PlayerRef.getComponentType();
    }
}
