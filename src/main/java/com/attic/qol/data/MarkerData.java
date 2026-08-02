package com.attic.qol.data;

import net.minecraft.nbt.NbtCompound;

public class MarkerData {
    private final String name;
    private final int x, y, z;
    private final String dimension;

    public MarkerData(String name, int x, int y, int z, String dimension) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimension = dimension;
    }

    public String getName() { return name; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getZ() { return z; }
    public String getDimension() { return dimension; }

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
        nbt.putString("name", name);
        nbt.putInt("x", x);
        nbt.putInt("y", y);
        nbt.putInt("z", z);
        nbt.putString("dimension", dimension);
        return nbt;
    }

    public static MarkerData fromNbt(NbtCompound nbt) {
        return new MarkerData(
            nbt.getString("name", ""),
            nbt.getInt("x", 0),
            nbt.getInt("y", 0),
            nbt.getInt("z", 0),
            nbt.getString("dimension", "")
        );
    }
}
