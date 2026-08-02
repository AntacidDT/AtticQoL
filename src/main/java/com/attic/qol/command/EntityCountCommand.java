package com.attic.qol.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.TypeFilter;

import java.util.HashMap;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.literal;

public class EntityCountCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("entitycount")
            .executes(ctx -> showEntityCount(ctx.getSource()))
        );
    }

    private static int showEntityCount(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) {
            source.sendError(Text.literal("This command can only be used by players"));
            return 0;
        }

        ServerWorld serverWorld = (ServerWorld) player.world;
        Map<String, Integer> counts = new HashMap<>();
        int total = 0;

        for (Entity entity : serverWorld.getEntitiesByType(TypeFilter.instanceOf(Entity.class), e -> true)) {
            if (entity.getUuid().equals(player.getUuid())) continue;

            String name = entity.getType().getName().getString();
            counts.merge(name, 1, Integer::sum);
            total++;
        }

        final int finalTotal = total;
        source.sendFeedback(() -> Text.literal("=== Entity Count (" + finalTotal + ") ===").formatted(Formatting.GOLD), false);

        counts.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(15)
            .forEach(entry -> {
                final String name = entry.getKey();
                final int count = entry.getValue();
                source.sendFeedback(() -> Text.literal(name + ": ").formatted(Formatting.GRAY)
                    .append(Text.literal(String.valueOf(count)).formatted(Formatting.WHITE)), false);
            });

        if (counts.size() > 15) {
            final int more = counts.size() - 15;
            source.sendFeedback(() -> Text.literal("... and " + more + " more types")
                .formatted(Formatting.DARK_GRAY), false);
        }

        return 1;
    }
}
