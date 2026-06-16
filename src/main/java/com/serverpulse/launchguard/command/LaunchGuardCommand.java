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
            for (String sub : new String[]{"help", "run", "plugins", "history", "reload", "version", "export", "validate"}) {
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
        return List.of();
    }
}
