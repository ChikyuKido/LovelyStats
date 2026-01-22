package io.github.chikyukido.lovelystats.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.chikyukido.lovelystats.pages.leaderboard.LeaderboardPage;

import javax.annotation.Nonnull;

public class LeaderboardCommand extends AbstractPlayerCommand {
    public LeaderboardCommand() {
        super("leaderboardStats","Show the stats leaderboard");
    }

    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        long start = System.currentTimeMillis();
        Player player = commandContext.senderAs(Player.class);
        player.getPageManager().openCustomPage(ref,store,new LeaderboardPage(playerRef));
        long end = System.currentTimeMillis();
        LOGGER.atInfo().log("Opened Stats page in %dms", end-start);
    }
}
