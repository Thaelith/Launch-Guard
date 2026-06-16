package com.serverpulse.launchguard.report;

import com.serverpulse.launchguard.check.CheckResult;
import com.serverpulse.launchguard.util.JsonUtil;

import java.time.Instant;

public class JsonReportRenderer {

    public String render(PreflightReport report, String source, String pluginVersion,
                         String serverName, String serverVersion, String bukkitVersion) {
        StringBuilder json = new StringBuilder();
        json.append("{");

        json.append(JsonUtil.jsonInt("schemaVersion", 1)).append(",");
        json.append(JsonUtil.jsonString("generatedAt", Instant.now().toString())).append(",");
        json.append(JsonUtil.jsonString("source", source)).append(",");

        json.append("\"launchGuard\": {");
        json.append(JsonUtil.jsonString("version", pluginVersion));
        json.append("},");

        json.append("\"server\": {");
        json.append(JsonUtil.jsonString("name", serverName)).append(",");
        json.append(JsonUtil.jsonString("version", serverVersion)).append(",");
        json.append(JsonUtil.jsonString("bukkitVersion", bukkitVersion));
        json.append("},");

        json.append("\"summary\": {");
        json.append(JsonUtil.jsonString("status", report.status().name())).append(",");
        json.append(JsonUtil.jsonInt("passed", report.passedCount())).append(",");
        json.append(JsonUtil.jsonInt("warnings", report.warnCount())).append(",");
        json.append(JsonUtil.jsonInt("failures", report.failCount())).append(",");
        json.append(JsonUtil.jsonInt("totalResults", report.results().size()));
        json.append("},");

        json.append("\"results\": [");
        var results = report.results();
        for (int i = 0; i < results.size(); i++) {
            if (i > 0) json.append(",");
            json.append(renderResult(results.get(i)));
        }
        json.append("]");

        json.append("}");
        return json.toString();
    }

    private String renderResult(CheckResult result) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(JsonUtil.jsonString("checkId", result.checkId())).append(",");
        sb.append(JsonUtil.jsonString("severity", result.severity().name())).append(",");
        sb.append(JsonUtil.jsonBoolean("passed", result.passed())).append(",");
        sb.append(JsonUtil.jsonString("message", result.message())).append(",");
        if (result.suggestion() != null) {
            sb.append(JsonUtil.jsonString("suggestion", result.suggestion()));
        } else {
            sb.append(JsonUtil.jsonNull("suggestion"));
        }
        sb.append("}");
        return sb.toString();
    }
}
