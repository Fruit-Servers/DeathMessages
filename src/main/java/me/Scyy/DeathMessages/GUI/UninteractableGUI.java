package me.Scyy.DeathMessages.GUI;

import me.Scyy.DeathMessages.Plugin;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class UninteractableGUI extends InventoryGUI {

    public UninteractableGUI(InventoryGUI lastGUI, Plugin plugin, Player player) {
        super(lastGUI, lastGUI.getName(), plugin, lastGUI.getSize(), player);

        this.inventoryItems = lastGUI.getInventoryItems();
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public InventoryGUI handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        return this;
    }

    @Override
    public boolean allowPlayerInventoryEdits() {
        return false;
    }
}
