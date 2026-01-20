package io.github.chikyukido.lovelystats.types;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlaytimeStats {
    private final UUID uuid;
    private final List<PlaytimeSession> sessions = new ArrayList<>();
    private PlaytimeSession currentSession = null;

    public PlaytimeStats(UUID uuid) {
        this.uuid = uuid;
    }
    public void startPlaytimeSession () {
        currentSession = new PlaytimeSession();
    }
    public void endPlaytimeSession () {
        if(currentSession == null) return;
        currentSession.stopSession();
        sessions.add(currentSession);
        currentSession = null;
    }
    public void increaseActivePlaytime(long playtime) {
        if(currentSession == null) {
            startPlaytimeSession();
        }
        currentSession.increaseActiveTime(playtime);
    }
    public void increaseIdlePlaytime(long playtime) {
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
