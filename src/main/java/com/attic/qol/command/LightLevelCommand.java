package com.attic.qol.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;

import static net.minecraft.server.command.CommandManager.literal;

public class LightLevelCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("lightlevel")
            .executes(ctx -> showLightLevel(ctx.getSource()))
        );
    }

    private static int showLightLevel(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) {
            source.sendError(Text.literal("This command can only be used by players"));
            return 0;
        }

        BlockPos pos = player.getBlockPos();
        int blockLight = player.world.getLightLevel(LightType.BLOCK, pos);
        int skyLight = player.world.getLightLevel(LightType.SKY, pos);
        int totalLight = Math.max(blockLight, skyLight);

        Formatting color;
        String mobSpawnInfo;
        if (totalLight <= 0) {
            color = Formatting.RED;
            mobSpawnInfo = "Hostile mobs can spawn";
        } else if (totalLight <= 7) {
            color = Formatting.GOLD;
            mobSpawnInfo = "Hostile mobs can spawn";
        } else {
            color = Formatting.GREEN;
            mobSpawnInfo = "No hostile mob spawns";
        }

        final int finalTotal = totalLight;
        source.sendFeedback(() -> Text.literal("=== Light Level ===").formatted(Formatting.GOLD), false);
        source.sendFeedback(() -> Text.literal("Block: ").formatted(Formatting.GRAY)
            .append(Text.literal(String.valueOf(blockLight)).formatted(Formatting.WHITE)), false);
        source.sendFeedback(() -> Text.literal("Sky: ").formatted(Formatting.GRAY)
            .append(Text.literal(String.valueOf(skyLight)).formatted(Formatting.WHITE)), false);
        source.sendFeedback(() -> Text.literal("Total: ").formatted(Formatting.GRAY)
            .append(Text.literal(String.valueOf(finalTotal)).formatted(color)), false);
        source.sendFeedback(() -> Text.literal("Status: ").formatted(Formatting.GRAY)
            .append(Text.literal(mobSpawnInfo).formatted(color)), false);

        return 1;
    }
}
