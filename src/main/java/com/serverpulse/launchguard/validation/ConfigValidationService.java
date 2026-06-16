package com.serverpulse.launchguard.validation;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.*;

public class ConfigValidationService {

    private final Plugin plugin;
    private final File dataFolder;

    private static final List<String> CONFIG_EXPECTED_KEYS = List.of(
            "showPassedChecks", "reportToConsole", "prefix",
            "runOnStartup", "startupDelayTicks", "saveReports",
            "reportsToKeep", "exportsToKeep"
    );

    public ConfigValidationService(Plugin plugin) {
        this.plugin = plugin;
        this.dataFolder = plugin.getDataFolder();
    }

    public ValidationReport validateAll() {
        ValidationReport report = new ValidationReport();
        report.addAll(validateConfig());
        report.addAll(validateChecks());
        report.addAll(validateMessages());
        return report;
    }

    public List<ValidationIssue> validateConfig() {
        List<ValidationIssue> issues = new ArrayList<>();
        File file = new File(dataFolder, "config.yml");

        if (!file.exists()) {
            issues.add(ValidationIssue.fail("config.yml", "File not found: config.yml"));
            return issues;
        }

        YamlConfiguration yaml = loadYaml(file);
        if (yaml == null) {
            issues.add(ValidationIssue.fail("config.yml", "YAML parse error in config.yml. Check for syntax errors."));
            return issues;
        }

        issues.add(ValidationIssue.pass("config.yml", "config.yml parsed successfully"));

        ConfigurationSection settings = yaml.getConfigurationSection("settings");
        if (settings == null) {
            issues.add(ValidationIssue.fail("config.yml", "Missing root section: settings"));
            return issues;
        }

        for (String key : yaml.getKeys(false)) {
            if (!key.equals("settings")) {
                issues.add(ValidationIssue.warn("config.yml", "Unknown root key: " + key));
            }
        }

        validateBooleanSetting(settings, "showPassedChecks", issues);
        validateBooleanSetting(settings, "reportToConsole", issues);
        validateBooleanSetting(settings, "runOnStartup", issues);
        validateBooleanSetting(settings, "saveReports", issues);

        validateStringSetting(settings, "prefix", issues);

        validatePositiveInt(settings, "startupDelayTicks", 100, issues);
        validatePositiveInt(settings, "reportsToKeep", 25, issues);
        validatePositiveInt(settings, "exportsToKeep", 25, issues);

        for (String key : settings.getKeys(false)) {
            if (!CONFIG_EXPECTED_KEYS.contains(key)) {
                issues.add(ValidationIssue.warn("config.yml", "Unknown key: settings." + key));
            }
        }

        return issues;
    }

    public List<ValidationIssue> validateChecks() {
        List<ValidationIssue> issues = new ArrayList<>();
        File file = new File(dataFolder, "checks.yml");

        if (!file.exists()) {
            issues.add(ValidationIssue.fail("checks.yml", "File not found: checks.yml"));
            return issues;
        }

        YamlConfiguration yaml = loadYaml(file);
        if (yaml == null) {
            issues.add(ValidationIssue.fail("checks.yml", "YAML parse error in checks.yml. Check for syntax errors."));
            return issues;
        }

        issues.add(ValidationIssue.pass("checks.yml", "checks.yml parsed successfully"));

        ConfigurationSection checks = yaml.getConfigurationSection("checks");
        if (checks == null) {
            issues.add(ValidationIssue.fail("checks.yml", "Missing root section: checks"));
            return issues;
        }

        for (String key : yaml.getKeys(false)) {
            if (!key.equals("checks")) {
                issues.add(ValidationIssue.warn("checks.yml", "Unknown root key: " + key));
            }
        }

        validatePluginCheck(checks, issues);
        validateCommandCheck(checks, issues);
        validateWorldCheck(checks, issues);
        validateLocationCheck(checks, issues);
        validatePermissionCheck(checks, issues);
        validatePluginInventoryCheck(checks, issues);

        for (String key : checks.getKeys(false)) {
            if (!List.of("plugins", "commands", "worlds", "locations", "permissions", "pluginInventory").contains(key)) {
                issues.add(ValidationIssue.warn("checks.yml", "Unknown check section: checks." + key));
            }
        }

        return issues;
    }

