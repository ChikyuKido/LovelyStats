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

        if (elevation > 0) {
            PlayerStatsHandler.get().addElevationUp(player.getUuid(),elevation);
        } else if (elevation < 0) {
            PlayerStatsHandler.get().addElevationDown(player.getUuid(),-elevation);
        }

        MovementStates states = state.getMovementStates();
        MovementStates lastState = lastStates.computeIfAbsent(player.getUuid(), _ -> new MovementStates());

        if (states.crouching || states.forcedCrouching) {
            PlayerStatsHandler.get().addDistanceSneaked(player.getUuid(),distance);
        } else if (states.swimming || states.inFluid || states.swimJumping) {
            PlayerStatsHandler.get().addDistanceSwam(player.getUuid(),distance);
        } else if (states.sprinting) {
            PlayerStatsHandler.get().addDistanceRun(player.getUuid(),distance);
        } else if (states.flying){
            PlayerStatsHandler.get().addDistanceWalked(player.getUuid(),distance);
        }else {
            PlayerStatsHandler.get().addDistanceWalked(player.getUuid(),distance);
        }

        if (states.climbing || states.mantling) PlayerStatsHandler.get().addDistanceClimbed(player.getUuid(),Math.max(0, elevation));
        if (states.falling) PlayerStatsHandler.get().addDistanceFallen(player.getUuid(),Math.max(0, -elevation));
        if (lastState.jumping && !states.jumping) {
            PlayerStatsHandler.get().incrementJumps(player.getUuid());
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
