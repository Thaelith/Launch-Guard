package com.serverpulse.launchguard.check.impl;

import com.serverpulse.launchguard.check.Check;
import com.serverpulse.launchguard.check.CheckContext;
import com.serverpulse.launchguard.check.CheckResult;
import org.bukkit.permissions.Permission;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PermissionNodeCheck implements Check {

    @Override
    public String id() {
        return "permissions";
    }

    @Override
    public String displayName() {
        return "Permission Nodes";
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CheckResult> run(CheckContext context) {
        List<CheckResult> results = new ArrayList<>();

        Map<String, Object> nodes = context.configValue("nodes", Map.of());
        if (nodes == null || nodes.isEmpty()) {
            return results;
        }

        List<String> shouldExist = (List<String>) nodes.getOrDefault("shouldExist", List.of());
        List<String> dangerous = (List<String>) nodes.getOrDefault("dangerous", List.of());

        if (shouldExist != null) {
            for (String node : shouldExist) {
                Permission perm = context.server().getPluginManager().getPermission(node);
                if (perm != null) {
                    results.add(CheckResult.pass(
                            id(),
                            "Permission node registered: " + node
                    ));
                } else {
                    results.add(CheckResult.warn(
                            id(),
                            "Permission node is not registered: " + node,
                            "Install or configure the plugin that provides this permission."
                    ));
                }
            }
        }

        if (dangerous != null) {
            for (String node : dangerous) {
                Permission perm = context.server().getPluginManager().getPermission(node);
                if (perm != null) {
                    results.add(CheckResult.warn(
                            id(),
                            "Dangerous permission node exists and should be reviewed: " + node,
                            "Review this permission assignment before launch."
                    ));
                }
            }
        }

        return results;
    }
}
