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

import javax.annotation.Nonnull;

public class StatsCommand extends AbstractPlayerCommand {

    RequiredArg<String> typeArg = this.withRequiredArg("type", "The type of statistic you want to see", ArgTypes.STRING);

    public StatsCommand() {
        super("stats", "overiew of your statistics");
    }

    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        String type = typeArg.get(commandContext);
        if (type.equals("playtime")) {
            PlaytimeCommand.run(playerRef);
        } else if (type.equals("block")) {
            BlockCommand.run(playerRef);
        } else if (type.equals("item")) {
            ItemCommand.run(playerRef);
        } else if (type.equals("player")) {
            PlayerCommand.run(playerRef);
        } else {
            playerRef.sendMessage(Message.raw("Unknown type: " + type));
        }
    }
}
