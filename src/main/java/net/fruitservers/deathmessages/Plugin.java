package net.fruitservers.deathmessages;

import net.fruitservers.deathmessages.Config.ConfigManager;
import net.fruitservers.deathmessages.Config.Settings;
import net.fruitservers.deathmessages.GUI.GUIListener;
import net.fruitservers.deathmessages.WorldGuard.WorldGuardManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

public class Plugin extends JavaPlugin {

    private ConfigManager configManager;

    private WorldGuardManager worldGuardManager;

    private boolean sendMessages = true;

    @Override
    public void onEnable() {

        // Register the Config Manager
        this.configManager = new ConfigManager(this);

        // WorldGuardManager is loaded during onLoad()

        // Register the Listeners
        Bukkit.getPluginManager().registerEvents(new PlayerEvent(this), this);
        Bukkit.getPluginManager().registerEvents(new GUIListener(this), this);

        // Register the command
        AdminCommand command = new AdminCommand(this);
        this.getCommand("fdm").setExecutor(command);
        this.getCommand("fdm").setTabCompleter(command);

    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    /**
     * Reload all configs registered by the {@link ConfigManager} for this plugin
     * @param sender Output for messages
     */
    public void reload(CommandSender sender) {
        try {
            sender.sendMessage("Reloading...");
            configManager.reloadConfigs();
            sender.sendMessage("Successfully reloaded!");
        } catch (Exception e) {
            sender.sendMessage("Error reloading! Check console for logs!");
            e.printStackTrace();
        }
    }

    @Override
    public void onLoad() {
        this.worldGuardManager = new WorldGuardManager(this);
        if(getServer().getPluginManager().getPlugin("WorldGuard") != null) {
            worldGuardManager.registerFlags();
        }
    }

    /**
     * Get the Settings for this plugin, each defined in config.yml
     * @return the Settings
     */
    public Settings getSettings() {
        return configManager.getSettings();
    }

    /**
     * Provides a bit of information about the plugin
     * @return the splash text
     */
    public List<String> getSplashText() {
        return Arrays.asList(
                ChatColor.translateAlternateColorCodes('&', "PLUGIN_NAME v" + getDescription().getVersion()), "Built by _Scyy");
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public WorldGuardManager getWorldGuardManager() {
        return worldGuardManager;
    }

    public void toggleMessages(CommandSender sender) {
        this.sendMessages = !sendMessages;
        if (sendMessages) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Death Messages have been &aenabled"));
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Death Messages have been &cdisabled"));
        }
    }

    public boolean isSendMessages() {
        return sendMessages;
    }
}
