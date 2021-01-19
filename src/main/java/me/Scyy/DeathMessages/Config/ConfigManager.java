package me.Scyy.DeathMessages.Config;

import me.Scyy.DeathMessages.Plugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ConfigManager {

    private final CustomMessageManager deathMessageManager;
    private final PlayerMessenger playerMessenger;
    private final Settings settings;
    private final Plugin plugin;

    /**
     * Load all configs in
     * @param plugin the plugin to get Plugin data folder references
     */
    public ConfigManager(Plugin plugin) {
        this.plugin = plugin;
        this.playerMessenger = new PlayerMessenger(plugin);
        this.settings = new Settings(plugin);
        this.deathMessageManager = new CustomMessageManager(this);
    }

    /**
     * Reloads all ConfigFiles registered to this handler
     */
    public void reloadConfigs() {
        playerMessenger.reloadConfig();
        settings.reloadConfig();
    }

    public Plugin getPlugin() {
        return plugin;
    }

    /**
     * Get the Player Messenger ConfigFile
     * @return the Player Messenger
     */
    public PlayerMessenger getPlayerMessenger() {
        return playerMessenger;
    }

    public CustomMessageManager getDeathMessageManager() {
        return deathMessageManager;
    }

    /**
     * Get the default Settings ConfigFile
     * @return the Settings
     */
    public Settings getSettings() {
        return settings;
    }

    public Map<UUID, CustomDeathMessageFile> loadDeathMessages() {
        File playerFile = new File(plugin.getDataFolder(), "players");
        if (!playerFile.exists()) {
            playerFile.mkdirs();
            return new HashMap<>();
        }

        // TODO - hot load player data when they join

        Map<UUID, CustomDeathMessageFile> messageFileMap = new HashMap<>();

        for (File file : playerFile.listFiles()) {
            String targetUUID = file.getName().replace(".yml", "");
            UUID target = null;
            try {
                target = UUID.fromString(targetUUID);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Could not load UUID of a player! Some custom player messages WILL be missing! Please report this bug!");
                return messageFileMap;
            }
            CustomDeathMessageFile deathMessageFile = new CustomDeathMessageFile(plugin, target);
            messageFileMap.put(target, deathMessageFile);
        }

        return messageFileMap;
    }

}
