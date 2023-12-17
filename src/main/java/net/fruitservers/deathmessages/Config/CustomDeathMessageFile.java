package net.fruitservers.deathmessages.Config;

import net.fruitservers.deathmessages.Plugin;

import java.io.IOException;
import java.util.UUID;

public class CustomDeathMessageFile extends ConfigFile {

    private final UUID uuid;

    public CustomDeathMessageFile(Plugin plugin, UUID uuid) {
        super(plugin, "players/" + uuid.toString() + ".yml", false);
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void addMessage(String message, String path) throws IOException {
        this.config.set(path, message);
        this.config.save(configFile);
    }

    public String getMessage(String path) {
        return this.config.getString(path);
    }
}
