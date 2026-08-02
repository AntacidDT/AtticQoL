package com.attic.qol.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.minecraft.server.command.CommandManager.literal;

public class WStatsCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("wstats")
            .executes(ctx -> showStats(ctx.getSource()))
        );
        dispatcher.register(literal("ping")
            .executes(ctx -> showStats(ctx.getSource()))
        );
    }

    private static int showStats(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) {
            source.sendError(Text.literal("This command can only be used by players"));
            return 0;
        }

        int ping = player.networkHandler.getLatency();
        ServerWorld serverWorld = (ServerWorld) player.world;
        boolean isSingleplayer = serverWorld.getServer().isSingleplayer();

        String connectionType = isSingleplayer ? "Singleplayer" : "Multiplayer";

        final String serverAddress;
        if (isSingleplayer) {
            serverAddress = "Local";
        } else {
            String ip = serverWorld.getServer().getServerIp();
            if (ip == null || ip.isEmpty()) {
                ip = "Unknown";
            }
            int port = serverWorld.getServer().getServerPort();
            serverAddress = port != 25565 ? ip + ":" + port : ip;
        }

        String dimension = player.world.getRegistryKey().getValue().toString();
        String dimensionName = switch (dimension) {
            case "minecraft:overworld" -> "Overworld";
            case "minecraft:the_nether" -> "The Nether";
            case "minecraft:the_end" -> "The End";
            default -> dimension;
        };

        String gameMode = player.interactionManager.getGameMode().getId();

        Formatting pingColor;
        String quality;
        if (ping < 30) {
            pingColor = Formatting.GREEN;
            quality = "Excellent";
        } else if (ping < 60) {
            pingColor = Formatting.GREEN;
            quality = "Good";
        } else if (ping < 100) {
            pingColor = Formatting.YELLOW;
            quality = "Fair";
        } else if (ping < 200) {
            pingColor = Formatting.GOLD;
            quality = "Poor";
        } else {
            pingColor = Formatting.RED;
            quality = "Bad";
        }

        source.sendFeedback(() -> Text.literal("=== Network Stats ===").formatted(Formatting.GOLD), false);

        source.sendFeedback(() -> Text.literal("Ping: ").formatted(Formatting.GRAY)
            .append(Text.literal(ping + "ms").formatted(pingColor))
            .append(Text.literal(" (" + quality + ")").formatted(pingColor)), false);

        source.sendFeedback(() -> Text.literal("Connection: ").formatted(Formatting.GRAY)
            .append(Text.literal(connectionType).formatted(Formatting.AQUA)), false);

        source.sendFeedback(() -> Text.literal("Server: ").formatted(Formatting.GRAY)
            .append(Text.literal(serverAddress).formatted(Formatting.WHITE)), false);

        source.sendFeedback(() -> Text.literal("Dimension: ").formatted(Formatting.GRAY)
            .append(Text.literal(dimensionName).formatted(Formatting.GREEN)), false);

        source.sendFeedback(() -> Text.literal("Game Mode: ").formatted(Formatting.GRAY)
            .append(Text.literal(capitalize(gameMode)).formatted(Formatting.YELLOW)), false);

        return 1;
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
