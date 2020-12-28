package me.Scyy.DeathMessages.GUI.Prompts;

import me.Scyy.DeathMessages.Plugin;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EntityMessagePrompt extends MessagePrompt {

    private final EntityType type;

    public EntityMessagePrompt(Plugin plugin, EntityType type) {
        super(plugin);
        this.type = type;
    }

    @Override
    public String getPrompt() {
        return plugin.getConfigManager().getPlayerMessenger().getMsg("guiMessages.newMessagePrompt", "%cause%", type.name());
    }

    @Override
    public @Nullable Prompt acceptInput(@NotNull ConversationContext conversationContext, @Nullable String s) {

        if (s == null) return this;

        if (s.equalsIgnoreCase("cancel")) {
            conversationContext.getForWhom().sendRawMessage(pm.getMsg("guiMessages.messageCancelled", "%cause%", type.name()));
        }

        if (!(conversationContext.getForWhom() instanceof Player)) {
            conversationContext.getForWhom().sendRawMessage("Not a player! Please report this bug!");
        }



        String tl = ChatColor.translateAlternateColorCodes('&', s);

        Player player = (Player) conversationContext.getForWhom();

        boolean success = plugin.getConfigManager().getDeathMessageManager().addMessage(player.getUniqueId(), tl, "ENTITY." + type.name());

        if (!success) {
            conversationContext.getForWhom().sendRawMessage("Could not save your message! Please report this bug!");
        }
        conversationContext.getForWhom().sendRawMessage(pm.getMsg("guiMessages.messageSaved", "%message%", tl));
        return null;
    }
}
