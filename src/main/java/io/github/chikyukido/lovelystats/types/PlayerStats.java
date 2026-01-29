package io.github.chikyukido.lovelystats.types;

import java.util.UUID;

public class PlayerStats extends SaveStat {
    private final UUID uuid;
    private double distanceWalked;
    private double distanceRun;
    private double distanceSwam;
    private double distanceFallen;
    private double distanceClimbed;
    private double distanceSneaked;
    private double elevationUp;
    private double elevationDown;
    private long chatMessages;
    private long deaths;
    private long jumps;

    // PvP
    private long playerDeaths;
    private long playerKills;
    private double playerDamageDealt;
    private double playerDamageReceived;

    public PlayerStats(UUID uuid, double distanceWalked, double distanceRun, double distanceSwam, double distanceFallen, double distanceClimbed, double distanceSneaked, double elevationUp, double elevationDown, long chatMessages, long deaths, long jumps, long playerDeaths, long playerKills, double playerDamageDealt, double playerDamageReceived) {
        this.uuid = uuid;
        this.distanceWalked = distanceWalked;
        this.distanceRun = distanceRun;
        this.distanceSwam = distanceSwam;
        this.distanceFallen = distanceFallen;
        this.distanceClimbed = distanceClimbed;
        this.distanceSneaked = distanceSneaked;
        this.elevationUp = elevationUp;
        this.elevationDown = elevationDown;
        this.chatMessages = chatMessages;
        this.deaths = deaths;
        this.jumps = jumps;
        this.playerDeaths = playerDeaths;
        this.playerKills = playerKills;
        this.playerDamageDealt = playerDamageDealt;
        this.playerDamageReceived = playerDamageReceived;
    }

    public PlayerStats(UUID uuid) {
        this.uuid = uuid;
    }

    public void addDistanceWalked(double amount) {
        distanceWalked += amount;
    }

    public void addDistanceRun(double amount) {
        distanceRun += amount;
    }

    public void addDistanceSwam(double amount) {
        distanceSwam += amount;
    }

    public void addDistanceFallen(double amount) {
        distanceFallen += amount;
    }

    public void addDistanceClimbed(double amount) {
        distanceClimbed += amount;
    }

    public void addDistanceSneaked(double amount) {
        distanceSneaked += amount;
    }

    public void addElevationUp(double amount) {
        elevationUp += amount;
    }

    public void addElevationDown(double amount) {
        elevationDown += amount;
    }

    public void incrementChatMessages() {
        chatMessages++;
    }

    public void incrementDeaths() {
        deaths++;
    }

    public void incrementJumps() {
        jumps++;
    }

    public void incrementPlayerDeaths() {
        playerDeaths++;
    }

    public void incrementPlayerKills() {
        playerKills++;
    }

    public void addPlayerDamageDealt(double amount) {
        playerDamageDealt += amount;
    }

    public void addPlayerDamageReceived(double amount) {
        playerDamageReceived += amount;
    }

    public UUID getUuid() {
        return uuid;
    }

    public double getDistanceWalked() {
        return distanceWalked;
    }

    public double getDistanceRun() {
        return distanceRun;
    }

    public double getDistanceSwam() {
        return distanceSwam;
    }

    public double getDistanceFallen() {
        return distanceFallen;
    }

    public double getDistanceClimbed() {
        return distanceClimbed;
    }

    public double getDistanceSneaked() {
        return distanceSneaked;
    }

    public double getElevationUp() {
        return elevationUp;
    }

    public double getElevationDown() {
        return elevationDown;
    }

    public long getJumps() {
        return jumps;
    }

    public long getChatMessages() {
        return chatMessages;
    }

    public long getDeaths() {
        return deaths;
    }

    public long getPlayerDeaths() {
        return playerDeaths;
    }

    public long getPlayerKills() {
        return playerKills;
    }

    public double getPlayerDamageDealt() {
        return playerDamageDealt;
    }

    public double getPlayerDamageReceived() {
        return playerDamageReceived;
    }
}
