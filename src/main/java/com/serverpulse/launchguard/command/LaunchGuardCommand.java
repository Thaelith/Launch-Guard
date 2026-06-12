package com.serverpulse.launchguard.command;

import com.serverpulse.launchguard.LaunchGuardPlugin;
import com.serverpulse.launchguard.check.Check;
import com.serverpulse.launchguard.check.CheckContext;
import com.serverpulse.launchguard.check.CheckResult;
import com.serverpulse.launchguard.report.PreflightReport;
import com.serverpulse.launchguard.report.ReportRenderer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LaunchGuardCommand implements CommandExecutor, TabCompleter {

    private final LaunchGuardPlugin plugin;

    public LaunchGuardCommand(LaunchGuardPlugin plugin) {
        this.plugin = plugin;
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
        sender.sendMessage(Component.text("/launchguard reload  - Reload configuration", NamedTextColor.GRAY));
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

        PreflightReport report = new PreflightReport();
        List<Check> checks = plugin.getCheckRegistry().getEnabledChecks();

        if (checks.isEmpty()) {
            sender.sendMessage(Component.text(prefix + " No checks are enabled. Check your checks.yml configuration.",
                    NamedTextColor.YELLOW));
            return true;
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

        ReportRenderer renderer = new ReportRenderer(plugin.getMessageManager(), plugin.getConfigManager());
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
            for (String sub : new String[]{"help", "run", "reload", "version"}) {
                if (sub.startsWith(partial)) {
                    completions.add(sub);
                }
            }
            return completions;
        }
        return List.of();
    }
}
