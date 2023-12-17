package net.fruitservers.deathmessages.GUI.Prompts;

import net.fruitservers.deathmessages.Config.PlayerMessenger;
import net.fruitservers.deathmessages.Plugin;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public abstract class MessagePrompt extends StringPrompt {

    protected final Plugin plugin;

    protected final PlayerMessenger pm;

    protected final UUID target;

    public MessagePrompt(Plugin plugin, UUID target) {
        this.plugin = plugin;
        this.pm = plugin.getConfigManager().getPlayerMessenger();
        this.target = target;
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
