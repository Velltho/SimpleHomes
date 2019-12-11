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
import net.lankylord.simplehomes.util.UUIDManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class OtherHomeCommand implements CommandExecutor {

    private final SimpleHomes simpleHomes;
    private final HomeManager homeManager;

    public OtherHomeCommand(SimpleHomes plugin, HomeManager manager) {
        simpleHomes = plugin;
        homeManager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (sender instanceof Player) {
            if (strings.length == 0) {
                return false;
            }
            final Player player = (Player) sender;
            final String homeName;
            if (strings.length == 2) {
                homeName = strings[1].toLowerCase();
            } else {
                homeName = "default";
            }
            final String targetName = strings[0].toLowerCase();
            new BukkitRunnable() {
                UUID targetUUID;

                @Override
                public void run() {
                    targetUUID = UUIDManager.getUUIDFromPlayer(targetName);
                    if (targetUUID != null) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                Location location = homeManager.getPlayerHome(targetUUID, homeName);
                                if (location == null) {
                                    location = homeManager.getPlayerHomeFromFile(targetUUID, homeName);
                                }
                                if (location != null) {
                                    player.teleport(location);
                                    player.sendMessage(LanguageManager.TELEPORT_OTHERHOME.replaceAll("%p", targetName));
                                } else {
                                    player.sendMessage(LanguageManager.HOME_NOT_FOUND);
                                }
                            }
                        }.runTask(simpleHomes);
                    } else {
                        player.sendMessage(LanguageManager.PLAYER_NOT_EXIST);
                    }
                }
            }.runTaskAsynchronously(simpleHomes);
        } else {
            sender.sendMessage(LanguageManager.PLAYER_COMMAND_ONLY);
        }
        return true;
    }
}

