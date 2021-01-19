package me.Scyy.DeathMessages;

import me.Scyy.DeathMessages.DeathHandler.Cause;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerEvent implements Listener {

    private final Plugin plugin;

    public PlayerEvent(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {

        if (!plugin.isSendMessages()) return;

        // Broadcasting of messages is handled by MessageUtils
        event.setDeathMessage(null);

        Cause handler = new Cause(plugin, event).delegateHandler();
        handler.sendMessage();

    }

}
