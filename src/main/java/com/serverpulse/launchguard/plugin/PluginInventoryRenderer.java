package com.serverpulse.launchguard.plugin;

import com.serverpulse.launchguard.check.CheckSeverity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.List;

public class PluginInventoryRenderer {

    public Component renderInventory(List<PluginInventoryEntry> entries, int enabledCount) {
        int disabledCount = entries.size() - enabledCount;
        List<Component> lines = new ArrayList<>();

        lines.add(Component.text("LaunchGuard Plugin Inventory", NamedTextColor.WHITE));
        lines.add(Component.empty());
        lines.add(Component.text("Plugins: " + entries.size() + " total, " + enabledCount
                + " enabled, " + disabledCount + " disabled", NamedTextColor.GRAY));
        lines.add(Component.empty());

        for (PluginInventoryEntry entry : entries) {
            CheckSeverity severity = entry.enabled() ? CheckSeverity.PASS : CheckSeverity.WARN;
            lines.add(statusLabel(severity)
                    .append(Component.text(" " + entry.name() + " " + entry.version()
                            + " " + entry.statusText(), NamedTextColor.GRAY)));
        }

        lines.add(Component.empty());
        lines.add(Component.text("Run /launchguard plugins verbose for metadata details.", NamedTextColor.GRAY));
        lines.add(Component.text("Run /launchguard plugins dependencies for dependency visibility.", NamedTextColor.GRAY));

        return joinLines(lines);
    }

    public Component renderVerboseInventory(List<PluginInventoryEntry> entries, int enabledCount) {
        int disabledCount = entries.size() - enabledCount;
        List<Component> lines = new ArrayList<>();

        lines.add(Component.text("LaunchGuard Plugin Inventory Details", NamedTextColor.WHITE));
        lines.add(Component.empty());
        lines.add(Component.text("Plugins: " + entries.size() + " total, " + enabledCount
                + " enabled, " + disabledCount + " disabled", NamedTextColor.GRAY));

        for (PluginInventoryEntry entry : entries) {
            lines.add(Component.empty());
            lines.add(Component.text("Plugin: " + entry.name(), NamedTextColor.WHITE));
            lines.add(Component.text("Status: " + entry.statusText(), NamedTextColor.GRAY));
            lines.add(Component.text("Version: " + entry.version(), NamedTextColor.GRAY));
            lines.add(Component.text("Main: " + entry.mainClass(), NamedTextColor.GRAY));
            lines.add(Component.text("API version: " + entry.apiVersion(), NamedTextColor.GRAY));
            lines.add(Component.text("Authors: " + listOrNone(entry.authors()), NamedTextColor.GRAY));
            lines.add(Component.text("Hard dependencies: " + listOrNone(entry.hardDependencies()), NamedTextColor.GRAY));
            lines.add(Component.text("Soft dependencies: " + listOrNone(entry.softDependencies()), NamedTextColor.GRAY));
        }

        return joinLines(lines);
    }

    public Component renderDependencyReport(
            List<PluginInventoryEntry> entries,
            List<PluginDependencyStatus> dependencyStatuses
    ) {
        List<Component> lines = new ArrayList<>();

        lines.add(Component.text("LaunchGuard Plugin Dependency Report", NamedTextColor.WHITE));
        lines.add(Component.empty());

        for (PluginInventoryEntry entry : entries) {
            if (entry.hardDependencies().isEmpty()) {
                lines.add(statusLabel(CheckSeverity.PASS)
                        .append(Component.text(" " + entry.name() + " has no hard dependencies", NamedTextColor.GRAY)));
            }

            for (PluginDependencyStatus status : dependencyStatuses) {
                if (!status.pluginName().equals(entry.name())) {
                    continue;
                }
                lines.add(statusLabel(status.severity())
                        .append(Component.text(" " + status.message(), NamedTextColor.GRAY)));
            }
        }

        return joinLines(lines);
    }

    private Component statusLabel(CheckSeverity severity) {
        return switch (severity) {
            case PASS -> Component.text("[PASS]", NamedTextColor.GREEN);
            case INFO -> Component.text("[INFO]", NamedTextColor.AQUA);
            case WARN -> Component.text("[WARN]", NamedTextColor.YELLOW);
            case FAIL -> Component.text("[FAIL]", NamedTextColor.RED);
        };
    }

    private String listOrNone(List<String> values) {
        if (values == null || values.isEmpty()) {
            return "none";
        }
        return String.join(", ", values);
    }

    private Component joinLines(List<Component> lines) {
        Component output = Component.empty();
        for (int i = 0; i < lines.size(); i++) {
            output = output.append(lines.get(i));
            if (i < lines.size() - 1) {
                output = output.append(Component.newline());
            }
        }
        return output;
    }
}
