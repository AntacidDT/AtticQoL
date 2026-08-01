package com.attic.qol;

import com.attic.qol.command.*;
import com.attic.qol.data.PlayerDataStorage;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AtticQoL implements ModInitializer {
    public static final String MOD_ID = "atticqol";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            WhereDeathCommand.register(dispatcher);
            LocationCommand.register(dispatcher);
            MarkerCommand.register(dispatcher);
            WStatsCommand.register(dispatcher);
            CompassCommand.register(dispatcher);
            TimePlayedCommand.register(dispatcher);
            LightLevelCommand.register(dispatcher);
            ChunkInfoCommand.register(dispatcher);
            WeatherCommand.register(dispatcher);
            DayCommand.register(dispatcher);
            HomeCommand.register(dispatcher);
            DepthCommand.register(dispatcher);
            ExpCommand.register(dispatcher);
            ArmorCommand.register(dispatcher);
            CoordsCommand.register(dispatcher);
            BiomeCommand.register(dispatcher);
            NearbyCommand.register(dispatcher);
            EntityCountCommand.register(dispatcher);
            StatsCommand.register(dispatcher);
            AtticQolCommand.register(dispatcher);
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            PlayerDataStorage storage = PlayerDataStorage.getServerState(server);
            storage.setJoinTime(handler.getPlayer().getUuid(), server.getOverworld().getTime());
        });

        LOGGER.info("Attic QoL initialized");
    }
}
