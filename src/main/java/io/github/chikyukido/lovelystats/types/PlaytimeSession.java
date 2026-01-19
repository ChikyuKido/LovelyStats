package io.github.chikyukido.lovelystats.types;

public class PlaytimeSession {
    private long startTime;
    private long stopTime;
    private long activeTime;
    private long idleTime;

    public PlaytimeSession(long startTime, long stopTime, long activeTime,long idleTime) {
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.activeTime = activeTime;
        this.idleTime = idleTime;
    }
    public PlaytimeSession() {
        this.startTime = System.currentTimeMillis()/1000;
        stopTime = -1;
    }
    public void stopSession() {
        stopTime = System.currentTimeMillis()/1000;
    }
    public void increaseActiveTime(long playtime) {
        this.activeTime += playtime;
    }
    public void increaseIdleTime(long playtime) {
        this.idleTime += playtime;
    }

    public long getStartTime() {
        return startTime;
    }
    public long getStopTime() {
        return stopTime;
    }

    public long getActiveTime() {
        return activeTime;
    }

    public long getIdleTime() {
        return idleTime;
    }
}
