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
package net.lankylord.simplehomes.storage;

import com.google.common.io.Files;
import net.lankylord.simplehomes.SimpleHomes;
import net.lankylord.simplehomes.configuration.ConfigManager;
import net.lankylord.simplehomes.util.UUIDFetcher;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class HomeFileManager {

    private final static String fileName = "Homes.yml";
    private final SimpleHomes instance;
    private YamlConfiguration homes;
    private File homesFile;

    public HomeFileManager(SimpleHomes instance) {
        this.instance = instance;
    }

    public YamlConfiguration getHomes() {
        if (homes == null) {
            this.reloadHomes();
        }
        return homes;
    }

    private void reloadHomes() {
        if (homesFile == null) {
            homesFile = new File(instance.getDataFolder(), fileName);
        }
        homes = YamlConfiguration.loadConfiguration(homesFile);

        InputStream defHomes = instance.getResource(fileName);
        if (defHomes != null) {
            // What don't we do when the Bukkit staff randomly deprecates methods?
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new BufferedReader(new InputStreamReader(defHomes)));
            homes.setDefaults(defConfig);
        }
    }

    public void saveHomes() {
        if (homes == null || homesFile == null) {
            return;
        }
        try {
            getHomes().save(homesFile);
        } catch (IOException e) {
            instance.getLogger().log(Level.SEVERE, "Could not save homes file to " + homesFile, e);
        }
    }

    public void UuidUpdate() {
        File oldFile = new File(instance.getDataFolder(), fileName + ".bak");

        instance.getLogger().info("Beginning UUID conversion. This might take a while...");
        YamlConfiguration oldConfig = getHomes();
        YamlConfiguration result = new YamlConfiguration();

        Map<String, UUID> users, lusers;
        try {
            users = new UUIDFetcher(new ArrayList<>(oldConfig.getKeys(false))).call();

            lusers = new HashMap<>(users.size());
            for (Map.Entry<String, UUID> e : users.entrySet()) {
                lusers.put(e.getKey().toLowerCase(), e.getValue());
            }
        } catch (Exception e) {
            return;
        }

        for (String key : oldConfig.getKeys(false)) {
            try {
                String target = lusers.get(key.toLowerCase()).toString();
                ConfigurationSection section = result.createSection(target);
                ConfigurationSection oldSection = oldConfig.getConfigurationSection(key);

                for (String homeName : oldSection.getKeys(false)) {
                    ConfigurationSection homeSection = section.createSection(homeName);
                    ConfigurationSection oldHome = oldSection.getConfigurationSection(homeName);
                    homeSection.set("world", oldHome.get("world"));
                    homeSection.set("x", oldHome.get("x"));
                    homeSection.set("y", oldHome.get("y"));
                    homeSection.set("z", oldHome.get("z"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            if (homesFile == null) {
                homesFile = new File(instance.getDataFolder(), fileName);
            }
            Files.copy(homesFile, oldFile);
            result.save(homesFile);
            reloadHomes();
            instance.getConfig().options().copyDefaults(true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        instance.getConfig().set("ConfigVersion", ConfigManager.CONFIG_VERSION);
        instance.getLogger().info("UUID conversion completed successfully.");
    }
}
