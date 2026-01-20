package io.github.chikyukido.lovelystats.save;

import io.github.chikyukido.lovelystats.types.PlayerStats;

import java.io.*;
import java.util.UUID;

public class PlayerStatsStorage implements StatsStorage<PlayerStats> {

    public static final PlayerStatsStorage INSTANCE = new PlayerStatsStorage();
    private static final File DATA_FOLDER = new File("playerstats");
    private static final int VERSION = 1;

    private PlayerStatsStorage() {}

    static {
        DATA_FOLDER.mkdirs();
    }

    @Override
    public File getDataFolder() {
        return DATA_FOLDER;
    }

    @Override
    public PlayerStats load(UUID uuid) throws IOException {
        File file = new File(DATA_FOLDER, uuid.toString() + ".bin");
        PlayerStats player = new PlayerStats(uuid.toString());

        if (!file.exists()) return player;

        try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))) {
            int version = in.readInt();
            if (version != VERSION) {
                throw new IOException("Unsupported player stats file version: " + version);
            }

            player.addDistanceWalked(in.readDouble());
            player.addDistanceRun(in.readDouble());
            player.addDistanceSwam(in.readDouble());
            player.addDistanceFallen(in.readDouble());
            player.addDistanceClimbed(in.readDouble());
            player.addDistanceSneaked(in.readDouble());
            player.addElevationUp(in.readDouble());
            player.addElevationDown(in.readDouble());

            long chatMessages = in.readLong();
            for (long i = 0; i < chatMessages; i++) player.incrementChatMessages();

            long deaths = in.readLong();
            for (long i = 0; i < deaths; i++) player.incrementDeaths();
            long jumps = in.readLong();
            for (long i = 0; i < jumps; i++) player.incrementJumps();
        }

        return player;
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
