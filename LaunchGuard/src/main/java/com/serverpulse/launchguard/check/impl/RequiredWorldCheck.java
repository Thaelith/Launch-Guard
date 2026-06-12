package com.serverpulse.launchguard.check.impl;

import com.serverpulse.launchguard.check.Check;
import com.serverpulse.launchguard.check.CheckContext;
import com.serverpulse.launchguard.check.CheckResult;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class RequiredWorldCheck implements Check {

    @Override
    public String id() {
        return "worlds";
    }

    @Override
    public String displayName() {
        return "Required Worlds";
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CheckResult> run(CheckContext context) {
        List<CheckResult> results = new ArrayList<>();
        List<String> required = context.configValue("required", List.of());

        if (required == null || required.isEmpty()) {
            return results;
        }

        for (String worldName : required) {
            World world = context.server().getWorld(worldName);
            if (world != null) {
                results.add(CheckResult.pass(
                        id(),
                        "World exists: " + worldName
                ));
            } else {
                results.add(CheckResult.fail(
                        id(),
                        "World missing: " + worldName,
                        "Check the world folder name or load the world before launch."
                ));
            }
        }

        return results;
    }
}
