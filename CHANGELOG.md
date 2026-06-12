# Changelog

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
- `launchguard.admin` includes run and reload children

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
