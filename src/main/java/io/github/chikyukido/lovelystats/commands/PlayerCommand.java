package io.github.chikyukido.lovelystats.commands;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import fi.sulku.hytale.TinyMsg;
import io.github.chikyukido.lovelystats.handler.PlayerStatsHandler;
import io.github.chikyukido.lovelystats.types.PlayerStats;

public class PlayerCommand {

    public static void run(PlayerRef playerRef) {
        PlayerStats stats = PlayerStatsHandler.get().getPlayerStats(playerRef.getUuid());

        String msg = "<gold><bold>Player Statistics</bold></gold>\n" +
                "<white>Distance Walked:</white> <green>" + stats.getDistanceWalked() + "</green>\n" +
                "<white>Distance Run:</white> <green>" + stats.getDistanceRun() + "</green>\n" +
                "<white>Distance Swam:</white> <green>" + stats.getDistanceSwam() + "</green>\n" +
                "<white>Distance Fallen:</white> <green>" + stats.getDistanceFallen() + "</green>\n" +
                "<white>Distance Climbed:</white> <green>" + stats.getDistanceClimbed() + "</green>\n" +
                "<white>Distance Sneaked:</white> <green>" + stats.getDistanceSneaked() + "</green>\n" +
                "<white>Elevation Up:</white> <green>" + stats.getElevationUp() + "</green>\n" +
                "<white>Elevation Down:</white> <green>" + stats.getElevationDown() + "</green>\n" +
                "<white>Jumps:</white> <green>" + stats.getJumps() + "</green>\n" +
                "<white>Chat Messages:</white> <green>" + stats.getChatMessages() + "</green>\n" +
                "<white>Deaths:</white> <red>" + stats.getDeaths() + "</red>\n";

        playerRef.sendMessage(TinyMsg.parse(msg));
    }
}
