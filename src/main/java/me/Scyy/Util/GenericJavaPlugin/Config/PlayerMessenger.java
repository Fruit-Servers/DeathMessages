package me.Scyy.Util.GenericJavaPlugin.Config;

import me.Scyy.Util.GenericJavaPlugin.Plugin;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerMessenger extends ConfigFile {

    /**
     * Regex pattern for finding hex codes for a given String
     */
    public static final Pattern hex = Pattern.compile("#[a-fA-F0-9]{6}");

    /**
     * Prefix for messages, can be ignored if set to "" in messages.yml
     */
    private final String prefix;

    /**
     * Constructs a player messenger with the plugin and declares the prefix for messages
     * @param plugin reference to the plugin
     */
    public PlayerMessenger(Plugin plugin) {
        super(plugin, "messages.yml");

        // Get the prefix
        String rawPrefix = config.getString("prefix");
        if (rawPrefix != null) this.prefix = ChatColor.translateAlternateColorCodes('&', rawPrefix);
        else this.prefix = "[COULD_NOT_LOAD_PREFIX]";

    }

    /**
     * For sending a message from messages.yml - use [NO_PREFIX]
     * @param sender The user to send the message to
     * @param path the path of the message in messages.yml
     */
    public void msg(CommandSender sender, String path) {
        this.msg(sender, path, (String) null);
    }

    /**
     * For sending a message with parts in the message that contains placeholders
     * @param sender The user to send the message to
     * @param path the path of the message in messages.yml
     * @param replacements an array of replacements with the placeholder and their replacements in pairs e.g.
     *                     "%player%", player.getName(), "%entity%", entity.getName() etc...
     */
    public void msg(CommandSender sender, String path, String... replacements) {
        String finalMessage = this.getMsg(path, replacements);
        if (finalMessage.equals("")) return;
        sender.sendMessage(finalMessage);
    }

    /**
     * For getting a message from messages.yml
     * @param path the path of the message in messages.yml
     */
    public String getMsg(String path) {
        return this.getMsg(path, (String) null);
    }

    /**
     * For getting a message with parts in the message that contains placeholders
     * @param path the path of the message in messages.yml
     * @param replacements an array of replacements with the placeholder and their replacements in pairs e.g.
     *                     "%player%", player.getName(), "%entity%", entity.getName() etc...
     */
    public String getMsg(String path, String... replacements) {

        String rawMessage = config.getString(path);

        if (rawMessage == null) return "Could not find message at " + path;


        if (rawMessage.equalsIgnoreCase("")) return "";

        String messagePrefix = "";

        if (!rawMessage.startsWith("[NO_PREFIX]")) {
            messagePrefix = prefix + " ";
        } else {
            rawMessage = rawMessage.substring(11);
        }

        // Manage Message Replacements
        if (replacements != null && replacements[0] != null) {

            if (replacements.length % 2 != 0) throw new IllegalArgumentException("Not all placeholders have a corresponding replacement");

            for (int i = 0; i < replacements.length; i += 2) {

                String placeholder = replacements[i];
                String replacement = replacements[i + 1];

                rawMessage = rawMessage.replaceAll(placeholder, replacement);

            }

        }

        // Replace hex colour codes
        Matcher hexMatcher = hex.matcher(rawMessage);
        while (hexMatcher.find()) {
            String hexCode = rawMessage.substring(hexMatcher.start(), hexMatcher.end());
            rawMessage = rawMessage.replace(hexCode, ChatColor.of(hexCode).toString());
        }

        // Translate normal colour codes and return the message
        return messagePrefix + ChatColor.translateAlternateColorCodes('&', rawMessage);

    }

    /**
     * For sending multiple messages stored as a list in messages.yml. Does not include the prefix
     * @param sender the user to send the messages to
     * @param path the path of the message list in messages.yml
     */
    public void msgList(CommandSender sender, String path) {
        this.msgList(sender, path, (String) null);
    }

    /**
     * For sending multiple messages stored as a list in messages.yml. Does not include the prefix
     * @param sender the user to send the messages to
     * @param path the path of the message list in messages.yml
     * @param replacements an array of replacements with the placeholder and their replacements in pairs e.g.
     *                     "%player%", player.getName(), "%entity%", entity.getName() etc...
     */
    public void msgList(CommandSender sender, String path, String... replacements) {
        for (String message : this.getListMsg(path, replacements)) {
            sender.sendMessage(message);
        }
    }

    /**
     * For getting multiple messages stored as a list in messages.yml. Ignores the prefix
     * @param path the path of the message list in messages.yml
     */
    public List<String> getListMsg(String path) {
        return this.getListMsg(path, (String) null);
    }

    /**
     * For getting multiple messages stored as a list in messages.yml. Ignores the Prefix
     * @param path the path of the message list in messages.yml
     * @param replacements an array of replacements with the placeholder and their replacements in pairs e.g.
     *                     "%player%", player.getName(), "%entity%", entity.getName() etc...
     */
    public List<String> getListMsg(String path, String... replacements) {

        List<String> rawList = config.getStringList(path);
        List<String> list = new LinkedList<>();

        if (rawList.size() == 0) return Collections.singletonList("Could not find message at " + path);

        for (String item : rawList) {

            // Manage Message Replacements
            if (replacements != null && replacements[0] != null) {

                if (replacements.length % 2 != 0) throw new IllegalArgumentException("Not all placeholders have a corresponding replacement");

                for (int i = 0; i < replacements.length; i += 2) {

                    String placeholder = replacements[i];
                    String replacement = replacements[i + 1];

                    item = item.replaceAll(placeholder, replacement);

                }

            }

            Matcher hexMatcher = hex.matcher(item);
            while (hexMatcher.find()) {
                String hexCode = item.substring(hexMatcher.start(), hexMatcher.end());
                item = item.replace(hexCode, ChatColor.of(hexCode).toString());
            }

            list.add(item);

        }

        return list;

    }

}
