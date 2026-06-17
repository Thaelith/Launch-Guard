package com.serverpulse.launchguard.baseline;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BaselineHtmlReportRenderer {

    public String render(BaselineDriftReport report, String baselineName, String pluginVersion,
                         String serverName, String serverVersion, String bukkitVersion) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n");
        html.append("<meta charset=\"UTF-8\">\n");
        html.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        html.append("<title>LaunchGuard Baseline Drift Report</title>\n");
        html.append("<style>\n");
        html.append("body{font-family:monospace;background:#1a1a2e;color:#e0e0e0;padding:20px;max-width:900px;margin:0 auto}\n");
        html.append("h1{color:#fff;font-size:1.5em;border-bottom:1px solid #333;padding-bottom:10px}\n");
        html.append(".meta{color:#888;margin-bottom:20px}\n.meta span{margin-right:20px}\n");
        html.append(".summary{background:#16213e;padding:15px;margin-bottom:20px;border-radius:4px}\n");
        html.append(".summary strong{color:#fff}\n");
        html.append("table{width:100%;border-collapse:collapse;margin-bottom:20px}\n");
        html.append("th{background:#16213e;padding:8px 12px;text-align:left;color:#aaa;font-weight:normal}\n");
        html.append("td{padding:8px 12px;border-bottom:1px solid #222}\n");
        html.append(".INFO{color:#42a5f5}.WARN{color:#ff9800}.FAIL{color:#f44336}\n");
        html.append(".M{color:#4caf50;font-weight:bold}.DW{color:#ff9800;font-weight:bold}.DI{color:#f44336;font-weight:bold}\n");
        html.append(".footer{color:#555;font-size:.85em;margin-top:30px;border-top:1px solid #333;padding-top:10px}\n");
        html.append("</style>\n</head>\n<body>\n");

        html.append("<h1>LaunchGuard Baseline Drift Report</h1>\n");
        html.append("<div class=\"meta\">\n");
        html.append("<span>Timestamp: ").append(h(timestamp)).append("</span>\n");
        html.append("<span>Baseline: ").append(h(baselineName)).append("</span>\n");
        html.append("<span>Version: ").append(h(pluginVersion)).append("</span>\n");
        html.append("</div>\n");
        html.append("<div class=\"meta\">\n");
        html.append("<span>Server: ").append(h(serverName)).append("</span>\n");
        html.append("<span>Minecraft: ").append(h(serverVersion)).append("</span>\n");
        html.append("<span>Bukkit: ").append(h(bukkitVersion)).append("</span>\n");
        html.append("</div>\n");

        String statusClass;
        switch (report.status()) {
            case MATCHES_BASELINE: statusClass = "M"; break;
            case DRIFT_DETECTED:  statusClass = "DW"; break;
            default:              statusClass = "DI";
        }
        html.append("<div class=\"summary\">\n");
        html.append("<p>Status: <strong class=\"").append(statusClass).append("\">")
                .append(h(report.status().name())).append("</strong></p>\n");
        html.append("<p>Info: ").append(report.infoCount())
                .append(" | Warnings: ").append(report.warnCount())
                .append(" | Failures: ").append(report.failCount())
                .append(" | Total: ").append(report.issues().size()).append("</p>\n");
        html.append("</div>\n");

        html.append("<table>\n<tr><th>Severity</th><th>Issue</th></tr>\n");
        for (BaselineDriftIssue issue : report.issues()) {
            html.append("<tr><td class=\"").append(issue.severity().name()).append("\">")
                    .append(h(issue.severity().name())).append("</td><td>")
                    .append(h(issue.message())).append("</td></tr>\n");
        }
        if (report.issues().isEmpty()) {
            html.append("<tr><td class=\"INFO\">PASS</td><td>Current server state matches baseline.</td></tr>\n");
        }
        html.append("</table>\n");

        html.append("<div class=\"footer\">\n");
        html.append("<p>This report was generated locally by LaunchGuard. ");
        html.append("It does not include player IPs, player UUIDs, full logs, tokens, webhook URLs, or secrets.</p>\n");
        html.append("</div>\n</body>\n</html>\n");

        return html.toString();
    }

    private static String h(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder(s.length() + 8);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '&': sb.append("&amp;"); break;
                case '<': sb.append("&lt;"); break;
                case '>': sb.append("&gt;"); break;
                case '"': sb.append("&quot;"); break;
                case '\'': sb.append("&#39;"); break;
                default: sb.append(c);
            }
        }
        return sb.toString();
    }
}
