package com.attic.qol.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.Biome;

import static net.minecraft.server.command.CommandManager.literal;

public class LocationCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("location")
            .executes(ctx -> showLocation(ctx.getSource()))
        );
    }

    private static int showLocation(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) {
            source.sendError(Text.literal("This command can only be used by players"));
            return 0;
        }

        BlockPos pos = player.getBlockPos();
        String dimensionKey = player.world.getRegistryKey().getValue().toString();
        String dimensionName = switch (dimensionKey) {
            case "minecraft:overworld" -> "Overworld";
            case "minecraft:the_nether" -> "The Nether";
            case "minecraft:the_end" -> "The End";
            default -> dimensionKey;
        };

        RegistryEntry<Biome> biomeEntry = player.world.getBiome(pos);
        String biomeName = biomeEntry.getKey()
            .map(key -> key.getValue().toString())
            .orElse("Unknown");

        source.sendFeedback(() -> Text.literal("=== Your Location ===").formatted(Formatting.GOLD), false);

        source.sendFeedback(() -> Text.literal("Coordinates: ").formatted(Formatting.GRAY)
            .append(Text.literal(pos.getX() + ", " + pos.getY() + ", " + pos.getZ())
                .formatted(Formatting.WHITE)), false);

        source.sendFeedback(() -> Text.literal("Dimension: ").formatted(Formatting.GRAY)
            .append(Text.literal(dimensionName).formatted(Formatting.AQUA)), false);

        source.sendFeedback(() -> Text.literal("Biome: ").formatted(Formatting.GRAY)
            .append(Text.literal(biomeName).formatted(Formatting.GREEN)), false);

        return 1;
    }
}
