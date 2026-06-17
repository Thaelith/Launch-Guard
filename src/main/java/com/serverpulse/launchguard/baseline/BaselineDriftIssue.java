package com.serverpulse.launchguard.baseline;

public record BaselineDriftIssue(
        BaselineDriftSeverity severity,
        String message
) {}
