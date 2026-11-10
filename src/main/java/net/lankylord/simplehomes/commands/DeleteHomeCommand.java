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

import net.lankylord.simplehomes.SimpleHomes;
import net.lankylord.simplehomes.configuration.languages.LanguageManager;
import net.lankylord.simplehomes.homes.HomeManager;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeleteHomeCommand implements CommandExecutor {

    private final HomeManager homeManager;
    private final SimpleHomes simpleHomes;

    public DeleteHomeCommand(SimpleHomes plugin, HomeManager manager) {
        simpleHomes = plugin;
        homeManager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(LanguageManager.PLAYER_COMMAND_ONLY);
            return false;
        }

        Player player = (Player) sender;
        String homeName = "default";
        if (strings.length == 1 && sender.hasPermission("simplehomes.multihomes")) {
            homeName = strings[0].toLowerCase();
        }

        final String homeToDelete = homeName;
        final UUID playerUUID = player.getUniqueId();

        simpleHomes.getServer().getAsyncScheduler().runNow(simpleHomes, asyncTask -> {
            homeManager.deleteHome(playerUUID, homeToDelete);

            player.getScheduler().run(simpleHomes, task -> {
                if (!player.isOnline()) {
                    return;
                }
                player.sendMessage(LanguageManager.HOME_DELETED);
            }, null);
        });

        return true;
    }
}
