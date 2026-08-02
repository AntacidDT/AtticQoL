package com.attic.qol.command;

import com.attic.qol.data.MarkerData;
import com.attic.qol.data.PlayerDataStorage;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class MarkerCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("marker")
            .then(literal("add")
                .then(argument("name", StringArgumentType.word())
                    .executes(ctx -> addMarker(ctx, StringArgumentType.getString(ctx, "name")))))
            .then(literal("remove")
                .then(argument("name", StringArgumentType.word())
                    .executes(ctx -> removeMarker(ctx, StringArgumentType.getString(ctx, "name")))))
            .then(literal("list")
                .executes(ctx -> listMarkers(ctx)))
        );
    }

    private static int addMarker(CommandContext<ServerCommandSource> ctx, String name) {
        ServerCommandSource source = ctx.getSource();
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) {
            source.sendError(Text.literal("This command can only be used by players"));
            return 0;
        }

        BlockPos pos = player.getBlockPos();
        String dimensionKey = player.world.getRegistryKey().getValue().toString();

        MarkerData marker = new MarkerData(name, pos.getX(), pos.getY(), pos.getZ(), dimensionKey);

        PlayerDataStorage storage = PlayerDataStorage.getServerState(source.getServer());
        storage.addMarker(player.getUuid(), marker);

        source.sendFeedback(() -> Text.literal("Marker '" + name + "' set at ")
            .formatted(Formatting.GREEN)
            .append(Text.literal(pos.getX() + ", " + pos.getY() + ", " + pos.getZ())
                .formatted(Formatting.WHITE)), false);

        return 1;
    }

    private static int removeMarker(CommandContext<ServerCommandSource> ctx, String name) {
        ServerCommandSource source = ctx.getSource();
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) {
            source.sendError(Text.literal("This command can only be used by players"));
            return 0;
        }

        PlayerDataStorage storage = PlayerDataStorage.getServerState(source.getServer());
        boolean removed = storage.removeMarker(player.getUuid(), name);

        if (removed) {
            source.sendFeedback(() -> Text.literal("Marker '" + name + "' removed.")
                .formatted(Formatting.YELLOW), false);
        } else {
            source.sendError(Text.literal("Marker '" + name + "' not found.").formatted(Formatting.RED));
        }

        return removed ? 1 : 0;
    }

    private static int listMarkers(CommandContext<ServerCommandSource> ctx) {
        ServerCommandSource source = ctx.getSource();
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) {
            source.sendError(Text.literal("This command can only be used by players"));
            return 0;
        }

        PlayerDataStorage storage = PlayerDataStorage.getServerState(source.getServer());
        List<MarkerData> markers = storage.getMarkers(player.getUuid());

        if (markers.isEmpty()) {
            source.sendError(Text.literal("No markers set. Use /marker add <name> to create one.")
                .formatted(Formatting.RED));
            return 0;
        }

        source.sendFeedback(() -> Text.literal("=== Markers (" + markers.size() + ") ===")
            .formatted(Formatting.GOLD), false);

        for (MarkerData marker : markers) {
            source.sendFeedback(() -> Text.literal("")
                .append(Text.literal(marker.getName()).formatted(Formatting.YELLOW))
                .append(Text.literal(" - ").formatted(Formatting.GRAY))
                .append(Text.literal(marker.getX() + ", " + marker.getY() + ", " + marker.getZ())
                    .formatted(Formatting.WHITE))
                .append(Text.literal(" (" + marker.getDimensionName() + ")").formatted(Formatting.AQUA)),
                false
            );
        }

        return 1;
    }
}
