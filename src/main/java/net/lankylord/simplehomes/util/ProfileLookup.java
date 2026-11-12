package net.lankylord.simplehomes.util;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.destroystokyo.paper.profile.PlayerProfile;

public class ProfileLookup {

    private ProfileLookup() {}

    public static CompletableFuture<UUID> getUUIDFromName(String name) {
        Player onlinePlayer = Bukkit.getPlayerExact(name);
        if (onlinePlayer != null) {
            return CompletableFuture.completedFuture(onlinePlayer.getUniqueId());
        }

        OfflinePlayer cached = Bukkit.getOfflinePlayerIfCached(name);
        if (cached != null) {
            return CompletableFuture.completedFuture(cached.getUniqueId());
        }

        PlayerProfile profile = Bukkit.createProfile(name);
        return CompletableFuture.supplyAsync(() -> {
            if (profile.complete(false)) {
                return profile.getId();
            }
            return null;
        });
    }

    public static CompletableFuture<String> getNameFromUUID(UUID uuid) {
        Player onlinePlayer = Bukkit.getPlayer(uuid);
        if (onlinePlayer != null) {
            return CompletableFuture.completedFuture(onlinePlayer.getName());
        }

        OfflinePlayer cached = Bukkit.getOfflinePlayer(uuid);
        if (cached != null) {
            return CompletableFuture.completedFuture(cached.getName());
        }

        PlayerProfile profile = Bukkit.createProfile(uuid);
        return CompletableFuture.supplyAsync(() -> {
            if (profile.complete(false)) {
                return profile.getName();
            }
            return null;
        });
    }
    
}
