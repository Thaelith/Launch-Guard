package com.serverpulse.launchguard.plugin;

import com.serverpulse.launchguard.check.CheckSeverity;

public record PluginDependencyStatus(
        String pluginName,
        String dependencyName,
        DependencyType type,
        boolean present,
        CheckSeverity severity
) {
    public enum DependencyType {
        HARD,
        SOFT
    }

    public String message() {
        String dependencyType = type == DependencyType.HARD ? "hard" : "soft";
        String state = present ? "found" : "missing";
        return pluginName + " " + dependencyType + " dependency " + state + ": " + dependencyName;
    }
}
