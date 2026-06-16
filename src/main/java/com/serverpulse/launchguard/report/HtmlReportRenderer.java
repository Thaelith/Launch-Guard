package com.serverpulse.launchguard.report;

import com.serverpulse.launchguard.check.CheckResult;
import com.serverpulse.launchguard.check.CheckSeverity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HtmlReportRenderer {

    public String render(PreflightReport report, String source, String pluginVersion,
                         String serverName, String serverVersion, String bukkitVersion) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html lang=\"en\">\n");
        html.append("<head>\n");
        html.append("<meta charset=\"UTF-8\">\n");
        html.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        html.append("<title>LaunchGuard Preflight Report</title>\n");
        html.append("<style>\n");
        html.append("body { font-family: monospace; background: #1a1a2e; color: #e0e0e0; padding: 20px; max-width: 900px; margin: 0 auto; }\n");
        html.append("h1 { color: #ffffff; font-size: 1.5em; border-bottom: 1px solid #333; padding-bottom: 10px; }\n");
        html.append(".meta { color: #888; margin-bottom: 20px; }\n");
        html.append(".meta span { margin-right: 20px; }\n");
        html.append(".summary { background: #16213e; padding: 15px; margin-bottom: 20px; border-radius: 4px; }\n");
        html.append(".summary strong { color: #ffffff; }\n");
        html.append(".status-ready { color: #4caf50; font-weight: bold; }\n");
        html.append(".status-warn { color: #ff9800; font-weight: bold; }\n");
        html.append(".status-fail { color: #f44336; font-weight: bold; }\n");
        html.append("table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }\n");
        html.append("th { background: #16213e; padding: 8px 12px; text-align: left; color: #aaa; font-weight: normal; }\n");
        html.append("td { padding: 8px 12px; border-bottom: 1px solid #222; }\n");
        html.append(".PASS { color: #4caf50; }\n");
        html.append(".INFO { color: #42a5f5; }\n");
        html.append(".WARN { color: #ff9800; }\n");
        html.append(".FAIL { color: #f44336; }\n");
        html.append(".suggestion { color: #888; font-size: 0.9em; }\n");
        html.append(".footer { color: #555; font-size: 0.85em; margin-top: 30px; border-top: 1px solid #333; padding-top: 10px; }\n");
        html.append("</style>\n");
        html.append("</head>\n");
        html.append("<body>\n");

        html.append("<h1>LaunchGuard Preflight Report</h1>\n");
        html.append("<div class=\"meta\">\n");
        html.append("<span>Timestamp: ").append(escape(timestamp)).append("</span>\n");
        html.append("<span>Source: ").append(escape(source)).append("</span>\n");
        html.append("<span>Version: ").append(escape(pluginVersion)).append("</span>\n");
        html.append("</div>\n");

        html.append("<div class=\"meta\">\n");
        html.append("<span>Server: ").append(escape(serverName)).append("</span>\n");
        html.append("<span>Minecraft: ").append(escape(serverVersion)).append("</span>\n");
        html.append("<span>Bukkit: ").append(escape(bukkitVersion)).append("</span>\n");
        html.append("</div>\n");

        html.append("<div class=\"summary\">\n");
        String statusClass;
        switch (report.status()) {
            case READY: statusClass = "status-ready"; break;
            case READY_WITH_WARNINGS: statusClass = "status-warn"; break;
            default: statusClass = "status-fail";
        }
        html.append("<p>Result: <strong class=\"").append(statusClass).append("\">")
                .append(escape(report.status().name())).append("</strong></p>\n");
        html.append("<p>");
        html.append("<span>Passed: ").append(report.passedCount()).append("</span> | ");
        html.append("<span>Warnings: ").append(report.warnCount()).append("</span> | ");
        html.append("<span>Failures: ").append(report.failCount()).append("</span> | ");
        html.append("<span>Total: ").append(report.results().size()).append("</span>");
        html.append("</p>\n");
        html.append("</div>\n");

        html.append("<table>\n");
        html.append("<tr><th>Severity</th><th>Passed</th><th>Check</th><th>Message</th><th>Suggestion</th></tr>\n");
        for (CheckResult result : report.results()) {
            html.append("<tr>");
            html.append("<td class=\"").append(result.severity().name()).append("\">")
                    .append(escape(result.severity().name())).append("</td>");
            html.append("<td>").append(result.passed() ? "Yes" : "No").append("</td>");
            html.append("<td>").append(escape(result.checkId())).append("</td>");
            html.append("<td>").append(escape(result.message())).append("</td>");
            html.append("<td class=\"suggestion\">");
            if (result.suggestion() != null) {
                html.append(escape(result.suggestion()));
            }
            html.append("</td>");
            html.append("</tr>\n");
        }
        html.append("</table>\n");

        html.append("<div class=\"footer\">\n");
        html.append("<p>This report was generated locally by LaunchGuard. ");
        html.append("It does not include server logs, tokens, webhook URLs, player IPs, or private file paths.</p>\n");
        html.append("</div>\n");

        html.append("</body>\n");
        html.append("</html>\n");

        return html.toString();
    }

    static String escape(String value) {
        if (value == null) return "";
        StringBuilder sb = new StringBuilder(value.length() + 8);
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
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
