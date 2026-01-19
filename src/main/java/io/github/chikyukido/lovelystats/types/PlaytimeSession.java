package io.github.chikyukido.lovelystats.types;

public class PlaytimeSession {
    private long startTime;
    private long stopTime;
    private double playtime;

    public PlaytimeSession(long startTime, long stopTime, double playtime) {
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.playtime = playtime;
    }
    public PlaytimeSession() {
        this.startTime = System.currentTimeMillis()/1000;
        stopTime = -1;
    }
    public void stopSession() {
        stopTime = System.currentTimeMillis()/1000;
    }
    public void stopSession(long stopTime) {
        this.stopTime = stopTime;
    }
    public void increasePlaytime(double playtime) {
        this.playtime += playtime;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getStopTime() {
        return stopTime;
    }
    public double getTotalPlaytime() {
        return playtime;
    }
}
