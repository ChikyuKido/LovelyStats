package io.github.chikyukido.lovelystats.types;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EntityStats {
    private final UUID uuid;
    private final Map<Long,SingleEntityStats> entities = new HashMap<>();

    public EntityStats(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Map<Long, SingleEntityStats> getEntities() {
        return entities;
    }
    public SingleEntityStats getEntityStats(long entityId) {
        return entities.computeIfAbsent(entityId, _ -> new SingleEntityStats(entityId));
    }
    public static class SingleEntityStats {
        private final long entityID;
        private long killed;
        private long killedBy;
        private double damageDealt;
        private double damageReceived;

        public SingleEntityStats(long entityID) {
            this.entityID = entityID;
        }

        public SingleEntityStats(long entityID, long killed, long killedBy, double damageDealt, double damageReceived) {
            this.entityID = entityID;
            this.killed = killed;
            this.killedBy = killedBy;
            this.damageDealt = damageDealt;
            this.damageReceived = damageReceived;
        }

        public void increaseKilled() {
            killed++;
        }
        public void increaseKilledBy() {
            killedBy++;
        }
        public void increaseDamageDealt(double amount) {
            damageDealt += amount;
        }
        public void increaseDamageReceived(double amount) {
            damageReceived += amount;
        }

        public long getEntityID() {
            return entityID;
        }

        public long getKilled() {
            return killed;
        }

        public long getKilledBy() {
            return killedBy;
        }

        public double getDamageDealt() {
            return damageDealt;
        }

        public double getDamageReceived() {
            return damageReceived;
        }
    }
}
