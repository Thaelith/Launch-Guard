package com.serverpulse.launchguard.report;

import com.serverpulse.launchguard.check.CheckResult;
import com.serverpulse.launchguard.check.CheckSeverity;
import com.serverpulse.launchguard.check.ReportStatus;
import com.serverpulse.launchguard.config.ConfigManager;
import com.serverpulse.launchguard.config.MessageManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ReportRenderer {

    private final MessageManager messages;
    private final ConfigManager config;

    public ReportRenderer(MessageManager messages, ConfigManager config) {
        this.messages = messages;
        this.config = config;
    }

    public Component render(PreflightReport report) {
        Component output = Component.empty();
        String prefix = messages.get("prefix");

        output = output.append(Component.text(prefix + " "));
        output = output.append(Component.text(messages.get("reportHeader", "LaunchGuard Preflight Report"), NamedTextColor.WHITE));
        output = output.append(Component.newline());

        String separator = "----------------------------------------";
        output = output.append(Component.text(separator, NamedTextColor.DARK_GRAY));
        output = output.append(Component.newline());

        boolean showPassed = config.showPassedChecks();

        for (CheckResult result : report.results()) {
            if (result.severity() == CheckSeverity.PASS && !showPassed) {
                continue;
            }
            output = output.append(formatResult(result));
            output = output.append(Component.newline());
        }

        output = output.append(Component.text(separator, NamedTextColor.DARK_GRAY));
        output = output.append(Component.newline());
        output = output.append(formatStatus(report.status()));
        output = output.append(Component.newline());
        output = output.append(Component.text(
                messages.get("passedCount", "Passed: %count%")
                        .replace("%count%", String.valueOf(report.passedCount())),
                NamedTextColor.GRAY));
        output = output.append(Component.newline());
        output = output.append(Component.text(
                messages.get("warningsCount", "Warnings: %count%")
                        .replace("%count%", String.valueOf(report.warnCount())),
                NamedTextColor.YELLOW));
        output = output.append(Component.newline());
        output = output.append(Component.text(
                messages.get("failuresCount", "Failures: %count%")
                        .replace("%count%", String.valueOf(report.failCount())),
                NamedTextColor.RED));

        return output;
    }

    private Component formatResult(CheckResult result) {
        String label;
        NamedTextColor color;

        switch (result.severity()) {
            case PASS:
                label = "[PASS]";
                color = NamedTextColor.GREEN;
                break;
            case INFO:
                label = "[INFO]";
                color = NamedTextColor.AQUA;
                break;
            case WARN:
                label = "[WARN]";
                color = NamedTextColor.YELLOW;
                break;
            case FAIL:
                label = "[FAIL]";
                color = NamedTextColor.RED;
                break;
            default:
                label = "[----]";
                color = NamedTextColor.GRAY;
        }

        Component line = Component.text(label + " ", color)
                .append(Component.text(result.message(), NamedTextColor.GRAY));

        if (result.suggestion() != null && !result.suggestion().isEmpty()) {
            line = line.append(Component.newline())
                    .append(Component.text("       -> ", NamedTextColor.DARK_GRAY))
                    .append(Component.text(result.suggestion(), NamedTextColor.GRAY));
        }

        return line;
    }

    private Component formatStatus(ReportStatus status) {
        String text;
        NamedTextColor color;

        switch (status) {
            case READY:
                text = messages.get("resultReady", "Result: READY");
                color = NamedTextColor.GREEN;
                break;
            case READY_WITH_WARNINGS:
                text = messages.get("resultReadyWithWarnings", "Result: READY_WITH_WARNINGS");
                color = NamedTextColor.YELLOW;
                break;
            case NOT_READY:
                text = messages.get("resultNotReady", "Result: NOT_READY");
                color = NamedTextColor.RED;
                break;
            default:
                text = "Result: UNKNOWN";
                color = NamedTextColor.GRAY;
        }

        return Component.text(text, color);
    }
}
