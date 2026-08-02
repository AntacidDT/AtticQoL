package com.attic.qol.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class DeathData {
    private final int x, y, z;
    private final String dimension;
    private final String biome;
    private final long worldTime;
    private final String cause;

    public DeathData(int x, int y, int z, String dimension, String biome, long worldTime, String cause) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimension = dimension;
        this.biome = biome;
        this.worldTime = worldTime;
        this.cause = cause;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getZ() { return z; }
    public String getDimension() { return dimension; }
    public String getBiome() { return biome; }
    public long getWorldTime() { return worldTime; }
    public String getCause() { return cause; }

    public BlockPos getPos() {
        return new BlockPos(x, y, z);
    }

    public String getFormattedTime() {
        long totalSeconds = worldTime / 20;
        long days = totalSeconds / 86400;
        long hours = (totalSeconds % 86400) / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        return days + "d " + hours + "h " + minutes + "m";
    }

    public String getDimensionName() {
        return switch (dimension) {
            case "minecraft:overworld" -> "Overworld";
            case "minecraft:the_nether" -> "The Nether";
            case "minecraft:the_end" -> "The End";
            default -> dimension;
        };
    }

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putInt("x", x);
        nbt.putInt("y", y);
        nbt.putInt("z", z);
        nbt.putString("dimension", dimension);
        nbt.putString("biome", biome);
        nbt.putLong("worldTime", worldTime);
        nbt.putString("cause", cause);
        return nbt;
    }

    public static DeathData fromNbt(NbtCompound nbt) {
        return new DeathData(
            nbt.getInt("x", 0),
            nbt.getInt("y", 0),
            nbt.getInt("z", 0),
            nbt.getString("dimension", ""),
            nbt.getString("biome", ""),
            nbt.getLong("worldTime", 0L),
            nbt.getString("cause", "")
        );
    }
}
