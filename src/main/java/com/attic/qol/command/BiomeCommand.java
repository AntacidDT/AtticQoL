package com.attic.qol.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

import static net.minecraft.server.command.CommandManager.literal;

public class BiomeCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("biome")
            .executes(ctx -> showBiome(ctx.getSource()))
        );
    }

    private static int showBiome(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) {
            source.sendError(Text.literal("This command can only be used by players"));
            return 0;
        }

        BlockPos pos = player.getBlockPos();
        RegistryEntry<Biome> biomeEntry = player.world.getBiome(pos);
        Biome biome = biomeEntry.value();

        String biomeName = biomeEntry.getKey()
            .map(key -> key.getValue().toString())
            .orElse("Unknown");

        float temperature = biome.getTemperature();
        boolean hasPrecipitation = biome.hasPrecipitation();
        boolean isCold = temperature < 0.15f;

        String tempCategory;
        Formatting tempColor;
        if (temperature >= 1.5f) {
            tempCategory = "Hot";
            tempColor = Formatting.RED;
        } else if (temperature >= 0.5f) {
            tempCategory = "Warm";
            tempColor = Formatting.YELLOW;
        } else if (temperature >= 0.15f) {
            tempCategory = "Temperate";
            tempColor = Formatting.GREEN;
        } else {
            tempCategory = "Cold";
            tempColor = Formatting.AQUA;
        }

        String precipitation;
        if (!hasPrecipitation) {
            precipitation = "None";
        } else if (isCold) {
            precipitation = "Snow";
        } else {
            precipitation = "Rain";
        }

        source.sendFeedback(() -> Text.literal("=== Biome Info ===").formatted(Formatting.GOLD), false);
        source.sendFeedback(() -> Text.literal("Biome: ").formatted(Formatting.GRAY)
            .append(Text.literal(biomeName).formatted(Formatting.GREEN)), false);
        source.sendFeedback(() -> Text.literal("Temperature: ").formatted(Formatting.GRAY)
            .append(Text.literal(String.format("%.2f", temperature) + " (" + tempCategory + ")").formatted(tempColor)), false);
        source.sendFeedback(() -> Text.literal("Precipitation: ").formatted(Formatting.GRAY)
            .append(Text.literal(precipitation).formatted(Formatting.AQUA)), false);

        return 1;
    }
}