    public List<ValidationIssue> validateMessages() {
        List<ValidationIssue> issues = new ArrayList<>();
        File file = new File(dataFolder, "messages.yml");

        if (!file.exists()) {
            issues.add(ValidationIssue.fail("messages.yml", "File not found: messages.yml"));
            return issues;
        }

        YamlConfiguration yaml = loadYaml(file);
        if (yaml == null) {
            issues.add(ValidationIssue.fail("messages.yml", "YAML parse error in messages.yml. Check for syntax errors."));
            return issues;
        }

        issues.add(ValidationIssue.pass("messages.yml", "messages.yml parsed successfully"));

        List<String> expectedKeys = List.of(
                "prefix", "noPermission", "reloadComplete", "reloadFailed",
                "unknownCommand", "runningChecks", "noChecksEnabled", "version",
                "reportHeader", "resultReady", "resultReadyWithWarnings", "resultNotReady",
                "passedCount", "warningsCount", "failuresCount"
        );

        for (String key : expectedKeys) {
            if (!yaml.contains(key)) {
                issues.add(ValidationIssue.warn("messages.yml", "Missing expected key: " + key));
            }
        }

        for (String key : yaml.getKeys(false)) {
            Object value = yaml.get(key);
            if (!(value instanceof String)) {
                issues.add(ValidationIssue.fail("messages.yml", "Value for '" + key + "' must be a string"));
            } else {
                String strValue = (String) value;
                if (strValue.isEmpty()) {
                    issues.add(ValidationIssue.warn("messages.yml", "Empty value for key: " + key));
                }
            }
            if (!expectedKeys.contains(key)) {
                issues.add(ValidationIssue.warn("messages.yml", "Unknown key: " + key));
            }
        }

        return issues;
    }

    private void validatePluginCheck(ConfigurationSection checks, List<ValidationIssue> issues) {
        ConfigurationSection plugins = checks.getConfigurationSection("plugins");
        if (plugins == null) return;
        validateBooleanSetting(plugins, "enabled", issues, "checks.plugins");

        boolean enabled = plugins.getBoolean("enabled", false);
        List<String> required = plugins.getStringList("required");
        if (required.isEmpty() && enabled) {
            issues.add(ValidationIssue.warn("checks.yml", "checks.plugins.required is empty while plugins check is enabled"));
        }
        validateStringList(plugins, "required", issues, "checks.plugins");

        for (String key : plugins.getKeys(false)) {
            if (!List.of("enabled", "required").contains(key)) {
                issues.add(ValidationIssue.warn("checks.yml", "Unknown key: checks.plugins." + key));
            }
        }
    }

    private void validateCommandCheck(ConfigurationSection checks, List<ValidationIssue> issues) {
        ConfigurationSection commands = checks.getConfigurationSection("commands");
        if (commands == null) return;
        validateBooleanSetting(commands, "enabled", issues, "checks.commands");

        boolean enabled = commands.getBoolean("enabled", false);
        List<String> required = commands.getStringList("required");
        if (required.isEmpty() && enabled) {
            issues.add(ValidationIssue.warn("checks.yml", "checks.commands.required is empty while commands check is enabled"));
        }
        validateStringList(commands, "required", issues, "checks.commands");

        for (String key : commands.getKeys(false)) {
            if (!List.of("enabled", "required").contains(key)) {
                issues.add(ValidationIssue.warn("checks.yml", "Unknown key: checks.commands." + key));
            }
        }
    }

    private void validateWorldCheck(ConfigurationSection checks, List<ValidationIssue> issues) {
        ConfigurationSection worlds = checks.getConfigurationSection("worlds");
        if (worlds == null) return;
        validateBooleanSetting(worlds, "enabled", issues, "checks.worlds");

        boolean enabled = worlds.getBoolean("enabled", false);
        List<String> required = worlds.getStringList("required");
        if (required.isEmpty() && enabled) {
            issues.add(ValidationIssue.warn("checks.yml", "checks.worlds.required is empty while worlds check is enabled"));
        }
        validateStringList(worlds, "required", issues, "checks.worlds");

        for (String key : worlds.getKeys(false)) {
            if (!List.of("enabled", "required").contains(key)) {
                issues.add(ValidationIssue.warn("checks.yml", "Unknown key: checks.worlds." + key));
            }
        }
    }

