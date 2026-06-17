package com.serverpulse.launchguard.baseline;

import java.util.*;

public class BaselineCompareService {

    @SuppressWarnings("unchecked")
    public BaselineDriftReport compare(Map<String, Object> baseline, Map<String, Object> current) {
        String name = Objects.toString(baseline.get("baselineName"), "unknown");
        BaselineDriftReport report = new BaselineDriftReport(name);

        Object schemaVersion = baseline.get("schemaVersion");
        if (!(schemaVersion instanceof Integer) || ((Integer) schemaVersion) != 1) {
            report.add(new BaselineDriftIssue(BaselineDriftSeverity.FAIL, "Unsupported baseline schema version"));
            return report;
        }

        try {
            List<Map<String, Object>> baselinePlugins = getListEntries(baseline, "plugins");
            List<Map<String, Object>> currentPlugins = getListEntries(current, "plugins");
            comparePlugins(report, baselinePlugins, currentPlugins);

            List<Map<String, Object>> baselineCommands = getListEntries(baseline, "commands");
            List<Map<String, Object>> currentCommands = getListEntries(current, "commands");
            compareCommands(report, baselineCommands, currentCommands);

            List<Map<String, Object>> baselineWorlds = getListEntries(baseline, "worlds");
            List<Map<String, Object>> currentWorlds = getListEntries(current, "worlds");
            compareWorlds(report, baselineWorlds, currentWorlds);

            Map<String, Object> baselineConfig = getMap(baseline, "launchGuardConfig");
            Map<String, Object> currentConfig = getMap(current, "launchGuardConfig");
            compareConfig(report, baselineConfig, currentConfig);
        } catch (ClassCastException | NullPointerException e) {
            report.clear();
            report.add(new BaselineDriftIssue(BaselineDriftSeverity.FAIL, "Baseline file is invalid or corrupt: " + name));
        }

        return report;
    }

    @SuppressWarnings("unchecked")
    private void comparePlugins(BaselineDriftReport report,
                                List<Map<String, Object>> baseline, List<Map<String, Object>> current) {
        Map<String, Map<String, Object>> baselineMap = indexByName(baseline);
        Map<String, Map<String, Object>> currentMap = indexByName(current);

        for (String name : baselineMap.keySet()) {
            Map<String, Object> b = baselineMap.get(name);
            Map<String, Object> c = currentMap.get(name);
            if (c == null) {
                report.add(new BaselineDriftIssue(BaselineDriftSeverity.WARN, "Plugin removed: " + name));
                continue;
            }
            boolean bEnabled = booleanValue(b, "enabled");
            boolean cEnabled = booleanValue(c, "enabled");
            if (bEnabled && !cEnabled) {
                report.add(new BaselineDriftIssue(BaselineDriftSeverity.WARN, "Plugin disabled since baseline: " + name));
            } else if (!bEnabled && cEnabled) {
                report.add(new BaselineDriftIssue(BaselineDriftSeverity.INFO, "Plugin enabled since baseline: " + name));
            }
            String bVer = Objects.toString(b.get("version"), "");
            String cVer = Objects.toString(c.get("version"), "");
            if (!bVer.equals(cVer)) {
                BaselineDriftSeverity sev = isDowngrade(bVer, cVer)
                        ? BaselineDriftSeverity.WARN : BaselineDriftSeverity.INFO;
                report.add(new BaselineDriftIssue(sev, "Plugin version changed: " + name + " " + bVer + " -> " + cVer));
            }
        }
        for (String name : currentMap.keySet()) {
            if (!baselineMap.containsKey(name)) {
                report.add(new BaselineDriftIssue(BaselineDriftSeverity.INFO, "Plugin added: " + name));
            }
        }
    }

    private void compareCommands(BaselineDriftReport report,
                                  List<Map<String, Object>> baseline, List<Map<String, Object>> current) {
        Map<String, Map<String, Object>> baselineMap = indexByName(baseline);
        Map<String, Map<String, Object>> currentMap = indexByName(current);

        for (String name : baselineMap.keySet()) {
            if (!currentMap.containsKey(name)) {
                report.add(new BaselineDriftIssue(BaselineDriftSeverity.WARN, "Command removed: /" + name));
            }
        }
        for (String name : currentMap.keySet()) {
            if (!baselineMap.containsKey(name)) {
                report.add(new BaselineDriftIssue(BaselineDriftSeverity.INFO, "Command added: /" + name));
            }
        }
    }

    private void compareWorlds(BaselineDriftReport report,
                                List<Map<String, Object>> baseline, List<Map<String, Object>> current) {
        Map<String, Map<String, Object>> baselineMap = indexByName(baseline);
        Map<String, Map<String, Object>> currentMap = indexByName(current);

        for (String name : baselineMap.keySet()) {
            if (!currentMap.containsKey(name)) {
                report.add(new BaselineDriftIssue(BaselineDriftSeverity.WARN, "World removed: " + name));
            }
        }
        for (String name : currentMap.keySet()) {
            if (!baselineMap.containsKey(name)) {
                report.add(new BaselineDriftIssue(BaselineDriftSeverity.INFO, "World added: " + name));
            }
        }
    }

