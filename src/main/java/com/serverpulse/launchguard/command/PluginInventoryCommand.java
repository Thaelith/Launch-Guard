package com.serverpulse.launchguard.command;

import com.serverpulse.launchguard.LaunchGuardPlugin;
import com.serverpulse.launchguard.plugin.PluginDependencyStatus;
import com.serverpulse.launchguard.plugin.PluginInventoryEntry;
import com.serverpulse.launchguard.plugin.PluginInventoryRenderer;
import com.serverpulse.launchguard.plugin.PluginInventoryService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.List;

public class PluginInventoryCommand {

    private final LaunchGuardPlugin plugin;
    private final PluginInventoryRenderer renderer;

    public PluginInventoryCommand(LaunchGuardPlugin plugin) {
        this.plugin = plugin;
        this.renderer = new PluginInventoryRenderer();
    }

    public boolean handle(CommandSender sender, String[] args) {
        if (!hasPermission(sender)) {
            sendNoPermission(sender);
            return true;
        }

        String mode = args.length > 1 ? args[1].toLowerCase() : "";
        PluginInventoryService service = new PluginInventoryService(plugin.getServer());
        List<PluginInventoryEntry> entries = service.collectInventory();
        int enabledCount = service.enabledCount(entries);

        switch (mode) {
            case "":
                sender.sendMessage(renderer.renderInventory(entries, enabledCount));
                return true;
            case "verbose":
                sender.sendMessage(renderer.renderVerboseInventory(entries, enabledCount));
                return true;
            case "dependencies":
                List<PluginDependencyStatus> statuses = service.collectDependencyStatuses(entries, true);
                sender.sendMessage(renderer.renderDependencyReport(entries, statuses));
                return true;
            default:
                sender.sendMessage(Component.text(plugin.getMessageManager().get("prefix") + " "
                        + "Unknown plugin inventory option. Use /launchguard plugins [verbose|dependencies].",
                        NamedTextColor.RED));
                return true;
        }
    }

    private boolean hasPermission(CommandSender sender) {
        return sender instanceof ConsoleCommandSender
                || sender.hasPermission("launchguard.plugins")
                || sender.hasPermission("launchguard.admin");
    }

    private void sendNoPermission(CommandSender sender) {
        String prefix = plugin.getMessageManager().get("prefix");
        sender.sendMessage(Component.text(prefix + " " +
                plugin.getMessageManager().get("noPermission"), NamedTextColor.RED));
    }
}
