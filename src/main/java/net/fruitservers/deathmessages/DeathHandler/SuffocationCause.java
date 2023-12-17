package net.fruitservers.deathmessages.DeathHandler;

import net.fruitservers.deathmessages.Plugin;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.entity.PlayerDeathEvent;

public class SuffocationCause extends Cause {

    public SuffocationCause(Plugin plugin, PlayerDeathEvent event) {
        super(plugin, event);
        Location pLoc = player.getLocation();
        Location bLoc = new Location(pLoc.getWorld(), pLoc.getBlockX(), pLoc.getBlockY() + 1, pLoc.getBlockZ());
        Block block = bLoc.getBlock();
        this.messageReplacements = new String[] {
                "%player%", player.getName(), "%block%", block.getType().name()
        };
    }
}
