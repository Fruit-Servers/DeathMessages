package me.Scyy.Util.GenericJavaPlugin.Config;

import me.Scyy.Util.GenericJavaPlugin.Plugin;

public class ConfigManager {

    private final PlayerMessenger playerMessenger;
    private final Settings settings;

    /**
     * Load all configs in
     * @param plugin the plugin to get Plugin data folder references
     */
    public ConfigManager(Plugin plugin) {
        this.playerMessenger = new PlayerMessenger(plugin);
        this.settings = new Settings(plugin);
    }

    /**
     * Reloads all ConfigFiles registered to this handler
     */
    public void reloadConfigs() {
        playerMessenger.reloadConfig();
        settings.reloadConfig();
    }

    /**
     * Get the Player Messenger ConfigFile
     * @return the Player Messenger
     */
    public PlayerMessenger getPlayerMessenger() {
        return playerMessenger;
    }

    /**
     * Get the default Settings ConfigFile
     * @return the Settings
     */
    public Settings getSettings() {
        return settings;
    }

}
