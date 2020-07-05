package com.keuin.kessentialfabric.command;

import com.keuin.kessentialfabric.util.Const;
import com.keuin.kessentialfabric.util.PrintUtil;
import com.keuin.kessentialfabric.util.SoundPlayer;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CommandHandler {

    private static final int SUCCESS = 1;
    private static final int FAILED = -1;


    /**
     * Send a message with a player mentioned..
     * params: player, [message].
     *
     * @param context the context.
     * @return stat code.
     */
    public static int at(CommandContext<ServerCommandSource> context) {
        MinecraftServer server = context.getSource().getMinecraftServer();
        Entity entity = context.getSource().getEntity();
        boolean atAll = false; // if @all

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

        atAll = Const.atAllString.equalsIgnoreCase(playerMentioned);

        // Validate player id
        if (!atAll && !playerIdSet.contains(playerMentioned)) {
            PrintUtil.msgErr(context, String.format("Player %s is not online.", playerMentioned));
            return FAILED;
        }

        // Send message
        PrintUtil.broadcast(String.format("<%s> [%s] %s", playerId, atAll ? "@all" : ("@" + playerMentioned), message), Formatting.BOLD);

        // Play sound for specific player
        if (atAll) {
            for (String playerIterate : playerIdSet) {
                new Thread(() -> {
                    SoundPlayer.playNotificationSoundToPlayer(server, playerIterate);
                }).start();
            }
        } else {
            new Thread(() -> {
                SoundPlayer.playNotificationSoundToPlayer(server, playerMentioned);
            }).start();
        }


        return SUCCESS;
    }

    /**
     * Send a message of self pos.
     *
     * @param context the context.
     * @return stat code.
     */
    public static int here(CommandContext<ServerCommandSource> context) {
        MinecraftServer server = context.getSource().getMinecraftServer();
        Entity entity = context.getSource().getEntity();

        // If the command is not executed by a player
        if (!(entity instanceof ServerPlayerEntity))
            return FAILED;

        // Get player info
        String playerId = entity.getName().getString();
        String playerLocationString = CommandHandler.getPlayerEntityLocationString((ServerPlayerEntity) entity);

        // Get message (optional)
        String message;
        try {
            message = StringArgumentType.getString(context, "message");
        } catch (IllegalArgumentException ignored) {
            // If you want to mod a shit project, then you have to use a crappy way.
            message = "";
        }

        // Set glowing effect
        final int glowingSeconds = 30;
        ((ServerPlayerEntity) entity).addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, glowingSeconds * 20));

        // Print broadcast text
        PrintUtil.broadcast(String.format("[ %s @ %s ] %s", playerId, playerLocationString, message));

        return SUCCESS;
    }

    /**
     * Lookup the location of a player.
     * params: player
     *
     * @param context the context.
     * @return stat code.
     */
    public static int whereIs(CommandContext<ServerCommandSource> context) {
        MinecraftServer server = context.getSource().getMinecraftServer();

        // Get desired player
        String playerId = StringArgumentType.getString(context, "player");
        ServerPlayerEntity serverPlayerEntity = server.getPlayerManager().getPlayer(playerId);
        if (serverPlayerEntity == null) {
            PrintUtil.msgErr(context, String.format("Player %s is not online.", playerId));
            return FAILED;
        } else {
            String locationString = getPlayerEntityLocationString(serverPlayerEntity);
            PrintUtil.msgInfo(context, String.format("Player %s is at %s.", serverPlayerEntity.getName().getString(), locationString));
        }

        return SUCCESS;
    }

    /**
     * Get specific player location string in the format "(100,200) with y=63 in world overworld".
     *
     * @param entity the player entity.
     * @return the location string.
     */
    private static String getPlayerEntityLocationString(ServerPlayerEntity entity) {
        String rawWorldName = entity.getEntityWorld().getDimension().getType().toString();
        String worldName = rawWorldName.startsWith("minecraft:") ? rawWorldName.substring("minecraft:".length()) : rawWorldName;
        String posString = String.format("(%.1f, %.1f) with y=%.1f", entity.getPos().getX(), entity.getPos().getZ(), entity.getPos().getY());
        return String.format("%s in %s", posString, worldName);
    }

}
