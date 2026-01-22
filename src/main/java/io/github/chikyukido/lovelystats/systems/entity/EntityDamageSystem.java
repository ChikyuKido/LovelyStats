package io.github.chikyukido.lovelystats.systems.entity;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import io.github.chikyukido.lovelystats.handler.EntityStatsHandler;
import io.github.chikyukido.lovelystats.util.Murmur3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EntityDamageSystem extends EntityEventSystem<EntityStore, Damage> {
    public EntityDamageSystem() {
        super(Damage.class);
    }

    @Override
    public void handle(int i, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Damage damage) {
        EntityStatMap entityStatMapComponent = archetypeChunk.getComponent(i, EntityStatMap.getComponentType());
        if (entityStatMapComponent == null) return;
        EntityStatValue health = entityStatMapComponent.get(DefaultEntityStatTypes.getHealth());
        if(health == null) return;
        boolean died = health.get() <= 0;

        NPCEntity npc = archetypeChunk.getComponent(i,NPCEntity.getComponentType());
        if(npc != null) {
            applyNPC(npc,damage,store,died);
            return;
        }
        Player player = archetypeChunk.getComponent(i, Player.getComponentType());
        if(player != null) {
            applyPlayer(player,damage,store,died);
        }
    }

    private void applyNPC(NPCEntity npc,Damage damage, Store<EntityStore> store,boolean died) {
        if(damage.getSource() instanceof Damage.EntitySource entitySource) {
            Ref<EntityStore> sourceRef = entitySource.getRef();
            if (!sourceRef.isValid()) return;

            Player player = store.getComponent(sourceRef,Player.getComponentType());
            if(player == null) return;
            EntityStatsHandler.get().increaseDamageDealt(player.getUuid(), Murmur3.hash64(sanitizeRoleName(npc.getRoleName())), damage.getAmount());
            if(died) {
                EntityStatsHandler.get().increaseKilled(player.getUuid(), Murmur3.hash64(sanitizeRoleName(npc.getRoleName())));
            }
        }

    }
    private void applyPlayer(Player player,Damage damage, Store<EntityStore> store,boolean died) {
        if (damage.getSource() instanceof Damage.EntitySource entitySource) {
            Ref<EntityStore> sourceRef = entitySource.getRef();
            if (!sourceRef.isValid()) return;

            NPCEntity npc = store.getComponent(sourceRef,NPCEntity.getComponentType());
            if(npc == null) return;

            EntityStatsHandler.get().increaseDamageReceived(player.getUuid(), Murmur3.hash64(sanitizeRoleName(npc.getRoleName())), damage.getAmount());
            if(died) {
                EntityStatsHandler.get().increaseKilledBY(player.getUuid(), Murmur3.hash64(sanitizeRoleName(npc.getRoleName())));
            }
        }
    }
    private String sanitizeRoleName(String roleName) {
        if (roleName.contains("_Patrol")) {
            roleName = roleName.replace("_Patrol", "");
        }
        if (roleName.contains("_Wander")) {
            roleName = roleName.replace("_Wander", "");
        }
        if (roleName.contains("Frog")) {
            roleName = "Frog_Green";
        }
        return roleName;
    }
    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return Archetype.empty();
    }
}
