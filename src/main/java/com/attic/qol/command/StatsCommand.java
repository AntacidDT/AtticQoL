package com.attic.qol.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import static net.minecraft.server.command.CommandManager.literal;

public class StatsCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("stats")
            .executes(ctx -> showStats(ctx.getSource()))
        );
    }

    private static int showStats(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) {
            source.sendError(Text.literal("This command can only be used by players"));
            return 0;
        }

        BlockPos pos = player.getBlockPos();
        float health = player.getHealth();
        float maxHealth = player.getMaxHealth();
        int hunger = player.getHungerManager().getFoodLevel();
        int armor = player.getArmor();
        int level = player.experienceLevel;

        String dimension = player.getWorld().getRegistryKey().getValue().toString();
        String dimensionName = switch (dimension) {
            case "minecraft:overworld" -> "Overworld";
            case "minecraft:the_nether" -> "Nether";
            case "minecraft:the_end" -> "End";
            default -> dimension;
        };

        Formatting healthColor = health > maxHealth * 0.5 ? Formatting.GREEN :
                                 health > maxHealth * 0.25 ? Formatting.YELLOW : Formatting.RED;
        Formatting hungerColor = hunger > 14 ? Formatting.GREEN :
                                 hunger > 6 ? Formatting.YELLOW : Formatting.RED;
        Formatting armorColor = armor > 10 ? Formatting.GREEN :
                                armor > 5 ? Formatting.YELLOW : Formatting.RED;

        source.sendFeedback(() -> Text.literal("=== Player Stats ===").formatted(Formatting.GOLD), false);
        source.sendFeedback(() -> Text.literal("Health: ").formatted(Formatting.GRAY)
            .append(Text.literal(String.format("%.1f", health) + "/" + String.format("%.1f", maxHealth)).formatted(healthColor)), false);
        source.sendFeedback(() -> Text.literal("Hunger: ").formatted(Formatting.GRAY)
            .append(Text.literal(hunger + "/20").formatted(hungerColor)), false);
        source.sendFeedback(() -> Text.literal("Armor: ").formatted(Formatting.GRAY)
            .append(Text.literal(String.valueOf(armor)).formatted(armorColor)), false);
        source.sendFeedback(() -> Text.literal("XP Level: ").formatted(Formatting.GRAY)
            .append(Text.literal(String.valueOf(level)).formatted(Formatting.AQUA)), false);
        source.sendFeedback(() -> Text.literal("Position: ").formatted(Formatting.GRAY)
            .append(Text.literal(pos.getX() + ", " + pos.getY() + ", " + pos.getZ()).formatted(Formatting.WHITE)), false);
        source.sendFeedback(() -> Text.literal("Dimension: ").formatted(Formatting.GRAY)
            .append(Text.literal(dimensionName).formatted(Formatting.GREEN)), false);

        return 1;
    }
}
