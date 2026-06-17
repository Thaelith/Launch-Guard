# LaunchGuard v1.0.0

LaunchGuard v1.0.0 adds baseline drift detection, baseline report/export integration, optional startup baseline comparison, configuration validation, JSON/HTML exports, report history, and comprehensive compatibility testing.

## What's New Since v0.5.0

### Baseline Drift Detection
Save a known-good server-state snapshot and compare the current server state against it later. Detect plugin additions/removals, command changes, world changes, and LaunchGuard configuration drift.

### Baseline Report & Export
Save plain-text baseline drift reports. Export baseline comparisons as JSON or HTML. View baseline report history.

### Optional Startup Baseline Compare
Automatically compare server state against a saved baseline on startup. Configurable delay, baseline name, and report saving.

### Configuration Validation
`/launchguard validate` checks config.yml, checks.yml, and messages.yml for YAML errors, invalid types, missing sections, unknown keys, duplicate entries, and common configuration mistakes.

### JSON and HTML Exports
Export preflight reports as versioned JSON or self-contained HTML. HTML exports do not load external resources.

### Compatibility
Smoke-tested on 15 stable Paper versions across 1.20.x and 1.21.x. Built with Java 17.

### Safety
LaunchGuard remains read-only. It never executes commands, modifies server state, or sends network requests.
