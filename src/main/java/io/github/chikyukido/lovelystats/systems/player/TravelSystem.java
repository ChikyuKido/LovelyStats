package io.github.chikyukido.lovelystats.systems.player;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.DelayedEntitySystem;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.MovementStates;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.chikyukido.lovelystats.handler.PlayerStatsHandler;
import io.github.chikyukido.lovelystats.types.PlayerStats;
import io.github.chikyukido.lovelystats.util.Instrumenter;
import it.unimi.dsi.fastutil.longs.Long2BooleanMap;
import it.unimi.dsi.fastutil.longs.Long2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TravelSystem extends DelayedEntitySystem<EntityStore> {
    private static final int INSTRUMENTER_ID_END = Instrumenter.register("TravelSystemEnd");
    Long2ObjectMap<Vector3d> lastPositions = new Long2ObjectOpenHashMap<>();
    Long2BooleanMap lastJumping = new Long2BooleanOpenHashMap();

    public TravelSystem() {
        super(0.1f);
    }

    @Override
    public void tick(float v, int i, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        long start = Instrumenter.enter(INSTRUMENTER_ID_END);
        PlayerRef player = archetypeChunk.getComponent(i, PlayerRef.getComponentType());
        TransformComponent transform = archetypeChunk.getComponent(i, TransformComponent.getComponentType());
        MovementStatesComponent state = archetypeChunk.getComponent(i, MovementStatesComponent.getComponentType());
        if (player == null || transform == null || state == null) {
            Instrumenter.exit(INSTRUMENTER_ID_END,start);
            return;
        }
        long key = player.getUuid().getMostSignificantBits();
        Vector3d currentPosition = transform.getPosition();
        Vector3d lastPos = lastPositions.computeIfAbsent(key, k -> new Vector3d(currentPosition));

        double distance = lastPos.distanceTo(currentPosition);
        if (distance <= 0.0 || Double.isNaN(distance)) {
            Instrumenter.exit(INSTRUMENTER_ID_END,start);
            return;
        }
        double elevation = currentPosition.y-lastPos.y;
        PlayerStats ps = PlayerStatsHandler.get().getPlayerStats(player.getUuid());
        ps.markDirty(); // mark dirty for the next save

        if (elevation > 0) {
            ps.addElevationUp(elevation);
        } else if (elevation < 0) {
            ps.addElevationDown(-elevation);
        }

        MovementStates states = state.getMovementStates();

        if (states.crouching || states.forcedCrouching) {
            ps.addDistanceSneaked(distance);
        } else if (states.swimming || states.inFluid || states.swimJumping) {
            ps.addDistanceSwam(distance);
        } else if (states.sprinting) {
            ps.addDistanceRun(distance);
        } else if (states.flying){
            ps.addDistanceWalked(distance);
        }else {
            ps.addDistanceWalked(distance);
        }

        if (states.climbing || states.mantling) ps.addDistanceClimbed(Math.max(0, elevation));
        if (states.falling) ps.addDistanceFallen(Math.max(0, -elevation));

        boolean wasJumping = lastJumping.getOrDefault(key, false);
        if (wasJumping && !states.jumping) {
            ps.incrementJumps();
        }

        lastPos.assign(currentPosition);
        lastJumping.put(key, states.jumping);
        Instrumenter.exit(INSTRUMENTER_ID_END,start);
    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return Archetype.of(PlayerRef.getComponentType(), TransformComponent.getComponentType(), MovementStatesComponent.getComponentType());
    }
}