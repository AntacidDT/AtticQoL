package com.attic.qol.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;

import java.util.*;

public class PlayerDataStorage extends PersistentState {
    private static final String DATA_NAME = "atticqol_playerdata";

    private final Map<UUID, List<DeathData>> deathHistory = new HashMap<>();
    private final Map<UUID, List<MarkerData>> markers = new HashMap<>();
    private final Map<UUID, Long> joinTimes = new HashMap<>();

    public PlayerDataStorage() {
        super();
    }

    public static PlayerDataStorage getServerState(MinecraftServer server) {
        PersistentStateManager manager = server.getOverworld().getPersistentStateManager();
        PlayerDataStorage state = manager.getOrCreate(
            new PersistentState.Type<>(
                PlayerDataStorage::new,
                PlayerDataStorage::fromNbt,
                null
            ),
            DATA_NAME
        );
        state.markDirty();
        return state;
    }

    public static PlayerDataStorage fromNbt(NbtCompound nbt, net.minecraft.registry.RegistryWrapper.WrapperLookup registryLookup) {
        PlayerDataStorage state = new PlayerDataStorage();

        NbtCompound deathsCompound = nbt.getCompound("deaths");
        for (String key : deathsCompound.getKeys()) {
            UUID uuid = UUID.fromString(key);
            NbtList list = deathsCompound.getList(key, NbtElement.COMPOUND_TYPE);
            List<DeathData> deaths = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                deaths.add(DeathData.fromNbt(list.getCompound(i)));
            }
            state.deathHistory.put(uuid, deaths);
        }

        NbtCompound markersCompound = nbt.getCompound("markers");
        for (String key : markersCompound.getKeys()) {
            UUID uuid = UUID.fromString(key);
            NbtList list = markersCompound.getList(key, NbtElement.COMPOUND_TYPE);
            List<MarkerData> markerList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                markerList.add(MarkerData.fromNbt(list.getCompound(i)));
            }
            state.markers.put(uuid, markerList);
        }

        NbtCompound joinTimesCompound = nbt.getCompound("joinTimes");
        for (String key : joinTimesCompound.getKeys()) {
            UUID uuid = UUID.fromString(key);
            state.joinTimes.put(uuid, joinTimesCompound.getLong(key));
        }

        return state;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, net.minecraft.registry.RegistryWrapper.WrapperLookup registryLookup) {
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

        return nbt;
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
