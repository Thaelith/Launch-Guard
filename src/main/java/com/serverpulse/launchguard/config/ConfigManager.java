package com.serverpulse.launchguard.config;

import com.serverpulse.launchguard.LaunchGuardPlugin;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ConfigManager {

    private final LaunchGuardPlugin plugin;
    private FileConfiguration config;
    private FileConfiguration previousConfig;
    private File configFile;

    public ConfigManager(LaunchGuardPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        saveDefaultResource("config.yml");
        configFile = new File(plugin.getDataFolder(), "config.yml");
        config = loadYamlSafe(configFile, "config.yml");
        if (config == null) {
            config = new YamlConfiguration();
        }
        applyDefaults("config.yml");
    }

    public boolean reloadConfig() {
        if (configFile == null) {
            configFile = new File(plugin.getDataFolder(), "config.yml");
        }
        if (!configFile.exists()) {
            saveDefaultResource("config.yml");
        }

        if (config != null) {
            previousConfig = config;
        }

        FileConfiguration newConfig = loadYamlSafe(configFile, "config.yml");
        if (newConfig == null) {
            plugin.getLogger().warning("config.yml reload failed; keeping previous configuration.");
            return false;
        }
        config = newConfig;
        applyDefaults("config.yml");
        return true;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public String getPrefix() {
        if (config == null) return "[LaunchGuard]";
        return config.getString("settings.prefix", "[LaunchGuard]");
    }

    public boolean showPassedChecks() {
        if (config == null) return true;
        return config.getBoolean("settings.showPassedChecks", true);
    }

    public boolean reportToConsole() {
        if (config == null) return true;
        return config.getBoolean("settings.reportToConsole", true);
    }

    public boolean isRunOnStartup() {
        if (config == null) return false;
        return config.getBoolean("settings.runOnStartup", false);
    }

    public int getStartupDelayTicks() {
        if (config == null) return 100;
        int value = config.getInt("settings.startupDelayTicks", 100);
        if (value < 1) return 100;
        return value;
    }

    public boolean isSaveReports() {
        if (config == null) return false;
        return config.getBoolean("settings.saveReports", false);
    }

    public int getReportsToKeep() {
        if (config == null) return 25;
        int value = config.getInt("settings.reportsToKeep", 25);
        if (value < 1) return 25;
        return value;
    }

    public int getExportsToKeep() {
        if (config == null) return 25;
        int value = config.getInt("settings.exportsToKeep", 25);
        if (value < 1) return 25;
        return value;
    }

    public int getBaselineReportsToKeep() {
        if (config == null) return 25;
        int value = config.getInt("settings.baselineReportsToKeep", 25);
        if (value < 1) return 25;
        return value;
    }

    public boolean isCompareBaselineOnStartup() {
        if (config == null) return false;
        return config.getBoolean("settings.compareBaselineOnStartup", false);
    }

    public String getStartupBaselineName() {
        if (config == null) return "production";
        return config.getString("settings.startupBaselineName", "production");
    }

    public boolean isStartupBaselineSaveReport() {
        if (config == null) return true;
        return config.getBoolean("settings.startupBaselineSaveReport", true);
    }

    public int getStartupBaselineDelayTicks() {
        if (config == null) return 120;
        int value = config.getInt("settings.startupBaselineDelayTicks", 120);
        if (value < 1) return 120;
        return value;
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
            config.setDefaults(defaultConfig);
        }
    }

    private void saveDefaultResource(String resourceName) {
        File file = new File(plugin.getDataFolder(), resourceName);
        if (!file.exists()) {
            plugin.saveResource(resourceName, false);
        }
    }
}
