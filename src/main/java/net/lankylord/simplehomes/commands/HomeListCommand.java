/*
 * Copyright (c) 2014, LankyLord
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.lankylord.simplehomes.commands;

import net.kyori.adventure.text.Component;
import net.lankylord.simplehomes.configuration.languages.LanguageManager;
import net.lankylord.simplehomes.homes.HomeManager;
import net.lankylord.simplehomes.util.ProfileLookup;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class HomeListCommand implements CommandExecutor {

    private final HomeManager homeManager;

    public HomeListCommand(HomeManager manager) {
        homeManager = manager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            handlePlayerCommand(player, args);
        } else {
            handleConsoleCommand(sender);
        }
        return true;
    }

    private void handlePlayerCommand(Player player, String[] args) {
        if (args.length == 0) {
            displayHomes(player, player.getUniqueId(), null);
            return;
        }

        String targetName = args[0];
        ProfileLookup.getUUIDFromName(targetName).thenAccept(uuid -> {
            if (uuid == null) {
                player.sendMessage(LanguageManager.PLAYER_NOT_EXIST);
                return;
            }
            displayHomes(player, uuid, targetName);
        });
    }

    private void displayHomes(CommandSender receiver, UUID targetUuid, String displayName) {
        Map<String, Location> playerHomes = homeManager.getPlayerHomes(targetUuid);
        if (playerHomes == null || playerHomes.isEmpty()) {
            receiver.sendMessage(LanguageManager.NO_HOMES_FOUND);
            return;
        }

        String sortedHomes = playerHomes.keySet().stream()
            .sorted(String.CASE_INSENSITIVE_ORDER)
            .collect(Collectors.joining(", "));

        Component prefix = displayName != null
            ? Component.text("[" + displayName + "]").append(LanguageManager.HOME_LIST_PREFIX)
            : LanguageManager.HOME_LIST_PREFIX;

        receiver.sendMessage(prefix.append(Component.text(" " + sortedHomes)));
    }

    private void handleConsoleCommand(CommandSender console) {
        Map<UUID, Map<String, Location>> allHomes = homeManager.getHomes();
        if (allHomes.isEmpty()) {
            console.sendMessage(LanguageManager.NO_HOMES_FOUND);
            return;
        }

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (Map.Entry<UUID, Map<String, Location>> entry : allHomes.entrySet()) {
            UUID uuid = entry.getKey();
            Map<String, Location> homes = entry.getValue();

            if (homes == null || homes.isEmpty()) continue;

            CompletableFuture<Void> future = ProfileLookup.getNameFromUUID(uuid).thenAccept(name -> {
                String fallbackName = (name != null) ? name : uuid.toString();
                String sortedHomes = homes.keySet().stream()
                    .sorted(String.CASE_INSENSITIVE_ORDER)
                    .collect(Collectors.joining(", "));
                
                console.sendMessage(Component.text("[" + fallbackName + "] ")
                    .append(LanguageManager.HOME_LIST_PREFIX)
                    .append(Component.text(" " + sortedHomes)));
            });

            futures.add(future);
        }
    }
}
