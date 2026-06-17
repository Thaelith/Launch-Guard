package com.serverpulse.launchguard.baseline;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Logger;

public class BaselineStore {

    private final Path baselinesDir;
    private final Logger logger;

    public BaselineStore(Path baselinesDir, Logger logger) {
        this.baselinesDir = baselinesDir;
        this.logger = logger;
    }

    public boolean exists(String name) {
        return getFile(name).exists();
    }

    public File getFile(String name) {
        return baselinesDir.resolve(name + ".yml").toFile();
    }

    public boolean save(String name, Map<String, Object> data) {
        try {
            Files.createDirectories(baselinesDir);
        } catch (IOException e) {
            logger.warning("Failed to create baselines directory: " + e.getMessage());
            return false;
        }
        File file = getFile(name);
        YamlConfiguration yaml = mapToYaml(data);
        try {
            yaml.save(file);
        } catch (IOException e) {
            logger.warning("Failed to save baseline: " + e.getMessage());
            return false;
        }
        return true;
    }

    public enum LoadStatus { FOUND, NOT_FOUND, INVALID }

    public record LoadResult(Map<String, Object> data, LoadStatus status) {}

    public LoadResult loadBaseline(String name) {
        File file = getFile(name);
        if (!file.exists()) return new LoadResult(null, LoadStatus.NOT_FOUND);
        try {
            YamlConfiguration yaml = new YamlConfiguration();
            yaml.load(file);
            Map<String, Object> data = yamlToMap(yaml);
            return new LoadResult(data, LoadStatus.FOUND);
        } catch (Exception e) {
            logger.warning("Failed to load baseline '" + name + "': " + e.getMessage());
            return new LoadResult(null, LoadStatus.INVALID);
        }
    }

    public Map<String, Object> load(String name) {
        return loadBaseline(name).data();
    }

    public List<BaselineEntry> listBaselines() {
        List<BaselineEntry> entries = new ArrayList<>();
        File dir = baselinesDir.toFile();
        if (!dir.exists() || !dir.isDirectory()) return entries;
        File[] files = dir.listFiles((d, n) -> n.endsWith(".yml"));
        if (files == null) return entries;
        for (File file : files) {
            String name = file.getName().replace(".yml", "");
            Map<String, Object> data = load(name);
            if (data != null) {
                String createdAt = Objects.toString(data.get("createdAt"), "unknown");
                String version = "unknown";
                Object launchGuard = data.get("launchGuardVersion");
                if (launchGuard != null) version = launchGuard.toString();
                Object serverObj = data.get("server");
                String serverVer = "unknown";
                if (serverObj instanceof Map) {
                    serverVer = Objects.toString(((Map<?, ?>) serverObj).get("version"), "unknown");
                }
                Object pluginsObj = data.get("plugins");
                int pluginCount = 0;
                if (pluginsObj instanceof List) {
                    pluginCount = ((List<?>) pluginsObj).size();
                }
                entries.add(new BaselineEntry(name, createdAt, version, serverVer, pluginCount));
            }
        }
        entries.sort(Comparator.comparing(BaselineEntry::name));
        return entries;
    }

    public boolean delete(String name) {
        File file = getFile(name);
        if (!file.exists()) return false;
        try {
            String filePath = file.getCanonicalPath();
            String dirPath = baselinesDir.toFile().getCanonicalPath();
            if (!filePath.startsWith(dirPath + File.separator) && !filePath.equals(dirPath)) {
                logger.warning("Refusing to delete file outside baselines directory: " + filePath);
                return false;
            }
        } catch (IOException e) {
            logger.warning("Failed to resolve baseline file path: " + e.getMessage());
            return false;
        }
        return file.delete();
    }

    @SuppressWarnings("unchecked")
    private static YamlConfiguration mapToYaml(Map<String, Object> data) {
        YamlConfiguration yaml = new YamlConfiguration();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            yaml.set(entry.getKey(), entry.getValue());
        }
        return yaml;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Map<String, Object> yamlToMap(YamlConfiguration yaml) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (String key : yaml.getKeys(false)) {
            Object value = yaml.get(key);
            if (value instanceof org.bukkit.configuration.ConfigurationSection) {
                result.put(key, convertSection((org.bukkit.configuration.ConfigurationSection) value));
            } else if (value instanceof List) {
                List<Object> list = new ArrayList<>();
                for (Object item : (List<?>) value) {
                    if (item instanceof org.bukkit.configuration.ConfigurationSection) {
                        list.add(convertSection((org.bukkit.configuration.ConfigurationSection) item));
                    } else {
                        list.add(item);
                    }
                }
                result.put(key, list);
            } else {
                result.put(key, value);
            }
        }
        return result;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Map<String, Object> convertSection(org.bukkit.configuration.ConfigurationSection section) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (String key : section.getKeys(false)) {
            Object value = section.get(key);
            if (value instanceof org.bukkit.configuration.ConfigurationSection) {
                map.put(key, convertSection((org.bukkit.configuration.ConfigurationSection) value));
            } else if (value instanceof List) {
                map.put(key, new ArrayList<>((List) value));
            } else {
                map.put(key, value);
            }
        }
        return map;
    }

    public record BaselineEntry(String name, String createdAt, String launchGuardVersion, String serverVersion, int pluginCount) {}
}
