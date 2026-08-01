package com.attic.qol.command;

import com.attic.qol.data.PlayerDataStorage;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.minecraft.server.command.CommandManager.literal;

public class TimePlayedCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("timeplayed")
            .executes(ctx -> showTimePlayed(ctx.getSource()))
        );
    }

    private static int showTimePlayed(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) {
            source.sendError(Text.literal("This command can only be used by players"));
            return 0;
        }

        PlayerDataStorage storage = PlayerDataStorage.getServerState(source.getServer());
        long joinTime = storage.getJoinTime(player.getUuid());

        if (joinTime == 0) {
            storage.setJoinTime(player.getUuid(), player.getServerWorld().getTime());
            source.sendFeedback(() -> Text.literal("Session started. Play time will be tracked.")
                .formatted(Formatting.YELLOW), false);
            return 1;
        }

        long currentTime = player.getServerWorld().getTime();
        long ticksPlayed = currentTime - joinTime;

        long totalSeconds = ticksPlayed / 20;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        String timeStr;
        if (hours > 0) {
            timeStr = hours + "h " + minutes + "m " + seconds + "s";
        } else if (minutes > 0) {
            timeStr = minutes + "m " + seconds + "s";
        } else {
            timeStr = seconds + "s";
        }

        source.sendFeedback(() -> Text.literal("Session play time: ").formatted(Formatting.GRAY)
            .append(Text.literal(timeStr).formatted(Formatting.AQUA)), false);

        return 1;
    }
}
