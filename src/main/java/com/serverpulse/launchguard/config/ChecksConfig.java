package com.serverpulse.launchguard.config;

import com.serverpulse.launchguard.LaunchGuardPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ChecksConfig {

    private final LaunchGuardPlugin plugin;
    private FileConfiguration checksConfig;
    private File checksFile;

    public ChecksConfig(LaunchGuardPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        saveDefaultResource("checks.yml");
        checksFile = new File(plugin.getDataFolder(), "checks.yml");
        checksConfig = loadYamlSafe(checksFile, "checks.yml");
        if (checksConfig == null) {
            checksConfig = new YamlConfiguration();
        }
        applyDefaults("checks.yml");
    }

    public boolean reload() {
        if (checksFile == null) {
            checksFile = new File(plugin.getDataFolder(), "checks.yml");
        }
        if (!checksFile.exists()) {
            saveDefaultResource("checks.yml");
        }

        FileConfiguration newConfig = loadYamlSafe(checksFile, "checks.yml");
        if (newConfig == null) {
            plugin.getLogger().warning("checks.yml reload failed; keeping previous configuration.");
            return false;
        }
        checksConfig = newConfig;
        applyDefaults("checks.yml");
        return true;
    }

    public boolean isCheckEnabled(String checkId) {
        if (checksConfig == null) return false;
        return checksConfig.getBoolean("checks." + checkId + ".enabled", false);
    }

    public Map<String, Object> getCheckConfig(String checkId) {
        if (checksConfig == null) return new LinkedHashMap<>();
        ConfigurationSection section = checksConfig.getConfigurationSection("checks." + checkId);
        if (section == null) {
            return new LinkedHashMap<>();
        }
        return toMap(section);
    }

    private Map<String, Object> toMap(ConfigurationSection section) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (String key : section.getKeys(false)) {
            Object value = section.get(key);
            if (value instanceof ConfigurationSection) {
                map.put(key, toMap((ConfigurationSection) value));
            } else if (value instanceof List) {
                map.put(key, new ArrayList<>((List<?>) value));
            } else {
                map.put(key, value);
            }
        }
        return map;
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
            checksConfig.setDefaults(defaultConfig);
        }
    }

    private void saveDefaultResource(String resourceName) {
        File file = new File(plugin.getDataFolder(), resourceName);
        if (!file.exists()) {
            plugin.saveResource(resourceName, false);
        }
    }
}
