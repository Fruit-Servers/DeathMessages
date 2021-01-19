package me.Scyy.DeathMessages.Config;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CustomMessageManager {

    private final Map<UUID, CustomDeathMessageFile> files;

    private final ConfigManager manager;

    public CustomMessageManager(ConfigManager manager) {
        this.manager = manager;
        this.files = new HashMap<>();
    }

    public ConfigManager getManager() {
        return manager;
    }

    public void loadMessageFile(UUID uuid) {
        if (files.containsKey(uuid)) return;
        this.files.put(uuid, new CustomDeathMessageFile(manager.getPlugin(), uuid));
    }

    public void unloadMessageFile(UUID uuid) {
        if (!files.containsKey(uuid)) return;
        // Does not appear to be a way to 'unload' a config file
        // hopefully the garbage collector cleans up with the loss of reference
        this.files.remove(uuid);
    }

    public boolean messageFileExists(UUID uuid) {
        return files.containsKey(uuid);
    }

    public boolean addMessage(UUID uuid, String message, String path) {
        if (!files.containsKey(uuid)) return false;
        try {
            files.get(uuid).addMessage(message, path);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getCustomMessage(UUID uuid, String path) {
        if (!files.containsKey(uuid)) return null;
        return files.get(uuid).getMessage(path);
    }

}
