package io.github.chikyukido.lovelystats.stats;

import io.github.chikyukido.lovelystats.save.StatsPlayerStorage;
import io.github.chikyukido.lovelystats.systems.PlaytimeSystem;

import java.util.concurrent.ConcurrentHashMap;

public class StatsHandler {
    private static final StatsHandler INSTANCE = new StatsHandler();
    private final ConcurrentHashMap<String, StatsPlayer> players = new ConcurrentHashMap<>();

    private StatsHandler() {}
    public static StatsHandler get() {
        return INSTANCE;
    }

    public void savePlayer(String uuid) {
        if(players.containsKey(uuid)) {
            StatsPlayer player = players.get(uuid);
            try {
                StatsPlayerStorage.save(player);
            } catch (Exception _) {}
        }
    }
    public void startPlaytimeSession(String uuid) {
        players.computeIfAbsent(uuid, StatsPlayer::new).startPlaytimeSession();
    }
    public void endPlaytimeSession(String uuid) {
        players.computeIfAbsent(uuid, StatsPlayer::new).endPlaytimeSession();
    }
    public void increasePlaytime(String uuid,double playtime) {
        players.computeIfAbsent(uuid,StatsPlayer::new).increasePlaytime(playtime);
    }
    public long getTotalPlaytime(String uuid) {
        return players.computeIfAbsent(uuid, StatsPlayer::new).getTotalPlaytime();
    }


}
