package net.fruitservers.deathmessages.Config;

import com.google.common.base.Charsets;
import net.fruitservers.deathmessages.Plugin;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Abstract representation for classes responsible for handing config files. A reference to the handler of this ConfigFile
 * is intentionally not provided to allow for multiple instances of managers. Possible feature - interface for managers
 */
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
     * Loads a virtual representation of the configuration file at the given path
     * @param plugin the Plugin class
     * @param configFilePath path to the file from this plugins Data Folder
     * @param loadFromResourceFolder if the configuration to load comes from the resources folder of the plugin
     */
    public ConfigFile(Plugin plugin, String configFilePath, boolean loadFromResourceFolder) {

        // Save the plugin reference
        this.plugin = plugin;

        // Save the message file path
        this.configFilePath = configFilePath;

        // Save the messages file
        this.configFile = new File(plugin.getDataFolder(), configFilePath);

        // Check if the file exists
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            if (loadFromResourceFolder) {
                plugin.saveResource(configFilePath, false);
            } else {
                try {
                    configFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

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
