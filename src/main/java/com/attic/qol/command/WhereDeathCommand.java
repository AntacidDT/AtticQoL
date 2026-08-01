package com.attic.qol.command;

import com.attic.qol.data.DeathData;
import com.attic.qol.data.PlayerDataStorage;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class WhereDeathCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("wheredeath")
            .executes(ctx -> showLastDeath(ctx))
            .then(literal("-l")
                .then(argument("index", IntegerArgumentType.integer(1, 16))
                    .executes(ctx -> showSpecificDeath(ctx, IntegerArgumentType.getInteger(ctx, "index"))))
                .then(literal("all")
                    .executes(ctx -> showAllDeaths(ctx))))
        );
    }

    private static int showLastDeath(CommandContext<ServerCommandSource> ctx) {
        ServerCommandSource source = ctx.getSource();
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) {
            source.sendError(Text.literal("This command can only be used by players"));
            return 0;
        }

        PlayerDataStorage storage = PlayerDataStorage.getServerState(source.getServer());
        List<DeathData> deaths = storage.getDeaths(player.getUuid());

        if (deaths.isEmpty()) {
            source.sendError(Text.literal("No death records found.").formatted(Formatting.RED));
            return 0;
        }

        DeathData death = deaths.get(0);
        sendDeathInfo(source, death, 1);
        return 1;
    }

    private static int showSpecificDeath(CommandContext<ServerCommandSource> ctx, int index) {
        ServerCommandSource source = ctx.getSource();
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) {
            source.sendError(Text.literal("This command can only be used by players"));
            return 0;
        }

        PlayerDataStorage storage = PlayerDataStorage.getServerState(source.getServer());
        List<DeathData> deaths = storage.getDeaths(player.getUuid());

        if (deaths.isEmpty()) {
            source.sendError(Text.literal("No death records found.").formatted(Formatting.RED));
            return 0;
        }

        if (index > deaths.size()) {
            source.sendError(Text.literal("Only " + deaths.size() + " death(s) recorded.").formatted(Formatting.RED));
            return 0;
        }

        DeathData death = deaths.get(index - 1);
        sendDeathInfo(source, death, index);
        return 1;
    }

    private static int showAllDeaths(CommandContext<ServerCommandSource> ctx) {
        ServerCommandSource source = ctx.getSource();
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) {
            source.sendError(Text.literal("This command can only be used by players"));
            return 0;
        }

        PlayerDataStorage storage = PlayerDataStorage.getServerState(source.getServer());
        List<DeathData> deaths = storage.getDeaths(player.getUuid());

        if (deaths.isEmpty()) {
            source.sendError(Text.literal("No death records found.").formatted(Formatting.RED));
            return 0;
        }

        source.sendFeedback(() -> Text.literal("=== Death History (" + deaths.size() + ") ===").formatted(Formatting.GOLD), false);

        for (int i = 0; i < deaths.size(); i++) {
            DeathData death = deaths.get(i);
            final int num = i + 1;
            source.sendFeedback(() -> Text.literal("")
                .append(Text.literal("#" + num + " ").formatted(Formatting.YELLOW))
                .append(Text.literal(death.getX() + ", " + death.getY() + ", " + death.getZ())
                    .formatted(Formatting.WHITE))
                .append(Text.literal(" (" + death.getDimensionName() + ")").formatted(Formatting.GRAY)),
                false
            );
        }

        return 1;
    }

    private static void sendDeathInfo(ServerCommandSource source, DeathData death, int index) {
        source.sendFeedback(() -> Text.literal("=== Death #" + index + " ===").formatted(Formatting.GOLD), false);

        source.sendFeedback(() -> Text.literal("Location: ").formatted(Formatting.GRAY)
            .append(Text.literal(death.getX() + ", " + death.getY() + ", " + death.getZ())
                .formatted(Formatting.WHITE)), false);

        source.sendFeedback(() -> Text.literal("Dimension: ").formatted(Formatting.GRAY)
            .append(Text.literal(death.getDimensionName()).formatted(Formatting.AQUA)), false);

        source.sendFeedback(() -> Text.literal("Biome: ").formatted(Formatting.GRAY)
            .append(Text.literal(death.getBiome()).formatted(Formatting.GREEN)), false);

        source.sendFeedback(() -> Text.literal("Time: ").formatted(Formatting.GRAY)
            .append(Text.literal(death.getFormattedTime() + " in-game").formatted(Formatting.YELLOW)), false);

        source.sendFeedback(() -> Text.literal("Cause: ").formatted(Formatting.GRAY)
            .append(Text.literal(death.getCause()).formatted(Formatting.RED)), false);
    }
}
