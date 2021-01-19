package me.Scyy.DeathMessages.Util;

import me.Scyy.DeathMessages.Config.PlayerMessenger;
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

    public static void broadcast(@NotNull String message, @NotNull Plugin plugin, @NotNull Player player, @Nullable BaseComponent hoverable) {

        WorldGuardManager manager = plugin.getWorldGuardManager();

        BaseComponent[] finalMessage = PlayerMessenger.toComponent(message, hoverable, null);

        // Check if the player is in a regioned area
        if (manager.isWorldGuardEnabled() && manager.isRestrictBroadcast(player)) {
            plugin.getWorldGuardManager().sendRestrictedBroadcast(player, finalMessage);
        } else {
            for (Player online : Bukkit.getOnlinePlayers()) {
                online.spigot().sendMessage(finalMessage);
            }
        }

    }

    public static String placeholders(String message, String... replacements) {

        // Manage Message Replacements
        if (replacements != null && replacements[0] != null) {

            if (replacements.length % 2 != 0) throw new IllegalArgumentException("Not all placeholders have a corresponding replacement");

            for (int i = 0; i < replacements.length; i += 2) {

                String placeholder = replacements[i];
                String replacement = replacements[i + 1];

                message = message.replaceAll(placeholder, replacement);

            }

            return message;

        }

        return message;

    }

}
