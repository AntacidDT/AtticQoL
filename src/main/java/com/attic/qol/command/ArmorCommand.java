package com.attic.qol.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.minecraft.server.command.CommandManager.literal;

public class ArmorCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("armor")
            .executes(ctx -> showArmor(ctx.getSource()))
        );
    }

    private static int showArmor(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) {
            source.sendError(Text.literal("This command can only be used by players"));
            return 0;
        }

        source.sendFeedback(() -> Text.literal("=== Armor Durability ===").formatted(Formatting.GOLD), false);

        String[] slotNames = {"Helmet", "Chestplate", "Leggings", "Boots"};
        EquipmentSlot[] slots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

        boolean hasArmor = false;
        for (int i = 0; i < 4; i++) {
            ItemStack stack = player.getEquippedStack(slots[i]);
            if (!stack.isEmpty()) {
                hasArmor = true;
                int maxDurability = stack.getMaxDamage();
                int currentDurability = maxDurability - stack.getDamage();
                int percent = maxDurability > 0 ? (currentDurability * 100) / maxDurability : 0;

                Formatting color;
                if (percent > 50) {
                    color = Formatting.GREEN;
                } else if (percent > 25) {
                    color = Formatting.YELLOW;
                } else {
                    color = Formatting.RED;
                }

                final String name = slotNames[i];
                final int cur = currentDurability;
                final int max = maxDurability;
                final int pct = percent;
                final Formatting c = color;

                source.sendFeedback(() -> Text.literal(name + ": ").formatted(Formatting.GRAY)
                    .append(Text.literal(cur + "/" + max + " (" + pct + "%)").formatted(c)), false);
            } else {
                final String name = slotNames[i];
                source.sendFeedback(() -> Text.literal(name + ": ").formatted(Formatting.GRAY)
                    .append(Text.literal("Empty").formatted(Formatting.DARK_GRAY)), false);
            }
        }

        if (!hasArmor) {
            source.sendFeedback(() -> Text.literal("No armor equipped!").formatted(Formatting.RED), false);
        }

        return 1;
    }
}
