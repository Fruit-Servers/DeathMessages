package net.fruitservers.deathmessages.GUI;

import net.fruitservers.deathmessages.Plugin;
import net.fruitservers.deathmessages.Util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.UUID;

// # # # E # O # # #
// E - Entity list
// O - other death type list

public class HomeGUI extends InventoryGUI {

    private final UUID target;

    public HomeGUI(InventoryGUI lastGUI, Plugin plugin, Player player, UUID target) {
        super(lastGUI, "&8Death Messages", plugin, 9, player);

        this.target = target;

        inventoryItems[3] = new ItemBuilder(Material.CREEPER_HEAD).name("&6Mobs").build();
        inventoryItems[5] = new ItemBuilder(Material.GRAY_DYE).name("&6Other").build();
    }

    @Override
    public InventoryGUI handleClick(InventoryClickEvent event) {

        int click = event.getRawSlot();

        event.setCancelled(true);

        if (click == 3) {
            return new EntityListGUI(this, plugin, player, target, 0);
        }

        if (click == 5) {
            return new OtherTypeListGUI(this, plugin, player, target, 0);
        }

        return new HomeGUI(this, plugin, player, target);

    }

    @Override
    public boolean allowPlayerInventoryEdits() {
        return false;
    }
}
