package io.github.chikyukido.lovelystats.save;

import io.github.chikyukido.lovelystats.types.PlayerStats;

import java.io.*;
import java.util.UUID;

public class PlayerStatsStorage implements StatsStorage<PlayerStats> {

    public static final PlayerStatsStorage INSTANCE = new PlayerStatsStorage();
    private static final File DATA_FOLDER = new File("mods/LovelyStats/playerstats");
    private static final File OLD_DATA_FOLDER = new File("playerstats");
    private static final int VERSION = 1;

    static {
        if (OLD_DATA_FOLDER.exists()) {
            OLD_DATA_FOLDER.renameTo(DATA_FOLDER);
        }
        DATA_FOLDER.mkdirs();
    }

    private PlayerStatsStorage() {
    }

    @Override
    public File getDataFolder() {
        return DATA_FOLDER;
    }

    @Override
    public PlayerStats load(UUID uuid) throws IOException {
        File file = new File(DATA_FOLDER, uuid.toString() + ".bin");
        if (!file.exists()) return new PlayerStats(uuid);

        try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))) {
            int version = in.readInt();
            if (version != VERSION) {
                throw new IOException("Unsupported player stats file version: " + version);
            }

            double distanceWalked = in.readDouble();
            double distanceRun = in.readDouble();
            double distanceSwam = in.readDouble();
            double distanceFallen = in.readDouble();
            double distanceClimbed = in.readDouble();
            double distanceSneaked = in.readDouble();
            double elevationUp = in.readDouble();
            double elevationDown = in.readDouble();

            long jumps = in.readLong();
            long chatMessages = in.readLong();
            long deaths = in.readLong();

            PlayerStats player = new PlayerStats(
                    uuid,
                    distanceWalked,
                    distanceRun,
                    distanceSwam,
                    distanceFallen,
                    distanceClimbed,
                    distanceSneaked,
                    elevationUp,
                    elevationDown,
                    chatMessages,
                    deaths,
                    jumps
            );
            return player;
        }
    }

    @Override
    public void store(PlayerStats player) throws IOException {
        File file = new File(DATA_FOLDER, player.getUuid() + ".bin");
        File temp = new File(DATA_FOLDER, player.getUuid() + ".bin.tmp");

        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(temp)))) {
            out.writeInt(VERSION);

            out.writeDouble(player.getDistanceWalked());
            out.writeDouble(player.getDistanceRun());
            out.writeDouble(player.getDistanceSwam());
            out.writeDouble(player.getDistanceFallen());
            out.writeDouble(player.getDistanceClimbed());
            out.writeDouble(player.getDistanceSneaked());
            out.writeDouble(player.getElevationUp());
            out.writeDouble(player.getElevationDown());

            out.writeLong(player.getJumps());
            out.writeLong(player.getChatMessages());
            out.writeLong(player.getDeaths());
        }

        if (!temp.renameTo(file)) {
            throw new IOException("Failed to save player stats file: " + file.getAbsolutePath());
        }
    }
}
