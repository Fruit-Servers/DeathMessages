package me.Scyy.DeathMessages.DeathHandler;

import me.Scyy.DeathMessages.Plugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class BlockCause extends Cause {

    private final EntityDamageByBlockEvent damageEvent;

    public BlockCause(Plugin plugin, PlayerDeathEvent event, EntityDamageByBlockEvent damageEvent) {
        super(plugin, event);
        this.damageEvent = damageEvent;
    }

    @Override
    public void sendMessage() {

        if (damageEvent.getDamager() == null) {
            super.sendMessage(EntityDamageEvent.DamageCause.CUSTOM.name());
        }

        Material killerType = damageEvent.getDamager().getType();

        // Placeholders
        this.messageReplacements = new String[] {
                PLAYER_PLACEHOLDER, player.getName(), BLOCK_PLACEHOLDER, killerType.name(),
                KILLER_PLACEHOLDER, killerType.name()
        };

        switch (killerType) {
            case SWEET_BERRY_BUSH:
            case CACTUS:
                super.sendMessage("CONTACT." + killerType.name());
        }

        if (damageEvent.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION) {

            Location deathLocation = player.getLocation();
            // Safe call as it fires where the player was, so guaranteed to be rendered
            Block block = deathLocation.getWorld().getBlockAt(deathLocation.getBlockX(), deathLocation.getBlockY() + 1, deathLocation.getBlockZ());

            this.messageReplacements = new String[] {
                    PLAYER_PLACEHOLDER, player.getName(), BLOCK_PLACEHOLDER, block.getType().toString(),
                    KILLER_PLACEHOLDER, block.getType().toString()
            };

            super.sendMessage(EntityDamageEvent.DamageCause.SUFFOCATION.name());
        }

        super.sendMessage();


    }
}
