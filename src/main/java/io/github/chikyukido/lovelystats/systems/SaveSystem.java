package io.github.chikyukido.lovelystats.systems;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.HytaleServer;
import io.github.chikyukido.lovelystats.handler.ItemPlayerHandler;
import io.github.chikyukido.lovelystats.handler.PlaytimePlayerHandler;
import java.util.concurrent.TimeUnit;

public class SaveSystem {
    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    public static void run() {
        HytaleServer.SCHEDULED_EXECUTOR.scheduleAtFixedRate(() -> {
            long startTime = System.nanoTime(); // start timing
            PlaytimePlayerHandler.get().saveAllPlayers();
            ItemPlayerHandler.get().saveAllPlayers();
            long duration = System.nanoTime() - startTime;
            LOGGER.atInfo().log("Saved all player data in %s", formatDuration(duration));
        }, 1, 1, TimeUnit.MINUTES);
    }

    private static String formatDuration(long nanos) {
        long ms = nanos / 1_000_000;
        long us = (nanos / 1_000) % 1_000;
        long ns = nanos % 1_000;
        return String.format("%dms %dµs %dns", ms, us, ns);
    }
}
