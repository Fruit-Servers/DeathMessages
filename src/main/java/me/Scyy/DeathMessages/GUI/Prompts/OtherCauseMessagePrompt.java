package me.Scyy.DeathMessages.GUI.Prompts;

import me.Scyy.DeathMessages.Plugin;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OtherCauseMessagePrompt extends MessagePrompt {

    private final DamageCause cause;

    public OtherCauseMessagePrompt(Plugin plugin, DamageCause cause) {
        super(plugin);
        this.cause = cause;
    }

    @Override
    public String getPrompt() {
        return plugin.getConfigManager().getPlayerMessenger().getRawMsg("guiMessages.newMessagePrompt", "%cause%", cause.name());
    }

    @Override
    public @Nullable Prompt acceptInput(@NotNull ConversationContext conversationContext, @Nullable String s) {

        if (s == null) return this;

        if (s.equalsIgnoreCase("cancel")) {
            conversationContext.getForWhom().sendRawMessage(pm.getRawMsg("guiMessages.messageCancelled", "%cause%", cause.name()));
        }

        if (!(conversationContext.getForWhom() instanceof Player)) {
            conversationContext.getForWhom().sendRawMessage("Not a player! Please report this bug!");
        }

        String tl = ChatColor.translateAlternateColorCodes('&', s);

        Player player = (Player) conversationContext.getForWhom();

        boolean success = plugin.getConfigManager().getDeathMessageManager().addMessage(player.getUniqueId(), tl, cause.name());

        if (!success) {
            conversationContext.getForWhom().sendRawMessage("Could not save your message! Please report this bug!");
        }
        conversationContext.getForWhom().sendRawMessage(pm.getRawMsg("guiMessages.messageSaved", "%message%", tl));
        return null;
    }
}
