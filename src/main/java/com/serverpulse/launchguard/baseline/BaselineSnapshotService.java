package com.serverpulse.launchguard.baseline;

import com.serverpulse.launchguard.LaunchGuardPlugin;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.*;

public class BaselineSnapshotService {

    private final Plugin plugin;

    private static SimpleCommandMap cachedCommandMap;
    private static boolean reflectionAttempted;

    public BaselineSnapshotService(Plugin plugin) {
        this.plugin = plugin;
    }

    public Map<String, Object> captureSnapshot(String baselineName) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("schemaVersion", 1);
        snapshot.put("baselineName", baselineName);
        snapshot.put("createdAt", Instant.now().toString());
        snapshot.put("launchGuardVersion", plugin.getDescription().getVersion());

        Map<String, String> serverMeta = new LinkedHashMap<>();
        serverMeta.put("name", plugin.getServer().getName());
        serverMeta.put("version", plugin.getServer().getMinecraftVersion());
        serverMeta.put("bukkitVersion", plugin.getServer().getBukkitVersion());
        snapshot.put("server", serverMeta);

        snapshot.put("plugins", capturePlugins());
        snapshot.put("commands", captureCommands());
        snapshot.put("worlds", captureWorlds());
        snapshot.put("launchGuardConfig", captureConfig());

        return snapshot;
    }

    private Map<String, Object> captureConfig() {
        Map<String, Object> config = new LinkedHashMap<>();
        if (plugin instanceof LaunchGuardPlugin) {
            LaunchGuardPlugin lg = (LaunchGuardPlugin) plugin;
            Map<String, Object> checksConfig = lg.getChecksConfig().getCheckConfig("plugins");
            config.put("configuredRequiredPlugins", checksConfig.getOrDefault("required", List.of()));
            Map<String, Object> cmdConfig = lg.getChecksConfig().getCheckConfig("commands");
            config.put("configuredRequiredCommands", cmdConfig.getOrDefault("required", List.of()));
            Map<String, Object> worldConfig = lg.getChecksConfig().getCheckConfig("worlds");
            config.put("configuredRequiredWorlds", worldConfig.getOrDefault("required", List.of()));
            Map<String, Object> permConfig = lg.getChecksConfig().getCheckConfig("permissions");
            @SuppressWarnings("unchecked")
            Map<String, Object> nodes = (Map<String, Object>) permConfig.getOrDefault("nodes", Map.of());
            config.put("configuredPermissionNodes", nodes);
            Map<String, Object> piConfig = lg.getChecksConfig().getCheckConfig("pluginInventory");
            Map<String, Object> pi = new LinkedHashMap<>();
            pi.put("enabled", piConfig.getOrDefault("enabled", false));
            pi.put("checkDependencies", piConfig.getOrDefault("checkDependencies", true));
            pi.put("warnOnSoftDependencyMissing", piConfig.getOrDefault("warnOnSoftDependencyMissing", true));
            config.put("pluginInventory", pi);
            @SuppressWarnings("unchecked")
            Map<String, Object> locConfig = lg.getChecksConfig().getCheckConfig("locations");
            Object entries = locConfig.get("entries");
            if (entries instanceof Map) {
                config.put("configuredLocationKeys", new ArrayList<>(((Map<?, ?>) entries).keySet()));
            } else {
                config.put("configuredLocationKeys", List.of());
            }
        }
        return config;
    }

    private List<Map<String, Object>> capturePlugins() {
        List<Map<String, Object>> plugins = new ArrayList<>();
        PluginManager pm = plugin.getServer().getPluginManager();
        for (Plugin p : pm.getPlugins()) {
            Map<String, Object> info = new LinkedHashMap<>();
            PluginDescriptionFile desc = p.getDescription();
            info.put("name", desc.getName());
            info.put("version", desc.getVersion());
            info.put("enabled", p.isEnabled());
            info.put("authors", new ArrayList<>(desc.getAuthors()));
            info.put("depend", new ArrayList<>(desc.getDepend()));
            info.put("softDepend", new ArrayList<>(desc.getSoftDepend()));
            info.put("loadBefore", new ArrayList<>(desc.getLoadBefore()));
            plugins.add(info);
        }
        plugins.sort(Comparator.comparing(m -> ((String) m.get("name")).toLowerCase()));
        return plugins;
    }

    private List<Map<String, Object>> captureCommands() {
        List<Map<String, Object>> commands = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        for (Plugin p : plugin.getServer().getPluginManager().getPlugins()) {
            for (Map.Entry<String, ?> cmdEntry : p.getDescription().getCommands().entrySet()) {
                @SuppressWarnings("unchecked")
                Map<String, Object> cmdData = (Map<String, Object>) cmdEntry.getValue();
                String name = cmdEntry.getKey();
                if (!seen.add(name)) continue;
                Map<String, Object> info = new LinkedHashMap<>();
                info.put("name", name);
                info.put("label", name);
                PluginCommand pluginCmd = plugin.getServer().getPluginCommand(name);
                info.put("plugin", pluginCmd != null ? pluginCmd.getPlugin().getName() : p.getName());
                List<String> aliases = cmdData.containsKey("aliases")
                        ? new ArrayList<>((List<String>) cmdData.get("aliases")) : new ArrayList<>();
                info.put("aliases", aliases);
                commands.add(info);
            }
        }

        SimpleCommandMap map = getCommandMap();
        if (map != null) {
            for (Command cmd : map.getCommands()) {
                String name = cmd.getName().toLowerCase();
                if (seen.add(name)) {
                    Map<String, Object> info = new LinkedHashMap<>();
                    info.put("name", name);
                    info.put("label", cmd.getLabel());
                    info.put("plugin", "server");
                    info.put("aliases", new ArrayList<>(cmd.getAliases()));
                    commands.add(info);
                }
            }
        }

        commands.sort(Comparator.comparing(m -> ((String) m.get("name")).toLowerCase()));
        return commands;
    }

    private List<Map<String, Object>> captureWorlds() {
        List<Map<String, Object>> worlds = new ArrayList<>();
        for (World world : plugin.getServer().getWorlds()) {
            Map<String, Object> info = new LinkedHashMap<>();
            info.put("name", world.getName());
            info.put("environment", world.getEnvironment().name());
            worlds.add(info);
        }
        worlds.sort(Comparator.comparing(m -> ((String) m.get("name")).toLowerCase()));
        return worlds;
    }

    private static SimpleCommandMap getCommandMap() {
        if (cachedCommandMap != null) return cachedCommandMap;
        if (reflectionAttempted) return null;
        reflectionAttempted = true;
        try {
            Field field = org.bukkit.Bukkit.getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            Object value = field.get(org.bukkit.Bukkit.getServer());
            if (value instanceof SimpleCommandMap) {
                cachedCommandMap = (SimpleCommandMap) value;
            }
        } catch (Exception ignored) {}
        return cachedCommandMap;
    }
}
