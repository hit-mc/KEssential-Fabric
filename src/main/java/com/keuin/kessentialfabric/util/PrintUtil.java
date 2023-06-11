package com.keuin.kessentialfabric.util;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class PrintUtil {

    private static final Object syncMessage = new Object();
    private static final Object syncBroadcast = new Object();

    private static final Style infoStyle = Style.EMPTY.withColor(Formatting.WHITE);
    private static final Style stressStyle = Style.EMPTY.withColor(Formatting.AQUA);
    private static final Style warnStyle = Style.EMPTY.withColor(Formatting.YELLOW);
    private static final Style errorStyle = Style.EMPTY.withColor(Formatting.DARK_RED);

    private static final Logger LOGGER = LogManager.getLogger();
    private static PlayerManager fuckMojang = null;

    public static void setPlayerManager(PlayerManager playerManager) {
        if (fuckMojang == null)
            fuckMojang = playerManager;
    }

    public static void broadcast(String message) {
        broadcast(message, Formatting.AQUA);
    }

    public static void broadcast(String message, Formatting formatting) {
        Style style = Style.EMPTY.withColor(formatting);
        synchronized (syncBroadcast) {
            if (fuckMojang != null){
                PrintUtil.info("Trying to send");
                MutableText literalText = Text.literal(message);
                literalText.setStyle(style);
                fuckMojang.broadcast(literalText, false);
            }

            else
                PrintUtil.error("Error in PrintUtil.broadcast: PlayerManager is not initialized.");
        }
    }

    public static CommandContext<ServerCommandSource> msgStress(CommandContext<ServerCommandSource> context, String messageText) {
        return msgStress(context, messageText, false);
    }

    public static CommandContext<ServerCommandSource> msgInfo(CommandContext<ServerCommandSource> context, String messageText) {
        return msgInfo(context, messageText, false);
    }

    public static CommandContext<ServerCommandSource> msgWarn(CommandContext<ServerCommandSource> context, String messageText) {
        return msgWarn(context, messageText, false);
    }

    public static CommandContext<ServerCommandSource> msgErr(CommandContext<ServerCommandSource> context, String messageText) {
        return msgErr(context, messageText, false);
    }

    public static CommandContext<ServerCommandSource> msgStress(CommandContext<ServerCommandSource> context, String messageText, boolean broadcastToOps) {
        return message(context, messageText, broadcastToOps, stressStyle);
    }

    public static CommandContext<ServerCommandSource> msgInfo(CommandContext<ServerCommandSource> context, String messageText, boolean broadcastToOps) {
        return message(context, messageText, broadcastToOps, infoStyle);
    }

    public static CommandContext<ServerCommandSource> msgWarn(CommandContext<ServerCommandSource> context, String messageText, boolean broadcastToOps) {
        return message(context, messageText, broadcastToOps, warnStyle);
    }

    public static CommandContext<ServerCommandSource> msgErr(CommandContext<ServerCommandSource> context, String messageText, boolean broadcastToOps) {
        return message(context, messageText, broadcastToOps, errorStyle);
    }

    private static CommandContext<ServerCommandSource> message(CommandContext<ServerCommandSource> context, String messageText, boolean broadcastToOps, Style style) {
        synchronized (syncMessage) {
            MutableText text = Text.literal(messageText);
            text.setStyle(style);
            context.getSource().sendFeedback(() -> text, broadcastToOps);
        }
        return context;
    }

    public static void debug(String string) {
        LOGGER.debug("[KBackup] " + string);
    }

    public static void info(String string) {
        LOGGER.info("[KBackup] " + string);
    }

    public static void warn(String string) {
        LOGGER.warn("[KBackup] " + string);
    }

    public static void error(String string) {
        LOGGER.error("[KBackup] " + string);
    }
}