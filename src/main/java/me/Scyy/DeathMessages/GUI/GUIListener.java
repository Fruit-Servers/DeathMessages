package me.Scyy.DeathMessages.GUI;

import me.Scyy.DeathMessages.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.PlayerInventory;

public class GUIListener implements Listener {

    private final Plugin plugin;

    public GUIListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {

        // Check if the click was a null click
        if (event.getView().getTopInventory().getHolder() == null) return;

        // Check if the inventory clicked is an inventory defined by this plugin
        if (!(event.getView().getTopInventory().getHolder() instanceof InventoryGUI)) return;

        InventoryGUI oldGUI = (InventoryGUI) event.getView().getTopInventory().getHolder();

        if (!oldGUI.allowPlayerInventoryEdits() && event.getClickedInventory() instanceof PlayerInventory) {
            event.setCancelled(true);
            return;
        }

        InventoryGUI newGUI = oldGUI.handleClick(event);

        if (oldGUI.shouldClose()) {

            Bukkit.getScheduler().runTask(plugin, () -> event.getWhoClicked().closeInventory());
            return;

        }

        // Reopen the new inventory
        Bukkit.getScheduler().runTask(plugin, () -> event.getWhoClicked().openInventory(newGUI.getInventory()));



    }

}
