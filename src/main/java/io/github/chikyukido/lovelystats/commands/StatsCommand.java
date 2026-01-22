package io.github.chikyukido.lovelystats.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.chikyukido.lovelystats.handler.RecordedPlayerHandler;
import io.github.chikyukido.lovelystats.pages.stats.StatsPage;

import javax.annotation.Nonnull;
import java.util.UUID;

public class StatsCommand extends AbstractPlayerCommand {

    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    public StatsCommand() {
        super("stats", "overiew of your statistics");
    }

    OptionalArg<UUID> playerArg = this.withOptionalArg("player","The player to display the stats for", ArgTypes.PLAYER_UUID);

    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        long start = System.currentTimeMillis();
        UUID playerUUID = playerArg.get(commandContext);
        if(playerUUID == null) {
            playerUUID = playerRef.getUuid();
        }
        if(RecordedPlayerHandler.get().getUsername(playerUUID) == null) {
            playerRef.sendMessage(Message.raw("This UUID is not recorded in the database. "));
        }else {
            Player player = commandContext.senderAs(Player.class);
            player.getPageManager().openCustomPage(ref, store, new StatsPage(playerRef, playerUUID));
        }
        long end = System.currentTimeMillis();
        LOGGER.atInfo().log("Opened Stats page in %dms", end-start);
    }
}
