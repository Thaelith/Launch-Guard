# LaunchGuard v1.0.0

LaunchGuard v1.0.0 is the first public marketplace release since v0.5.0.

This release includes all development work from v0.6 through v0.9, including baseline drift detection, baseline report/export integration, optional startup baseline comparison, configuration validation, JSON/HTML exports, v1 readiness QA, compatibility testing, and release packaging.

## Since v0.5.0

### Baseline Drift Detection (v0.6)
- `/launchguard baseline save`, `list`, `compare`, `delete`
- Local YAML baseline snapshots under `plugins/LaunchGuard/baselines/`
- Drift detection for plugins, commands, worlds, and LaunchGuard configuration

### Baseline Report & Export (v0.7)
- `/launchguard baseline compare save`
- `/launchguard baseline history` and `history latest`
- `/launchguard baseline export json` and `export html`
- Plain-text baseline drift reports
- JSON/HTML baseline drift exports

### Optional Startup Baseline Compare (v0.8)
- `settings.compareBaselineOnStartup`
- `settings.startupBaselineName`
- `settings.startupBaselineSaveReport`
- `settings.startupBaselineDelayTicks`
- Startup baseline text report saving

### v1 Readiness (v0.9)
- Full compatibility QA across 15 stable Paper versions
- Documentation, safety, and release preparation

## Safety

LaunchGuard remains read-only. It does not execute plugin commands, configured commands, modify server state, send network requests, or capture private data.

## Compatibility

Fully smoke-tested on:
Paper 1.20.1 through 1.20.6
Paper 1.21.1 through 1.21.10

Load-tested only on Paper 1.21.11 due to test environment limitations.

Built with Java 17 target compatibility. Use the Java version required by your Paper server version.
