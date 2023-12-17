package net.fruitservers.deathmessages.GUI;

import net.fruitservers.deathmessages.Plugin;
import net.fruitservers.deathmessages.Util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public abstract class InventoryGUI implements InventoryHolder {

    /**
     * A reference to the GUI before this one. Null if the GUI system was just opened
     */
    protected InventoryGUI lastGUI;

    /**
     * Main plugin reference
     */
    protected final Plugin plugin;

    /**
     * The array of items in the inventory
     */
    protected ItemStack[] inventoryItems;

    /**
     * The inventory to be displayed to the user
     */
    protected final Inventory inventory;

    /**
     * Player currently viewing this GUI
     */
    protected final Player player;

    /**
     * Name of the GUI that is displayed at the top
     */
    protected final String name;

    /**
     * Size of the GUI, also found from {@code inventoryItems.length}
     */
    protected final int size;

    /**
     * Flag for if the listener should close the inventory, overrides reopen
     */
    protected boolean close = false;

    public InventoryGUI(InventoryGUI lastGUI, String name, Plugin plugin, int size, Player player) {
        this.lastGUI = lastGUI;
        this.plugin = plugin;
        this.player = player;
        this.name = name;
        this.size = size;
        this.inventoryItems = initialiseDefaultPage(size);
        this.inventory = Bukkit.createInventory(this, size, ChatColor.translateAlternateColorCodes('&', name));

    }

    /**
     * Creates a double chest full of nameless black stained glass panes
     * @return the itemstack array with the glass panes
     */
    private ItemStack[] initialiseDefaultPage(int size) {
        ItemStack[] defaultInv = new ItemStack[size];
        for (int i = 0; i < size; i++) {
            defaultInv[i] = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(" ").build();
        }

        return defaultInv;

    }

    public String getName() {
        return this.name;
    }

    public int getSize() {
        return this.size;
    }

    public abstract InventoryGUI handleClick(InventoryClickEvent event);

    // Getters and Setters
    public InventoryGUI getLastGUI() {
        return lastGUI;
    }

    public void setLastGUI(InventoryGUI lastGUI) {
        this.lastGUI = lastGUI;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public ItemStack[] getInventoryItems() {
        return inventoryItems;
    }

    public Inventory getInventory() {

        // Assign the inventory items to the inventory
        inventory.setContents(inventoryItems);

        // Return the inventory
        return inventory;
    }

    public boolean shouldClose() {
        return close;
    }

    public abstract boolean allowPlayerInventoryEdits();

}
