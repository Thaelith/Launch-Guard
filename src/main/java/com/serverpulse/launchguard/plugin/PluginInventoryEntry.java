package com.serverpulse.launchguard.plugin;

import java.util.List;

public record PluginInventoryEntry(
        String name,
        String version,
        boolean enabled,
        String mainClass,
        String apiVersion,
        List<String> authors,
        List<String> hardDependencies,
        List<String> softDependencies
) {
    public PluginInventoryEntry {
        name = valueOrUnknown(name);
        version = valueOrUnknown(version);
        mainClass = valueOrUnknown(mainClass);
        apiVersion = valueOrUnknown(apiVersion);
        authors = cleanList(authors);
        hardDependencies = cleanList(hardDependencies);
        softDependencies = cleanList(softDependencies);
    }

    public String statusText() {
        return enabled ? "enabled" : "disabled";
    }

    private static String valueOrUnknown(String value) {
        if (value == null || value.isBlank()) {
            return "unknown";
        }
        return value;
    }

    private static List<String> cleanList(List<String> values) {
        if (values == null || values.isEmpty()) {
            return List.of();
        }
        return values.stream()
                .filter(value -> value != null && !value.isBlank())
                .toList();
    }
}
