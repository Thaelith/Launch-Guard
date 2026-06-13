package com.serverpulse.launchguard.plugin;

import com.serverpulse.launchguard.check.CheckSeverity;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class PluginInventoryService {

    private final Server server;

    public PluginInventoryService(Server server) {
        this.server = server;
    }

    public List<PluginInventoryEntry> collectInventory() {
        List<PluginInventoryEntry> entries = new ArrayList<>();
        for (Plugin plugin : server.getPluginManager().getPlugins()) {
            PluginDescriptionFile description = plugin.getDescription();
            entries.add(new PluginInventoryEntry(
                    description.getName(),
                    description.getVersion(),
                    plugin.isEnabled(),
                    description.getMainClass(),
                    description.getAPIVersion(),
                    description.getAuthors(),
                    description.getDepend(),
                    description.getSoftDepend()
            ));
        }

        entries.sort(Comparator.comparing(PluginInventoryEntry::name, String.CASE_INSENSITIVE_ORDER));
        return entries;
    }

    public List<PluginDependencyStatus> collectDependencyStatuses(
            List<PluginInventoryEntry> entries,
            boolean warnOnSoftDependencyMissing
    ) {
        Set<String> installedPluginNames = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (PluginInventoryEntry entry : entries) {
            installedPluginNames.add(entry.name());
        }

        List<PluginDependencyStatus> statuses = new ArrayList<>();
        for (PluginInventoryEntry entry : entries) {
            addDependencyStatuses(
                    statuses,
                    entry,
                    entry.hardDependencies(),
                    PluginDependencyStatus.DependencyType.HARD,
                    installedPluginNames,
                    warnOnSoftDependencyMissing
            );
            addDependencyStatuses(
                    statuses,
                    entry,
                    entry.softDependencies(),
                    PluginDependencyStatus.DependencyType.SOFT,
                    installedPluginNames,
                    warnOnSoftDependencyMissing
            );
        }
        return statuses;
    }

    public int enabledCount(List<PluginInventoryEntry> entries) {
        int count = 0;
        for (PluginInventoryEntry entry : entries) {
            if (entry.enabled()) {
                count++;
            }
        }
        return count;
    }

    private void addDependencyStatuses(
            List<PluginDependencyStatus> statuses,
            PluginInventoryEntry entry,
            List<String> dependencyNames,
            PluginDependencyStatus.DependencyType type,
            Set<String> installedPluginNames,
            boolean warnOnSoftDependencyMissing
    ) {
        for (String dependencyName : dependencyNames) {
            boolean present = installedPluginNames.contains(dependencyName);
            statuses.add(new PluginDependencyStatus(
                    entry.name(),
                    dependencyName,
                    type,
                    present,
                    severityFor(type, present, warnOnSoftDependencyMissing)
            ));
        }
    }

    private CheckSeverity severityFor(
            PluginDependencyStatus.DependencyType type,
            boolean present,
            boolean warnOnSoftDependencyMissing
    ) {
        if (present) {
            return CheckSeverity.PASS;
        }
        if (type == PluginDependencyStatus.DependencyType.HARD) {
            return CheckSeverity.FAIL;
        }
        return warnOnSoftDependencyMissing ? CheckSeverity.WARN : CheckSeverity.INFO;
    }
}
