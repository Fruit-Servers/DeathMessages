package net.fruitservers.deathmessages.GUI;

import net.fruitservers.deathmessages.Config.CustomMessageManager;
import net.fruitservers.deathmessages.Config.Settings;
import net.fruitservers.deathmessages.GUI.Prompts.OtherCauseMessagePrompt;
import net.fruitservers.deathmessages.Plugin;
import net.fruitservers.deathmessages.Util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
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

public class OtherTypeListGUI extends InventoryGUI {

    private final UUID target;

    private int page;

    private final String typeIdentifier = "&r&8type: &7";

    public OtherTypeListGUI(InventoryGUI lastGUI, Plugin plugin, Player player, UUID target, int page) {
        super(lastGUI, "&8Death Messages", plugin, 54, player);

        Settings settings = plugin.getSettings();
        CustomMessageManager manager = plugin.getConfigManager().getDeathMessageManager();

        this.page = page;
        this.target = target;
        List<DamageCause> causes = settings.getOtherItemTypes();

        // Index of the warp list
        int startIndex = 28 * page;
        int endIndex = 28 * (page + 1);

        // Index of the GUI inventory
        int invIndex = 10;

        // Iterate over the player warps
        for (int i = startIndex; i < endIndex; i++) {

            // Put the item in the array if it is accessible, otherwise add air
            if (i < causes.size()) {

                DamageCause cause = causes.get(i);

                String preview = manager.getCustomMessage(target, cause.name());
                if (preview == null) preview = "N/A";

                preview = ChatColor.translateAlternateColorCodes('&', preview);
                preview = ChatColor.stripColor(preview);

                ItemStack rawItem = settings.getOtherItem(cause);
                if (rawItem.getItemMeta().getLore() != null) {
                    ItemMeta meta = rawItem.getItemMeta();
                    List<String> lore = new ArrayList<>();
                    lore.add(ChatColor.translateAlternateColorCodes('&', typeIdentifier + cause.toString()));
                    for (String line : meta.getLore()) {
                        lore.add(line.replaceAll("%preview%", preview));
                    }
                    meta.setLore(lore);
                    rawItem.setItemMeta(meta);
                }

                // Add the item
                inventoryItems[invIndex] = rawItem;

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
            DamageCause selected = this.getType(clicked);

            // Error Checking
            if (selected == null) {
                player.sendMessage("Could not determine selected death cause. Please report this bug!!");
                return new OtherTypeListGUI(this, plugin, player, target, page);
            }

            // Start a Conversation
            ConversationFactory factory = new ConversationFactory(plugin);
            Conversation conv = factory.withFirstPrompt(new OtherCauseMessagePrompt(plugin, target, selected)).withLocalEcho(false)
                    .buildConversation(player);
            conv.begin();

            this.close = true;

            return new UninteractableGUI(this, plugin, player);

        }

        // Back a page
        if (click == 46 && inventoryItems[46].getType() == Material.ARROW) {
            return new OtherTypeListGUI(this, plugin, player, target, --page);
        }

        // Forward a page
        if (click == 52 && inventoryItems[52].getType() == Material.ARROW) {
            return new OtherTypeListGUI(this, plugin, player, target, ++page);
        }

        // Back to home
        if (click == 49 && inventoryItems[49].getType() == Material.BARRIER) {
            return new HomeGUI(this, plugin, player, target);
        }

        return new OtherTypeListGUI(this, plugin, player, target, page);

    }

    private DamageCause getType(ItemStack item) {
        List<String> lore = item.getLore();
        if (lore == null) return null;
        String rawCause = lore.get(0).split(": ")[1];
        String cause = ChatColor.stripColor(rawCause);
        try {
            return DamageCause.valueOf(cause);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public boolean allowPlayerInventoryEdits() {
        return false;
    }
}
