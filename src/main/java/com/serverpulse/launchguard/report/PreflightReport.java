package com.serverpulse.launchguard.report;

import com.serverpulse.launchguard.check.CheckResult;
import com.serverpulse.launchguard.check.CheckSeverity;
import com.serverpulse.launchguard.check.ReportStatus;

import java.util.ArrayList;
import java.util.List;

public class PreflightReport {

    private final List<CheckResult> results = new ArrayList<>();

    public void addResult(CheckResult result) {
        results.add(result);
    }

    public void addResults(List<CheckResult> results) {
        this.results.addAll(results);
    }

    public List<CheckResult> results() {
        return List.copyOf(results);
    }

    public int passedCount() {
        int count = 0;
        for (CheckResult r : results) {
            if (r.severity() == CheckSeverity.PASS || r.severity() == CheckSeverity.INFO) {
                count++;
            }
        }
        return count;
    }

    public int warnCount() {
        int count = 0;
        for (CheckResult r : results) {
            if (r.severity() == CheckSeverity.WARN) {
                count++;
            }
        }
        return count;
    }

    public int failCount() {
        int count = 0;
        for (CheckResult r : results) {
            if (r.severity() == CheckSeverity.FAIL) {
                count++;
            }
        }
        return count;
    }

    public ReportStatus status() {
        if (failCount() > 0) {
            return ReportStatus.NOT_READY;
        }
        if (warnCount() > 0) {
            return ReportStatus.READY_WITH_WARNINGS;
        }
        return ReportStatus.READY;
    }
}
