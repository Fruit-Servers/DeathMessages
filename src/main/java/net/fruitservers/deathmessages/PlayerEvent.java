package net.fruitservers.deathmessages;

import net.fruitservers.deathmessages.DeathHandler.Cause;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerEvent implements Listener {

    private final Plugin plugin;

    public PlayerEvent(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeathEvent(PlayerDeathEvent event) {

        if (!plugin.isSendMessages()) return;

        // Broadcasting of messages is handled by MessageUtils
        event.setDeathMessage(null);

        if (plugin.getWorldGuardManager().isRestrictAll(event.getEntity())) return;

        Cause handler = new Cause(plugin, event).delegateHandler();
        handler.sendMessage();
    }

}
