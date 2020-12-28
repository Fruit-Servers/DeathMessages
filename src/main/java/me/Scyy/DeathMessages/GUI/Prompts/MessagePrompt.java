package me.Scyy.DeathMessages.GUI.Prompts;

import me.Scyy.DeathMessages.Config.PlayerMessenger;
import me.Scyy.DeathMessages.Plugin;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class MessagePrompt extends StringPrompt {

    protected final Plugin plugin;

    protected final PlayerMessenger pm;

    public MessagePrompt(Plugin plugin) {
        this.plugin = plugin;
        this.pm = plugin.getConfigManager().getPlayerMessenger();
    }

    @NotNull
    @Override
    public String getPromptText(@NotNull ConversationContext conversationContext) {
        return this.getPrompt();
    }

    public abstract String getPrompt();

    @Nullable
    @Override
    public abstract Prompt acceptInput(@NotNull ConversationContext conversationContext, @Nullable String s);
}
