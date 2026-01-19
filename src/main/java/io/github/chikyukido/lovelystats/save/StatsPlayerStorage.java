package io.github.chikyukido.lovelystats.save;

import io.github.chikyukido.lovelystats.stats.StatsPlayer;
import io.github.chikyukido.lovelystats.types.PlaytimeSession;

import java.io.*;
import java.util.List;

public class StatsPlayerStorage {
    private static final int VERSION = 1;
    private static final File DATA_FOLDER = new File("mods/LovelyStats");

    static {
        DATA_FOLDER.mkdirs();
    }

    public static void save(StatsPlayer player) throws IOException {
        File file = new File(DATA_FOLDER, player.getUuid() + ".bin");
        File temp = new File(DATA_FOLDER, player.getUuid() + ".bin.tmp");

        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(temp)))) {
            out.writeInt(VERSION);

            // current session
            double currentPlaytime = 0;
            long currentStartTime = 0;
            long currentStopTime = 0;

            if (player.getCurrentSession() != null) {
                PlaytimeSession current = player.getCurrentSession();
                currentPlaytime = current.getTotalPlaytime();
                currentStartTime = current.getStartTime();
                currentStopTime = System.currentTimeMillis() / 1000;
            }

            out.writeLong(currentStartTime);
            out.writeLong(currentStopTime);
            out.writeDouble(currentPlaytime);

            // past sessions
            List<PlaytimeSession> sessions = player.getSessions();
            out.writeInt(sessions.size());

            for (PlaytimeSession session : sessions) {
                out.writeLong(session.getStartTime());
                out.writeLong(session.getStopTime());
                out.writeDouble(session.getTotalPlaytime());
            }
        }

        if (!temp.renameTo(file)) {
            throw new IOException("Failed to save player file: " + file.getAbsolutePath());
        }
    }

    public static StatsPlayer load(String uuid) throws IOException {
        File file = new File(DATA_FOLDER, uuid + ".bin");
        StatsPlayer player = new StatsPlayer(uuid);

        if (!file.exists()) return player;

        try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))) {
            int version = in.readInt();
            if (version != VERSION) throw new IOException("Unsupported player file version: " + version);

            // load the current session if available and add it to the sessions
            // this should only happen when the server was force closed
            long currentStartTime = in.readLong();
            long currentStopTime = in.readLong();
            double currentPlaytime = in.readDouble();

            if (currentStartTime > 0) {
                PlaytimeSession session = new PlaytimeSession(currentStartTime, currentStopTime, currentPlaytime);
                player.getSessions().add(session);
            }

            int sessionCount = in.readInt();
            for (int i = 0; i < sessionCount; i++) {
                long startTime = in.readLong();
                long stopTime = in.readLong();
                double totalPlaytime = in.readDouble();

                PlaytimeSession session = new PlaytimeSession(startTime,stopTime,totalPlaytime);
                player.getSessions().add(session);
            }
        }

        return player;
    }

}
