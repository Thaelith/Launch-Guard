package com.serverpulse.launchguard.check;

public record CheckResult(
        String checkId,
        CheckSeverity severity,
        boolean passed,
        String message,
        String suggestion
) {
    public static CheckResult pass(String checkId, String message) {
        return new CheckResult(checkId, CheckSeverity.PASS, true, message, null);
    }

    public static CheckResult info(String checkId, String message, String suggestion) {
        return new CheckResult(checkId, CheckSeverity.INFO, true, message, suggestion);
    }

    public static CheckResult warn(String checkId, String message, String suggestion) {
        return new CheckResult(checkId, CheckSeverity.WARN, false, message, suggestion);
    }

    public static CheckResult fail(String checkId, String message, String suggestion) {
        return new CheckResult(checkId, CheckSeverity.FAIL, false, message, suggestion);
    }
}
