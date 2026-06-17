package com.serverpulse.launchguard.baseline;

import java.util.ArrayList;
import java.util.List;

public class BaselineDriftReport {

    private final String baselineName;
    private final List<BaselineDriftIssue> issues = new ArrayList<>();

    public BaselineDriftReport(String baselineName) {
        this.baselineName = baselineName;
    }

    public String baselineName() { return baselineName; }

    public void add(BaselineDriftIssue issue) { issues.add(issue); }

    public void clear() { issues.clear(); }

    public List<BaselineDriftIssue> issues() { return List.copyOf(issues); }

    public int infoCount() {
        return (int) issues.stream().filter(i -> i.severity() == BaselineDriftSeverity.INFO).count();
    }

    public int warnCount() {
        return (int) issues.stream().filter(i -> i.severity() == BaselineDriftSeverity.WARN).count();
    }

    public int failCount() {
        return (int) issues.stream().filter(i -> i.severity() == BaselineDriftSeverity.FAIL).count();
    }

    public BaselineDriftStatus status() {
        if (failCount() > 0) return BaselineDriftStatus.BASELINE_INVALID;
        if (!issues.isEmpty()) return BaselineDriftStatus.DRIFT_DETECTED;
        return BaselineDriftStatus.MATCHES_BASELINE;
    }
}
