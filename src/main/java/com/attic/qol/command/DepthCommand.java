package com.attic.qol.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;

import static net.minecraft.server.command.CommandManager.literal;

public class DepthCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("depth")
            .executes(ctx -> showDepth(ctx.getSource()))
        );
    }

    private static int showDepth(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) {
            source.sendError(Text.literal("This command can only be used by players"));
            return 0;
        }

        BlockPos pos = player.getBlockPos();
        int seaLevel = 63;
        int surfaceY = player.world.getTopY(Heightmap.Type.WORLD_SURFACE, pos.getX(), pos.getZ());
        int depth = seaLevel - pos.getY();
        int fromSurface = surfaceY - pos.getY();

        String zone;
        Formatting color;
        if (pos.getY() > 200) {
            zone = "High altitude";
            color = Formatting.AQUA;
        } else if (pos.getY() > seaLevel) {
            zone = "Above sea level";
            color = Formatting.GREEN;
        } else if (pos.getY() == seaLevel) {
            zone = "Sea level";
            color = Formatting.YELLOW;
        } else if (pos.getY() > 0) {
            zone = "Underground";
            color = Formatting.GOLD;
        } else if (pos.getY() > -64) {
            zone = "Deep underground";
            color = Formatting.RED;
        } else {
            zone = "Deepslate layer";
            color = Formatting.DARK_RED;
        }

        source.sendFeedback(() -> Text.literal("=== Depth Info ===").formatted(Formatting.GOLD), false);
        source.sendFeedback(() -> Text.literal("Y Level: ").formatted(Formatting.GRAY)
            .append(Text.literal(String.valueOf(pos.getY())).formatted(Formatting.WHITE)), false);
        source.sendFeedback(() -> Text.literal("Sea Level: ").formatted(Formatting.GRAY)
            .append(Text.literal(String.valueOf(seaLevel)).formatted(Formatting.WHITE)), false);
        source.sendFeedback(() -> Text.literal("From Sea Level: ").formatted(Formatting.GRAY)
            .append(Text.literal((depth >= 0 ? "+" : "") + depth + " blocks")
                .formatted(depth >= 0 ? Formatting.GREEN : Formatting.RED)), false);
        source.sendFeedback(() -> Text.literal("From Surface: ").formatted(Formatting.GRAY)
            .append(Text.literal(fromSurface + " blocks below").formatted(Formatting.YELLOW)), false);
        source.sendFeedback(() -> Text.literal("Zone: ").formatted(Formatting.GRAY)
            .append(Text.literal(zone).formatted(color)), false);

        return 1;
    }
}
