package io.github.chikyukido.lovelystats.save;

import io.github.chikyukido.lovelystats.stats.PlaytimePlayer;
import io.github.chikyukido.lovelystats.types.PlaytimeSession;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlaytimePlayerStorage {
    private static final int VERSION = 1;
    private static final File DATA_FOLDER = new File("mods/LovelyStats/playtime");

    static {
        DATA_FOLDER.mkdirs();
    }

    public static void save(PlaytimePlayer player) throws IOException {
        File file = new File(DATA_FOLDER, player.getUuid().toString() + ".bin");
        File temp = new File(DATA_FOLDER, player.getUuid().toString() + ".bin.tmp");

        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(temp)))) {
            out.writeInt(VERSION);

            // current session
            long currentActivePlaytime = 0;
            long currentIdlePlaytime = 0;
            long currentStartTime = 0;
            long currentStopTime = 0;

            if (player.getCurrentSession() != null) {
                PlaytimeSession current = player.getCurrentSession();
                currentActivePlaytime = current.getActiveTime();
                currentIdlePlaytime = current.getIdleTime();
                currentStartTime = current.getStartTime();
                currentStopTime = System.currentTimeMillis() / 1000;
            }

            out.writeLong(currentStartTime);
            out.writeLong(currentStopTime);
            out.writeLong(currentActivePlaytime);
            out.writeLong(currentIdlePlaytime);

            // past sessions
            List<PlaytimeSession> sessions = player.getSessions();
            out.writeInt(sessions.size());

            for (PlaytimeSession session : sessions) {
                out.writeLong(session.getStartTime());
                out.writeLong(session.getStopTime());
                out.writeLong(session.getActiveTime());
                out.writeLong(session.getIdleTime());
            }
        }

        if (!temp.renameTo(file)) {
            throw new IOException("Failed to save player file: " + file.getAbsolutePath());
        }
    }

    public static PlaytimePlayer load(UUID uuid) throws IOException {
        File file = new File(DATA_FOLDER, uuid.toString() + ".bin");
        PlaytimePlayer player = new PlaytimePlayer(uuid);

        if (!file.exists()) return player;

        try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))) {
            int version = in.readInt();
            if (version != VERSION) throw new IOException("Unsupported player file version: " + version);

            // load the current session if available and add it to the sessions
            // this should only happen when the server was force closed
            long currentStartTime = in.readLong();
            long currentStopTime = in.readLong();
            long currentActivePlaytime = in.readLong();
            long currentIdlePlaytime = in.readLong();

            if (currentStartTime > 0) {
                PlaytimeSession session = new PlaytimeSession(currentStartTime, currentStopTime, currentActivePlaytime,currentIdlePlaytime);
                player.getSessions().add(session);
            }

            int sessionCount = in.readInt();
            for (int i = 0; i < sessionCount; i++) {
                long startTime = in.readLong();
                long stopTime = in.readLong();
                long activeTime = in.readLong();
                long idleTime = in.readLong();

                PlaytimeSession session = new PlaytimeSession(startTime,stopTime,activeTime,idleTime);
                player.getSessions().add(session);
            }
        }

        return player;
    }
    public static List<PlaytimePlayer> loadAll() throws IOException {
        File[] files = DATA_FOLDER.listFiles((dir, name) ->
                name.endsWith(".bin") && !name.endsWith(".bin.tmp")
        );

        if (files == null) return List.of();

        List<PlaytimePlayer> players = new ArrayList<>(files.length);

        for (File file : files) {
            String name = file.getName();
            String uuidPart = name.substring(0, name.length() - 4);

            try {
                UUID uuid = UUID.fromString(uuidPart);
                players.add(load(uuid));
            } catch (IllegalArgumentException _) {
            }
        }

        return players;
    }


}
