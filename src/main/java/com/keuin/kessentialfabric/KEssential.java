package com.keuin.kessentialfabric;

import com.keuin.kessentialfabric.command.CommandRegister;
import com.keuin.kessentialfabric.command.suggestion.PlayerNameSuggestionProvider;
import com.keuin.kessentialfabric.util.PrintUtil;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class KEssential implements ModInitializer, ServerLifecycleEvents.ServerStarting {

	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		System.out.println("Binding events and commands ...");
		CommandRegistrationCallback.EVENT.register(CommandRegister::registerCommands);
		ServerLifecycleEvents.SERVER_STARTING.register(this);
	}

	@Override
	public void onServerStarting(MinecraftServer server) {
		// Initialize player manager reference
		PrintUtil.setPlayerManager(server.getPlayerManager());
		// Initialize suggestion provider server
		PlayerNameSuggestionProvider.setServer(server);

		LOGGER.info("KEssential is a free software. Project home: https://github.com/keuin/KEssential-Fabric");
	}
}
