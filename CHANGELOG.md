# Changelog

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
