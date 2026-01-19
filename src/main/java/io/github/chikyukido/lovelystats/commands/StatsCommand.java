package io.github.chikyukido.lovelystats.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.chikyukido.lovelystats.stats.StatsHandler;

import javax.annotation.Nonnull;

public class StatsCommand extends AbstractPlayerCommand {

    public StatsCommand() {
        super("stats", "overiew of your statistics");
    }

    RequiredArg<String> typeArg = this.withRequiredArg("playtime","See your total playtime", ArgTypes.STRING);

    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        String type = typeArg.get(commandContext);
        if (type.equals("playtime")) {
            long playtime = StatsHandler.get().getTotalPlaytime(playerRef.getUuid().toString());
            playerRef.sendMessage(Message.raw("Your total playtime is: " + playtime + " seconds"));
        } else {
            playerRef.sendMessage(Message.raw("Unknown type: " + type));
        }
    }
}
