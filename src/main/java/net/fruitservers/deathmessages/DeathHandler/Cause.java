package net.fruitservers.deathmessages.DeathHandler;

import net.fruitservers.deathmessages.Config.CustomMessageManager;
import net.fruitservers.deathmessages.Config.PlayerMessenger;
import net.fruitservers.deathmessages.Plugin;
import net.fruitservers.deathmessages.Util.MessageUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Cause {

    protected String messagePath;

    protected String worldPrefix;

    protected final Plugin plugin;

    protected final CustomMessageManager messageFileManager;

    protected final PlayerMessenger pm;

    protected final Player player;

    protected final PlayerDeathEvent deathEvent;

    protected final EntityDamageEvent playerDamageEvent;

    protected final Random random;

    protected String[] messageReplacements;

    protected ItemStack hoverableItem = null;

    public static final String PLAYER_PLACEHOLDER = "%player%";
    public static final String KILLER_PLACEHOLDER = "%killer%";
    public static final String ITEM_PLACEHOLDER = "%item%";
    public static final String ITEM_NAME_PLACEHOLDER = "%item_name%";
    public static final String FALL_HEIGHT_PLACEHOLDER = "%fall_height%";
    public static final String BLOCK_PLACEHOLDER = "%block%";

    public Cause(Plugin plugin, PlayerDeathEvent event) {
        this.plugin = plugin;
        this.messageFileManager = plugin.getConfigManager().getDeathMessageManager();
        this.pm = plugin.getConfigManager().getPlayerMessenger();
        this.deathEvent = event;
        this.player = event.getEntity();
        this.random = new Random();
        this.playerDamageEvent = player.getLastDamageCause();
        List<String> worlds = plugin.getConfigManager().getSettings().getWorlds();
        if (worlds.contains(player.getWorld().getName())) this.worldPrefix = player.getWorld().getName() + ".";
        else this.worldPrefix = "DEFAULT.";
        this.messageReplacements = new String[] {
                "%player%", player.getName()
        };
    }

    public Cause delegateHandler() {

        if (playerDamageEvent == null) throw new IllegalStateException("Player died without cause");

        if (playerDamageEvent instanceof EntityDamageByEntityEvent) {
            return new EntityCause(plugin, deathEvent, (EntityDamageByEntityEvent) playerDamageEvent);
        }

        if (playerDamageEvent instanceof EntityDamageByBlockEvent) {
            return new BlockCause(plugin, deathEvent, (EntityDamageByBlockEvent) playerDamageEvent);
        }

        if (playerDamageEvent.getCause() == DamageCause.FALL) {
            return new FallCause(plugin, deathEvent, playerDamageEvent);
        }

        if (playerDamageEvent.getCause() == DamageCause.SUFFOCATION) {
            return new SuffocationCause(plugin, deathEvent);
        }

        return this;
    }

    public void sendMessage() {
        this.sendMessage(playerDamageEvent.getCause().name());
    }

    public void sendMessage(String initialMessagePath) {
        this.sendMessage(initialMessagePath, "CUSTOM");
    }

    public void sendMessage(String initialMessagePath, String alternativeMessagePath) {

        this.messagePath = initialMessagePath;

        String message = messageFileManager.getCustomMessage(player.getUniqueId(), messagePath);

        // Custom message for the player
        if (message != null) {
            String prefix = plugin.getSettings().getCustomMessagePrefix();
            this.message(prefix + message);
            return;
        }

        messagePath = worldPrefix + messagePath;

        List<String> messages = pm.getRawListMsg(messagePath, messageReplacements);

        // If there are no messages for the specific world
        if (messages.size() == 0) messages = pm.getRawListMsg(worldPrefix + alternativeMessagePath, messageReplacements);

        // If there are no messages for that specific message type
        if (messages.size() == 0) {
            this.message("Could not find message at " + worldPrefix + alternativeMessagePath);
            return;
        }

        // Get a random message
        String randomMessage = messages.get(random.nextInt(messages.size()));

        randomMessage = PlayerMessenger.markForInteractEvent(randomMessage, ITEM_PLACEHOLDER);

        this.message(randomMessage);

    }

    private void message(String message) {
        MessageUtils.broadcast(message, plugin, player, hoverableItem);
    }

}
