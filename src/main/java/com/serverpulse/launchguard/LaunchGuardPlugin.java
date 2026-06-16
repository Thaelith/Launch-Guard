package com.serverpulse.launchguard;

import com.serverpulse.launchguard.check.CheckRegistry;
import com.serverpulse.launchguard.check.impl.PermissionNodeCheck;
import com.serverpulse.launchguard.check.impl.PluginInventoryCheck;
import com.serverpulse.launchguard.check.impl.RequiredCommandCheck;
import com.serverpulse.launchguard.check.impl.RequiredPluginCheck;
import com.serverpulse.launchguard.check.impl.RequiredWorldCheck;
import com.serverpulse.launchguard.check.impl.SafeLocationCheck;
import com.serverpulse.launchguard.command.LaunchGuardCommand;
import com.serverpulse.launchguard.config.ChecksConfig;
import com.serverpulse.launchguard.config.ConfigManager;
import com.serverpulse.launchguard.config.MessageManager;
import com.serverpulse.launchguard.report.JsonExportFileWriter;
import com.serverpulse.launchguard.report.PlainTextReportRenderer;
import com.serverpulse.launchguard.report.PreflightReport;
import com.serverpulse.launchguard.report.PreflightRunner;
import com.serverpulse.launchguard.report.ReportFileWriter;
import com.serverpulse.launchguard.report.ReportRenderer;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

public class LaunchGuardPlugin extends JavaPlugin {

    private ConfigManager configManager;
    private ChecksConfig checksConfig;
    private MessageManager messageManager;
    private CheckRegistry checkRegistry;
    private LaunchGuardCommand commandHandler;
    private ReportFileWriter reportFileWriter;
    private JsonExportFileWriter jsonExportWriter;

    @Override
    public void onEnable() {
        getDataFolder().mkdirs();

        this.configManager = new ConfigManager(this);
        this.checksConfig = new ChecksConfig(this);
        this.messageManager = new MessageManager(this);
        this.checkRegistry = new CheckRegistry();

        loadAll();

        File reportsDir = new File(getDataFolder(), "reports");
        this.reportFileWriter = new ReportFileWriter(reportsDir.toPath(), getLogger());

        File exportsDir = new File(getDataFolder(), "exports");
        this.jsonExportWriter = new JsonExportFileWriter(exportsDir.toPath(), getLogger());

        this.commandHandler = new LaunchGuardCommand(this);
        PluginCommand mainCommand = getCommand("launchguard");
        if (mainCommand != null) {
            mainCommand.setExecutor(commandHandler);
            mainCommand.setTabCompleter(commandHandler);
        } else {
            getLogger().warning("Command 'launchguard' not found in plugin.yml. The plugin will not respond to commands.");
        }

        if (configManager.isRunOnStartup()) {
            int delayTicks = configManager.getStartupDelayTicks();
            new BukkitRunnable() {
                @Override
                public void run() {
                    runStartupCheck();
                }
            }.runTaskLater(this, delayTicks);
        }

        getLogger().info("LaunchGuard v" + getDescription().getVersion() + " enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("LaunchGuard disabled.");
    }

    public void loadAll() {
        configManager.loadConfig();
        checksConfig.load();
        messageManager.load();
        registerChecks();
    }

    public boolean reloadAll() {
        boolean configOk = configManager.reloadConfig();
        boolean checksOk = checksConfig.reload();
        boolean messagesOk = messageManager.reload();

        if (configOk && checksOk && messagesOk) {
            checkRegistry.clear();
            registerChecks();
            return true;
        }

        getLogger().warning("Reload partially failed: config=" + configOk
                + " checks=" + checksOk + " messages=" + messagesOk);
        return false;
    }

    private void registerChecks() {
        checkRegistry.register(new RequiredPluginCheck(), checksConfig.isCheckEnabled("plugins"));
        checkRegistry.register(new RequiredCommandCheck(), checksConfig.isCheckEnabled("commands"));
        checkRegistry.register(new RequiredWorldCheck(), checksConfig.isCheckEnabled("worlds"));
        checkRegistry.register(new SafeLocationCheck(), checksConfig.isCheckEnabled("locations"));
        checkRegistry.register(new PermissionNodeCheck(), checksConfig.isCheckEnabled("permissions"));
        checkRegistry.register(new PluginInventoryCheck(), checksConfig.isCheckEnabled("pluginInventory"));
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public ChecksConfig getChecksConfig() {
        return checksConfig;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public CheckRegistry getCheckRegistry() {
        return checkRegistry;
    }

    public ReportFileWriter getReportFileWriter() {
        return reportFileWriter;
    }

    public JsonExportFileWriter getJsonExportWriter() {
        return jsonExportWriter;
    }

    private void runStartupCheck() {
        getLogger().info("Running startup preflight check.");

        try {
            PreflightRunner runner = new PreflightRunner(this);
            PreflightReport report = runner.run();

            ReportRenderer renderer = new ReportRenderer(messageManager, configManager);
            net.kyori.adventure.text.Component output = renderer.render(report);
            Bukkit.getConsoleSender().sendMessage(output);

            if (configManager.isSaveReports()) {
                PlainTextReportRenderer plainRenderer = new PlainTextReportRenderer();
                String plainText = plainRenderer.render(report, "startup",
                        getDescription().getVersion(), configManager.showPassedChecks());
                reportFileWriter.save(plainText, "startup");
                reportFileWriter.prune(configManager.getReportsToKeep());
            }
        } catch (Exception e) {
            getLogger().warning("Startup preflight check failed with an error: " + e.getMessage());
        }
    }
}
