package com.serverpulse.launchguard.check;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

import java.util.Map;

public class CheckContext {
    private final Server server;
    private final Plugin plugin;
    private final Map<String, Object> config;

    public CheckContext(Server server, Plugin plugin, Map<String, Object> config) {
        this.server = server;
        this.plugin = plugin;
        this.config = config;
    }

    public Server server() {
        return server;
    }

    public Plugin plugin() {
        return plugin;
    }

    public Map<String, Object> config() {
        return config;
    }

    @SuppressWarnings("unchecked")
    public <T> T configValue(String key, T defaultValue) {
        Object value = config.get(key);
        if (value == null) return defaultValue;
        try {
            return (T) value;
        } catch (ClassCastException e) {
            return defaultValue;
        }
    }
}
