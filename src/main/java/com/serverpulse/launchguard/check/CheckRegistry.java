package com.serverpulse.launchguard.check;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CheckRegistry {

    private final Map<String, Check> checks = new LinkedHashMap<>();
    private final Map<String, Boolean> enabled = new LinkedHashMap<>();

    public void register(Check check, boolean isEnabled) {
        checks.put(check.id(), check);
        enabled.put(check.id(), isEnabled);
    }

    public void setEnabled(String checkId, boolean isEnabled) {
        enabled.put(checkId, isEnabled);
    }

    public boolean isEnabled(String checkId) {
        return enabled.getOrDefault(checkId, false);
    }

    public List<Check> getEnabledChecks() {
        List<Check> result = new ArrayList<>();
        for (Map.Entry<String, Check> entry : checks.entrySet()) {
            if (enabled.getOrDefault(entry.getKey(), false)) {
                result.add(entry.getValue());
            }
        }
        return result;
    }

    public List<Check> getAllChecks() {
        return new ArrayList<>(checks.values());
    }

    public Check get(String id) {
        return checks.get(id);
    }

    public void clear() {
        checks.clear();
        enabled.clear();
    }
}
