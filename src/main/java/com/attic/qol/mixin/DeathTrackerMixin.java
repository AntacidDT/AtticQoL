package com.attic.qol.mixin;

import com.attic.qol.data.DeathData;
import com.attic.qol.data.PlayerDataStorage;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class DeathTrackerMixin {

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onDeath(DamageSource damageSource, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        ServerWorld world = (ServerWorld) player.world;
        BlockPos pos = player.getBlockPos();

        RegistryEntry<Biome> biomeEntry = world.getBiome(pos);
        String biomeName = biomeEntry.getKey()
            .map(key -> key.getValue().toString())
            .orElse("unknown");

        String dimensionKey = world.getRegistryKey().getValue().toString();

        String cause = damageSource.getDeathMessage(player).getString();

        DeathData death = new DeathData(
            pos.getX(),
            pos.getY(),
            pos.getZ(),
            dimensionKey,
            biomeName,
            world.getTime(),
            cause
        );

        PlayerDataStorage storage = PlayerDataStorage.getServerState(world.getServer());
        storage.addDeath(player.getUuid(), death);
    }
}
