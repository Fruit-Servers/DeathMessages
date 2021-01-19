package me.Scyy.DeathMessages.DeathHandler;

import me.Scyy.DeathMessages.Plugin;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class FallCause extends Cause {

    private final long fallHeight;

    public FallCause(Plugin plugin, PlayerDeathEvent deathEvent, EntityDamageEvent playerDamageEvent) {
        super(plugin, deathEvent);
        this.fallHeight = Math.round(playerDamageEvent.getFinalDamage() + 3);
    }

    @Override
    public void sendMessage() {

        this.messageReplacements = new String[] {
                PLAYER_PLACEHOLDER, player.getName(), FALL_HEIGHT_PLACEHOLDER, Long.toString(fallHeight)
        };

        super.sendMessage(EntityDamageEvent.DamageCause.FALL.name());

    }
}
