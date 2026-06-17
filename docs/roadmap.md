# Roadmap

LaunchGuard Lite v0.1 was the initial release focusing on essential read-only pre-launch checks.

## v0.2.0

- Plugin inventory report
- Plugin dependency visibility report
- Read-only plugin health overview
- Optional plugin inventory preflight section, disabled by default

## v0.3.0

- Optional startup preflight checks
- Saved plain text report files
- `/launchguard history` and `/launchguard history latest` commands
- Report retention (configurable max files)
- `launchguard.history` permission

## v0.5.0

- `/launchguard export json`
- `/launchguard export html`
- `/launchguard validate`
- `launchguard.export` and `launchguard.validate` permissions
- JSON report export with versioned schema
- Static HTML report export
- Configuration validation for config.yml, checks.yml, and messages.yml

## v0.6.0-SNAPSHOT (Merged to main, not released publicly)

- `/launchguard baseline save`, `list`, `compare`, `delete`
- `launchguard.baseline` permission
- Local YAML baseline snapshots under `plugins/LaunchGuard/baselines/`
- Baseline drift detection for plugins, commands, worlds, and configuration

## v0.7.0-SNAPSHOT (Merged to main, not released publicly)

- `/launchguard baseline compare save`
- `/launchguard baseline export json` and `export html`
- `/launchguard baseline history` and `history latest`
- Plain-text baseline drift reports
- JSON/HTML baseline drift exports

## v0.8.0-SNAPSHOT (Merged to main, not released publicly)

- Optional startup baseline comparison
- `compareBaselineOnStartup`, `startupBaselineName`, `startupBaselineSaveReport`, `startupBaselineDelayTicks`
- Startup baseline text report saving

## v0.9.0-SNAPSHOT (Merged to main, not released publicly)

- Stabilization, QA, documentation, compatibility, and v1.0 readiness preparation

## v1.0.0

- First public marketplace release since v0.5.0
- Includes all v0.6 through v0.9 development
- Release packaging and final documentation

## Future Candidates

- Additional location safety blocks
- More configurable report wording
- API for read-only third-party checks

## Not Planned

- Executing arbitrary commands configured by server owners
- Modifying server state (plugins, worlds, blocks, permissions)
- Teleporting players
- Installing, downloading, enabling, disabling, or reloading plugins
- Sending network requests or uploading data
