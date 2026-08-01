package com.attic.qol.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

import static net.minecraft.server.command.CommandManager.literal;

public class CompassCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("compass")
            .executes(ctx -> showDirection(ctx.getSource()))
        );
    }

    private static int showDirection(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) {
            source.sendError(Text.literal("This command can only be used by players"));
            return 0;
        }

        float yaw = MathHelper.wrapDegrees(player.getYaw());
        String direction = getDirection(yaw);
        String arrow = getArrow(yaw);

        source.sendFeedback(() -> Text.literal("Facing: ").formatted(Formatting.GRAY)
            .append(Text.literal(direction + " " + arrow).formatted(Formatting.AQUA)), false);

        return 1;
    }

    private static String getDirection(float yaw) {
        if (yaw >= -22.5 && yaw < 22.5) return "South";
        if (yaw >= 22.5 && yaw < 67.5) return "Southwest";
        if (yaw >= 67.5 && yaw < 112.5) return "West";
        if (yaw >= 112.5 && yaw < 157.5) return "Northwest";
        if (yaw >= 157.5 || yaw < -157.5) return "North";
        if (yaw >= -157.5 && yaw < -112.5) return "Northeast";
        if (yaw >= -112.5 && yaw < -67.5) return "East";
        if (yaw >= -67.5 && yaw < -22.5) return "Southeast";
        return "Unknown";
    }

    private static String getArrow(float yaw) {
        if (yaw >= -22.5 && yaw < 22.5) return "\u2193";
        if (yaw >= 22.5 && yaw < 67.5) return "\u2199";
        if (yaw >= 67.5 && yaw < 112.5) return "\u2190";
        if (yaw >= 112.5 && yaw < 157.5) return "\u2196";
        if (yaw >= 157.5 || yaw < -157.5) return "\u2191";
        if (yaw >= -157.5 && yaw < -112.5) return "\u2197";
        if (yaw >= -112.5 && yaw < -67.5) return "\u2192";
        if (yaw >= -67.5 && yaw < -22.5) return "\u2198";
        return "";
    }
}
