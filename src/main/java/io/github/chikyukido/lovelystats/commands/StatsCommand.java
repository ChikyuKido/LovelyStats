package io.github.chikyukido.lovelystats.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.chikyukido.lovelystats.pages.StatsPage;

import javax.annotation.Nonnull;

public class StatsCommand extends AbstractPlayerCommand {

    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    public StatsCommand() {
        super("stats", "overiew of your statistics");
    }

    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        long start = System.currentTimeMillis();
        Player player = commandContext.senderAs(Player.class);
        player.getPageManager().openCustomPage(ref,store,new StatsPage(playerRef));
        long end = System.currentTimeMillis();
        LOGGER.atInfo().log("Opened Stats page in %dms", end-start);
    }
}
