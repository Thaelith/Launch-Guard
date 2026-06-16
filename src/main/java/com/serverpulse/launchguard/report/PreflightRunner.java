package com.serverpulse.launchguard.report;

import com.serverpulse.launchguard.LaunchGuardPlugin;
import com.serverpulse.launchguard.check.Check;
import com.serverpulse.launchguard.check.CheckContext;
import com.serverpulse.launchguard.check.CheckResult;

import java.util.List;
import java.util.Map;

public class PreflightRunner {

    private final LaunchGuardPlugin plugin;

    public PreflightRunner(LaunchGuardPlugin plugin) {
        this.plugin = plugin;
    }

    public PreflightReport run() {
        PreflightReport report = new PreflightReport();
        List<Check> checks = plugin.getCheckRegistry().getEnabledChecks();

        if (checks.isEmpty()) {
            return report;
        }

        for (Check check : checks) {
            Map<String, Object> checkConfig = plugin.getChecksConfig().getCheckConfig(check.id());
            CheckContext context = new CheckContext(plugin.getServer(), plugin, checkConfig);
            try {
                List<CheckResult> results = check.run(context);
                report.addResults(results);
            } catch (Exception e) {
                plugin.getLogger().warning("Check '" + check.id() + "' failed: " + e.getMessage());
                report.addResult(CheckResult.fail(check.id(),
                        "Check failed with an internal error: " + check.displayName(),
                        "See console for details."));
            }
        }

        return report;
    }
}
