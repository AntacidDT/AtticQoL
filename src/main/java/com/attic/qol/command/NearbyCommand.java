package com.attic.qol.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Vec3d;

import static net.minecraft.server.command.CommandManager.literal;

public class NearbyCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("nearby")
            .executes(ctx -> showNearby(ctx.getSource()))
        );
    }

    private static int showNearby(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) {
            source.sendError(Text.literal("This command can only be used by players"));
            return 0;
        }

        ServerWorld serverWorld = (ServerWorld) player.world;
        Vec3d playerPos = player.pos;

        int passiveCount = 0;
        int hostileCount = 0;
        int itemsCount = 0;
        int otherCount = 0;

        for (Entity entity : serverWorld.getEntitiesByType(TypeFilter.instanceOf(Entity.class), e -> true)) {
            if (entity.getUuid().equals(player.getUuid())) continue;

            double distance = entity.pos.distanceTo(playerPos);
            if (distance > 128) continue;

            if (entity instanceof Monster) {
                hostileCount++;
            } else if (entity instanceof AnimalEntity) {
                passiveCount++;
            } else if (entity instanceof ItemEntity) {
                itemsCount++;
            } else {
                otherCount++;
            }
        }

        final int passive = passiveCount;
        final int hostile = hostileCount;
        final int items = itemsCount;
        final int other = otherCount;
        final int total = passive + hostile + items + other;

        source.sendFeedback(() -> Text.literal("=== Nearby Entities ===").formatted(Formatting.GOLD), false);
        source.sendFeedback(() -> Text.literal("Passive Mobs: ").formatted(Formatting.GRAY)
            .append(Text.literal(String.valueOf(passive)).formatted(Formatting.GREEN)), false);
        source.sendFeedback(() -> Text.literal("Hostile Mobs: ").formatted(Formatting.GRAY)
            .append(Text.literal(String.valueOf(hostile)).formatted(Formatting.RED)), false);
        source.sendFeedback(() -> Text.literal("Items: ").formatted(Formatting.GRAY)
            .append(Text.literal(String.valueOf(items)).formatted(Formatting.YELLOW)), false);
        source.sendFeedback(() -> Text.literal("Other: ").formatted(Formatting.GRAY)
            .append(Text.literal(String.valueOf(other)).formatted(Formatting.AQUA)), false);
        source.sendFeedback(() -> Text.literal("Total: ").formatted(Formatting.GRAY)
            .append(Text.literal(String.valueOf(total)).formatted(Formatting.WHITE)), false);

        return 1;
    }
}
