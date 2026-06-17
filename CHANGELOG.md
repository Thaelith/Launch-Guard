# Changelog

## 1.0.0

### Public Release
- First public marketplace release since v0.5.0.
- Includes all development since v0.5.0: baseline drift detection, baseline report/export integration, optional startup baseline comparison, configuration validation, JSON/HTML exports, report history, v1 readiness QA, compatibility testing, and release packaging.

### Previous development milestones merged into this release
- v0.6: Baseline drift detection
- v0.7: Baseline report save, history, JSON/HTML export integration
- v0.8: Optional startup baseline comparison
- v0.9: Stabilization, QA, documentation, compatibility, and safety polish

## 0.9.0-SNAPSHOT

### Changed
- Started v1.0 readiness and stabilization work.
- Updated project version to `0.9.0-SNAPSHOT`.
- Audited command help, permissions, configuration, reports, exports, baselines, and documentation.
- Updated documentation for development and marketplace release strategy.

### Safety
- Re-verified LaunchGuard's read-only behavior.
- Re-verified report, export, baseline, and baseline report file boundaries.
- Re-verified that no network calls are introduced.

### Notes
- v0.9 is a stabilization milestone, not a marketplace release.
- Marketplace stable remains v0.5.0 until v1.0.0, unless a critical hotfix is needed.

## 0.8.0-SNAPSHOT

### Added
- Added optional startup baseline comparison.
- Added `settings.compareBaselineOnStartup`.
- Added `settings.startupBaselineName`.
- Added `settings.startupBaselineSaveReport`.
- Added `settings.startupBaselineDelayTicks`.
- Added startup baseline text report saving under `plugins/LaunchGuard/reports/baseline/` when enabled.

### Safety
- Startup baseline comparison is disabled by default.
- Startup baseline comparison does not execute plugin commands or configured commands.
- Startup baseline comparison does not stop, restart, or block the server.
- Startup baseline comparison does not modify server state.
- Startup baseline comparison does not modify baseline files.
- Startup baseline reports are local files only.
- No network calls are introduced.

### Limitations
- Startup JSON/HTML baseline export is not included in this version.
- Startup baseline comparison only compares against an existing saved baseline.
- Startup baseline comparison does not verify plugin runtime behavior.

## 0.7.0-SNAPSHOT

### Added
- Added `/launchguard baseline compare <name> save`.
- Added `/launchguard baseline export json <name>`.
- Added `/launchguard baseline export html <name>`.
- Added `/launchguard baseline history`.
- Added `/launchguard baseline history latest`.
- Added local plain-text baseline drift reports under `plugins/LaunchGuard/reports/baseline/`.
- Added local JSON baseline drift exports under `plugins/LaunchGuard/exports/`.
- Added local self-contained HTML baseline drift exports under `plugins/LaunchGuard/exports/`.

### Safety
- Baseline report and export commands do not execute plugin commands or configured commands.
- Baseline reports and exports are local files only.
- Baseline HTML exports do not load external resources.
- Baseline report saving writes only under `plugins/LaunchGuard/reports/baseline/`.
- Baseline exports write only under `plugins/LaunchGuard/exports/`.
- No network calls are introduced.

### Limitations
- Startup baseline comparison is not included in this version.
- Baseline exports do not verify plugin runtime behavior.
- Baseline report history is local file based.

## 0.6.0-SNAPSHOT

### Added
- Added `/launchguard baseline save <name>`.
- Added `/launchguard baseline list`.
- Added `/launchguard baseline compare <name>`.
- Added `/launchguard baseline delete <name>`.
- Added `launchguard.baseline` permission.
- Added local YAML baseline snapshots under `plugins/LaunchGuard/baselines/`.
- Added baseline drift detection for plugins, commands, worlds, and selected LaunchGuard check configuration.

### Safety
- Baseline commands do not execute plugin commands or configured commands.
- Baseline snapshots are local YAML files only.
- Baseline save writes only `.yml` files under `plugins/LaunchGuard/baselines/`.
- Baseline delete removes only validated `.yml` baseline files inside `plugins/LaunchGuard/baselines/`.
- Baseline comparison does not modify server state.
- No network calls are introduced.

### Limitations
- Baselines capture a server-state snapshot, not full plugin behavior.
- Baseline comparison does not verify plugin runtime correctness.
- Command ownership may be unavailable for some commands depending on Bukkit/Paper internals.
- Baselines do not include player data, logs, tokens, webhook URLs, or secrets.

## 0.5.0