    private void validateLocationCheck(ConfigurationSection checks, List<ValidationIssue> issues) {
        ConfigurationSection locations = checks.getConfigurationSection("locations");
        if (locations == null) return;
        validateBooleanSetting(locations, "enabled", issues, "checks.locations");

        boolean enabled = locations.getBoolean("enabled", false);
        ConfigurationSection entries = locations.getConfigurationSection("entries");
        if (entries == null) {
            if (enabled) {
                issues.add(ValidationIssue.warn("checks.yml", "checks.locations.entries is missing while locations check is enabled"));
            }
            return;
        }
        if (entries.getKeys(false).isEmpty() && enabled) {
            issues.add(ValidationIssue.warn("checks.yml", "checks.locations.entries is empty while locations check is enabled"));
        }

        for (String name : entries.getKeys(false)) {
            ConfigurationSection locSection = entries.getConfigurationSection(name);
            if (locSection == null) {
                issues.add(ValidationIssue.fail("checks.yml", "checks.locations.entries." + name + " must be a section"));
                continue;
            }

            if (!locSection.isString("world") || locSection.getString("world", "").isEmpty()) {
                issues.add(ValidationIssue.fail("checks.yml", "checks.locations.entries." + name + ".world must be a non-empty string"));
            }

            if (!locSection.contains("x")) {
                issues.add(ValidationIssue.fail("checks.yml", "checks.locations.entries." + name + ".x is missing"));
            } else if (!locSection.isDouble("x") && !locSection.isInt("x")) {
                issues.add(ValidationIssue.fail("checks.yml", "checks.locations.entries." + name + ".x must be a number"));
            }
            if (!locSection.contains("y")) {
                issues.add(ValidationIssue.fail("checks.yml", "checks.locations.entries." + name + ".y is missing"));
            } else if (!locSection.isDouble("y") && !locSection.isInt("y")) {
                issues.add(ValidationIssue.fail("checks.yml", "checks.locations.entries." + name + ".y must be a number"));
            } else {
                double yVal = locSection.getDouble("y");
                if (yVal < -64 || yVal > 320) {
                    issues.add(ValidationIssue.warn("checks.yml", "checks.locations.entries." + name + ".y is outside typical Minecraft world height range (-64 to 320)"));
                }
            }
            if (!locSection.contains("z")) {
                issues.add(ValidationIssue.fail("checks.yml", "checks.locations.entries." + name + ".z is missing"));
            } else if (!locSection.isDouble("z") && !locSection.isInt("z")) {
                issues.add(ValidationIssue.fail("checks.yml", "checks.locations.entries." + name + ".z must be a number"));
            }

            if (locSection.contains("safe") && !locSection.isBoolean("safe")) {
                issues.add(ValidationIssue.warn("checks.yml", "checks.locations.entries." + name + ".safe should be a boolean"));
            }
        }

        for (String key : locations.getKeys(false)) {
            if (!List.of("enabled", "entries").contains(key)) {
                issues.add(ValidationIssue.warn("checks.yml", "Unknown key: checks.locations." + key));
            }
        }
    }

