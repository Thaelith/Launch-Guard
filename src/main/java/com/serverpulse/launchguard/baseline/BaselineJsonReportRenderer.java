package com.serverpulse.launchguard.baseline;

import com.serverpulse.launchguard.util.JsonUtil;

import java.time.Instant;

public class BaselineJsonReportRenderer {

    public String render(BaselineDriftReport report, String baselineName, String pluginVersion,
                         String serverName, String serverVersion, String bukkitVersion) {
        StringBuilder json = new StringBuilder();
        json.append("{");

        json.append(JsonUtil.jsonInt("schemaVersion", 1)).append(",");
        json.append(JsonUtil.jsonString("reportType", "baseline-drift")).append(",");
        json.append(JsonUtil.jsonString("baselineName", baselineName)).append(",");
        json.append(JsonUtil.jsonString("generatedAt", Instant.now().toString())).append(",");

        json.append("\"launchGuard\": {");
        json.append(JsonUtil.jsonString("version", pluginVersion));
        json.append("},");

        json.append("\"server\": {");
        json.append(JsonUtil.jsonString("name", serverName)).append(",");
        json.append(JsonUtil.jsonString("version", serverVersion)).append(",");
        json.append(JsonUtil.jsonString("bukkitVersion", bukkitVersion));
        json.append("},");

        json.append(JsonUtil.jsonString("status", report.status().name())).append(",");

        json.append("\"summary\": {");
        json.append(JsonUtil.jsonInt("info", report.infoCount())).append(",");
        json.append(JsonUtil.jsonInt("warnings", report.warnCount())).append(",");
        json.append(JsonUtil.jsonInt("failures", report.failCount())).append(",");
        json.append(JsonUtil.jsonInt("total", report.issues().size()));
        json.append("},");

        json.append("\"issues\": [");
        var issues = report.issues();
        for (int i = 0; i < issues.size(); i++) {
            if (i > 0) json.append(",");
            BaselineDriftIssue issue = issues.get(i);
            json.append("{");
            json.append(JsonUtil.jsonString("severity", issue.severity().name())).append(",");
            json.append(JsonUtil.jsonString("message", issue.message()));
            json.append("}");
        }
        json.append("],");

        json.append("\"safety\": {");
        json.append(JsonUtil.jsonBoolean("localOnly", true)).append(",");
        json.append(JsonUtil.jsonBoolean("containsPlayerIpAddresses", false)).append(",");
        json.append(JsonUtil.jsonBoolean("containsSecrets", false)).append(",");
        json.append(JsonUtil.jsonBoolean("containsLogs", false));
        json.append("}");

        json.append("}");
        return json.toString();
    }
}
