package me.Scyy.DeathMessages.Util;

import me.Scyy.DeathMessages.PlayerOofEvent;
import me.Scyy.DeathMessages.Plugin;
import me.Scyy.DeathMessages.WorldGuard.WorldGuardManager;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MessageUtils {

    public static BaseComponent composeItem(ItemStack itemStack) {

        String itemName = itemStack.getItemMeta() != null && itemStack.getItemMeta().hasDisplayName() ? itemStack.getItemMeta().getDisplayName() : itemStack.getType().toString();

        BaseComponent component = new TextComponent(itemName);

        StringBuilder itemDescriptor = new StringBuilder();

        // Verify the ItemMeta is valid
        if (itemStack.getItemMeta() != null) {

            // Add on any enchants
            if (itemStack.getItemMeta().hasEnchants()) {
                for (Enchantment enchantment : itemStack.getItemMeta().getEnchants().keySet()) {
                    itemDescriptor.append(Enchantments.getPrintable(enchantment));
                }
            }

            // Add on any stored enchants
            if (itemStack.getItemMeta() instanceof EnchantmentStorageMeta) {
                for (Enchantment enchantment : ((EnchantmentStorageMeta) itemStack.getItemMeta()).getStoredEnchants().keySet()) {
                    itemDescriptor.append(Enchantments.getPrintable(enchantment));
                }
            }

            // Add on lore
            if (itemStack.getItemMeta().getLore() != null) {
                for (String loreLine : itemStack.getItemMeta().getLore()) {
                    itemDescriptor.append(loreLine);
                }
            }

        }

        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new Text(itemDescriptor.toString())));

        return component;


    }

    public static BaseComponent composeMessage(@NotNull String rawMessage, @Nullable BaseComponent itemReplacement) {

        if (itemReplacement == null) itemReplacement = new TextComponent(PlayerOofEvent.ITEM_PLACEHOLDER_NA);

        String[] splitMessage = rawMessage.split(PlayerOofEvent.ITEM_PLACEHOLDER);
        BaseComponent message = new TextComponent();

        for (int i = 0; i < splitMessage.length; i++) {
            String split = splitMessage[i];
            message.addExtra(split);
            if (i < splitMessage.length - 1) message.addExtra(itemReplacement);
        }

        return message;

    }

    public static void broadcast(@NotNull String message, @NotNull Plugin plugin, @NotNull Player player, @Nullable BaseComponent hoverable) {

        BaseComponent finalMessage = MessageUtils.composeMessage(message, hoverable);

        WorldGuardManager manager = plugin.getWorldGuardManager();

        // Check if the player is in a regioned area
        if (manager.isWorldGuardEnabled() && manager.isRestrictBroadcast(player)) {
            plugin.getWorldGuardManager().sendRestrictedBroadcast(player, finalMessage);
        } else {
            Bukkit.broadcast(finalMessage);
        }

    }

}
