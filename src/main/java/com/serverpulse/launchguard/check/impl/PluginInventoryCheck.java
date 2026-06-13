package com.serverpulse.launchguard.check.impl;

import com.serverpulse.launchguard.check.Check;
import com.serverpulse.launchguard.check.CheckContext;
import com.serverpulse.launchguard.check.CheckResult;
import com.serverpulse.launchguard.check.CheckSeverity;
import com.serverpulse.launchguard.plugin.PluginDependencyStatus;
import com.serverpulse.launchguard.plugin.PluginInventoryEntry;
import com.serverpulse.launchguard.plugin.PluginInventoryService;

import java.util.ArrayList;
import java.util.List;

public class PluginInventoryCheck implements Check {

    @Override
    public String id() {
        return "pluginInventory";
    }

    @Override
    public String displayName() {
        return "Plugin Inventory";
    }

    @Override
    public List<CheckResult> run(CheckContext context) {
        PluginInventoryService service = new PluginInventoryService(context.server());
        List<PluginInventoryEntry> entries = service.collectInventory();
        int enabledCount = service.enabledCount(entries);
        int disabledCount = entries.size() - enabledCount;

        List<CheckResult> results = new ArrayList<>();
        results.add(CheckResult.info(
                id(),
                "Plugin inventory: " + entries.size() + " total, " + enabledCount
                        + " enabled, " + disabledCount + " disabled.",
                null
        ));

        boolean checkDependencies = context.configValue("checkDependencies", true);
        if (!checkDependencies) {
            return results;
        }

        boolean warnOnSoftDependencyMissing = context.configValue("warnOnSoftDependencyMissing", true);
        List<PluginDependencyStatus> statuses = service.collectDependencyStatuses(entries, warnOnSoftDependencyMissing);
        List<PluginDependencyStatus> missingStatuses = statuses.stream()
                .filter(status -> !status.present())
                .toList();

        if (missingStatuses.isEmpty()) {
            results.add(CheckResult.pass(
                    id(),
                    "Plugin dependency visibility: no missing dependencies reported by loaded plugin metadata."
            ));
            return results;
        }

        for (PluginDependencyStatus status : missingStatuses) {
            results.add(toResult(status));
        }

        return results;
    }

    private CheckResult toResult(PluginDependencyStatus status) {
        if (status.severity() == CheckSeverity.FAIL) {
            return CheckResult.fail(
                    id(),
                    status.message(),
                    "Install and enable the missing hard dependency before launch."
            );
        }
        if (status.severity() == CheckSeverity.WARN) {
            return CheckResult.warn(
                    id(),
                    status.message(),
                    "Review whether this optional dependency is required by the plugin configuration."
            );
        }
        return CheckResult.info(
                id(),
                status.message(),
                "Optional dependency is not installed. Review plugin configuration if this integration is expected."
        );
    }
}
