package com.serverpulse.launchguard.validation;

public record ValidationIssue(
        String source,
        ValidationSeverity severity,
        String message
) {
    public static ValidationIssue pass(String source, String message) {
        return new ValidationIssue(source, ValidationSeverity.PASS, message);
    }

    public static ValidationIssue info(String source, String message) {
        return new ValidationIssue(source, ValidationSeverity.INFO, message);
    }

    public static ValidationIssue warn(String source, String message) {
        return new ValidationIssue(source, ValidationSeverity.WARN, message);
    }

    public static ValidationIssue fail(String source, String message) {
        return new ValidationIssue(source, ValidationSeverity.FAIL, message);
    }
}
