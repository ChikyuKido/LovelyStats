package io.github.chikyukido.lovelystats.save;

import io.github.chikyukido.lovelystats.types.ItemPlayer;

import java.io.*;
import java.util.*;

public class ItemPlayerStorage implements PlayerStorage<ItemPlayer>{
    public static final ItemPlayerStorage INSTANCE = new ItemPlayerStorage();

    private static final int VERSION = 1;
    private static final File DATA_FOLDER = new File("mods/LovelyStats/block");

    static {
        DATA_FOLDER.mkdirs();
    }

    @Override
    public void store(ItemPlayer player) throws IOException {
        File file = new File(DATA_FOLDER, player.getUuid().toString() + ".bin");
        File temp = new File(DATA_FOLDER, player.getUuid().toString() + ".bin.tmp");

        try (DataOutputStream out = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(temp)))) {

            out.writeInt(VERSION);

            writeMap(out, player.getBlocksBroken());
            writeMap(out, player.getBlocksPlaced());
            writeMap(out, player.getCollected());
            writeMap(out, player.getDropped());
            writeMap(out, player.getUsed());
            writeMap(out, player.getCrafted());
            writeMap(out, player.getToolsBroken());
        }

        if (!temp.renameTo(file)) {
            throw new IOException("Failed to save player file: " + file.getAbsolutePath());
        }
    }

    @Override
    public ItemPlayer load(UUID uuid) throws IOException {
        File file = new File(DATA_FOLDER, uuid.toString() + ".bin");
        ItemPlayer player = new ItemPlayer(uuid);

        if (!file.exists()) return player;

        try (DataInputStream in = new DataInputStream(
                new BufferedInputStream(new FileInputStream(file)))) {

            int version = in.readInt();
            if (version != VERSION) {
                throw new IOException("Unsupported block player file version: " + version);
            }


            readMap(in, player.getBlocksBroken());
            readMap(in, player.getBlocksPlaced());
            readMap(in, player.getCollected());
            readMap(in, player.getDropped());
            readMap(in, player.getUsed());
            readMap(in, player.getCrafted());
            readMap(in, player.getToolsBroken());
        }

        return player;
    }

    private void writeMap(DataOutputStream out, Map<Long, Long> map) throws IOException {
        out.writeInt(map.size());
        for (var entry : map.entrySet()) {
            out.writeLong(entry.getKey());
            out.writeLong(entry.getValue());
        }
    }

    private void readMap(DataInputStream in, Map<Long, Long> map) throws IOException {
        int count = in.readInt();
        for (int i = 0; i < count; i++) {
            long key = in.readLong();
            long value = in.readLong();
            map.put(key, value);
        }
    }

    @Override
    public File getDataFolder() {
        return DATA_FOLDER;
    }
}
