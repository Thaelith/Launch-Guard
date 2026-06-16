package com.serverpulse.launchguard.report;

import com.serverpulse.launchguard.check.CheckResult;
import com.serverpulse.launchguard.check.CheckSeverity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PlainTextReportRenderer {

    private static final String SEPARATOR = "----------------------------------------";

    public String render(PreflightReport report, String source, String version, boolean showPassed) {
        StringBuilder sb = new StringBuilder();

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        sb.append("LaunchGuard Preflight Report\n");
        sb.append("Timestamp: ").append(timestamp).append("\n");
        sb.append("Source: ").append(source).append("\n");
        sb.append("Version: ").append(version).append("\n\n");
        sb.append(SEPARATOR).append("\n");

        for (CheckResult result : report.results()) {
            if (result.severity() == CheckSeverity.PASS && !showPassed) {
                continue;
            }
            sb.append(formatPlainResult(result));
        }

        sb.append(SEPARATOR).append("\n");
        sb.append(formatPlainStatus(report)).append("\n");
        sb.append("Passed: ").append(report.passedCount()).append("\n");
        sb.append("Warnings: ").append(report.warnCount()).append("\n");
        sb.append("Failures: ").append(report.failCount()).append("\n");

        return sb.toString();
    }

    private String formatPlainResult(CheckResult result) {
        String label = switch (result.severity()) {
            case PASS -> "[PASS]";
            case INFO -> "[INFO]";
            case WARN -> "[WARN]";
            case FAIL -> "[FAIL]";
        };

        StringBuilder sb = new StringBuilder();
        sb.append(label).append(" ").append(result.message()).append("\n");

        if (result.suggestion() != null && !result.suggestion().isEmpty()) {
            sb.append("  -> ").append(result.suggestion()).append("\n");
        }

        return sb.toString();
    }

    private String formatPlainStatus(PreflightReport report) {
        return switch (report.status()) {
            case READY -> "Result: READY";
            case READY_WITH_WARNINGS -> "Result: READY_WITH_WARNINGS";
            case NOT_READY -> "Result: NOT_READY";
        };
    }
}
