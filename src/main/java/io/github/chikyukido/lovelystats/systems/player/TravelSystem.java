package io.github.chikyukido.lovelystats.systems.player;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.MovementStates;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.chikyukido.lovelystats.handler.PlayerStatsHandler;
import io.github.chikyukido.lovelystats.types.PlayerStats;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TravelSystem extends EntityTickingSystem<EntityStore> {

    private static final Map<UUID, Vector3d> lastPositions = new HashMap<>();
    private static final Map<UUID, MovementStates> lastStates = new HashMap<>();

    @Override
    public void tick(float v, int i, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        PlayerRef player = archetypeChunk.getComponent(i, PlayerRef.getComponentType());
        TransformComponent transform = archetypeChunk.getComponent(i, TransformComponent.getComponentType());
        MovementStatesComponent state = archetypeChunk.getComponent(i, MovementStatesComponent.getComponentType());
        if (player == null || transform == null || state == null) return;
        Vector3d currentPosition = new Vector3d(transform.getPosition());
        Vector3d lastPosition = lastPositions.computeIfAbsent(player.getUuid(),
                _ -> new Vector3d(currentPosition));


        double distance = lastPosition.distanceTo(currentPosition);
        if (distance <= 0.0 || Double.isNaN(distance)) return;
        double elevation = currentPosition.getY() - lastPosition.getY();
        PlayerStats stats = PlayerStatsHandler.get().getPlayerStats(player.getUuid());

        if (elevation > 0) {
            stats.addElevationUp(elevation);
        } else if (elevation < 0) {
            stats.addElevationDown(-elevation);
        }

        MovementStates states = state.getMovementStates();
        MovementStates lastState = lastStates.computeIfAbsent(player.getUuid(), _ -> new MovementStates());

        if (states.crouching || states.forcedCrouching) {
            stats.addDistanceSneaked(distance);
        } else if (states.swimming || states.inFluid || states.swimJumping) {
            stats.addDistanceSwam(distance);
        } else if (states.sprinting) {
            stats.addDistanceRun(distance);
        } else if (states.flying){
            stats.addDistanceWalked(distance);
        }else {
            stats.addDistanceWalked(distance);
        }

        if (states.climbing || states.mantling) stats.addDistanceClimbed(Math.max(0, elevation));
        if (states.falling) stats.addDistanceFallen(Math.max(0, -elevation));
        if (lastState.jumping && !states.jumping) {
            stats.incrementJumps();
        }

        lastPosition.assign(currentPosition);
        lastStates.put(player.getUuid(), new MovementStates(states));
    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return Archetype.of(PlayerRef.getComponentType(), TransformComponent.getComponentType(), MovementStatesComponent.getComponentType());
    }
}
