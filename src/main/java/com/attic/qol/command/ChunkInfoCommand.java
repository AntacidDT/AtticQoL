package com.attic.qol.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import static net.minecraft.server.command.CommandManager.literal;

public class ChunkInfoCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("chunkinfo")
            .executes(ctx -> showChunkInfo(ctx.getSource()))
        );
    }

    private static int showChunkInfo(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) {
            source.sendError(Text.literal("This command can only be used by players"));
            return 0;
        }

        BlockPos pos = player.getBlockPos();
        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;
        int regionX = chunkX >> 5;
        int regionZ = chunkZ >> 5;

        source.sendFeedback(() -> Text.literal("=== Chunk Info ===").formatted(Formatting.GOLD), false);
        source.sendFeedback(() -> Text.literal("Block: ").formatted(Formatting.GRAY)
            .append(Text.literal(pos.getX() + ", " + pos.getY() + ", " + pos.getZ())
                .formatted(Formatting.WHITE)), false);
        source.sendFeedback(() -> Text.literal("Chunk: ").formatted(Formatting.GRAY)
            .append(Text.literal(chunkX + ", " + chunkZ).formatted(Formatting.AQUA)), false);
        source.sendFeedback(() -> Text.literal("Region: ").formatted(Formatting.GRAY)
            .append(Text.literal("r." + regionX + "." + regionZ + ".mca").formatted(Formatting.YELLOW)), false);
        source.sendFeedback(() -> Text.literal("Chunk Pos: ").formatted(Formatting.GRAY)
            .append(Text.literal((pos.getX() & 15) + ", " + (pos.getZ() & 15))
                .formatted(Formatting.GREEN)), false);

        return 1;
    }
}
