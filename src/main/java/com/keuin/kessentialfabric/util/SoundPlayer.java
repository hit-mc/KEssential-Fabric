package com.keuin.kessentialfabric.util;

import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;

public class SoundPlayer {

    public static void playSoundToPlayer(MinecraftServer server, ServerPlayerEntity player, SoundEvent sound, SoundCategory category, BlockPos pos, float volume, float pitch) {
        player.networkHandler.sendPacket(new PlaySoundS2CPacket(sound, category, pos.getX(), pos.getY(), pos.getZ(), volume, pitch));
    }

    public static boolean playNotificationSoundToPlayer(MinecraftServer server, String playerId) {
        ServerPlayerEntity serverPlayerEntity = server.getPlayerManager().getPlayer(playerId);
        if (serverPlayerEntity == null)
            return false;
        for (int i = 0; i < 3; i++) {
            SoundPlayer.playSoundToPlayer(
                    server, serverPlayerEntity,
                    SoundEvents.BLOCK_NOTE_BLOCK_BELL,
                    SoundCategory.MASTER,
                    serverPlayerEntity.getBlockPos(),
                    1, 1
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
