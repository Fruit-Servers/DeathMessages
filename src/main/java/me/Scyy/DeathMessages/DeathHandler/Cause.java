package me.Scyy.DeathMessages.DeathHandler;

import me.Scyy.DeathMessages.Config.CustomMessageManager;
import me.Scyy.DeathMessages.Config.PlayerMessenger;
import me.Scyy.DeathMessages.Plugin;
import me.Scyy.DeathMessages.Util.MessageUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.List;
import java.util.Random;

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

    protected BaseComponent hoverableItem = new TextComponent(ITEM_PLACEHOLDER);

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

        return this;
    }

    public void sendMessage() {
        this.sendMessage(playerDamageEvent.getCause().name());
    }

    public void sendMessage(String initialMessagePath) {

        this.messagePath = initialMessagePath;

        String message = messageFileManager.getCustomMessage(player.getUniqueId(), messagePath);

        if (message != null) this.message(message);

        messagePath = worldPrefix + messagePath;

        List<String> messages = pm.getRawListMsg(messagePath, messageReplacements);

        if (messages == null) messages = pm.getRawListMsg(worldPrefix + "CUSTOM", messageReplacements);

        // Get a random message
        String randomMessage = messages.get(random.nextInt(messages.size()));

        randomMessage = PlayerMessenger.markForHoverEvent(randomMessage, "%item%");

        this.message(randomMessage);

    }

    private void message(String message) {
        MessageUtils.broadcast(message, plugin, player, hoverableItem);
    }

}
