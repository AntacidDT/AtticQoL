package com.attic.qol.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class PlayerDataStorage {
    private static final String DATA_NAME = "atticqol_playerdata";

    private final Map<UUID, List<DeathData>> deathHistory = new HashMap<>();
    private final Map<UUID, List<MarkerData>> markers = new HashMap<>();
    private final Map<UUID, Long> joinTimes = new HashMap<>();
    private boolean dirty = false;
    private Path savePath;

    public PlayerDataStorage() {
    }

    public void init(MinecraftServer server) {
        this.savePath = server.getSavePath(WorldSavePath.ROOT).resolve("data").resolve(DATA_NAME + ".dat");
        load();
    }

    private static final Map<MinecraftServer, PlayerDataStorage> INSTANCES = new WeakHashMap<>();

    public static PlayerDataStorage getServerState(MinecraftServer server) {
        return INSTANCES.computeIfAbsent(server, s -> {
            PlayerDataStorage state = new PlayerDataStorage();
            state.init(s);
            return state;
        });
    }

    private void load() {
        if (savePath == null || !Files.exists(savePath)) return;
        try {
            NbtCompound nbt = NbtIo.read(savePath);
            if (nbt == null) return;

            NbtCompound deathsCompound = nbt.getCompoundOrEmpty("deaths");
            for (String key : deathsCompound.getKeys()) {
                UUID uuid = UUID.fromString(key);
                NbtList list = deathsCompound.getListOrEmpty(key);
                List<DeathData> deaths = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    deaths.add(DeathData.fromNbt(list.getCompoundOrEmpty(i)));
                }
                deathHistory.put(uuid, deaths);
            }

            NbtCompound markersCompound = nbt.getCompoundOrEmpty("markers");
            for (String key : markersCompound.getKeys()) {
                UUID uuid = UUID.fromString(key);
                NbtList list = markersCompound.getListOrEmpty(key);
                List<MarkerData> markerList = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    markerList.add(MarkerData.fromNbt(list.getCompoundOrEmpty(i)));
                }
                markers.put(uuid, markerList);
            }

            NbtCompound joinTimesCompound = nbt.getCompoundOrEmpty("joinTimes");
            for (String key : joinTimesCompound.getKeys()) {
                UUID uuid = UUID.fromString(key);
                joinTimes.put(uuid, joinTimesCompound.getLong(key, 0L));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void save() {
        if (savePath == null || !dirty) return;
        try {
            NbtCompound nbt = new NbtCompound();

            NbtCompound deathsCompound = new NbtCompound();
            for (Map.Entry<UUID, List<DeathData>> entry : deathHistory.entrySet()) {
                NbtList list = new NbtList();
                for (DeathData death : entry.getValue()) {
                    list.add(death.toNbt());
                }
                deathsCompound.put(entry.getKey().toString(), list);
            }
            nbt.put("deaths", deathsCompound);

            NbtCompound markersCompound = new NbtCompound();
            for (Map.Entry<UUID, List<MarkerData>> entry : markers.entrySet()) {
                NbtList list = new NbtList();
                for (MarkerData marker : entry.getValue()) {
                    list.add(marker.toNbt());
                }
                markersCompound.put(entry.getKey().toString(), list);
            }
            nbt.put("markers", markersCompound);

            NbtCompound joinTimesCompound = new NbtCompound();
            for (Map.Entry<UUID, Long> entry : joinTimes.entrySet()) {
                joinTimesCompound.putLong(entry.getKey().toString(), entry.getValue());
            }
            nbt.put("joinTimes", joinTimesCompound);

            Files.createDirectories(savePath.getParent());
            NbtIo.write(nbt, savePath);
            dirty = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void markDirty() {
        dirty = true;
        save();
    }

    public void addDeath(UUID playerUuid, DeathData death) {
        List<DeathData> deaths = deathHistory.computeIfAbsent(playerUuid, k -> new ArrayList<>());
        deaths.add(0, death);
        if (deaths.size() > 16) {
            deaths.subList(16, deaths.size()).clear();
        }
        markDirty();
    }

    public List<DeathData> getDeaths(UUID playerUuid) {
        return deathHistory.getOrDefault(playerUuid, Collections.emptyList());
    }

    public void addMarker(UUID playerUuid, MarkerData marker) {
        List<MarkerData> playerMarkers = markers.computeIfAbsent(playerUuid, k -> new ArrayList<>());
        playerMarkers.removeIf(m -> m.getName().equalsIgnoreCase(marker.getName()));
        playerMarkers.add(marker);
        markDirty();
    }

    public boolean removeMarker(UUID playerUuid, String name) {
        List<MarkerData> playerMarkers = markers.get(playerUuid);
        if (playerMarkers == null) return false;
        boolean removed = playerMarkers.removeIf(m -> m.getName().equalsIgnoreCase(name));
        if (removed) markDirty();
        return removed;
    }

    public List<MarkerData> getMarkers(UUID playerUuid) {
        return markers.getOrDefault(playerUuid, Collections.emptyList());
    }

    public void setJoinTime(UUID playerUuid, long time) {
        joinTimes.put(playerUuid, time);
        markDirty();
    }

    public long getJoinTime(UUID playerUuid) {
        return joinTimes.getOrDefault(playerUuid, 0L);
    }
}
