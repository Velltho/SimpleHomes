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
package net.lankylord.simplehomes.configuration.languages;

import org.bukkit.configuration.file.FileConfiguration;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class LanguageManager {

    public static Component HOME_DELETED = Component.text("Home deleted.", NamedTextColor.YELLOW);
    public static Component HOME_LIST_PREFIX = Component.text("Homes:", NamedTextColor.YELLOW);
    public static Component HOME_NOT_FOUND = Component.text("Home not found.", NamedTextColor.RED);
    public static Component HOME_MAX_REACHED = Component.text("Home cannot be set. The maximum number of homes has been reached", NamedTextColor.RED);
    public static Component HOME_SET =  Component.text("Home set.", NamedTextColor.YELLOW);
    public static Component NO_HOMES_FOUND =  Component.text("No homes found.", NamedTextColor.RED);
    public static Component PLAYER_COMMAND_ONLY =  Component.text("Only players may issue that command.", NamedTextColor.RED);
    public static Component TELEPORT_OTHERHOME =  Component.text("Teleported to %p's home.", NamedTextColor.YELLOW);
    public static Component TELEPORT_SUCCESS =  Component.text("Teleported.", NamedTextColor.YELLOW);
    public static Component PLAYER_NOT_EXIST =  Component.text("That player doesn't exist.", NamedTextColor.RED);

    public LanguageManager(LanguageFileManager fileManager) {
        loadMessages(fileManager.getLanguageConfig());
    }

    private Component loadString(String string) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(string);
    }

    private void loadMessages(FileConfiguration languageConfig) {
        HOME_DELETED = loadString(languageConfig.getString("home-deleted"));
        HOME_LIST_PREFIX = loadString(languageConfig.getString("home-list-prefix"));
        HOME_NOT_FOUND = loadString(languageConfig.getString("home-not-found"));
        HOME_MAX_REACHED = loadString(languageConfig.getString("home-max-reached"));
        HOME_SET = loadString(languageConfig.getString("home-set"));
        NO_HOMES_FOUND = loadString(languageConfig.getString("no-homes-found"));
        PLAYER_COMMAND_ONLY = loadString(languageConfig.getString("player-command-only"));
        TELEPORT_OTHERHOME = loadString(languageConfig.getString("teleport-otherhome"));
        TELEPORT_SUCCESS = loadString(languageConfig.getString("teleport-success"));
        PLAYER_NOT_EXIST = loadString(languageConfig.getString("player-not-exist"));
    }
}