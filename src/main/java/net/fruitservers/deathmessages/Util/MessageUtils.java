package net.fruitservers.deathmessages.Util;

import net.fruitservers.deathmessages.Config.PlayerMessenger;
import net.fruitservers.deathmessages.Plugin;
import net.fruitservers.deathmessages.WorldGuard.WorldGuardManager;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.regex.Pattern;

public class MessageUtils {

    public static TextComponent composeItem(@Nullable ItemStack itemStack) {

        if (itemStack == null || itemStack.getType() == Material.AIR) return new TextComponent("their Fists");

        String itemName = itemStack.getItemMeta() != null && itemStack.getItemMeta().hasDisplayName() ? itemStack.getItemMeta().getDisplayName() : itemStack.getI18NDisplayName();
        StringBuilder builder = new StringBuilder(itemName).append("\n");

        if (itemStack.hasItemMeta() && itemStack.getItemMeta() != null) {

            ItemMeta meta = itemStack.getItemMeta();
            if (meta.hasEnchants()) {
                if (!meta.hasDisplayName()) builder.insert(0, ChatColor.AQUA);

                for (Enchantment enchantment : meta.getEnchants().keySet()) {
                    int level = meta.getEnchantLevel(enchantment);
                    builder.append(ChatColor.GRAY).append(Enchantments.getPrintable(enchantment, level)).append("\n");
                }
            }
            if (meta instanceof EnchantmentStorageMeta) {
                EnchantmentStorageMeta ESmeta = (EnchantmentStorageMeta) meta;
                for (Enchantment enchantment : ESmeta.getStoredEnchants().keySet()) {
                    int level = ESmeta.getStoredEnchantLevel(enchantment);
                    builder.append(ChatColor.GRAY).append(Enchantments.getPrintable(enchantment, level)).append("\n");
                }
            }
            if (meta.hasLore() && meta.getLore() != null) {
                for (String loreLine : meta.getLore()) {
                    if (loreLine.startsWith("#") || loreLine.startsWith(String.valueOf(ChatColor.COLOR_CHAR))) {
                        builder.append(loreLine).append("\n");
                    } else {
                        builder.append(ChatColor.DARK_PURPLE).append(loreLine).append("\n");
                    }
                }
            }

        }

        // Remove the trailing new line
        builder.delete(builder.length() - 1, builder.length());

        TextComponent component = new TextComponent(itemName);
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(builder.toString())));

        return component;

    }

    public static void broadcast(@NotNull String message, @NotNull Plugin plugin, @NotNull Player player, @Nullable ItemStack hoverableItem) {

        WorldGuardManager manager = plugin.getWorldGuardManager();

        TextComponent hoverable = composeItem(hoverableItem);

        BaseComponent[] finalMessage = PlayerMessenger.toComponent(message, hoverable);

        if (plugin.getSettings().logMessages() && plugin.isSendMessages()) {
            Bukkit.getConsoleSender().spigot().sendMessage(finalMessage);
        }

        // Check if the player is in a regioned area
        if (manager.isWorldGuardEnabled() && manager.isRestrictBroadcast(player)) {
            plugin.getWorldGuardManager().sendRestrictedBroadcast(player, finalMessage);
        } else {
            for (Player online : Bukkit.getOnlinePlayers()) {
                online.spigot().sendMessage(finalMessage);
            }
        }

    }

    public static String reduceCapitalisation(String message) {
        int capsCount = 0;
        for (int i = 0; i < message.length() - 1; i++) {
            char c = message.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                capsCount++;
            }
        }
        if (capsCount > 5) return message.toLowerCase(Locale.ROOT);
        else return message;
    }

}
