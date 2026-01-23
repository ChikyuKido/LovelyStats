package io.github.chikyukido.lovelystats.test;

import io.github.chikyukido.lovelystats.types.*;
import io.github.chikyukido.lovelystats.util.IdHashMap;

import java.util.*;

public class GenerateData {
    private static final Random RANDOM = new Random();

    public static List<PlaytimeStats> generateRandomStats(List<UUID> uuids) {
        List<PlaytimeStats> statsList = new ArrayList<>();
        for (UUID uuid : uuids) {
            PlaytimeStats stats = new PlaytimeStats(uuid);
            int sessionCount = 1 + RANDOM.nextInt(50);
            for (int i = 0; i < sessionCount; i++) {
                long stop = System.currentTimeMillis() / 1000 - RANDOM.nextInt(86400);
                long duration = 300 + RANDOM.nextInt(3600);
                long active = RANDOM.nextInt((int) duration);
                long idle = duration - active;
                long start = stop - duration;
                stats.getSessions().add(new PlaytimeSession(start, stop, active, idle));
            }
            statsList.add(stats);
        }
        return statsList;
    }

    public static List<PlayerStats> generateRandomPlayerStats(List<UUID> uuids) {
        List<PlayerStats> list = new ArrayList<>();
        for (UUID uuid : uuids) {
            double distanceWalked = RANDOM.nextDouble() * 10000;
            double distanceRun = RANDOM.nextDouble() * 5000;
            double distanceSwam = RANDOM.nextDouble() * 2000;
            double distanceFallen = RANDOM.nextDouble() * 500;
            double distanceClimbed = RANDOM.nextDouble() * 1000;
            double distanceSneaked = RANDOM.nextDouble() * 500;
            double elevationUp = RANDOM.nextDouble() * 1000;
            double elevationDown = RANDOM.nextDouble() * 1000;
            long jumps = RANDOM.nextInt(5000);
            long chatMessages = RANDOM.nextInt(1000);
            long deaths = RANDOM.nextInt(100);
            PlayerStats player = new PlayerStats(uuid, distanceWalked, distanceRun, distanceSwam, distanceFallen, distanceClimbed, distanceSneaked, elevationUp, elevationDown, chatMessages, deaths, jumps);
            list.add(player);
        }
        return list;
    }

    public static List<ItemStats> generateRandomItemStats(List<UUID> uuids) {
        List<ItemStats> list = new ArrayList<>();
        for (UUID uuid : uuids) {
            ItemStats stats = new ItemStats(uuid);
            fillRandomMap(stats.getBlocksBroken(), 50);
            fillRandomMap(stats.getBlocksPlaced(), 50);
            fillRandomMap(stats.getCollected(), 50);
            fillRandomMap(stats.getDropped(), 50);
            fillRandomMap(stats.getUsed(), 50);
            fillRandomMap(stats.getCrafted(), 50);
            fillRandomMap(stats.getToolsBroken(), 50);
            list.add(stats);
        }
        return list;
    }

    private static void fillRandomMap(Map<Long, Long> map, int maxEntries) {
        int entries = 1 + RANDOM.nextInt(maxEntries);
        for (int i = 0; i < entries; i++) {
            Long key = IdHashMap.ITEM_HASHMAP.keySet().stream().toList().get(RANDOM.nextInt(IdHashMap.ITEM_HASHMAP.size()));
            long value = RANDOM.nextInt(1000);
            map.put(key, value);
        }
    }

    public static List<EntityStats> generateRandomEntityStats(List<UUID> uuids) {
        List<EntityStats> list = new ArrayList<>();
        for (UUID uuid : uuids) {
            EntityStats stats = new EntityStats(uuid);
            int entityCount = 1 + RANDOM.nextInt(20);
            for (int i = 0; i < entityCount; i++) {
                long entityID = IdHashMap.ENTITY_HASHMAP.keySet().stream().toList().get(RANDOM.nextInt(IdHashMap.ENTITY_HASHMAP.size()));
                long killed = RANDOM.nextInt(50);
                long killedBy = RANDOM.nextInt(50);
                double damageDealt = RANDOM.nextDouble() * 1000;
                double damageReceived = RANDOM.nextDouble() * 1000;
                stats.getEntities().put(entityID, new EntityStats.SingleEntityStats(entityID, killed, killedBy, damageDealt, damageReceived));
            }
            list.add(stats);
        }
        return list;
    }

    private static final String CHAR_POOL = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static Map<UUID, String> generateRandomNames(List<UUID> uuids) {
        Map<UUID, String> map = new HashMap<>();
        for (UUID uuid : uuids) {
            String name = randomNameFromUUID(uuid);
            map.put(uuid, name);
        }
        return map;
    }

    private static String randomNameFromUUID(UUID uuid) {
        long seed = uuid.getMostSignificantBits() ^ uuid.getLeastSignificantBits();
        Random rand = new Random(seed);
        int length = 5 + rand.nextInt(8);
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHAR_POOL.charAt(rand.nextInt(CHAR_POOL.length())));
        }
        return sb.toString();
    }
}