### Added
- Added `/launchguard export json`.
- Added `/launchguard export html`.
- Added `/launchguard validate`.
- Added `launchguard.export` permission.
- Added `launchguard.validate` permission.
- Added local JSON and HTML export files under `plugins/LaunchGuard/exports/`.
- Added export retention with `settings.exportsToKeep`.
- Added versioned JSON schema with `schemaVersion: 1`.
- Added configuration validation for `config.yml`, `checks.yml`, and `messages.yml`.

### Changed
- Updated documentation for exports and configuration validation.
- Updated version from `0.5.0-SNAPSHOT` to `0.5.0`.

### Safety
- Export and validation commands are read-only.
- Validation reads configuration files but does not modify them.
- Validation does not reload config automatically.
- Validation does not run preflight checks.
- Exports are written only under `plugins/LaunchGuard/exports/`.
- HTML exports are self-contained and do not load external resources.
- Export retention deletes only `.json` and `.html` files inside the exports directory.
- No network calls are introduced.

### Limitations
- Export is manual only.
- Validation does not perform cross-file validation.
- Validation does not check actual plugin runtime behavior.
- Message key validation uses a static expected-key list.
- JSON and HTML exports are local files only.

## 0.3.0

### Added
- Added optional startup preflight checks.
- Added saved plain text report files under `plugins/LaunchGuard/reports/`.
- Added `/launchguard history`.
- Added `/launchguard history latest`.
- Added `launchguard.history` permission.
- Added report retention with `settings.reportsToKeep`.

### Changed
- Refactored preflight execution into a reusable runner used by manual and startup checks.
- Updated documentation for startup checks, saved reports, and report history.
- Updated version from `0.3.0-SNAPSHOT` to `0.3.0`.

### Safety
- Startup checks are read-only.
- Saved reports are written only under `plugins/LaunchGuard/reports/`.
- Report history reads only from `plugins/LaunchGuard/reports/`.
- Report retention deletes only `.txt` files inside the reports directory.
- LaunchGuard does not execute commands, modify server state, or send network requests.

### Limitations
- Startup checks use a configurable fixed delay.
- Reports are plain text only.
- Report history has no search or filter support.

## 0.2.0

### Added
- Added `/launchguard plugins` for a concise installed plugin inventory.
- Added `/launchguard plugins verbose` for detailed plugin metadata.
- Added `/launchguard plugins dependencies` for dependency visibility.
- Added `launchguard.plugins` permission.
- Added optional `pluginInventory` preflight integration in `checks.yml`.

### Changed
- Updated documentation for plugin inventory and dependency visibility.
- Updated version from `0.2.0-SNAPSHOT` to `0.2.0`.

### Safety
- Plugin inventory is read-only.
- LaunchGuard does not install, download, enable, disable, reload, or modify plugins.
- LaunchGuard does not execute plugin commands or configured commands.

### Limitations
- Plugin inventory reports metadata, enabled state, and dependency visibility only.
- It does not verify plugin configuration correctness or full runtime behavior.
- Missing hard dependencies may not appear if Paper prevents the dependent plugin from loading.

## 0.1.0 - Initial Lite Release

First public release of LaunchGuard Lite.

### Checks
- Required plugin check: detects missing or disabled plugins
- Required command check: detects missing commands (plugin.yml and command map)
- Required world check: detects missing loaded worlds
- Safe location check: detects unsafe blocks, missing worlds, invalid coordinates, unloaded chunks
- Permission node check: flags missing expected nodes and dangerous registered nodes

### Features
- `/launchguard` command with help, run, reload, version subcommands
- Aliases: `/lg`, `/preflight`
- Tab completion for subcommands
- Configurable via config.yml, checks.yml, messages.yml
- Reload command with YAML error recovery
- Plain text report with [PASS] [WARN] [FAIL] [INFO] labels
- Console and chat output
- Suggestions displayed below findings

### Safety
- All checks are read-only; no commands executed, no blocks modified, no players teleported
- Unloaded chunks report WARN instead of loading chunks
- Invalid coordinates reported as FAIL instead of silently falling back
- Broken YAML on reload keeps previous valid configuration

### Permissions
- All permissions default to op-only
- `launchguard.use` controls basic command access
- `launchguard.run` controls check execution
- `launchguard.reload` controls config reload
- `launchguard.admin` includes use, run, and reload children

### Configuration
- config.yml: showPassedChecks, reportToConsole, prefix
- checks.yml: per-check enable/disable, required lists, location entries, permission nodes
- messages.yml: all user-facing messages customizable
- Example configs: survival, skyblock, boxpvp, lobby

### Documentation
- README with installation, commands, permissions, configuration
- docs/ directory with detailed guides
- TESTING.md with test scenarios
- RELEASE_CHECKLIST.md for pre-release verification
- Marketplace descriptions for Hangar, Modrinth, Spigot, BuiltByBit
