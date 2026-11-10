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

import net.lankylord.simplehomes.SimpleHomes;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

}
