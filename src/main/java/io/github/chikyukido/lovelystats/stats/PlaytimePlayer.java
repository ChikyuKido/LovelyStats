package io.github.chikyukido.lovelystats.stats;

import io.github.chikyukido.lovelystats.types.PlaytimeSession;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlaytimePlayer {
    private final UUID uuid;
    private final List<PlaytimeSession> sessions = new ArrayList<>();
    private PlaytimeSession currentSession = null;

    public PlaytimePlayer(UUID uuid) {
        this.uuid = uuid;
    }
    void startPlaytimeSession () {
        currentSession = new PlaytimeSession();
    }
    void endPlaytimeSession () {
        if(currentSession == null) return;
        currentSession.stopSession();
        sessions.add(currentSession);
        currentSession = null;
    }
    void increaseActivePlaytime(long playtime) {
        if(currentSession == null) {
            startPlaytimeSession();
        }
        currentSession.increaseActiveTime(playtime);
    }
    void increaseIdlePlaytime(long playtime) {
        if(currentSession == null) {
            startPlaytimeSession();
        }
        currentSession.increaseIdleTime(playtime);
    }
    public UUID getUuid() {
        return uuid;
    }

    public List<PlaytimeSession> getSessions() {
        return sessions;
    }

    public PlaytimeSession getCurrentSession() {
        return currentSession;
    }
}