    private Map<String, Map<String, Object>> indexByName(List<Map<String, Object>> list) {
        Map<String, Map<String, Object>> map = new LinkedHashMap<>();
        for (Map<String, Object> item : list) {
            String name = Objects.toString(item.get("name"), "").toLowerCase();
            if (!name.isEmpty()) map.put(name, item);
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> getListEntries(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (!(value instanceof List)) return List.of();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object item : (List<?>) value) {
            if (item instanceof Map) result.add((Map<String, Object>) item);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> getList(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value instanceof List) return (List<Map<String, Object>>) value;
        return List.of();
    }

    private static boolean booleanValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value instanceof Boolean && (Boolean) value;
    }

    private static boolean isDowngrade(String oldVer, String newVer) {
        try {
            String[] oldParts = oldVer.replaceAll("[^0-9.]", "").split("\\.");
            String[] newParts = newVer.replaceAll("[^0-9.]", "").split("\\.");
            int len = Math.min(oldParts.length, newParts.length);
            for (int i = 0; i < len; i++) {
                int o = oldParts[i].isEmpty() ? 0 : Integer.parseInt(oldParts[i]);
                int n = newParts[i].isEmpty() ? 0 : Integer.parseInt(newParts[i]);
                if (n < o) return true;
                if (n > o) return false;
            }
        } catch (NumberFormatException ignored) {}
        return false;
    }

    @SuppressWarnings("unchecked")
    private void compareConfig(BaselineDriftReport report, Map<String, Object> baseline, Map<String, Object> current) {
        if (baseline == null || current == null) return;

        List<String> bPlugins = sortStringList((List<String>) baseline.getOrDefault("configuredRequiredPlugins", List.of()));
        List<String> cPlugins = sortStringList((List<String>) current.getOrDefault("configuredRequiredPlugins", List.of()));
        if (!bPlugins.equals(cPlugins)) {
            report.add(new BaselineDriftIssue(BaselineDriftSeverity.WARN, "Required plugin list changed in checks.yml"));
        }

        List<String> bCmds = sortStringList(normalizeCommands((List<String>) baseline.getOrDefault("configuredRequiredCommands", List.of())));
        List<String> cCmds = sortStringList(normalizeCommands((List<String>) current.getOrDefault("configuredRequiredCommands", List.of())));
        if (!bCmds.equals(cCmds)) {
            report.add(new BaselineDriftIssue(BaselineDriftSeverity.WARN, "Required command list changed in checks.yml"));
        }

        List<String> bWorlds = sortStringList((List<String>) baseline.getOrDefault("configuredRequiredWorlds", List.of()));
        List<String> cWorlds = sortStringList((List<String>) current.getOrDefault("configuredRequiredWorlds", List.of()));
        if (!bWorlds.equals(cWorlds)) {
            report.add(new BaselineDriftIssue(BaselineDriftSeverity.WARN, "Required world list changed in checks.yml"));
        }

        Map<String, Object> bNodes = (Map<String, Object>) baseline.getOrDefault("configuredPermissionNodes", Map.of());
        Map<String, Object> cNodes = (Map<String, Object>) current.getOrDefault("configuredPermissionNodes", Map.of());
        List<String> bShould = sortStringList((List<String>) bNodes.getOrDefault("shouldExist", List.of()));
        List<String> cShould = sortStringList((List<String>) cNodes.getOrDefault("shouldExist", List.of()));
        List<String> bDanger = sortStringList((List<String>) bNodes.getOrDefault("dangerous", List.of()));
        List<String> cDanger = sortStringList((List<String>) cNodes.getOrDefault("dangerous", List.of()));
        if (!bShould.equals(cShould) || !bDanger.equals(cDanger)) {
            report.add(new BaselineDriftIssue(BaselineDriftSeverity.WARN, "Permission node list changed in checks.yml"));
        }

        Map<String, Object> bPi = (Map<String, Object>) baseline.getOrDefault("pluginInventory", Map.of());
        Map<String, Object> cPi = (Map<String, Object>) current.getOrDefault("pluginInventory", Map.of());
        if (!Objects.equals(bPi.get("enabled"), cPi.get("enabled"))
                || !Objects.equals(bPi.get("checkDependencies"), cPi.get("checkDependencies"))
                || !Objects.equals(bPi.get("warnOnSoftDependencyMissing"), cPi.get("warnOnSoftDependencyMissing"))) {
            report.add(new BaselineDriftIssue(BaselineDriftSeverity.WARN, "Plugin inventory settings changed in checks.yml"));
        }

        List<String> bLocs = sortStringList((List<String>) baseline.getOrDefault("configuredLocationKeys", List.of()));
        List<String> cLocs = sortStringList((List<String>) current.getOrDefault("configuredLocationKeys", List.of()));
        if (!bLocs.equals(cLocs)) {
            report.add(new BaselineDriftIssue(BaselineDriftSeverity.WARN, "Configured location list changed in checks.yml"));
        }
    }

    private static Map<String, Object> getMap(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) value;
            return map;
        }
        return Map.of();
    }

    private static List<String> sortStringList(List<String> list) {
        List<String> sorted = new ArrayList<>(list);
        sorted.sort(String.CASE_INSENSITIVE_ORDER);
        return sorted;
    }

    private static List<String> normalizeCommands(List<String> commands) {
        List<String> result = new ArrayList<>();
        for (String cmd : commands) {
            result.add(cmd.startsWith("/") ? cmd.substring(1).toLowerCase() : cmd.toLowerCase());
        }
        result.sort(String.CASE_INSENSITIVE_ORDER);
        return result;
    }
}
