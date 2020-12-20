package me.Scyy.Util.GenericJavaPlugin.Config;

import me.Scyy.Util.GenericJavaPlugin.Plugin;

public class Settings extends ConfigFile {

    /**
     * Create a ConfigFile for the default 'config.yml' file
     * Intended to be used as a read-only file, it is highly recommended that
     *  {@link org.bukkit.configuration.file.YamlConfiguration#set(String, Object)} is not used on this file as comments will be overwritten
     * @param plugin the Plugin
     */
    public Settings(Plugin plugin) {
        super(plugin, "config.yml");
    }
}
