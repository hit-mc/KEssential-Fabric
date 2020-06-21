package com.keuin.kessentialfabric.command;

import com.keuin.kessentialfabric.command.suggestion.PlayerNameSuggestionProvider;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public final class CommandRegister {
    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {

        // register /at for chat with a player mentioned
        dispatcher.register(
                CommandManager.literal("at")
                        .then(
                                CommandManager.argument("player", StringArgumentType.string())
                                        .suggests(PlayerNameSuggestionProvider.getProvider())
                                        .executes(CommandHandler::at)
                                        .then(CommandManager.argument("message", StringArgumentType.greedyString()).executes(CommandHandler::at))
                        )
        );

        // register /here and /pos for saying current pos
        dispatcher.register(
                CommandManager.literal("here")
                        .executes(CommandHandler::here)
                        .then(CommandManager.argument("message", StringArgumentType.greedyString()).executes(CommandHandler::here))
        );
        dispatcher.register(
                CommandManager.literal("pos")
                        .executes(CommandHandler::here)
                        .then(CommandManager.argument("message", StringArgumentType.greedyString()).executes(CommandHandler::here))
        );

    }
}