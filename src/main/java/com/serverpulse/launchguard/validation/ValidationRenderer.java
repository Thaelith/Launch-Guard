package com.serverpulse.launchguard.validation;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ValidationRenderer {

    public Component render(ValidationReport report) {
        Component output = Component.empty();

        output = output.append(Component.text("LaunchGuard Configuration Validation", NamedTextColor.WHITE));
        output = output.append(Component.newline());
        output = output.append(Component.text("----------------------------------------", NamedTextColor.DARK_GRAY));
        output = output.append(Component.newline());

        for (ValidationIssue issue : report.issues()) {
            output = output.append(formatIssue(issue));
            output = output.append(Component.newline());
        }

        output = output.append(Component.text("----------------------------------------", NamedTextColor.DARK_GRAY));
        output = output.append(Component.newline());
        output = output.append(formatStatus(report));
        output = output.append(Component.newline());

        output = output.append(Component.text("Passed: " + report.passedCount(), NamedTextColor.GRAY));
        output = output.append(Component.newline());
        output = output.append(Component.text("Warnings: " + report.warnCount(), NamedTextColor.YELLOW));
        output = output.append(Component.newline());
        output = output.append(Component.text("Failures: " + report.failCount(), NamedTextColor.RED));

        return output;
    }

    private Component formatIssue(ValidationIssue issue) {
        String label;
        NamedTextColor color;
        switch (issue.severity()) {
            case PASS:  label = "[PASS]"; color = NamedTextColor.GREEN; break;
            case INFO:  label = "[INFO]"; color = NamedTextColor.AQUA; break;
            case WARN:  label = "[WARN]"; color = NamedTextColor.YELLOW; break;
            case FAIL:  label = "[FAIL]"; color = NamedTextColor.RED; break;
            default:    label = "[----]"; color = NamedTextColor.GRAY;
        }
        return Component.text(label + " " + issue.message(), color);
    }

    private Component formatStatus(ValidationReport report) {
        String text;
        NamedTextColor color;
        switch (report.status()) {
            case VALID:                text = "Result: VALID";                color = NamedTextColor.GREEN; break;
            case VALID_WITH_WARNINGS:  text = "Result: VALID_WITH_WARNINGS";  color = NamedTextColor.YELLOW; break;
            default:                   text = "Result: INVALID";              color = NamedTextColor.RED;
        }
        return Component.text(text, color);
    }
}
