package com.serverpulse.launchguard.baseline;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class BaselineRenderer {

    public Component renderDrift(BaselineDriftReport report) {
        Component output = Component.empty();

        output = output.append(Component.text("LaunchGuard Baseline Drift Report: " + report.baselineName(), NamedTextColor.WHITE));
        output = output.append(Component.newline());
        output = output.append(Component.text("----------------------------------------", NamedTextColor.DARK_GRAY));
        output = output.append(Component.newline());

        if (report.issues().isEmpty()) {
            output = output.append(Component.text("[PASS] Current server state matches baseline.", NamedTextColor.GREEN));
        } else {
            for (BaselineDriftIssue issue : report.issues()) {
                output = output.append(formatIssue(issue));
                output = output.append(Component.newline());
            }
        }

        output = output.append(Component.text("----------------------------------------", NamedTextColor.DARK_GRAY));
        output = output.append(Component.newline());
        output = output.append(formatStatus(report));
        output = output.append(Component.newline());

        output = output.append(Component.text("Info: " + report.infoCount(), NamedTextColor.AQUA));
        output = output.append(Component.newline());
        output = output.append(Component.text("Warnings: " + report.warnCount(), NamedTextColor.YELLOW));
        output = output.append(Component.newline());
        output = output.append(Component.text("Failures: " + report.failCount(), NamedTextColor.RED));

        return output;
    }

    private Component formatIssue(BaselineDriftIssue issue) {
        String label;
        NamedTextColor color;
        switch (issue.severity()) {
            case INFO: label = "[INFO]"; color = NamedTextColor.AQUA; break;
            case WARN: label = "[WARN]"; color = NamedTextColor.YELLOW; break;
            case FAIL: label = "[FAIL]"; color = NamedTextColor.RED; break;
            default:   label = "[----]"; color = NamedTextColor.GRAY;
        }
        return Component.text(label + " " + issue.message(), color);
    }

    private Component formatStatus(BaselineDriftReport report) {
        String text;
        NamedTextColor color;
        switch (report.status()) {
            case MATCHES_BASELINE: text = "Result: MATCHES_BASELINE"; color = NamedTextColor.GREEN; break;
            case DRIFT_DETECTED:  text = "Result: DRIFT_DETECTED";  color = NamedTextColor.YELLOW; break;
            default:              text = "Result: BASELINE_INVALID"; color = NamedTextColor.RED;
        }
        return Component.text(text, color);
    }
}
