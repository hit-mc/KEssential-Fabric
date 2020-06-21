package com.keuin.kessentialfabric.util;

import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;

public class SoundPlayer {

    public static void playSoundToPlayer(MinecraftServer server, ServerPlayerEntity player, SoundEvent sound, SoundCategory category, BlockPos pos, float volume, float pitch) {
        player.networkHandler.sendPacket(new PlaySoundS2CPacket(sound, category, pos.getX(), pos.getY(), pos.getZ(), volume, pitch));
    }

}
