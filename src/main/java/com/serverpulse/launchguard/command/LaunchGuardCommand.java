package com.serverpulse.launchguard.command;

import com.serverpulse.launchguard.LaunchGuardPlugin;
import com.serverpulse.launchguard.check.Check;
import com.serverpulse.launchguard.report.ExportFileWriter;
import com.serverpulse.launchguard.report.HtmlReportRenderer;
import com.serverpulse.launchguard.report.JsonReportRenderer;
import com.serverpulse.launchguard.report.PlainTextReportRenderer;
import com.serverpulse.launchguard.report.PreflightReport;
import com.serverpulse.launchguard.report.PreflightRunner;
import com.serverpulse.launchguard.report.ReportFileWriter;
import com.serverpulse.launchguard.report.ReportRenderer;
import com.serverpulse.launchguard.validation.ConfigValidationService;
import com.serverpulse.launchguard.validation.ValidationReport;
import com.serverpulse.launchguard.validation.ValidationRenderer;
import com.serverpulse.launchguard.baseline.BaselineHtmlReportRenderer;
import com.serverpulse.launchguard.baseline.BaselineJsonReportRenderer;
import com.serverpulse.launchguard.baseline.BaselineTextReportRenderer;
import com.serverpulse.launchguard.baseline.BaselineCompareService;
import com.serverpulse.launchguard.baseline.BaselineDriftIssue;
import com.serverpulse.launchguard.baseline.BaselineDriftReport;
import com.serverpulse.launchguard.baseline.BaselineDriftSeverity;
import com.serverpulse.launchguard.baseline.BaselineNameValidator;
import com.serverpulse.launchguard.baseline.BaselineRenderer;
import com.serverpulse.launchguard.baseline.BaselineSnapshotService;
import com.serverpulse.launchguard.baseline.BaselineStore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LaunchGuardCommand implements CommandExecutor, TabCompleter {

    private final LaunchGuardPlugin plugin;
    private final PluginInventoryCommand pluginInventoryCommand;

    public LaunchGuardCommand(LaunchGuardPlugin plugin) {
        this.plugin = plugin;
        this.pluginInventoryCommand = new PluginInventoryCommand(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender == null) return true;

        String subcommand = args.length > 0 ? args[0].toLowerCase() : "help";

        switch (subcommand) {
            case "help":
                return handleHelp(sender);
            case "run":
                return handleRun(sender);
            case "reload":
                return handleReload(sender);
            case "plugins":
                return pluginInventoryCommand.handle(sender, args);
            case "history":
                return handleHistory(sender, args);
            case "export":
                return handleExport(sender, args);
            case "validate":
                return handleValidate(sender);
            case "baseline":
                return handleBaseline(sender, args);
            case "version":
                return handleVersion(sender);
            default:
                return handleUnknown(sender, subcommand);
        }
    }

    private boolean handleHelp(CommandSender sender) {
        String prefix = plugin.getMessageManager().get("prefix");
        sender.sendMessage(Component.text(prefix + " LaunchGuard Commands", NamedTextColor.WHITE));
        sender.sendMessage(Component.text("/launchguard help    - Show this help", NamedTextColor.GRAY));
        sender.sendMessage(Component.text("/launchguard run     - Run pre-launch checks", NamedTextColor.GRAY));
        sender.sendMessage(Component.text("/launchguard plugins - Show plugin inventory report", NamedTextColor.GRAY));
        sender.sendMessage(Component.text("/launchguard history - Show saved report history", NamedTextColor.GRAY));
        sender.sendMessage(Component.text("/launchguard reload  - Reload configuration", NamedTextColor.GRAY));
        sender.sendMessage(Component.text("/launchguard export  - Export report as JSON or HTML", NamedTextColor.GRAY));
        sender.sendMessage(Component.text("/launchguard validate - Validate configuration files", NamedTextColor.GRAY));
        sender.sendMessage(Component.text("/launchguard baseline - Save, list, compare, or delete baselines", NamedTextColor.GRAY));
        sender.sendMessage(Component.text("/launchguard version - Show version", NamedTextColor.GRAY));
        return true;
    }

    private boolean handleUnknown(CommandSender sender, String attempted) {
        String prefix = plugin.getMessageManager().get("prefix");
        sender.sendMessage(Component.text(prefix + " " +
                plugin.getMessageManager().get("unknownCommand", "Unknown subcommand. Use /launchguard help."),
                NamedTextColor.RED));
        return true;
    }

    private boolean handleRun(CommandSender sender) {
        if (!sender.hasPermission("launchguard.run") && !sender.hasPermission("launchguard.admin")) {
            sendNoPermission(sender);
            return true;
        }

        String prefix = plugin.getMessageManager().get("prefix");
        sender.sendMessage(Component.text(prefix + " " +
                plugin.getMessageManager().get("runningChecks"), NamedTextColor.WHITE));

        List<Check> checks = plugin.getCheckRegistry().getEnabledChecks();

        if (checks.isEmpty()) {
            sender.sendMessage(Component.text(prefix + " No checks are enabled. Check your checks.yml configuration.",
                    NamedTextColor.YELLOW));
            return true;
        }

        PreflightRunner runner = new PreflightRunner(plugin);
        PreflightReport report = runner.run();

        ReportRenderer renderer = new ReportRenderer(plugin.getMessageManager(), plugin.getConfigManager());
        Component output = renderer.render(report);

        sender.sendMessage(output);

        if (plugin.getConfigManager().reportToConsole() && !(sender instanceof ConsoleCommandSender)) {
            plugin.getServer().getConsoleSender().sendMessage(output);
        }

        if (plugin.getConfigManager().isSaveReports()) {
            saveManualReport(report, sender);
        }

        return true;
    }

    private void saveManualReport(PreflightReport report, CommandSender sender) {
        try {
            PlainTextReportRenderer plainRenderer = new PlainTextReportRenderer();
            String plainText = plainRenderer.render(report, "manual",
                    plugin.getDescription().getVersion(), plugin.getConfigManager().showPassedChecks());
            ReportFileWriter writer = plugin.getReportFileWriter();
            Path savedPath = writer.save(plainText, "manual");
            writer.prune(plugin.getConfigManager().getReportsToKeep());

            if (savedPath != null) {
                sender.sendMessage(Component.text("Saved report: plugins/LaunchGuard/reports/"
                        + savedPath.getFileName(), NamedTextColor.GRAY));
            } else {
                sender.sendMessage(Component.text("Failed to save report file.", NamedTextColor.YELLOW));
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to save manual report: " + e.getMessage());
            sender.sendMessage(Component.text("Failed to save report file. Check console for details.", NamedTextColor.YELLOW));
        }
    }

    private boolean handleHistory(CommandSender sender, String[] args) {
        if (!sender.hasPermission("launchguard.history") && !sender.hasPermission("launchguard.admin")) {
            sendNoPermission(sender);
            return true;
        }

        String mode = args.length > 1 ? args[1].toLowerCase() : "";

        if (mode.equals("latest")) {
            return handleHistoryLatest(sender);
        }

        return handleHistoryList(sender);
    }

    private boolean handleHistoryList(CommandSender sender) {
        ReportFileWriter writer = plugin.getReportFileWriter();
        List<ReportFileWriter.ReportFileInfo> files = writer.listRecent(10);

        String prefix = plugin.getMessageManager().get("prefix");
        sender.sendMessage(Component.text(prefix + " LaunchGuard Report History", NamedTextColor.WHITE));

        if (files.isEmpty()) {
            sender.sendMessage(Component.text("[INFO] No saved reports found.", NamedTextColor.AQUA));
            return true;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault());

        for (ReportFileWriter.ReportFileInfo info : files) {
            String modified = formatter.format(Instant.ofEpochMilli(info.lastModified()));
            String size = formatFileSize(info.size());
            sender.sendMessage(Component.text("  " + info.name() + "  " + modified + "  " + size, NamedTextColor.GRAY));
        }

        return true;
    }

    private boolean handleHistoryLatest(CommandSender sender) {
        ReportFileWriter writer = plugin.getReportFileWriter();
        Path latestPath = writer.getLatestReport();

        if (latestPath == null) {
            String prefix = plugin.getMessageManager().get("prefix");
            sender.sendMessage(Component.text(prefix + " [INFO] No saved reports found.", NamedTextColor.AQUA));
            return true;
        }

        try {
            String content = Files.readString(latestPath);
            final int maxChars = 8000;
            if (content.length() > maxChars) {
                content = content.substring(0, maxChars) + "\n\n... (report truncated, file is too large)";
            }
            sender.sendMessage(Component.text(content, NamedTextColor.GRAY));
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to read report file: " + e.getMessage());
            String prefix = plugin.getMessageManager().get("prefix");
            sender.sendMessage(Component.text(prefix + " Failed to read the latest report file.", NamedTextColor.RED));
        }

        return true;
    }

    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
    }

    private boolean handleExport(CommandSender sender, String[] args) {
        if (!sender.hasPermission("launchguard.export") && !sender.hasPermission("launchguard.admin")) {
            sendNoPermission(sender);
            return true;
        }

        String mode = args.length > 1 ? args[1].toLowerCase() : "";
        switch (mode) {
            case "json":
                return handleExportJson(sender);
            case "html":
                return handleExportHtml(sender);
            default:
                String prefix = plugin.getMessageManager().get("prefix");
                sender.sendMessage(Component.text(prefix + " Usage: /launchguard export <json|html>", NamedTextColor.RED));
                return true;
        }
    }

    private boolean handleExportJson(CommandSender sender) {
        String prefix = plugin.getMessageManager().get("prefix");
        sender.sendMessage(Component.text(prefix + " Running pre-launch checks for JSON export.", NamedTextColor.WHITE));

        PreflightRunner runner = new PreflightRunner(plugin);
        PreflightReport report = runner.run();

        JsonReportRenderer jsonRenderer = new JsonReportRenderer();
        String serverName = plugin.getServer().getName();
        String serverVersion = plugin.getServer().getMinecraftVersion();
        String bukkitVersion = plugin.getServer().getBukkitVersion();
        String json = jsonRenderer.render(report, "manual",
                plugin.getDescription().getVersion(),
                serverName, serverVersion, bukkitVersion);

        try {
            ExportFileWriter writer = plugin.getExportWriter();
            Path savedPath = writer.save(json, "manual", "json");
            writer.prune(plugin.getConfigManager().getExportsToKeep());

            if (savedPath != null) {
                sender.sendMessage(Component.text("Saved JSON export: plugins/LaunchGuard/exports/"
                        + savedPath.getFileName(), NamedTextColor.GRAY));
            } else {
                sender.sendMessage(Component.text("Failed to save JSON export file.", NamedTextColor.YELLOW));
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to save JSON export: " + e.getMessage());
            sender.sendMessage(Component.text("Failed to save JSON export file. Check console for details.", NamedTextColor.YELLOW));
        }

        return true;
    }

    private boolean handleExportHtml(CommandSender sender) {
        String prefix = plugin.getMessageManager().get("prefix");
        sender.sendMessage(Component.text(prefix + " Running pre-launch checks for HTML export.", NamedTextColor.WHITE));

        PreflightRunner runner = new PreflightRunner(plugin);
        PreflightReport report = runner.run();

        HtmlReportRenderer htmlRenderer = new HtmlReportRenderer();
        String serverName = plugin.getServer().getName();
        String serverVersion = plugin.getServer().getMinecraftVersion();
        String bukkitVersion = plugin.getServer().getBukkitVersion();
        String html = htmlRenderer.render(report, "manual",
                plugin.getDescription().getVersion(),
                serverName, serverVersion, bukkitVersion);

        try {
            ExportFileWriter writer = plugin.getExportWriter();
            Path savedPath = writer.save(html, "manual", "html");
            writer.prune(plugin.getConfigManager().getExportsToKeep());

            if (savedPath != null) {
                sender.sendMessage(Component.text("Saved HTML export: plugins/LaunchGuard/exports/"
                        + savedPath.getFileName(), NamedTextColor.GRAY));
            } else {
                sender.sendMessage(Component.text("Failed to save HTML export file.", NamedTextColor.YELLOW));
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to save HTML export: " + e.getMessage());
            sender.sendMessage(Component.text("Failed to save HTML export file. Check console for details.", NamedTextColor.YELLOW));
        }

        return true;
    }

    private boolean handleValidate(CommandSender sender) {
        if (!sender.hasPermission("launchguard.validate") && !sender.hasPermission("launchguard.admin")) {
            sendNoPermission(sender);
            return true;
        }

        String prefix = plugin.getMessageManager().get("prefix");
        sender.sendMessage(Component.text(prefix + " Running configuration validation.", NamedTextColor.WHITE));

        ConfigValidationService service = new ConfigValidationService(plugin);
        ValidationReport report = service.validateAll();

        ValidationRenderer renderer = new ValidationRenderer();
        Component output = renderer.render(report);
        sender.sendMessage(output);

        if (plugin.getConfigManager().reportToConsole() && !(sender instanceof ConsoleCommandSender)) {
            plugin.getServer().getConsoleSender().sendMessage(output);
        }

        return true;
    }

    private boolean handleBaseline(CommandSender sender, String[] args) {
        if (!sender.hasPermission("launchguard.baseline") && !sender.hasPermission("launchguard.admin")) {
            sendNoPermission(sender);
            return true;
        }

        String sub = args.length > 1 ? args[1].toLowerCase() : "";
        String name = args.length > 2 ? args[2] : "";

        switch (sub) {
            case "save":    return handleBaselineSave(sender, name);
            case "list":    return handleBaselineList(sender);
            case "compare":
                if (args.length > 3 && args[3].equalsIgnoreCase("save")) {
                    return handleBaselineCompareSave(sender, name);
                }
                return handleBaselineCompare(sender, name);
            case "delete":  return handleBaselineDelete(sender, name);
            case "export":
                return handleBaselineExport(sender, args);
            case "history":
                return handleBaselineHistory(sender, args);
            default:
                String prefix = plugin.getMessageManager().get("prefix");
                sender.sendMessage(Component.text(prefix + " Usage: /launchguard baseline <save|list|compare|delete|export|history> [name]", NamedTextColor.RED));
                return true;
        }
    }

    private boolean handleBaselineSave(CommandSender sender, String name) {
        String error = BaselineNameValidator.validate(name);
        if (error != null) {
            sender.sendMessage(Component.text(error, NamedTextColor.RED));
            return true;
        }
        BaselineStore store = plugin.getBaselineStore();
        if (store.exists(name)) {
            sender.sendMessage(Component.text("Baseline already exists: " + name, NamedTextColor.RED));
            return true;
        }
        BaselineSnapshotService service = new BaselineSnapshotService(plugin);
        Map<String, Object> snapshot = service.captureSnapshot(name);
        if (store.save(name, snapshot)) {
            sender.sendMessage(Component.text("Saved baseline: " + name, NamedTextColor.GREEN));
        } else {
            sender.sendMessage(Component.text("Failed to save baseline: " + name, NamedTextColor.RED));
        }
        return true;
    }

    private boolean handleBaselineList(CommandSender sender) {
        BaselineStore store = plugin.getBaselineStore();
        List<BaselineStore.BaselineEntry> entries = store.listBaselines();

        String prefix = plugin.getMessageManager().get("prefix");
        sender.sendMessage(Component.text(prefix + " LaunchGuard Baselines", NamedTextColor.WHITE));

        if (entries.isEmpty()) {
            sender.sendMessage(Component.text("[INFO] No baselines saved.", NamedTextColor.AQUA));
            return true;
        }
        for (BaselineStore.BaselineEntry entry : entries) {
            sender.sendMessage(Component.text("  " + entry.name() + "  created=" + entry.createdAt()
                    + "  server=" + entry.serverVersion() + "  plugins=" + entry.pluginCount(), NamedTextColor.GRAY));
        }
        return true;
    }

    private boolean handleBaselineCompare(CommandSender sender, String name) {
        String error = BaselineNameValidator.validate(name);
        if (error != null) {
            sender.sendMessage(Component.text(error, NamedTextColor.RED));
            return true;
        }
        BaselineStore store = plugin.getBaselineStore();
        BaselineStore.LoadResult loadResult = store.loadBaseline(name);
        if (loadResult.status() == BaselineStore.LoadStatus.NOT_FOUND) {
            sender.sendMessage(Component.text("Baseline not found: " + name, NamedTextColor.RED));
            return true;
        }
        if (loadResult.status() == BaselineStore.LoadStatus.INVALID) {
            BaselineDriftReport report = new BaselineDriftReport(name);
            report.add(new BaselineDriftIssue(BaselineDriftSeverity.FAIL,
                    "Baseline file is invalid or corrupt: " + name));
            BaselineRenderer renderer = new BaselineRenderer();
            sender.sendMessage(renderer.renderDrift(report));
            return true;
        }
        Map<String, Object> baseline = loadResult.data();

        Object schemaVersion = baseline.get("schemaVersion");
        if (!(schemaVersion instanceof Integer) || ((Integer) schemaVersion) != 1) {
            BaselineDriftReport report = new BaselineDriftReport(name);
            report.add(new BaselineDriftIssue(BaselineDriftSeverity.FAIL,
                    "Unsupported baseline schema version"));
            BaselineRenderer renderer = new BaselineRenderer();
            sender.sendMessage(renderer.renderDrift(report));
            return true;
        }

        BaselineSnapshotService service = new BaselineSnapshotService(plugin);
        Map<String, Object> current = service.captureSnapshot(name);

        BaselineCompareService compareService = new BaselineCompareService();
        BaselineDriftReport report = compareService.compare(baseline, current);

        BaselineRenderer renderer = new BaselineRenderer();
        Component output = renderer.renderDrift(report);
        sender.sendMessage(output);
        return true;
    }

    private boolean handleBaselineDelete(CommandSender sender, String name) {
        String error = BaselineNameValidator.validate(name);
        if (error != null) {
            sender.sendMessage(Component.text(error, NamedTextColor.RED));
            return true;
        }
        BaselineStore store = plugin.getBaselineStore();
        if (!store.exists(name)) {
            sender.sendMessage(Component.text("Baseline not found: " + name, NamedTextColor.RED));
            return true;
        }
        if (store.delete(name)) {
            sender.sendMessage(Component.text("Deleted baseline: " + name, NamedTextColor.GREEN));
        } else {
            sender.sendMessage(Component.text("Failed to delete baseline: " + name, NamedTextColor.RED));
        }
        return true;
    }

    private BaselineDriftReport runDriftCompare(String name) {
        BaselineStore store = plugin.getBaselineStore();
        BaselineStore.LoadResult loadResult = store.loadBaseline(name);
        if (loadResult.status() == BaselineStore.LoadStatus.NOT_FOUND
                || loadResult.status() == BaselineStore.LoadStatus.INVALID) {
            return null;
        }
        Map<String, Object> baseline = loadResult.data();
        Object schemaVersion = baseline.get("schemaVersion");
        if (!(schemaVersion instanceof Integer) || ((Integer) schemaVersion) != 1) {
            return null;
        }
        BaselineSnapshotService service = new BaselineSnapshotService(plugin);
        Map<String, Object> current = service.captureSnapshot(name);
        BaselineCompareService compareService = new BaselineCompareService();
        return compareService.compare(baseline, current);
    }

    private boolean handleBaselineCompareSave(CommandSender sender, String name) {
        String error = BaselineNameValidator.validate(name);
        if (error != null) { sender.sendMessage(Component.text(error, NamedTextColor.RED)); return true; }

        BaselineStore store = plugin.getBaselineStore();
        if (!store.exists(name)) {
            sender.sendMessage(Component.text("Baseline not found: " + name, NamedTextColor.RED));
            return true;
        }

        BaselineDriftReport report = runDriftCompare(name);
        if (report == null) {
            BaselineDriftReport errReport = new BaselineDriftReport(name);
            errReport.add(new BaselineDriftIssue(BaselineDriftSeverity.FAIL, "Baseline file is invalid or corrupt: " + name));
            BaselineRenderer renderer = new BaselineRenderer();
            sender.sendMessage(renderer.renderDrift(errReport));
            return true;
        }

        BaselineRenderer renderer = new BaselineRenderer();
        sender.sendMessage(renderer.renderDrift(report));

        try {
            BaselineTextReportRenderer textRenderer = new BaselineTextReportRenderer();
            String text = textRenderer.render(report, plugin.getDescription().getVersion(),
                    plugin.getServer().getMinecraftVersion());
            ReportFileWriter writer = plugin.getBaselineReportWriter();
            java.nio.file.Path savedPath = writer.save(text, "baseline_" + name);
            writer.prune(plugin.getConfigManager().getBaselineReportsToKeep());
            if (savedPath != null) {
                sender.sendMessage(Component.text("Saved baseline report: plugins/LaunchGuard/reports/baseline/" + savedPath.getFileName(), NamedTextColor.GRAY));
            } else {
                sender.sendMessage(Component.text("Failed to save baseline report.", NamedTextColor.YELLOW));
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to save baseline report: " + e.getMessage());
            sender.sendMessage(Component.text("Failed to save baseline report.", NamedTextColor.YELLOW));
        }
        return true;
    }

    private boolean handleBaselineExport(CommandSender sender, String[] args) {
        String format = args.length > 2 ? args[2].toLowerCase() : "";
        String name = args.length > 3 ? args[3] : "";
        if (!format.equals("json") && !format.equals("html")) {
            String prefix = plugin.getMessageManager().get("prefix");
            sender.sendMessage(Component.text(prefix + " Usage: /launchguard baseline export <json|html> <name>", NamedTextColor.RED));
            return true;
        }
        String error = BaselineNameValidator.validate(name);
        if (error != null) { sender.sendMessage(Component.text(error, NamedTextColor.RED)); return true; }

        BaselineStore store = plugin.getBaselineStore();
        if (!store.exists(name)) {
            sender.sendMessage(Component.text("Baseline not found: " + name, NamedTextColor.RED));
            return true;
        }

        BaselineDriftReport report = runDriftCompare(name);
        if (report == null) {
            BaselineDriftReport errReport = new BaselineDriftReport(name);
            errReport.add(new BaselineDriftIssue(BaselineDriftSeverity.FAIL, "Baseline file is invalid or corrupt: " + name));
            BaselineRenderer renderer = new BaselineRenderer();
            sender.sendMessage(renderer.renderDrift(errReport));
            return true;
        }

        try {
            ExportFileWriter writer = plugin.getExportWriter();
            if (format.equals("json")) {
                BaselineJsonReportRenderer jsonRenderer = new BaselineJsonReportRenderer();
                String json = jsonRenderer.render(report, name, plugin.getDescription().getVersion(),
                        plugin.getServer().getName(), plugin.getServer().getMinecraftVersion(),
                        plugin.getServer().getBukkitVersion());
                Path saved = writer.save(json, "baseline_" + name, "json");
                if (saved != null) sender.sendMessage(Component.text("Saved baseline JSON export: plugins/LaunchGuard/exports/" + saved.getFileName(), NamedTextColor.GRAY));
                else sender.sendMessage(Component.text("Failed to save baseline JSON export.", NamedTextColor.YELLOW));
            } else {
                BaselineHtmlReportRenderer htmlRenderer = new BaselineHtmlReportRenderer();
                String html = htmlRenderer.render(report, name, plugin.getDescription().getVersion(),
                        plugin.getServer().getName(), plugin.getServer().getMinecraftVersion(),
                        plugin.getServer().getBukkitVersion());
                Path saved = writer.save(html, "baseline_" + name, "html");
                if (saved != null) sender.sendMessage(Component.text("Saved baseline HTML export: plugins/LaunchGuard/exports/" + saved.getFileName(), NamedTextColor.GRAY));
                else sender.sendMessage(Component.text("Failed to save baseline HTML export.", NamedTextColor.YELLOW));
            }
            writer.prune(plugin.getConfigManager().getExportsToKeep());
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to save baseline export: " + e.getMessage());
            sender.sendMessage(Component.text("Failed to save baseline export file.", NamedTextColor.YELLOW));
        }
        return true;
    }

    private boolean handleBaselineHistory(CommandSender sender, String[] args) {
        String mode = args.length > 2 ? args[2].toLowerCase() : "";
        ReportFileWriter writer = plugin.getBaselineReportWriter();

        if (mode.equals("latest")) {
            Path latest = writer.getLatestReport();
            if (latest == null) {
                sender.sendMessage(Component.text("[INFO] No saved baseline reports found.", NamedTextColor.AQUA));
                return true;
            }
            try {
                String content = java.nio.file.Files.readString(latest);
                int max = 8000;
                if (content.length() > max) content = content.substring(0, max) + "\n\n... (report truncated)";
                sender.sendMessage(Component.text(content, NamedTextColor.GRAY));
            } catch (Exception e) {
                sender.sendMessage(Component.text("Failed to read baseline report.", NamedTextColor.RED));
            }
            return true;
        }

        List<ReportFileWriter.ReportFileInfo> files = writer.listRecent(10);
        sender.sendMessage(Component.text("[LaunchGuard] Baseline Report History", NamedTextColor.WHITE));
        if (files.isEmpty()) {
            sender.sendMessage(Component.text("[INFO] No saved baseline reports found.", NamedTextColor.AQUA));
            return true;
        }
        var formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(java.time.ZoneId.systemDefault());
        for (ReportFileWriter.ReportFileInfo info : files) {
            String modified = formatter.format(java.time.Instant.ofEpochMilli(info.lastModified()));
            String size = formatFileSize(info.size());
            sender.sendMessage(Component.text("  " + info.name() + "  " + modified + "  " + size, NamedTextColor.GRAY));
        }
        return true;
    }

    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("launchguard.reload") && !sender.hasPermission("launchguard.admin")) {
            sendNoPermission(sender);
            return true;
        }

        boolean ok = plugin.reloadAll();
        String prefix = plugin.getMessageManager().get("prefix");

        if (ok) {
            sender.sendMessage(Component.text(prefix + " " +
                    plugin.getMessageManager().get("reloadComplete"), NamedTextColor.GREEN));
            plugin.getLogger().info("Configuration reloaded by " + sender.getName());
        } else {
            sender.sendMessage(Component.text(prefix + " " +
                    plugin.getMessageManager().get("reloadFailed"), NamedTextColor.RED));
        }
        return true;
    }

    private boolean handleVersion(CommandSender sender) {
        String prefix = plugin.getMessageManager().get("prefix");
        String version = plugin.getMessageManager().get("version", "LaunchGuard version %version%")
                .replace("%version%", plugin.getDescription().getVersion());
        sender.sendMessage(Component.text(prefix + " " + version, NamedTextColor.WHITE));
        return true;
    }

    private void sendNoPermission(CommandSender sender) {
        String prefix = plugin.getMessageManager().get("prefix");
        sender.sendMessage(Component.text(prefix + " " +
                plugin.getMessageManager().get("noPermission"), NamedTextColor.RED));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            String partial = args[0].toLowerCase();
            for (String sub : new String[]{"help", "run", "plugins", "history", "reload", "version", "export", "validate", "baseline"}) {
                if (sub.startsWith(partial)) {
                    completions.add(sub);
                }
            }
            return completions;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("plugins")) {
            List<String> completions = new ArrayList<>();
            String partial = args[1].toLowerCase();
            for (String sub : new String[]{"verbose", "dependencies"}) {
                if (sub.startsWith(partial)) {
                    completions.add(sub);
                }
            }
            return completions;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("history")) {
            List<String> completions = new ArrayList<>();
            String partial = args[1].toLowerCase();
            for (String sub : new String[]{"latest"}) {
                if (sub.startsWith(partial)) {
                    completions.add(sub);
                }
            }
            return completions;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("export")) {
            List<String> completions = new ArrayList<>();
            String partial = args[1].toLowerCase();
            for (String sub : new String[]{"json", "html"}) {
                if (sub.startsWith(partial)) {
                    completions.add(sub);
                }
            }
            return completions;
        }
        if (args.length >= 2 && args[0].equalsIgnoreCase("baseline")) {
            if (args.length == 2) {
                List<String> completions = new ArrayList<>();
                String partial = args[1].toLowerCase();
                for (String sub : new String[]{"save", "list", "compare", "delete", "export", "history"}) {
                    if (sub.startsWith(partial)) completions.add(sub);
                }
                return completions;
            }
            if (args.length == 3 && args[1].equalsIgnoreCase("export")) {
                List<String> completions = new ArrayList<>();
                String partial = args[2].toLowerCase();
                for (String sub : new String[]{"json", "html"}) {
                    if (sub.startsWith(partial)) completions.add(sub);
                }
                return completions;
            }
            if (args.length == 3 && args[1].equalsIgnoreCase("history")) {
                List<String> completions = new ArrayList<>();
                String partial = args[2].toLowerCase();
                if ("latest".startsWith(partial)) completions.add("latest");
                return completions;
            }
            if (args.length >= 3 && (args[1].equalsIgnoreCase("compare") || args[1].equalsIgnoreCase("delete")
                    || (args.length == 4 && args[1].equalsIgnoreCase("export")))) {
                String targetArg = args[1].equalsIgnoreCase("export") ? args[2] : "";
                if (args[1].equalsIgnoreCase("export")) {
                    if (args.length == 4) {
                        BaselineStore store = plugin.getBaselineStore();
                        List<String> names = store.listBaselines().stream()
                                .map(BaselineStore.BaselineEntry::name).toList();
                        List<String> completions = new ArrayList<>();
                        String partial = args[3].toLowerCase();
                        for (String n : names) { if (n.startsWith(partial)) completions.add(n); }
                        return completions;
                    }
                } else {
                    BaselineStore store = plugin.getBaselineStore();
                    List<String> names = store.listBaselines().stream()
                            .map(BaselineStore.BaselineEntry::name).toList();
                    List<String> completions = new ArrayList<>();
                    String partial = args[2].toLowerCase();
                    for (String n : names) { if (n.startsWith(partial)) completions.add(n); }
                    if (args.length == 4 && args[1].equalsIgnoreCase("compare")) {
                        if ("save".startsWith(args[3].toLowerCase())) completions.add("save");
                    }
                    return completions;
                }
            }
        }
        return List.of();
    }
}
