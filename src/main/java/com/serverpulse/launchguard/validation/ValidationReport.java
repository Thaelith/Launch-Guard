package com.serverpulse.launchguard.validation;

import java.util.ArrayList;
import java.util.List;

public class ValidationReport {

    private final List<ValidationIssue> issues = new ArrayList<>();

    public void add(ValidationIssue issue) {
        issues.add(issue);
    }

    public void addAll(List<ValidationIssue> issues) {
        this.issues.addAll(issues);
    }

    public List<ValidationIssue> issues() {
        return List.copyOf(issues);
    }

    public int passedCount() {
        int count = 0;
        for (ValidationIssue i : issues) {
            if (i.severity() == ValidationSeverity.PASS) count++;
        }
        return count;
    }

    public int warnCount() {
        int count = 0;
        for (ValidationIssue i : issues) {
            if (i.severity() == ValidationSeverity.WARN) count++;
        }
        return count;
    }

    public int failCount() {
        int count = 0;
        for (ValidationIssue i : issues) {
            if (i.severity() == ValidationSeverity.FAIL) count++;
        }
        return count;
    }

    public ValidationStatus status() {
        if (failCount() > 0) return ValidationStatus.INVALID;
        if (warnCount() > 0) return ValidationStatus.VALID_WITH_WARNINGS;
        return ValidationStatus.VALID;
    }
}
