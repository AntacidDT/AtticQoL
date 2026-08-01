package com.attic.qol.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import static net.minecraft.server.command.CommandManager.literal;

public class CoordsCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("coords")
            .executes(ctx -> showCoords(ctx.getSource()))
        );
    }

    private static int showCoords(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) {
            source.sendError(Text.literal("This command can only be used by players"));
            return 0;
        }

        BlockPos pos = player.getBlockPos();

        source.sendFeedback(() -> Text.literal("")
            .append(Text.literal("X: ").formatted(Formatting.RED))
            .append(Text.literal(String.valueOf(pos.getX())).formatted(Formatting.WHITE))
            .append(Text.literal("  Y: ").formatted(Formatting.GREEN))
            .append(Text.literal(String.valueOf(pos.getY())).formatted(Formatting.WHITE))
            .append(Text.literal("  Z: ").formatted(Formatting.BLUE))
            .append(Text.literal(String.valueOf(pos.getZ())).formatted(Formatting.WHITE)),
            false
        );

        return 1;
    }
}
