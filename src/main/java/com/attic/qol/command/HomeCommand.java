package com.attic.qol.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import static net.minecraft.server.command.CommandManager.literal;

public class HomeCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("home")
            .executes(ctx -> showHome(ctx.getSource()))
        );
    }

    private static int showHome(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) {
            source.sendError(Text.literal("This command can only be used by players"));
            return 0;
        }

        BlockPos spawnPos = player.getSpawnPointPosition();
        String dimension = player.getSpawnPointDimension().getValue().toString();
        String dimensionName = switch (dimension) {
            case "minecraft:overworld" -> "Overworld";
            case "minecraft:the_nether" -> "The Nether";
            case "minecraft:the_end" -> "The End";
            default -> dimension;
        };

        if (spawnPos == null) {
            source.sendFeedback(() -> Text.literal("No spawn point set.").formatted(Formatting.YELLOW), false);
            return 1;
        }

        source.sendFeedback(() -> Text.literal("=== Spawn Point ===").formatted(Formatting.GOLD), false);
        source.sendFeedback(() -> Text.literal("Location: ").formatted(Formatting.GRAY)
            .append(Text.literal(spawnPos.getX() + ", " + spawnPos.getY() + ", " + spawnPos.getZ())
                .formatted(Formatting.WHITE)), false);
        source.sendFeedback(() -> Text.literal("Dimension: ").formatted(Formatting.GRAY)
            .append(Text.literal(dimensionName).formatted(Formatting.AQUA)), false);

        return 1;
    }
}
