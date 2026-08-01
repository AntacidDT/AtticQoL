package com.attic.qol.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.minecraft.server.command.CommandManager.literal;

public class ExpCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("exp")
            .executes(ctx -> showExp(ctx.getSource()))
        );
    }

    private static int showExp(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) {
            source.sendError(Text.literal("This command can only be used by players"));
            return 0;
        }

        int level = player.experienceLevel;
        int totalXp = player.totalExperience;
        float progress = player.experienceProgress;
        final int xpToNext = Math.max(0, getXpForNextLevel(level) - (int)(getXpForNextLevel(level) * progress));

        Formatting color;
        if (level >= 30) {
            color = Formatting.GREEN;
        } else if (level >= 15) {
            color = Formatting.YELLOW;
        } else {
            color = Formatting.RED;
        }

        source.sendFeedback(() -> Text.literal("=== Experience ===").formatted(Formatting.GOLD), false);
        source.sendFeedback(() -> Text.literal("Level: ").formatted(Formatting.GRAY)
            .append(Text.literal(String.valueOf(level)).formatted(color)), false);
        source.sendFeedback(() -> Text.literal("Total XP: ").formatted(Formatting.GRAY)
            .append(Text.literal(String.valueOf(totalXp)).formatted(Formatting.WHITE)), false);
        source.sendFeedback(() -> Text.literal("Progress: ").formatted(Formatting.GRAY)
            .append(Text.literal(String.format("%.0f%%", progress * 100)).formatted(Formatting.AQUA)), false);
        source.sendFeedback(() -> Text.literal("To Next Level: ").formatted(Formatting.GRAY)
            .append(Text.literal(xpToNext + " XP").formatted(Formatting.YELLOW)), false);

        return 1;
    }

    private static int getXpForNextLevel(int level) {
        if (level >= 30) {
            return 112 + (level - 30) * 9;
        } else if (level >= 15) {
            return 37 + (level - 15) * 5;
        } else {
            return 7 + level * 2;
        }
    }
}
