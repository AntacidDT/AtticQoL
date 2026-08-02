package com.attic.qol.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.World;

import static net.minecraft.server.command.CommandManager.literal;

public class WeatherCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("weather")
            .executes(ctx -> showWeather(ctx.getSource()))
        );
    }

    private static int showWeather(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) {
            source.sendError(Text.literal("This command can only be used by players"));
            return 0;
        }

        World world = player.world;
        boolean isRaining = world.isRaining();
        boolean isThundering = world.isThundering();

        String weather;
        Formatting color;
        if (isThundering) {
            weather = "Thunderstorm";
            color = Formatting.RED;
        } else if (isRaining) {
            weather = "Rain";
            color = Formatting.BLUE;
        } else {
            weather = "Clear";
            color = Formatting.YELLOW;
        }

        long timeOfDay = world.getTimeOfDay() % 24000;
        boolean isDay = timeOfDay < 12000;

        source.sendFeedback(() -> Text.literal("=== Weather ===").formatted(Formatting.GOLD), false);
        source.sendFeedback(() -> Text.literal("Weather: ").formatted(Formatting.GRAY)
            .append(Text.literal(weather).formatted(color)), false);
        source.sendFeedback(() -> Text.literal("Time: ").formatted(Formatting.GRAY)
            .append(Text.literal(isDay ? "Day" : "Night").formatted(isDay ? Formatting.YELLOW : Formatting.BLUE)), false);
        source.sendFeedback(() -> Text.literal("Time Ticks: ").formatted(Formatting.GRAY)
            .append(Text.literal(String.valueOf(timeOfDay)).formatted(Formatting.WHITE)), false);

        return 1;
    }
}
