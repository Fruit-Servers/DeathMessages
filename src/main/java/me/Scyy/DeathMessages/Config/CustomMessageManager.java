package me.Scyy.DeathMessages.Config;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class CustomMessageManager {

    private final Map<UUID, CustomDeathMessageFile> files;

    private final ConfigManager manager;

    public CustomMessageManager(ConfigManager manager, Map<UUID, CustomDeathMessageFile> files) {
        this.files = files;
        this.manager = manager;
    }

    public ConfigManager getManager() {
        return manager;
    }

    public boolean addMessage(UUID uuid, String message, String path) {

        // Verify the file exists
        CustomDeathMessageFile file = files.get(uuid);
        if (file == null) {
            this.createFile(uuid);
            return this.addMessage(uuid, message, path);
        }

        try {
            file.addMessage(message, path);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void createFile(UUID uuid) {
        this.files.put(uuid, new CustomDeathMessageFile(manager.getPlugin(), uuid));
    }

}
