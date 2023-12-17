package net.fruitservers.deathmessages.GUI.Prompts;

import net.fruitservers.deathmessages.Plugin;
import net.fruitservers.deathmessages.Util.MessageUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

public class EntityMessagePrompt extends MessagePrompt {

    private final EntityType type;

    public EntityMessagePrompt(Plugin plugin, UUID target, EntityType type) {
        super(plugin, target);
        this.type = type;
    }

    @Override
    public String getPrompt() {
        String rawPrompt = pm.getRawMsg("guiMessages.newMessagePrompt", "%cause%", type.name());
        return ChatColor.translateAlternateColorCodes('&', rawPrompt);
    }

    @Override
    public @Nullable Prompt acceptInput(@NotNull ConversationContext conversationContext, @Nullable String s) {

        if (!(conversationContext.getForWhom() instanceof Player)) {
            conversationContext.getForWhom().sendRawMessage("Not a player! Please report this bug!");
            return null;
        }

        Player player = (Player) conversationContext.getForWhom();

        if (s == null) return this;

        if (s.equalsIgnoreCase("cancel")) {
            String rawMessage = pm.getRawMsg("guiMessages.messageCancelled", "%cause%", type.name());
            conversationContext.getForWhom().sendRawMessage(ChatColor.translateAlternateColorCodes('&', rawMessage));
            return null;
        }

        if (s.equalsIgnoreCase("none")) {
            String rawMessage = pm.getRawMsg("guiMessages.messageCancelled", "%cause%", type.name());
            conversationContext.getForWhom().sendRawMessage(ChatColor.translateAlternateColorCodes('&', rawMessage));
            plugin.getConfigManager().getDeathMessageManager().addMessage(player.getUniqueId(), null, "ENTITY." + type.name());
            return null;
        }

        // Prevent the player from formatting their message
        s = s.replaceAll("&", "");
        s = ChatColor.stripColor(s);

        // Prevents people spamming caps in their messages
        s = MessageUtils.reduceCapitalisation(s);

        // Format the players name to be fancy looking
        String nameFormat = plugin.getSettings().getPlayerNameFormat().replace("%player%", player.getName());
        String regex = "(?i)" + Pattern.quote(player.getName().toLowerCase(Locale.ROOT));
        s = s.replaceAll(regex, nameFormat);

        boolean success = plugin.getConfigManager().getDeathMessageManager().addMessage(target, s, "ENTITY." + type.name());

        if (!success) {
            conversationContext.getForWhom().sendRawMessage("Could not save your message! Please report this bug!");
            return null;
        }

        String rawMessage = pm.getRawMsg("guiMessages.messageSaved", "%message%", s, "%cause%", type.name());
        conversationContext.getForWhom().sendRawMessage(ChatColor.translateAlternateColorCodes('&', rawMessage));
        return null;
    }
}
