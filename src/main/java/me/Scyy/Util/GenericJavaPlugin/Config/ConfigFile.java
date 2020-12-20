package me.Scyy.Util.GenericJavaPlugin.Config;

import com.google.common.base.Charsets;
import me.Scyy.Util.GenericJavaPlugin.Plugin;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public abstract class ConfigFile {

    /**
     * The main plugin instance
     */
    protected final Plugin plugin;

    /**
     * The interactable configuration for getting and setting of objects
     */
    protected YamlConfiguration config;

    /**
     * The file for the configuration
     */
    protected final File configFile;

    /**
     * File path of the config file from the Plugins data folder
     */
    protected final String configFilePath;

    /**
     * Attaches the configuration getter/setter to the File specified at {@code configFilePath} or if the file is not found
     * Loads one from the plugin files
     * @param plugin the Plugin class
     * @param configFilePath path to the file from this plugins Data Folder
     */
    public ConfigFile(Plugin plugin, String configFilePath) {

        // Save the plugin reference
        this.plugin = plugin;

        // Save the message file path
        this.configFilePath = configFilePath;

        // Save the messages file
        this.configFile = new File(plugin.getDataFolder(), configFilePath);

        // Check if the file exists
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            plugin.saveResource(configFilePath, false);
        }

        // Create the yml reference
        this.config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException ex) {
            ex.printStackTrace();
        }

    }

    /**
     * Reloads config by reading updating the reference to the file
     */
    public void reloadConfig() {
        try {
            config = YamlConfiguration.loadConfiguration(configFile);
            InputStream defIMessagesStream = plugin.getResource(configFilePath);
            if (defIMessagesStream != null) {
                config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defIMessagesStream, Charsets.UTF_8)));
            }
        } catch (Exception ex) {
            plugin.getLogger().warning("Could not reload config at " + this.getConfigFilePath());
            ex.printStackTrace();
        }
    }

    /**
     * Gets the configuration getter/setter
     * @return the configuration getter/setter
     */
    public YamlConfiguration getConfig() {
        return config;
    }

    /**
     * Gets the File for this ConfigFile
     * @return the File
     */
    public File getConfigFile() {
        return configFile;
    }

    /**
     * Gets the path to the File (including the file) from the plugins data folder
     * @return the path
     */
    public String getConfigFilePath() {
        return configFilePath;
    }

    /**
     * Sets the configuration getter/setter. Required so that the file can be reloaded
     * @param config the configuration getter/setter
     */
    public void setConfig(YamlConfiguration config) {
        this.config = config;
    }
}
