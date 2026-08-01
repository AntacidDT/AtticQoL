package com.attic.qol.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.minecraft.server.command.CommandManager.literal;

public class AtticQolCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("atticqol")
            .executes(ctx -> showHelp(ctx.getSource()))
        );
    }

    private static int showHelp(ServerCommandSource source) {
        source.sendFeedback(() -> Text.literal(""), false);
        source.sendFeedback(() -> Text.literal("=== Attic QoL ===").formatted(Formatting.GOLD), false);
        source.sendFeedback(() -> Text.literal("Quality of life commands").formatted(Formatting.GRAY), false);
        source.sendFeedback(() -> Text.literal(""), false);

        source.sendFeedback(() -> Text.literal("--- Death & Location ---").formatted(Formatting.YELLOW), false);
        source.sendFeedback(() -> cmd("/wheredeath", "Show last death info (coords, biome, cause)"), false);
        source.sendFeedback(() -> cmd("/wheredeath -l [1-16]", "Show specific previous death"), false);
        source.sendFeedback(() -> cmd("/wheredeath -l all", "Show all 16 deaths (coords only)"), false);
        source.sendFeedback(() -> cmd("/location", "Show current coords, biome, dimension"), false);
        source.sendFeedback(() -> cmd("/home", "Show world spawn point"), false);
        source.sendFeedback(() -> cmd("/depth", "Show depth relative to sea level"), false);
        source.sendFeedback(() -> Text.literal(""), false);

        source.sendFeedback(() -> Text.literal("--- Markers ---").formatted(Formatting.YELLOW), false);
        source.sendFeedback(() -> cmd("/marker add <name>", "Save current position"), false);
        source.sendFeedback(() -> cmd("/marker remove <name>", "Remove a saved marker"), false);
        source.sendFeedback(() -> cmd("/marker list", "Show all saved markers"), false);
        source.sendFeedback(() -> Text.literal(""), false);

        source.sendFeedback(() -> Text.literal("--- World Info ---").formatted(Formatting.YELLOW), false);
        source.sendFeedback(() -> cmd("/lightlevel", "Show light level at your position"), false);
        source.sendFeedback(() -> cmd("/chunkinfo", "Show chunk coordinates and region"), false);
        source.sendFeedback(() -> cmd("/weather", "Show current weather info"), false);
        source.sendFeedback(() -> cmd("/day", "Show current in-game day"), false);
        source.sendFeedback(() -> Text.literal(""), false);

        source.sendFeedback(() -> Text.literal("--- Player Stats ---").formatted(Formatting.YELLOW), false);
        source.sendFeedback(() -> cmd("/wstats", "Show network stats (ping, connection)"), false);
        source.sendFeedback(() -> cmd("/compass", "Show facing direction"), false);
        source.sendFeedback(() -> cmd("/timeplayed", "Show session play time"), false);
        source.sendFeedback(() -> Text.literal(""), false);

        return 1;
    }

    private static Text cmd(String name, String desc) {
        return Text.literal(name + " ").formatted(Formatting.AQUA)
            .append(Text.literal("- " + desc).formatted(Formatting.GRAY));
    }
}
