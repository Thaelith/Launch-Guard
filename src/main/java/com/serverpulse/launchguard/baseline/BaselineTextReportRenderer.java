package com.serverpulse.launchguard.baseline;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BaselineTextReportRenderer {

    private static final String SEPARATOR = "----------------------------------------\n";

    public String render(BaselineDriftReport report, String pluginVersion, String serverVersion) {
        StringBuilder sb = new StringBuilder();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        sb.append("LaunchGuard Baseline Drift Report\n");
        sb.append("Timestamp: ").append(timestamp).append("\n");
        sb.append("Baseline: ").append(report.baselineName()).append("\n");
        sb.append("LaunchGuard: ").append(pluginVersion).append("\n");
        sb.append("Server: ").append(serverVersion).append("\n");
        sb.append("Status: ").append(report.status().name()).append("\n\n");
        sb.append(SEPARATOR);

        if (report.issues().isEmpty()) {
            sb.append("[PASS] Current server state matches baseline.\n");
        } else {
            for (BaselineDriftIssue issue : report.issues()) {
                sb.append("[").append(issue.severity().name()).append("] ");
                sb.append(issue.message()).append("\n");
            }
        }

        sb.append(SEPARATOR);
        sb.append("Info: ").append(report.infoCount()).append("\n");
        sb.append("Warnings: ").append(report.warnCount()).append("\n");
        sb.append("Failures: ").append(report.failCount()).append("\n\n");
        sb.append("This report was generated locally by LaunchGuard. ");
        sb.append("It does not include player IPs, player UUIDs, full logs, tokens, webhook URLs, or secrets.\n");

        return sb.toString();
    }
}
