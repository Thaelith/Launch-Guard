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

        List<Map<String, Object>> baselinePlugins = getList(baseline, "plugins");
        List<Map<String, Object>> currentPlugins = getList(current, "plugins");
        comparePlugins(report, baselinePlugins, currentPlugins);

        List<Map<String, Object>> baselineCommands = getList(baseline, "commands");
        List<Map<String, Object>> currentCommands = getList(current, "commands");
        compareCommands(report, baselineCommands, currentCommands);

        List<Map<String, Object>> baselineWorlds = getList(baseline, "worlds");
        List<Map<String, Object>> currentWorlds = getList(current, "worlds");
        compareWorlds(report, baselineWorlds, currentWorlds);

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
}
