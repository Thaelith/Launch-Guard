package com.serverpulse.launchguard;

import com.serverpulse.launchguard.check.CheckRegistry;
import com.serverpulse.launchguard.check.impl.PermissionNodeCheck;
import com.serverpulse.launchguard.check.impl.RequiredCommandCheck;
import com.serverpulse.launchguard.check.impl.RequiredPluginCheck;
import com.serverpulse.launchguard.check.impl.RequiredWorldCheck;
import com.serverpulse.launchguard.check.impl.SafeLocationCheck;
import com.serverpulse.launchguard.command.LaunchGuardCommand;
import com.serverpulse.launchguard.config.ChecksConfig;
import com.serverpulse.launchguard.config.ConfigManager;
import com.serverpulse.launchguard.config.MessageManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class LaunchGuardPlugin extends JavaPlugin {

    private ConfigManager configManager;
    private ChecksConfig checksConfig;
    private MessageManager messageManager;
    private CheckRegistry checkRegistry;
    private LaunchGuardCommand commandHandler;

    @Override
    public void onEnable() {
        getDataFolder().mkdirs();

        this.configManager = new ConfigManager(this);
        this.checksConfig = new ChecksConfig(this);
        this.messageManager = new MessageManager(this);
        this.checkRegistry = new CheckRegistry();

        loadAll();

        this.commandHandler = new LaunchGuardCommand(this);
        PluginCommand mainCommand = getCommand("launchguard");
        if (mainCommand != null) {
            mainCommand.setExecutor(commandHandler);
            mainCommand.setTabCompleter(commandHandler);
        } else {
            getLogger().warning("Command 'launchguard' not found in plugin.yml. The plugin will not respond to commands.");
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
}
