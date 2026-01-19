package io.github.chikyukido.lovelystats.stats;

import io.github.chikyukido.lovelystats.save.PlaytimePlayerStorage;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlaytimePlayerHandler {
    private static final PlaytimePlayerHandler INSTANCE = new PlaytimePlayerHandler();
    private final ConcurrentHashMap<UUID, PlaytimePlayer> players = new ConcurrentHashMap<>();

    private PlaytimePlayerHandler() {}
    public static PlaytimePlayerHandler get() {
        return INSTANCE;
    }
    public static void init() {
        try {
            var players = PlaytimePlayerStorage.loadAll();
            for (PlaytimePlayer player : players) {
                INSTANCE.players.put(player.getUuid(), player);
            }
        } catch (IOException _) {

        }
    }

    public void savePlayer(UUID uuid) {
        if(players.containsKey(uuid)) {
            PlaytimePlayer player = players.get(uuid);
            try {
                PlaytimePlayerStorage.save(player);
            } catch (Exception _) {}
        }
    }
    public void startPlaytimeSession(UUID uuid) {
        players.computeIfAbsent(uuid, PlaytimePlayer::new).startPlaytimeSession();
    }
    public void endPlaytimeSession(UUID uuid) {
        players.computeIfAbsent(uuid, PlaytimePlayer::new).endPlaytimeSession();
    }
    public void increaseActivePlaytime(UUID uuid,long playtime) {
        players.computeIfAbsent(uuid, PlaytimePlayer::new).increaseActivePlaytime(playtime);
    }
    public void increaseIdlePlaytime(UUID uuid,long playtime) {
        players.computeIfAbsent(uuid, PlaytimePlayer::new).increaseIdlePlaytime(playtime);
    }
    public PlaytimePlayer getPlaytimeForPlayer(UUID uuid) {
        return players.computeIfAbsent(uuid, PlaytimePlayer::new);
    }


}
