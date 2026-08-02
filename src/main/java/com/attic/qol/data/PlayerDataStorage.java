package com.attic.qol.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.PersistentStateType;

import java.util.*;

public class PlayerDataStorage extends PersistentState {
    private static final String DATA_NAME = "atticqol_playerdata";

    private final Map<UUID, List<DeathData>> deathHistory = new HashMap<>();
    private final Map<UUID, List<MarkerData>> markers = new HashMap<>();
    private final Map<UUID, Long> joinTimes = new HashMap<>();

    public PlayerDataStorage() {
        super();
    }

    private static PlayerDataStorage fromNbt(NbtCompound nbt) {
        PlayerDataStorage state = new PlayerDataStorage();

        NbtCompound deathsCompound = nbt.getCompoundOrEmpty("deaths");
        for (String key : deathsCompound.getKeys()) {
            UUID uuid = UUID.fromString(key);
            NbtList list = deathsCompound.getListOrEmpty(key);
            List<DeathData> deaths = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                deaths.add(DeathData.fromNbt(list.getCompoundOrEmpty(i)));
            }
            state.deathHistory.put(uuid, deaths);
        }

        NbtCompound markersCompound = nbt.getCompoundOrEmpty("markers");
        for (String key : markersCompound.getKeys()) {
            UUID uuid = UUID.fromString(key);
            NbtList list = markersCompound.getListOrEmpty(key);
            List<MarkerData> markerList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                markerList.add(MarkerData.fromNbt(list.getCompoundOrEmpty(i)));
            }
            state.markers.put(uuid, markerList);
        }

        NbtCompound joinTimesCompound = nbt.getCompoundOrEmpty("joinTimes");
        for (String key : joinTimesCompound.getKeys()) {
            UUID uuid = UUID.fromString(key);
            state.joinTimes.put(uuid, joinTimesCompound.getLong(key, 0L));
        }

        return state;
    }

    private NbtCompound toNbt() {
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

        return nbt;
    }

    private static final Codec<PlayerDataStorage> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.STRING.optionalFieldOf("data", "").forGetter(x -> "")
        ).apply(instance, ignored -> new PlayerDataStorage())
    );

    private static final PersistentStateType<PlayerDataStorage> TYPE = new PersistentStateType<>(
        DATA_NAME,
        PlayerDataStorage::new,
        CODEC,
        null
    );

    public static PlayerDataStorage getServerState(MinecraftServer server) {
        PersistentStateManager manager = server.getOverworld().getPersistentStateManager();
        PlayerDataStorage state = manager.getOrCreate(TYPE);
        state.markDirty();
        return state;
    }

    @Override
    public void setDirty(boolean dirty) {
        super.setDirty(dirty);
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
