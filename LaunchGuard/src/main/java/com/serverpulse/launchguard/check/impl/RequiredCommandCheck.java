package com.serverpulse.launchguard.check.impl;

import com.serverpulse.launchguard.check.Check;
import com.serverpulse.launchguard.check.CheckContext;
import com.serverpulse.launchguard.check.CheckResult;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class RequiredCommandCheck implements Check {

    private static SimpleCommandMap cachedCommandMap;
    private static boolean reflectionAttempted;

    @Override
    public String id() {
        return "commands";
    }

    @Override
    public String displayName() {
        return "Required Commands";
    }

    @Override
    public List<CheckResult> run(CheckContext context) {
        List<CheckResult> results = new ArrayList<>();
        List<String> required = context.configValue("required", List.of());

        if (required == null || required.isEmpty()) {
            return results;
        }

        for (String rawName : required) {
            String commandName = rawName.startsWith("/") ? rawName.substring(1) : rawName;

            boolean found = false;

            Command pluginCmd = context.server().getPluginCommand(commandName);
            if (pluginCmd != null) {
                found = true;
            }

            if (!found) {
                Command mapCmd = getCommandFromMap(commandName);
                if (mapCmd != null) {
                    found = true;
                }
            }

            if (found) {
                results.add(CheckResult.pass(
                        id(),
                        "Command registered: /" + commandName
                ));
            } else {
                results.add(CheckResult.fail(
                        id(),
                        "Command missing: /" + commandName,
                        "Install or configure the plugin that provides this command."
                ));
            }
        }

        return results;
    }

    private static Command getCommandFromMap(String name) {
        SimpleCommandMap map = getCommandMap();
        if (map == null) return null;
        return map.getCommand(name);
    }

    private static SimpleCommandMap getCommandMap() {
        if (cachedCommandMap != null) return cachedCommandMap;
        if (reflectionAttempted) return null;

        reflectionAttempted = true;
        try {
            Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            Object value = field.get(Bukkit.getServer());
            if (value instanceof SimpleCommandMap) {
                cachedCommandMap = (SimpleCommandMap) value;
                return cachedCommandMap;
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // CommandMap not accessible; fall back to PluginCommand-only detection
        }
        return null;
    }
}
