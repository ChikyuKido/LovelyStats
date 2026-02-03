package io.github.chikyukido.lovelystats.save;

import io.github.chikyukido.lovelystats.types.EntityStats;
import io.github.chikyukido.lovelystats.types.EntityStats.SingleEntityStats;
import io.github.chikyukido.lovelystats.util.IdHashMap;
import io.github.chikyukido.lovelystats.util.Murmur3;
import io.github.chikyukido.lovelystats.util.NPCRoles;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

public class EntityStatsStorage implements StatsStorage<EntityStats> {
    public static final EntityStatsStorage INSTANCE = new EntityStatsStorage();

    private static final int VERSION = 1;
    private static final File DATA_FOLDER = new File("mods/LovelyStats/entity");

    static {
        DATA_FOLDER.mkdirs();
    }

    private EntityStatsStorage() {}

    @Override
    public void store(EntityStats stats) throws IOException {
        File file = new File(DATA_FOLDER, stats.getUuid().toString() + ".bin");
        File temp = new File(DATA_FOLDER, stats.getUuid().toString() + ".bin.tmp");

        try (DataOutputStream out = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(temp)))) {

            out.writeInt(VERSION);

            Map<Long, SingleEntityStats> entities = stats.getEntities();
            out.writeInt(entities.size());

            for (SingleEntityStats entity : entities.values()) {
                out.writeLong(entity.getEntityID());
                out.writeLong(entity.getKilled());
                out.writeLong(entity.getKilledBy());
                out.writeDouble(entity.getDamageDealt());
                out.writeDouble(entity.getDamageReceived());
            }
        }

        try {
            Files.move(
                    temp.toPath(),
                    file.toPath(),
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE
            );
        } catch (IOException e) {
            throw new IOException(
                    "Failed to save entity stats file: " + file.getAbsolutePath(),
                    e
            );
        }
    }

    @Override
    public EntityStats load(UUID uuid) throws IOException {
        File file = new File(DATA_FOLDER, uuid.toString() + ".bin");
        EntityStats stats = new EntityStats(uuid);

        if (!file.exists()) return stats;

        try (DataInputStream in = new DataInputStream(
                new BufferedInputStream(new FileInputStream(file)))) {

            int version = in.readInt();
            if (version != VERSION) {
                throw new IOException("Unsupported entity stats file version: " + version);
            }

            int entityCount = in.readInt();
            Map<Long, SingleEntityStats> entities = stats.getEntities();

            for (int i = 0; i < entityCount; i++) {
                long entityID = in.readLong();
                String entityIDStr = IdHashMap.ENTITY_ID_HASHMAP.get(entityID);
                if(entityIDStr == null) {
                    continue;
                }
                String sanitizedIDStr = NPCRoles.getRole(entityIDStr);
                long killed = in.readLong();
                long killedBy = in.readLong();
                double damageDealt = in.readDouble();
                double damageReceived = in.readDouble();
                long newEntityID = Murmur3.hash64(sanitizedIDStr);
                if(!entities.containsKey(newEntityID)) {
                    entities.put(entityID, new SingleEntityStats(newEntityID, killed, killedBy, damageDealt, damageReceived));
                }else {
                    entities.compute(newEntityID, (k, stat) -> new SingleEntityStats(stat.getEntityID(),
                            stat.getKilled() + killed,
                            stat.getKilledBy() + killedBy,
                            stat.getDamageDealt() + damageDealt,
                            stat.getDamageReceived() + damageReceived)
                    );
                }
            }
        }

        return stats;
    }

    @Override
    public File getDataFolder() {
        return DATA_FOLDER;
    }
}
