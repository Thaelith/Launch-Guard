package com.serverpulse.launchguard.check.impl;

import com.serverpulse.launchguard.check.Check;
import com.serverpulse.launchguard.check.CheckContext;
import com.serverpulse.launchguard.check.CheckResult;
import com.serverpulse.launchguard.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class SafeLocationCheck implements Check {

    @Override
    public String id() {
        return "locations";
    }

    @Override
    public String displayName() {
        return "Safe Locations";
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CheckResult> run(CheckContext context) {
        List<CheckResult> results = new ArrayList<>();
        Map<String, Object> entries = context.configValue("entries", new LinkedHashMap<>());

        if (entries == null || entries.isEmpty()) {
            return results;
        }

        Logger logger = context.plugin().getLogger();

        for (Map.Entry<String, Object> entry : entries.entrySet()) {
            String locationName = entry.getKey();

            if (!(entry.getValue() instanceof Map)) {
                results.add(CheckResult.fail(id(),
                        "Location config malformed: " + locationName,
                        "Check the checks.yml location entry format."));
                continue;
            }

            Map<String, Object> locData = (Map<String, Object>) entry.getValue();
            String worldName = locData.getOrDefault("world", "").toString();

            if (worldName.isEmpty()) {
                results.add(CheckResult.fail(id(),
                        "Location has no world configured: " + locationName,
                        "Add a world field to this location entry in checks.yml."));
                continue;
            }

            double x = parseCoord(locData.get("x"), "x", locationName, logger);
            double y = parseCoord(locData.get("y"), "y", locationName, logger);
            double z = parseCoord(locData.get("z"), "z", locationName, logger);

            Object safeObj = locData.get("safe");
            boolean expectedSafe = safeObj == null || Boolean.parseBoolean(safeObj.toString());

            World world = context.server().getWorld(worldName);
            if (world == null) {
                results.add(CheckResult.fail(id(),
                        "Location world missing: " + locationName + " (expected world: " + worldName + ")",
                        "Check the world folder name or load the world before launch."));
                continue;
            }

            Location loc = new Location(world, x, y, z);
            LocationUtil.SafeResult safety = LocationUtil.checkSafety(loc);

            if (safety.safe() && expectedSafe) {
                String note = safety.reason() != null ? " (" + safety.reason() + ")" : "";
                results.add(CheckResult.pass(id(),
                        "Location safe: " + locationName + note));
            } else if (safety.safe() && !expectedSafe) {
                results.add(CheckResult.info(id(),
                        "Location appears safe but marked unsafe: " + locationName,
                        "Review the safe flag for this location in checks.yml."));
            } else if (!safety.safe() && expectedSafe) {
                results.add(CheckResult.fail(id(),
                        "Location unsafe: " + locationName + " (" + safety.reason() + ")",
                        "Review and fix the location before opening the server."));
            } else {
                results.add(CheckResult.warn(id(),
                        "Location unsafe (expected): " + locationName + " (" + safety.reason() + ")",
                        "This location is marked as expected unsafe. Review before launch."));
            }
        }

        return results;
    }

    private double parseCoord(Object value, String axis, String locationName, Logger logger) {
        if (value == null) {
            logger.warning("Location '" + locationName + "': missing '" + axis + "' coordinate, using 0.");
            return 0;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            logger.warning("Location '" + locationName + "': invalid '" + axis + "' value '"
                    + value + "', using 0.");
            return 0;
        }
    }
}
