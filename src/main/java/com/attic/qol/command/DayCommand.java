package com.attic.qol.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.minecraft.server.command.CommandManager.literal;

public class DayCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("day")
            .executes(ctx -> showDay(ctx.getSource()))
        );
    }

    private static int showDay(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) {
            source.sendError(Text.literal("This command can only be used by players"));
            return 0;
        }

        long time = player.getWorld().getTime();
        long day = time / 24000;
        long timeOfDay = time % 24000;

        long hours = (timeOfDay + 6000) % 24000 / 1000;
        long minutes = (timeOfDay + 6000) % 24000 % 1000 * 60 / 1000;

        boolean isDay = timeOfDay < 12000;
        String phase = isDay ? "Day" : "Night";

        source.sendFeedback(() -> Text.literal("=== Day Info ===").formatted(Formatting.GOLD), false);
        source.sendFeedback(() -> Text.literal("Day: ").formatted(Formatting.GRAY)
            .append(Text.literal(String.valueOf(day)).formatted(Formatting.WHITE)), false);
        source.sendFeedback(() -> Text.literal("Time: ").formatted(Formatting.GRAY)
            .append(Text.literal(String.format("%02d:%02d", hours, minutes)).formatted(Formatting.AQUA)), false);
        source.sendFeedback(() -> Text.literal("Phase: ").formatted(Formatting.GRAY)
            .append(Text.literal(phase).formatted(isDay ? Formatting.YELLOW : Formatting.BLUE)), false);

        return 1;
    }
}
