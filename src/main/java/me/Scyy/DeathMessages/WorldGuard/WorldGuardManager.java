package me.Scyy.DeathMessages.WorldGuard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.Scyy.DeathMessages.Plugin;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class WorldGuardManager {

    private final Plugin plugin;

    public static final StateFlag RESTRICT_BROADCAST = new StateFlag("fdm-restrictbroadcast", false);

    private boolean enabled;

    public WorldGuardManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public boolean isRestrictBroadcast(Player player) {
        if (!enabled) return false;
        Location loc = new Location(BukkitAdapter.adapt(player.getLocation().getWorld()),
                player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
        ApplicableRegionSet regions = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().getApplicableRegions(loc);
        return regions.testState(null, RESTRICT_BROADCAST);
    }

    public void sendRestrictedBroadcast(Player player, BaseComponent[] message) {

        if (!enabled) return;

        // Get a list of all regions the player is currently in
        Location loc = new Location((Extent) player.getLocation().getWorld(),
                player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
        ApplicableRegionSet regions = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().getApplicableRegions(loc);

        // Iterate over each region
        for (ProtectedRegion region : regions.getRegions()) {

            // Check if the region has the required flag and is of type allow
            if (region.getFlag(RESTRICT_BROADCAST) != null && region.getFlag(RESTRICT_BROADCAST) == StateFlag.State.ALLOW) {

                // Iterate over every online player and check if they are in the region
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {

                    // If the player is in the region
                    if (region.contains(onlinePlayer.getLocation().getBlockX(), onlinePlayer.getLocation().getBlockY(), onlinePlayer.getLocation().getBlockZ())) {
                        onlinePlayer.spigot().sendMessage(message);
                    }

                }

            }

        }

    }

    public void registerFlags() {

        try {
            FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();

            try {
                // register our flag with the registry
                registry.register(RESTRICT_BROADCAST);
                plugin.getLogger().info("World Guard flags registered");
                this.enabled = true;
            } catch (FlagConflictException e) {
                e.printStackTrace();
                plugin.getLogger().warning("World Guard flags not registered!");
            }
        } catch (NoClassDefFoundError e) {
            System.out.println("[DeathMessages] World Guard instance not found!"); //Don't use the Logger here
        }

    }

    public boolean isWorldGuardEnabled() {
        return enabled;
    }

}
