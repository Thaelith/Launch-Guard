package com.serverpulse.launchguard.check.impl;

import com.serverpulse.launchguard.check.Check;
import com.serverpulse.launchguard.check.CheckContext;
import com.serverpulse.launchguard.check.CheckResult;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class RequiredPluginCheck implements Check {

    @Override
    public String id() {
        return "plugins";
    }

    @Override
    public String displayName() {
        return "Required Plugins";
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CheckResult> run(CheckContext context) {
        List<CheckResult> results = new ArrayList<>();
        List<String> required = context.configValue("required", List.of());

        if (required == null || required.isEmpty()) {
            return results;
        }

        for (String pluginName : required) {
            Plugin plugin = context.server().getPluginManager().getPlugin(pluginName);
            if (plugin != null && plugin.isEnabled()) {
                results.add(CheckResult.pass(
                        id(),
                        "Plugin loaded: " + pluginName
                ));
            } else {
                results.add(CheckResult.fail(
                        id(),
                        "Plugin missing or disabled: " + pluginName,
                        "Install and enable the missing plugin before launch."
                ));
            }
        }

        return results;
    }
}
