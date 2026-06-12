package com.serverpulse.launchguard.config;

import com.serverpulse.launchguard.LaunchGuardPlugin;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class MessageManager {

    private final LaunchGuardPlugin plugin;
    private FileConfiguration messages;
    private File messagesFile;

    public MessageManager(LaunchGuardPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        saveDefaultResource("messages.yml");
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        messages = loadYamlSafe(messagesFile, "messages.yml");
        if (messages == null) {
            messages = new YamlConfiguration();
        }
        applyDefaults("messages.yml");
    }

    public boolean reload() {
        if (messagesFile == null) {
            messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        }
        if (!messagesFile.exists()) {
            saveDefaultResource("messages.yml");
        }

        FileConfiguration newMessages = loadYamlSafe(messagesFile, "messages.yml");
        if (newMessages == null) {
            plugin.getLogger().warning("messages.yml reload failed; keeping previous messages.");
            return false;
        }
        messages = newMessages;
        applyDefaults("messages.yml");
        return true;
    }

    public String get(String key) {
        if (messages == null) return "";
        if (key.equals("prefix")) {
            return plugin.getConfigManager().getPrefix();
        }
        return messages.getString(key, "");
    }

    public String get(String key, String defaultValue) {
        if (messages == null) return defaultValue;
        return messages.getString(key, defaultValue);
    }

    private FileConfiguration loadYamlSafe(File file, String resourceName) {
        try {
            YamlConfiguration yaml = new YamlConfiguration();
            yaml.load(file);
            return yaml;
        } catch (InvalidConfigurationException e) {
            plugin.getLogger().warning("Invalid YAML in " + resourceName + ": " + e.getMessage());
            return null;
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load " + resourceName + ": " + e.getMessage());
            return null;
        }
    }

    private void applyDefaults(String resourceName) {
        InputStream defaults = plugin.getResource(resourceName);
        if (defaults != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaults, StandardCharsets.UTF_8));
            messages.setDefaults(defaultConfig);
        }
    }

    private void saveDefaultResource(String resourceName) {
        File file = new File(plugin.getDataFolder(), resourceName);
        if (!file.exists()) {
            plugin.saveResource(resourceName, false);
        }
    }
}
