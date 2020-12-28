package me.Scyy.DeathMessages.Config;

import me.Scyy.DeathMessages.Plugin;
import me.Scyy.DeathMessages.Util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Settings extends ConfigFile {

    private List<ItemStack> entityItems;

    private List<ItemStack> otherDeathItems;

    /**
     * Create a ConfigFile for the default 'config.yml' file
     * Intended to be used as a read-only file, it is highly recommended that
     *  {@link org.bukkit.configuration.file.YamlConfiguration#set(String, Object)} is not used on this file as comments will be overwritten
     * @param plugin the Plugin
     */
    public Settings(Plugin plugin) {
        super(plugin, "config.yml", true);
        this.entityItems = loadEntityItems();
        this.otherDeathItems = loadOtherDeathItems();
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        this.entityItems = loadEntityItems();
        this.otherDeathItems = loadOtherDeathItems();
    }

    public List<ItemStack> getEntityItems() {
        return entityItems;
    }

    public List<ItemStack> getOtherDeathItems() {
        return otherDeathItems;
    }

    private List<ItemStack> loadEntityItems() throws IllegalArgumentException, NullPointerException {
        ConfigurationSection entities = config.getConfigurationSection("ENTITY");
        List<ItemStack> list = new ArrayList<>();
        if (entities == null || entities.getKeys(false).size() == 0) {
            plugin.getLogger().warning("No entity types found in config.yml");
            return list;
        }

        for (String rawEntity : entities.getKeys(false)) {

            // Verify the entity is valid - error thrown if invalid
            EntityType.valueOf(rawEntity);
            String name = entities.getString(rawEntity + ".name", "NAME_NOT_FOUND_IN_CONFIG");
            assert name != null;
            List<String> lore = entities.getStringList(rawEntity + ".lore");
            // Add the entity type to the lore
            lore.add("&8Type: " + rawEntity);
            Material material = Material.valueOf(entities.getString(rawEntity + ".itemType"));
            ItemStack item = new ItemBuilder(material).name(name).lore(lore).build();
            list.add(item);
        }

        return list;
    }

    private List<ItemStack> loadOtherDeathItems() throws IllegalArgumentException, NullPointerException {
        ConfigurationSection types = config.getConfigurationSection("OTHER");
        List<ItemStack> list = new ArrayList<>();
        if (types == null || types.getKeys(false).size() == 0) {
            plugin.getLogger().warning("No other death types found in config.yml");
            return list;
        }

        for (String rawType : types.getKeys(false)) {

            // Verify the entity is valid - error thrown if invalid
            DamageCause.valueOf(rawType);
            String name = types.getString(rawType + ".name", "NAME_NOT_FOUND_IN_CONFIG");
            assert name != null;
            List<String> lore = types.getStringList(rawType + ".lore");
            // Add the entity type to the lore
            lore.add("&8Type: " + rawType);
            Material material = Material.valueOf(types.getString(rawType + ".itemType", Material.BARRIER.name()));
            ItemStack item = new ItemBuilder(material).name(name).lore(lore).build();
            list.add(item);
        }

        return list;
    }
}
