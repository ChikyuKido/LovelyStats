package io.github.chikyukido.lovelystats.handler;

import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RecordedPlayerHandler {

    private static final RecordedPlayerHandler INSTANCE = new RecordedPlayerHandler();
    private final ConcurrentHashMap<UUID, String> players = new ConcurrentHashMap<>();
    private static final File DATA_FOLDER = new File("mods/LovelyStats/recorded_players");

    private RecordedPlayerHandler() {
    }

    public static RecordedPlayerHandler get() {
        return INSTANCE;
    }

    public static void init() {
        DATA_FOLDER.mkdirs();
        try {
            File[] files = DATA_FOLDER.listFiles((dir, name) ->
                    name.endsWith(".txt")
            );
            if (files == null) return;

            for (File file : files) {
                String name = file.getName();
                String uuidPart = name.substring(0, name.length() - 4);
                try {
                    UUID uuid = UUID.fromString(uuidPart);
                    String username = Files.readString(file.toPath());
                    INSTANCE.players.put(uuid, username);
                } catch (IllegalArgumentException ignored) {
                }
            }
        } catch (IOException _) {
        }
    }

    public void savePlayer(UUID uuid) {
        if (players.containsKey(uuid)) {
            File file = new File(DATA_FOLDER, uuid.toString() + ".txt");
            try {
                Files.writeString(file.toPath(),players.get(uuid));
            } catch (IOException _) {

            }
        }
    }
    public String getUsername(UUID player)  {
        return players.get(player);
    }
    public List<UUID> getPlayers() {
        return new ArrayList<>(players.keySet());
    }

    public static void onPlayerConnect(PlayerConnectEvent event) {
        INSTANCE.players.put(event.getPlayerRef().getUuid(),event.getPlayerRef().getUsername());
        INSTANCE.savePlayer(event.getPlayerRef().getUuid());
    }

}
