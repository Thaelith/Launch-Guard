package com.serverpulse.launchguard.baseline;

import com.serverpulse.launchguard.LaunchGuardPlugin;
import com.serverpulse.launchguard.report.ReportFileWriter;
import org.bukkit.Bukkit;

public class StartupBaselineCompareService {

    private final LaunchGuardPlugin plugin;

    public StartupBaselineCompareService(LaunchGuardPlugin plugin) {
        this.plugin = plugin;
    }

    public void run() {
        String name = plugin.getConfigManager().getStartupBaselineName();

        if (!BaselineNameValidator.isValid(name)) {
            plugin.getLogger().info("Startup baseline compare skipped: invalid baseline name. Use 1-32 characters: letters, numbers, underscore, or dash.");
            return;
        }

        plugin.getLogger().info("Running startup baseline compare: " + name);

        try {
            BaselineStore store = plugin.getBaselineStore();
            BaselineStore.LoadResult loadResult = store.loadBaseline(name);

            if (loadResult.status() == BaselineStore.LoadStatus.NOT_FOUND) {
                plugin.getLogger().info("Startup baseline compare skipped: baseline not found: " + name);
                return;
            }

            BaselineDriftReport report;
            if (loadResult.status() == BaselineStore.LoadStatus.INVALID) {
                report = new BaselineDriftReport(name);
                report.add(new BaselineDriftIssue(BaselineDriftSeverity.FAIL,
                        "Baseline file is invalid or corrupt: " + name));
            } else {
                Object schemaVersion = loadResult.data().get("schemaVersion");
                if (!(schemaVersion instanceof Integer) || ((Integer) schemaVersion) != 1) {
                    report = new BaselineDriftReport(name);
                    report.add(new BaselineDriftIssue(BaselineDriftSeverity.FAIL,
                            "Unsupported baseline schema version"));
                } else {
                    BaselineSnapshotService snapshotService = new BaselineSnapshotService(plugin);
                    var current = snapshotService.captureSnapshot(name);
                    BaselineCompareService compareService = new BaselineCompareService();
                    report = compareService.compare(loadResult.data(), current);
                }
            }

            BaselineRenderer renderer = new BaselineRenderer();
            Bukkit.getConsoleSender().sendMessage(renderer.renderDrift(report));

            if (plugin.getConfigManager().isStartupBaselineSaveReport()) {
                saveReport(report, name);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Startup baseline compare failed: " + e.getMessage());
        }
    }

    private void saveReport(BaselineDriftReport report, String baselineName) {
        try {
            BaselineTextReportRenderer textRenderer = new BaselineTextReportRenderer();
            String text = textRenderer.render(report, plugin.getDescription().getVersion(),
                    plugin.getServer().getMinecraftVersion());
            ReportFileWriter writer = plugin.getBaselineReportWriter();
            var savedPath = writer.save(text, "startup_baseline_" + baselineName);
            writer.prune(plugin.getConfigManager().getBaselineReportsToKeep());
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to save startup baseline report: " + e.getMessage());
        }
    }
}
