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
                        "Location config invalid: " + locationName + " (world is missing)",
                        "Add a world field to this location entry in checks.yml."));
                continue;
            }

            CoordResult xResult = parseCoord(locData.get("x"), "x");
            CoordResult yResult = parseCoord(locData.get("y"), "y");
            CoordResult zResult = parseCoord(locData.get("z"), "z");

            if (!xResult.valid()) {
                results.add(CheckResult.fail(id(),
                        "Location config invalid: " + locationName + " (x is missing or not numeric)",
                        "Fix the location coordinates in checks.yml."));
                continue;
            }
            if (!yResult.valid()) {
                results.add(CheckResult.fail(id(),
                        "Location config invalid: " + locationName + " (y is missing or not numeric)",
                        "Fix the location coordinates in checks.yml."));
                continue;
            }
            if (!zResult.valid()) {
                results.add(CheckResult.fail(id(),
                        "Location config invalid: " + locationName + " (z is missing or not numeric)",
                        "Fix the location coordinates in checks.yml."));
                continue;
            }

            double x = xResult.value();
            double y = yResult.value();
            double z = zResult.value();

            World world = context.server().getWorld(worldName);
            if (world == null) {
                results.add(CheckResult.fail(id(),
                        "Location world missing: " + locationName + " (expected world: " + worldName + ")",
                        "Check the world folder name or load the world before launch."));
                continue;
            }

            if (y < world.getMinHeight() || y > world.getMaxHeight()) {
                results.add(CheckResult.fail(id(),
                        "Location config invalid: " + locationName
                                + " (y is outside world height limits: min=" + world.getMinHeight()
                                + ", max=" + world.getMaxHeight() + ")",
                        "Fix the y coordinate in checks.yml."));
                continue;
            }

            Object safeObj = locData.get("safe");
            boolean expectedSafe = safeObj == null || Boolean.parseBoolean(safeObj.toString());

            Location loc = new Location(world, x, y, z);
            LocationUtil.SafeResult safety = LocationUtil.checkSafety(loc);

            if (safety.isUnknown()) {
                results.add(CheckResult.warn(id(),
                        "Location could not be fully checked because the chunk is not loaded: " + locationName,
                        "Visit or load the area once, then run the check again."));
            } else if (safety.isSafe() && expectedSafe) {
                results.add(CheckResult.pass(id(),
                        "Location safe: " + locationName));
            } else if (safety.isSafe() && !expectedSafe) {
                results.add(CheckResult.info(id(),
                        "Location appears safe but marked unsafe: " + locationName,
                        "Review the safe flag for this location in checks.yml."));
            } else if (safety.isUnsafe() && expectedSafe) {
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

    private CoordResult parseCoord(Object value, String axis) {
        if (value == null) {
            return CoordResult.INVALID;
        }
        if (value instanceof Number) {
            return CoordResult.valid(((Number) value).doubleValue());
        }
        try {
            return CoordResult.valid(Double.parseDouble(value.toString()));
        } catch (NumberFormatException e) {
            return CoordResult.INVALID;
        }
    }

    private record CoordResult(double value, boolean valid) {
        static final CoordResult INVALID = new CoordResult(0, false);

        static CoordResult valid(double value) {
            return new CoordResult(value, true);
        }
    }
}
