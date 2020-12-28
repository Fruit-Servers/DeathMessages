package me.Scyy.DeathMessages;

import me.Scyy.DeathMessages.Util.MessageUtils;
import me.Scyy.DeathMessages.WorldGuard.WorldGuardManager;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class PlayerOofEvent implements Listener {

    // Placeholders
    public static final String PLAYER_PLACEHOLDER = "%player%";
    public static final String KILLER_PLACEHOLDER = "%killer%";
    public static final String ITEM_PLACEHOLDER = "%item%";
    public static final String ITEM_NAME_PLACEHOLDER = "%item_name%";
    public static final String FALL_HEIGHT_PLACEHOLDER = "%fall_height%";

    public static final String KILLER_PLACEHOLDER_NA = "KILLER_NOT_APPLICABLE";
    public static final String ITEM_PLACEHOLDER_NA = "ITEM_HOVER_NOT_APPLICABLE";
    public static final String ITEM_NAME_PLACEHOLDER_NA = "ITEM_NAME_NOT_APPLICABLE";
    public static final String FALL_HEIGHT_PLACEHOLDER_NA = "FALL_HEIGHT_NOT_APPLICABLE";

    private final Plugin plugin;

    private final Random random;

    public PlayerOofEvent(Plugin plugin) {
        this.plugin = plugin;
        this.random = new Random();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeathEvent(PlayerDeathEvent event) {

        // Check if messages have been toggled off
        if (!plugin.isSendMessages()) {
            event.setDeathMessage(null);
            return;
        }

        // Get the player and their associated Damage Event
        Player player = event.getEntity();
        EntityDamageEvent damageEvent = player.getLastDamageCause();

        Set<String> worlds = plugin.getConfigManager().getPlayerMessenger().getConfig().getKeys(false);

        // Verify the Damage Event
        if (player.getLastDamageCause() == null) {
            throw new IllegalArgumentException("Could not get cause of death for death of " + event.getEntity().toString());
        }

        // Last thing that killed the player was an entity - so handle it differently
        if (damageEvent instanceof EntityDamageByEntityEvent) {
            this.killedByEntity((EntityDamageByEntityEvent) damageEvent, worlds);
            event.setDeathMessage(null);
            return;
        }

        String messagePath = worlds.contains(player.getWorld().getName()) ? player.getWorld().getName() + "." : "DEFAULT.";

        DamageCause cause = damageEvent != null ? player.getLastDamageCause().getCause() : DamageCause.CUSTOM;

        // Ignore Entity Messages - They are handled by EntityDamageByEntityEvent
        switch (cause) {
            case ENTITY_ATTACK:
            case ENTITY_EXPLOSION:
            case ENTITY_SWEEP_ATTACK:
                plugin.getLogger().warning("Death to entity not correctly triggered! Please report this bug!");
                plugin.getLogger().warning("Player: " + player.getName() + ", Cause: " + cause.toString());
                return;
        }

        // Placeholders
        String playerName = player.getName();
        String fallHeight = FALL_HEIGHT_PLACEHOLDER_NA;

        // If the event was a fall, set the fall height
        if (cause == DamageCause.FALL) {
            fallHeight = Integer.toString((int) player.getLastDamageCause().getDamage() + 3);
        }

        messagePath += cause.toString();
        List<String> messages = plugin.getConfigManager().getPlayerMessenger().getListMsg(messagePath,
                PLAYER_PLACEHOLDER, playerName, KILLER_PLACEHOLDER, KILLER_PLACEHOLDER_NA, ITEM_PLACEHOLDER,
                ITEM_PLACEHOLDER_NA, ITEM_NAME_PLACEHOLDER, ITEM_NAME_PLACEHOLDER_NA, FALL_HEIGHT_PLACEHOLDER,
                fallHeight);

        String message = messages.get(random.nextInt(messages.size()));

        // Send the message
        MessageUtils.broadcast(message, plugin, player, null);

    }

    private void killedByEntity(EntityDamageByEntityEvent event, Set<String> worlds) {

        // Get the Player and Killer from this event
        Player player = (Player) event.getEntity();
        Entity killer = event.getDamager();

        // Initialise the message path based on the world
        String messagePath = "DEFAULT.";
        String worldName = player.getWorld().getName();
        if (worlds.contains(worldName)) messagePath = worldName + ".";

        // Initialise Placeholders
        String playerName = player.getName();
        String killerName = killer.getName();
        String itemName = ITEM_NAME_PLACEHOLDER_NA;
        BaseComponent item = new TextComponent(ITEM_PLACEHOLDER_NA);

        // If a player killed the other player
        if (killer instanceof Player) {
            Player playerKiller = (Player) killer;
            itemName = this.getMainHandName(playerKiller);
            item = this.getMainHandHoverable(playerKiller);
        }

        // If the player was shot by an arrow
        if (killer instanceof Projectile) {
            Projectile projectile = (Projectile) killer;
            if (projectile.getShooter() instanceof Player) {
                Player playerKiller = (Player) projectile.getShooter();
                itemName = this.getMainHandName(playerKiller);
                item = this.getMainHandHoverable(playerKiller);
                killer = playerKiller;
            } else if (projectile.getShooter() instanceof Entity) {
                killer = (Entity) projectile.getShooter();
            } else {
                this.killedByUnknown(event, worlds);
            }
        }

        // Update the message path
        messagePath += "ENTITY." + killer.getType().toString();

        // Get a message, factoring in all placeholders
        List<String> messages = plugin.getConfigManager().getPlayerMessenger().getListMsg(messagePath,
                PLAYER_PLACEHOLDER, playerName, KILLER_PLACEHOLDER, killerName,
                ITEM_NAME_PLACEHOLDER, itemName, FALL_HEIGHT_PLACEHOLDER, FALL_HEIGHT_PLACEHOLDER_NA);

        // Get a random message from the list
        String selectedMessage = messages.get(random.nextInt(messages.size()));

        MessageUtils.broadcast(selectedMessage, plugin, player, item);

    }

    private void killedByUnknown(EntityDamageEvent event, Set<String> worlds) {

        if (!(event.getEntity() instanceof Player)) throw new IllegalArgumentException("Player death event not triggered by a player!");

        Player player = (Player) event.getEntity();
        String playerName = player.getName();

        String messagePath = "DEFAULT.CUSTOM";
        String worldName = player.getWorld().getName();
        if (worlds.contains(worldName)) messagePath = worldName + ".CUSTOM";

        // Get a message, factoring in all placeholders
        List<String> messages = plugin.getConfigManager().getPlayerMessenger().getListMsg(messagePath,
                PLAYER_PLACEHOLDER, playerName, KILLER_PLACEHOLDER, KILLER_PLACEHOLDER_NA,
                ITEM_NAME_PLACEHOLDER, ITEM_NAME_PLACEHOLDER_NA, FALL_HEIGHT_PLACEHOLDER, FALL_HEIGHT_PLACEHOLDER_NA);

        // Get a random message from the list
        String selectedMessage = messages.get(random.nextInt(messages.size()));

        MessageUtils.broadcast(selectedMessage, plugin, player, null);

    }

    /**
     * Utility method for getting the name of the players item they used
     * @param player the player
     * @return name of the item
     */
    private String getMainHandName(Player player) {
        ItemStack mainHand;
        if (player.getMainHand() == MainHand.RIGHT) mainHand = player.getInventory().getItemInMainHand();
        else mainHand = player.getInventory().getItemInOffHand();
        if (mainHand.getType() == Material.AIR) return "Fists";
        return (mainHand.getItemMeta() != null ? mainHand.getItemMeta().getDisplayName() : mainHand.getType().name());
    }

    /**
     * Utility method for composing an interactable message that displays the item used in chat
     * @param player the player
     * @return the hoverable text to append to the message
     */
    private BaseComponent getMainHandHoverable(Player player) {
        ItemStack mainHand;
        if (player.getMainHand() == MainHand.RIGHT) mainHand = player.getInventory().getItemInMainHand();
        else mainHand = player.getInventory().getItemInOffHand();
        if (mainHand.getType() == Material.AIR) return  new TextComponent(this.getMainHandName(player));
        return MessageUtils.composeItem(mainHand);
    }

}
