package com.keuin.kessentialfabric.command.suggestion;


import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class PlayerNameSuggestionProvider {

    private static final Object syncCache = new Object();
    private static final long CACHE_TTL = 3000;
    private static List<String> candidateCacheList = Collections.emptyList();
    private static long cacheUpdateTime = 0;

    private static MinecraftServer server;

    public static void setServer(MinecraftServer server) {
        PlayerNameSuggestionProvider.server = server;
    }

    public static void updateCandidateList() {
        synchronized (syncCache) {
            candidateCacheList = Arrays.asList(server.getPlayerNames());
            cacheUpdateTime = System.currentTimeMillis();
        }
    }

//    private static void updateCandidateList(Collection<String> stringCollection) {
//        candidateList.clear();
//        candidateList.addAll(stringCollection);
//    }

    public static SuggestionProvider<ServerCommandSource> getProvider() {
        return (context, builder) -> getCompletableFuture(builder);
    }

    private static CompletableFuture<Suggestions> getCompletableFuture(SuggestionsBuilder builder) {
        if (isCacheExpired())
            updateCandidateList();
        String remaining = builder.getRemaining().toLowerCase(Locale.ROOT);
        synchronized (syncCache) {
            if (candidateCacheList.isEmpty()) { // If the list is empty then return no suggestions
                return Suggestions.empty(); // No suggestions
            }

            for (String string : candidateCacheList) { // Iterate through the supplied list
                if (string.toLowerCase(Locale.ROOT).startsWith(remaining)) {
                    builder.suggest(string); // Add every single entry to suggestions list.
                }
            }
        }
        return builder.buildFuture(); // Create the CompletableFuture containing all the suggestions
    }

    private static boolean isCacheExpired() {
        return System.currentTimeMillis() - cacheUpdateTime > CACHE_TTL || cacheUpdateTime == 0;
    }
}