    private void validatePermissionCheck(ConfigurationSection checks, List<ValidationIssue> issues) {
        ConfigurationSection permissions = checks.getConfigurationSection("permissions");
        if (permissions == null) return;
        validateBooleanSetting(permissions, "enabled", issues, "checks.permissions");

        boolean enabled = permissions.getBoolean("enabled", false);
        ConfigurationSection nodes = permissions.getConfigurationSection("nodes");
        if (nodes == null) {
            if (enabled) {
                issues.add(ValidationIssue.warn("checks.yml", "checks.permissions.nodes is missing while permissions check is enabled"));
            }
            return;
        }

        List<String> shouldExist = nodes.getStringList("shouldExist");
        List<String> dangerous = nodes.getStringList("dangerous");
        if (shouldExist.isEmpty() && dangerous.isEmpty() && enabled) {
            issues.add(ValidationIssue.warn("checks.yml", "checks.permissions.nodes has no entries while permissions check is enabled"));
        }

        validateStringList(nodes, "shouldExist", issues, "checks.permissions.nodes");
        validateStringList(nodes, "dangerous", issues, "checks.permissions.nodes");

        for (String key : nodes.getKeys(false)) {
            if (!List.of("shouldExist", "dangerous").contains(key)) {
                issues.add(ValidationIssue.warn("checks.yml", "Unknown key: checks.permissions.nodes." + key));
            }
        }
        for (String key : permissions.getKeys(false)) {
            if (!List.of("enabled", "nodes").contains(key)) {
                issues.add(ValidationIssue.warn("checks.yml", "Unknown key: checks.permissions." + key));
            }
        }
    }

    private void validatePluginInventoryCheck(ConfigurationSection checks, List<ValidationIssue> issues) {
        ConfigurationSection pi = checks.getConfigurationSection("pluginInventory");
        if (pi == null) return;
        validateBooleanSetting(pi, "enabled", issues, "checks.pluginInventory");
        validateBooleanSetting(pi, "checkDependencies", issues, "checks.pluginInventory");
        validateBooleanSetting(pi, "warnOnSoftDependencyMissing", issues, "checks.pluginInventory");

        for (String key : pi.getKeys(false)) {
            if (!List.of("enabled", "checkDependencies", "warnOnSoftDependencyMissing").contains(key)) {
                issues.add(ValidationIssue.warn("checks.yml", "Unknown key: checks.pluginInventory." + key));
            }
        }
    }

    private void validateBooleanSetting(ConfigurationSection section, String key, List<ValidationIssue> issues) {
        validateBooleanSetting(section, key, issues, "settings");
    }

    private void validateBooleanSetting(ConfigurationSection section, String key, List<ValidationIssue> issues, String context) {
        if (!section.contains(key)) return;
        Object value = section.get(key);
        if (!(value instanceof Boolean)) {
            issues.add(ValidationIssue.fail("config.yml", context + "." + key + " must be a boolean (true/false)"));
        }
    }

    private void validateStringSetting(ConfigurationSection section, String key, List<ValidationIssue> issues) {
        if (!section.contains(key)) return;
        Object value = section.get(key);
        if (!(value instanceof String)) {
            issues.add(ValidationIssue.fail("config.yml", "settings." + key + " must be a string"));
        }
    }

    private void validatePositiveInt(ConfigurationSection section, String key, int defaultValue, List<ValidationIssue> issues) {
        if (!section.contains(key)) return;
        Object value = section.get(key);
        if (value instanceof Number) {
            int num = ((Number) value).intValue();
            if (num < 1) {
                issues.add(ValidationIssue.warn("config.yml", "settings." + key + " should be at least 1 (currently " + num + ")"));
            }
        } else {
            issues.add(ValidationIssue.fail("config.yml", "settings." + key + " must be a number"));
        }
    }

    private void validateStringList(ConfigurationSection section, String key, List<ValidationIssue> issues, String context) {
        if (!section.contains(key)) return;
        List<?> list = section.getList(key);
        if (list == null) {
            issues.add(ValidationIssue.fail("config.yml", context + "." + key + " must be a list"));
            return;
        }
        Set<String> seen = new HashSet<>();
        for (Object item : list) {
            if (!(item instanceof String)) {
                issues.add(ValidationIssue.fail("config.yml", context + "." + key + " must contain only string values"));
                return;
            }
            String str = (String) item;
            if (str.isEmpty()) {
                issues.add(ValidationIssue.fail("checks.yml", context + "." + key + " contains an empty string"));
            }
            if (!seen.add(str)) {
                issues.add(ValidationIssue.warn("checks.yml", context + "." + key + " contains duplicate entry: " + str));
            }
        }
    }

    private YamlConfiguration loadYaml(File file) {
        try {
            YamlConfiguration yaml = new YamlConfiguration();
            yaml.load(file);
            return yaml;
        } catch (InvalidConfigurationException e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
