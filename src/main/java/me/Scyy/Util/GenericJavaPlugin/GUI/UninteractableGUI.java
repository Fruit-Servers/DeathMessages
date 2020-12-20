package me.Scyy.Util.GenericJavaPlugin.GUI;

import me.Scyy.Util.GenericJavaPlugin.Plugin;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class UninteractableGUI extends InventoryGUI {

    /**
     * Copies everything from the last GUI
     * @param lastGUI the GUI pen before this one
     * @param plugin the Plugin
     * @param player the player viewing this GUI
     */
    public UninteractableGUI(InventoryGUI lastGUI, Plugin plugin, Player player) {
        super(lastGUI, plugin, player, lastGUI.name, lastGUI.size);

        this.inventoryItems = lastGUI.getInventoryItems();
    }

    /**
     * Denies any sort of interaction with the GUI
     * @param event The click event in the GUI
     * @return this same GUI
     */
    @Override
    public InventoryGUI handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        return this;
    }

    /**
     * Denies the ability to interact with the players own inventory
     * @return false
     */
    @Override
    public boolean allowPlayerInventoryEdits() {
        return false;
    }
}
