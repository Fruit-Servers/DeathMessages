package me.Scyy.DeathMessages.GUI;

import me.Scyy.DeathMessages.GUI.Prompts.EntityMessagePrompt;
import me.Scyy.DeathMessages.Plugin;
import me.Scyy.DeathMessages.Util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

// # # # # # # # # #
// # 0 0 0 0 0 0 0 #
// # 0 0 0 0 0 0 0 #
// # 0 0 0 0 0 0 0 #
// # 0 0 0 0 0 0 0 #
// # B # # E # # F #
// B - back page
// E - back to home
// P - Forward page

public class EntityListGUI extends InventoryGUI {

    private final UUID target;

    private int page;

    public EntityListGUI(InventoryGUI lastGUI, Plugin plugin, Player player, UUID target, int page) {
        super(lastGUI, "&6Death Messages", plugin, 54, player);

        this.page = page;
        this.target = target;
        List<ItemStack> items = plugin.getConfigManager().getSettings().getEntityItems();

        // Index of the warp list
        int startIndex = 28 * page;
        int endIndex = 28 * (page + 1);

        // Index of the GUI inventory
        int invIndex = 10;

        // Iterate over the player warps
        for (int i = startIndex; i < endIndex; i++) {

            // Put the item in the array if it is accessible, otherwise add air
            if (i < items.size()) {

                // Add the item
                inventoryItems[invIndex] = items.get(i);

            } else {

                // Add an empty value to the inventory array
                inventoryItems[invIndex] = null;

            }

            invIndex++;

            // Check if the inventory slot is one from the edge, and move index to other side
            if ((invIndex - 8) % 9 == 0) invIndex += 2;

        }

        // Check if the page is not 0 and if so add the previous pagination arrow
        if (page != 0) {

            inventoryItems[46] = new ItemBuilder(Material.ARROW).name("&6Page " + page).build();

        }

        // determine the page number
        int nextPageNum = page + 2;

        // Add the next pagination arrow
        inventoryItems[52] = new ItemBuilder(Material.ARROW).name("&6Page " + nextPageNum).build();

        // Add the back button
        inventoryItems[49] = new ItemBuilder(Material.BARRIER).name("&6Back to Home").build();

    }

    @Override
    public InventoryGUI handleClick(InventoryClickEvent event) {

        int click = event.getRawSlot();

        event.setCancelled(true);

        // slot of the warp in the filtered warp list
        int listSlot = -1;

        // Check if the item clicked was a valid list slot
        if (9 < click && click < 17) listSlot = click - 10;
        else if (18 < click && click < 26) listSlot = click - 12;
        else if (27 < click && click < 35) listSlot = click - 14;
        else if (36 < click && click < 42) listSlot = click - 16;

        // If the user clicks on a valid item
        if (listSlot != -1 && inventoryItems[click] != null) {

            ItemStack clicked = inventoryItems[click];
            EntityType selected = this.getType(clicked);

            // Error Checking
            if (selected == EntityType.UNKNOWN) {
                player.sendMessage("Could not determine selected mob. Please report this bug!!");
                return new EntityListGUI(this, plugin, player, target, page);
            }

            // Start a Conversation
            ConversationFactory factory = new ConversationFactory(plugin);
            Conversation conv = factory.withFirstPrompt(new EntityMessagePrompt(plugin, selected)).withLocalEcho(false)
                    .buildConversation(player);
            conv.begin();

            this.close = true;

            return new UninteractableGUI(this, plugin, player);

        }

        // Back a page
        if (click == 46 && inventoryItems[46].getType() == Material.ARROW) {
            return new EntityListGUI(this, plugin, player, target, --page);
        }

        // Forward a page
        if (click == 52 && inventoryItems[52].getType() == Material.ARROW) {
            return new EntityListGUI(this, plugin, player, target, ++page);
        }

        // Back to home
        if (click == 49 && inventoryItems[49].getType() == Material.BARRIER) {
            return new HomeGUI(this, plugin, player, target);
        }

        return new EntityListGUI(this, plugin, player, target, page);

    }

    private EntityType getType(ItemStack item) {
        List<String> lore = item.getLore();
        if (lore == null) return EntityType.UNKNOWN;
        String rawEntity = lore.get(lore.size() - 1).split(": ")[1];
        try {
            return EntityType.valueOf(rawEntity);
        } catch (IllegalArgumentException e) {
            return EntityType.UNKNOWN;
        }
    }

    @Override
    public boolean allowPlayerInventoryEdits() {
        return false;
    }
}
