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

package net.lankylord.simplehomes;

import net.gravitydevelopment.updater.Updater;
import net.lankylord.simplehomes.commands.DeleteHomeCommand;
import net.lankylord.simplehomes.commands.HomeCommand;
import net.lankylord.simplehomes.commands.HomeListCommand;
import net.lankylord.simplehomes.commands.OtherHomeCommand;
import net.lankylord.simplehomes.commands.ReloadCommand;
import net.lankylord.simplehomes.commands.SetHomeCommand;
import net.lankylord.simplehomes.configuration.ConfigManager;
import net.lankylord.simplehomes.configuration.languages.LanguageFileManager;
import net.lankylord.simplehomes.configuration.languages.LanguageManager;
import net.lankylord.simplehomes.homes.HomeManager;
import net.lankylord.simplehomes.listeners.GatewayListener;
import net.lankylord.simplehomes.storage.HomeFileManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

public class SimpleHomes extends JavaPlugin {

    private HomeFileManager homeFileManager;
    private HomeManager homeManager;

    private boolean updateAvailable = false;

    @Override
    public void onEnable() {
        FileConfiguration config = this.getConfig();

        this.homeFileManager = new HomeFileManager(this);
        this.homeManager = new HomeManager(homeFileManager);
        if (new File(this.getDataFolder(), "config.yml").exists()) {
            if (getConfig().getInt("ConfigVersion") < ConfigManager.CONFIG_VERSION_UUID_INTRODUCED || !getConfig().isSet("ConfigVersion")) {
                homeFileManager.UuidUpdate();
            }
        }
        //this.saveDefaultConfig();
        config.options().copyDefaults(true);
        this.saveConfig();
        LanguageFileManager languageFileManager = new LanguageFileManager(this);
        languageFileManager.saveLanguages();
        new LanguageManager(languageFileManager);

        new ConfigManager(this);
        homeFileManager.saveHomes();
        loadCommands();
        loadListeners();
        if (getConfig().getBoolean("AutoUpdater.Enabled", true)) {
            loadUpdater();
        }
        getLogger().info("SimpleHomes Enabled!");
    }

    @Override
    public void onDisable() {
        homeFileManager.saveHomes();
        getLogger().log(Level.INFO, "SimpleHomes Disabled!");
    }

    private void loadUpdater() {
        if (getConfig().getBoolean("AutoUpdater.Enabled")) {
            String mode = getConfig().getString("AutoUpdater.Mode");
            Updater.UpdateType updateType;
            switch (mode.toLowerCase()) {
                case "update":
                    //Update the plugin automatically
                    updateType = Updater.UpdateType.DEFAULT;
                    getLogger().log(Level.INFO, "AutoUpdater Enabled: Update");
                    break;
                case "notify":
                    //Notify admins of available updates
                    updateType = Updater.UpdateType.NO_DOWNLOAD;
                    getLogger().log(Level.INFO, "AutoUpdater Enabled: Notify");
                    break;
                default:
                    //Default to notification mode
                    updateType = Updater.UpdateType.NO_DOWNLOAD;
                    getLogger().log(Level.INFO, "AutoUpdater Enabled: Invalid Mode - Defaulting to Notify");
                    break;
            }

            Updater updater = new Updater(this, 48509, this.getFile(), updateType, true);
            Updater.UpdateResult updateResult = updater.getResult();

            switch (updateResult) {
                case SUCCESS:
                    //Update detected and downloaded
                    getLogger().log(Level.INFO, "Plugin will be updated at next restart");
                    updateAvailable = true;
                    break;
                case FAIL_DOWNLOAD:
                case FAIL_DBO:
                case FAIL_NOVERSION:
                    //Fail to get updates
                    getLogger().log(Level.WARNING, "Plugin failed to check for updates");
                    break;
                case UPDATE_AVAILABLE:
                    //Update detected but not downloaded
                    getLogger().log(Level.INFO, "There is an update available on BukkitDev");
                    updateAvailable = true;
                    break;
                case NO_UPDATE:
                    //No update found
                    getLogger().log(Level.INFO, "No update found.");
                    break;
			default:
				break;
            }
        }
    }

    private void loadCommands() {
        this.getCommand("delhome").setExecutor(new DeleteHomeCommand(homeManager));
        this.getCommand("home").setExecutor(new HomeCommand(homeManager));
        this.getCommand("homelist").setExecutor(new HomeListCommand(homeManager));
        this.getCommand("otherhome").setExecutor(new OtherHomeCommand(this, homeManager));
        this.getCommand("sethome").setExecutor(new SetHomeCommand(homeManager));
        this.getCommand("shreload").setExecutor(new ReloadCommand(this));
    }

    private void loadListeners() {
        this.getServer().getPluginManager().registerEvents(new GatewayListener(homeManager, updateAvailable), this);
    }
}
