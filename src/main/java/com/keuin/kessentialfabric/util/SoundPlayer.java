package com.keuin.kessentialfabric.util;

import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class SoundPlayer {

    public static void playSoundToPlayer(MinecraftServer server, ServerPlayerEntity player, RegistryEntry<SoundEvent> sound, SoundCategory category, Vec3d pos, float volume, float pitch, long seed) {
        player.networkHandler.sendPacket(new PlaySoundS2CPacket(sound, category, pos.getX(), pos.getY(), pos.getZ(), volume, pitch, seed));
    }

    public static boolean playNotificationSoundToPlayer(MinecraftServer server, String playerId) {
        ServerPlayerEntity serverPlayerEntity = server.getPlayerManager().getPlayer(playerId);
        if (serverPlayerEntity == null)
            return false;
        for (int i = 0; i < 3; i++) {
            SoundPlayer.playSoundToPlayer(
                    server, serverPlayerEntity,
                    RegistryEntry.of(SoundEvents.BLOCK_NOTE_BLOCK_BELL.value()),
                    SoundCategory.MASTER,
                    serverPlayerEntity.getPos(),
                    1, 1, 0
            );
            try {
                Thread.sleep(250);
            } catch (InterruptedException ignored) {
                return false;
            }
        }
        return true;
    }

}
