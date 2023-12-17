package net.fruitservers.deathmessages.DeathHandler;

import net.fruitservers.deathmessages.Plugin;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;

public class EntityCause extends Cause {

    private final EntityDamageByEntityEvent damageEvent;

    // Placeholder information
    private String killerName;
    private EntityType killerType;

    public EntityCause(Plugin plugin, PlayerDeathEvent event, EntityDamageByEntityEvent damageEvent) {
        super(plugin, event);
        this.damageEvent = damageEvent;
        this.killerType = damageEvent.getDamager().getType();
        this.killerName = damageEvent.getDamager().getName();
    }

    @Override
    public void sendMessage() {

        if (killerType == EntityType.PRIMED_TNT) {
            super.sendMessage("BLOCK_EXPLOSION");
            return;
        }

        if (killerType == EntityType.LIGHTNING) {
            super.sendMessage("LIGHTNING");
            return;
        }

        // Check what kind of entity damaged the player
        String itemName = ITEM_NAME_PLACEHOLDER;
        if (damageEvent.getDamager() instanceof Player) {
            Player playerKiller = (Player) damageEvent.getDamager();
            killerName = playerKiller.getName();
            killerType = EntityType.PLAYER;
            itemName = this.getMainHandName(playerKiller);
            hoverableItem = this.getMainHand(playerKiller);
        } else if (damageEvent.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) damageEvent.getDamager();
            // Projectile was shot by a player
            if (projectile.getShooter() instanceof Player) {
                Player playerKiller = (Player) projectile.getShooter();
                killerName = playerKiller.getName();
                killerType = EntityType.PLAYER;
                itemName = this.getMainHandName(playerKiller);
                hoverableItem = this.getMainHand(playerKiller);
            // Projectile was shot by an entity
            } else if (projectile.getShooter() instanceof Entity) {
                Entity killer = (Entity) projectile.getShooter();
                killerName = killer.getCustomName() != null ? killer.getCustomName() : killer.getName();
                killerType = killer.getType();
                // TODO - add ability to see what item the entity used?
            // Unknown killer (most likely a dispenser, very rare scenario)
            } else {
                killerType = EntityType.UNKNOWN;
            }
        }

        // Assign placeholders
        this.messageReplacements = new String[] {
                PLAYER_PLACEHOLDER, player.getName(), KILLER_PLACEHOLDER, killerName,
                ITEM_NAME_PLACEHOLDER, itemName
        };

        // Compose the message
        String messagePath = "ENTITY." + killerType.name();

        super.sendMessage(messagePath, "ENTITY.DEFAULT");

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
    private ItemStack getMainHand(Player player) {
        ItemStack mainHand;
        if (player.getMainHand() == MainHand.RIGHT) mainHand = player.getInventory().getItemInMainHand();
        else mainHand = player.getInventory().getItemInOffHand();
        return mainHand;
    }
}
