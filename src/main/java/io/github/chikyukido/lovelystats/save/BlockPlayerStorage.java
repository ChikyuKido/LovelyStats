package io.github.chikyukido.lovelystats.save;

import io.github.chikyukido.lovelystats.types.BlockPlayer;

import java.io.*;
import java.util.*;

public class BlockPlayerStorage implements PlayerStorage<BlockPlayer>{
    public static final BlockPlayerStorage INSTANCE = new BlockPlayerStorage();

    private static final int VERSION = 1;
    private static final File DATA_FOLDER = new File("mods/LovelyStats/block");

    static {
        DATA_FOLDER.mkdirs();
    }

    @Override
    public void store(BlockPlayer player) throws IOException {
        File file = new File(DATA_FOLDER, player.getUuid().toString() + ".bin");
        File temp = new File(DATA_FOLDER, player.getUuid().toString() + ".bin.tmp");

        try (DataOutputStream out = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(temp)))) {

            out.writeInt(VERSION);

            // blocks broken
            Map<Long, Long> broken = player.getBlocksBroken();
            out.writeInt(broken.size());
            for (var entry : broken.entrySet()) {
                out.writeLong(entry.getKey());
                out.writeLong(entry.getValue());
            }

            // blocks placed
            Map<Long, Long> placed = player.getBlocksPlaced();
            out.writeInt(placed.size());
            for (var entry : placed.entrySet()) {
                out.writeLong(entry.getKey());
                out.writeLong(entry.getValue());
            }
        }

        if (!temp.renameTo(file)) {
            throw new IOException("Failed to save player file: " + file.getAbsolutePath());
        }
    }

    @Override
    public BlockPlayer load(UUID uuid) throws IOException {
        File file = new File(DATA_FOLDER, uuid.toString() + ".bin");
        BlockPlayer player = new BlockPlayer(uuid);

        if (!file.exists()) return player;

        try (DataInputStream in = new DataInputStream(
                new BufferedInputStream(new FileInputStream(file)))) {

            int version = in.readInt();
            if (version != VERSION) {
                throw new IOException("Unsupported block player file version: " + version);
            }

            // blocks broken
            int brokenCount = in.readInt();
            for (int i = 0; i < brokenCount; i++) {
                long blockId = in.readLong();
                long count = in.readLong();
                player.getBlocksBroken().put(blockId, count);
            }

            // blocks placed
            int placedCount = in.readInt();
            for (int i = 0; i < placedCount; i++) {
                long blockId = in.readLong();
                long count = in.readLong();
                player.getBlocksPlaced().put(blockId, count);
            }
        }

        return player;
    }

    @Override
    public File getDataFolder() {
        return DATA_FOLDER;
    }
}
