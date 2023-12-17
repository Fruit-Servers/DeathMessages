package net.fruitservers.deathmessages.Config;

import net.fruitservers.deathmessages.Plugin;
import net.fruitservers.deathmessages.Util.ItemBuilder;
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

    public List<EntityType> getEntityItemTypes() {
        List<EntityType> entities = new ArrayList<>();
        ConfigurationSection entitySection = config.getConfigurationSection("ENTITY");

        if (entitySection == null) {
            plugin.getLogger().warning("No Custom Entity GUI elements found in config");
            return entities;
        }

        for (String rawEntity : entitySection.getKeys(false)) {
            try {
                entities.add(EntityType.valueOf(rawEntity));
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning(rawEntity + " is not a valid entity type");
            }
        }
        return entities;
    }

    public ItemStack getEntityItem(EntityType type) {
        return this.getItem("ENTITY." + type.name());
    }

    public List<DamageCause> getOtherItemTypes() {
        List<DamageCause> causes = new ArrayList<>();
        ConfigurationSection otherSection = config.getConfigurationSection("OTHER");

        if (otherSection == null) {
            plugin.getLogger().warning("No Custom Entity GUI elements found in config");
            return causes;
        }

        for (String rawCause : otherSection.getKeys(false)) {
            try {
                causes.add(DamageCause.valueOf(rawCause));
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning(rawCause + " is not a valid death cause");
            }
        }
        return causes;
    }

    public ItemStack getOtherItem(DamageCause cause) {
        return this.getItem("OTHER." + cause.name());
    }

    public List<ItemStack> getOtherDeathItems() {
        return otherDeathItems;
    }

    private ItemStack getItem(String path) {
        String name = config.getString(path + ".name");
        if (name == null) name = "ITEM_NAME_NOT_FOUND";
        String itemType = config.getString(path + ".itemType");
        List<String> lore = config.getStringList(path + ".lore");
        Material material = Material.STONE;
        try {
            material = Material.valueOf(itemType);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Could not create an item! " + itemType + " is not a valid item type");
            return new ItemStack(material);
        }
        return new ItemBuilder(material).name(name).lore(lore).build();
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

    public List<String> getWorlds() {
        return config.getStringList("worlds");
    }

    public boolean logMessages() {
        return config.getBoolean("logCustomDeathMessages", true);
    }

    public String getCustomMessagePrefix() {
        return config.getString("customMessagePrefix", "");
    }

    public String getPlayerNameFormat() {
        return config.getString("playerNameFormat", "&3&o%player%&r&7");
    }
}
