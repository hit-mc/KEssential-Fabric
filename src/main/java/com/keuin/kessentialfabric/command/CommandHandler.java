package com.keuin.kessentialfabric.command;

import com.keuin.kessentialfabric.util.PrintUtil;
import com.keuin.kessentialfabric.util.SoundPlayer;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CommandHandler {

    private static final int SUCCESS = 1;
    private static final int FAILED = -1;

    /**
     * Send a message with a player mentioned.
     * params: player, [message]
     *
     * @param context the context
     * @return stat code
     */
    public static int at(CommandContext<ServerCommandSource> context) {
        MinecraftServer server = context.getSource().getMinecraftServer();
        Entity entity = context.getSource().getEntity();

        // If the command is not executed by a player
        if (!(entity instanceof ServerPlayerEntity))
            return FAILED;

        // Get executor info
        String playerId = entity.getName().getString();

        Set<String> playerIdSet = new HashSet<>(Arrays.asList(server.getPlayerNames()));
        String playerMentioned = StringArgumentType.getString(context, "player");
        String message;
        try {
            message = StringArgumentType.getString(context, "message");
        } catch (IllegalArgumentException ignored) {
            // If you want to mod a shit project, then you have to use a crappy way.
            message = "";
        }


        // Validate player id
        if (!playerIdSet.contains(playerMentioned)) {
            PrintUtil.msgErr(context, String.format("Player %s is not online.", playerMentioned));
            return FAILED;
        }

        // Send message
        PrintUtil.broadcast(String.format("<%s> [@%s] %s", playerId, playerMentioned, message), Formatting.BOLD);

        // Play sound for specific player
        ServerPlayerEntity serverPlayerEntity = server.getPlayerManager().getPlayer(playerMentioned);

        if (serverPlayerEntity != null) {
            ServerWorld playerWorld = serverPlayerEntity.getServerWorld();
            new Thread(() -> {
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
                    }
                }
            }).start();
        }

        return SUCCESS;
    }

    /**
     * Send a message of self pos
     *
     * @param context the context
     * @return stat code
     */
    public static int here(CommandContext<ServerCommandSource> context) {
        MinecraftServer server = context.getSource().getMinecraftServer();
        Entity entity = context.getSource().getEntity();

        // If the command is not executed by a player
        if (!(entity instanceof ServerPlayerEntity))
            return FAILED;

        // Get player info
        String playerId = entity.getName().getString();
        String rawWorldName = entity.getEntityWorld().getDimension().getType().toString();
        String worldName = rawWorldName.startsWith("minecraft:") ? rawWorldName.substring("minecraft:".length()) : rawWorldName;
        String posString = String.format("(%.0f, %.0f) with y=%.0f", entity.getPos().getX(), entity.getPos().getZ(), entity.getPos().getY());

        // Get message (optional)
        String message;
        try {
            message = StringArgumentType.getString(context, "message");
        } catch (IllegalArgumentException ignored) {
            // If you want to mod a shit project, then you have to use a crappy way.
            message = "";
        }

        // Print broadcast text
        PrintUtil.broadcast(String.format("[ %s @ %s in %s ] %s", playerId, posString, worldName, message));

        return SUCCESS;
    }

}
