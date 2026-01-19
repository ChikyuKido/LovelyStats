package io.github.chikyukido.lovelystats.stats;

import io.github.chikyukido.lovelystats.types.PlaytimeSession;

import java.util.ArrayList;
import java.util.List;

public class StatsPlayer {
    private String uuid;
    private List<PlaytimeSession> sessions = new ArrayList<>();
    private PlaytimeSession currentSession = null;

    public StatsPlayer(String uuid) {
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
    void increasePlaytime(double playtime) {
        if(currentSession == null) {
            startPlaytimeSession();
        }
        currentSession.increasePlaytime(playtime);
    }
    long getTotalPlaytime() {
        return (long) currentSession.getTotalPlaytime();
    }

    public String getUuid() {
        return uuid;
    }

    public List<PlaytimeSession> getSessions() {
        return sessions;
    }

    public PlaytimeSession getCurrentSession() {
        return currentSession;
    }
}
