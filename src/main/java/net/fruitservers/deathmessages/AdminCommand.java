package net.fruitservers.deathmessages;

import net.fruitservers.deathmessages.Config.PlayerMessenger;
import net.fruitservers.deathmessages.GUI.HomeGUI;
import net.fruitservers.deathmessages.GUI.InventoryGUI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AdminCommand implements TabExecutor {

    private final Plugin plugin;

    private final PlayerMessenger pm;

    public AdminCommand(Plugin plugin) {
        this.plugin = plugin;
        this.pm = plugin.getConfigManager().getPlayerMessenger();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        // GUI
        if (args.length == 0) {
            if (!sender.hasPermission("fdm.gui")) {
                pm.msg(sender, "errorMessages.noPermission"); return true;
            }
            if (!(sender instanceof Player player)) {
                pm.msg(sender, "errorMessages.mustBePlayer"); return true;
            }

            // Initialise the players Message file if it's their first time running the command
            if (!plugin.getConfigManager().getDeathMessageManager().messageFileExists(player.getUniqueId())) {
                plugin.getConfigManager().getDeathMessageManager().loadMessageFile(player.getUniqueId());
            }

            InventoryGUI gui = new HomeGUI(null, plugin, player, player.getUniqueId());
            player.openInventory(gui.getInventory());
            return true;
        }

        // IDE flags this with a warning for not enough case statements - ignored due to more commands expected when plugin is created
        switch (args[0]) {
            case "reload":
                if (!sender.hasPermission("fdm.reload")) {
                    pm.msg(sender, "errorMessages.noPermission"); return true;
                }
                plugin.reload(sender);
                return true;
            // GUI
            case "manage":
                if (!sender.hasPermission("fdm.gui.others")) {
                    pm.msg(sender, "errorMessages.noPermission"); return true;
                }
                if (!(sender instanceof Player)) {
                    pm.msg(sender, "errorMessages.mustBePlayer"); return true;
                }
                this.manageSubcommand((Player) sender, args);
            return true;
            case "toggle":
                if (!sender.hasPermission("fdm.toggle")) {
                    pm.msg(sender, "errorMessages.noPermission"); return true;
                }
                plugin.toggleMessages(sender);
                return true;
            case "about":
                for (String message : plugin.getSplashText()) {
                    sender.sendMessage(message);
                }
                return true;
            default:
                pm.msg(sender, "errorMessages.invalidCommand");
                return true;
        }

    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        List<String> list = new ArrayList<>();

        // IDE flags this with a warning for not enough case statements - ignored due to more commands expected when plugin is created
        switch (args.length) {
            case 1:
                if (sender.hasPermission("fdm.reload")) list.add("reload");
                if (sender.hasPermission("fdm.toggle")) list.add("toggle");
                if (sender.hasPermission("fdm.gui.others")) list.add("manage");
                return list;
            case 2:
                if (sender.hasPermission("fdm.gui.others") && args[0].equalsIgnoreCase("manage")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        list.add(player.getName());
                    }
                    return list;
                }
            default:
                return list;
        }
    }

    private void manageSubcommand(Player player, String[] args) {

        if (args.length < 2) {
            pm.msg(player, "errorMessages.invalidCommandLength");
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (!target.hasPlayedBefore()) {
            pm.msg(player, "errorMessages.playerNotFound"); return;
        }

        // If the players data file has not been loaded, load it now
        plugin.getConfigManager().getDeathMessageManager().loadMessageFile(target.getUniqueId());

        InventoryGUI gui = new HomeGUI(null, plugin, player, target.getUniqueId());
        player.openInventory(gui.getInventory());

    }
}
