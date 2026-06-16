# LaunchGuard

Read-only pre-launch checks for Paper Minecraft servers.

Latest release: v0.3.0. Use the GitHub Releases page, Hangar, Modrinth, or SpigotMC to download the latest stable release.

## Download

- [GitHub Releases](https://github.com/Thaelith/Launch-Guard/releases)
- [Hangar](https://hangar.papermc.io/Thaelith/LaunchGuard)
- [Modrinth](https://modrinth.com/plugin/launchguard)
- [SpigotMC](https://www.spigotmc.org/resources/launchguard.136118/)

## What LaunchGuard Is

LaunchGuard is a Paper-first server administration utility that runs read-only checks before you open your server to players. It inspects configured plugin requirements, command registration, world availability, location safety, permission nodes, and plugin metadata, then produces a clear READY / NOT_READY report.

Use LaunchGuard before:

- Opening a new server
- Updating plugins
- Launching a new season
- Disabling the whitelist
- Deploying to production

## What LaunchGuard Checks

[PASS] indicates the check passed. [WARN] indicates a potential issue. [FAIL] indicates a problem that should be fixed before launch. [INFO] provides additional context.

### Required Plugins
Checks each plugin listed in `checks.plugins.required`. Reports PASS if the plugin is loaded and enabled, FAIL if missing or disabled.

### Required Commands
Checks each command listed in `checks.commands.required`. Inspects both plugin.yml-registered commands and server-level command map entries. Reports PASS if the command is registered, FAIL if not found. Commands are inspected, never executed.

### Required Worlds
Checks each world listed in `checks.worlds.required`. Reports PASS if the world is loaded, FAIL if missing.

### Safe Locations
For each configured location, checks:
- The target world exists
- Coordinates are valid (present and numeric)
- Y coordinate is within world bounds
- The block at the location is not lava, fire, cactus, powder snow, etc.
- The block below is solid enough to stand on
- If the chunk is not loaded, reports WARN (does not force-load chunks)
- Invalid coordinates (missing, non-numeric, out of bounds) are reported as FAIL

### Permission Nodes
- `shouldExist` nodes: reports PASS if registered, WARN if not found
- `dangerous` nodes: reports WARN if registered (for review)

### Plugin Inventory and Dependency Visibility
Reports installed plugin metadata and enabled state. The plugin dependency report reads hard and soft dependencies from visible plugin metadata and reports whether those dependencies are installed.

Plugin inventory does not verify that each plugin is correctly configured. It reports installed plugin metadata, enabled state, and dependency visibility.

## What LaunchGuard Does Not Do

This is the Lite version. It inspects state and reports issues. It does not:

- Execute arbitrary server commands
- Execute plugin commands
- Install, download, enable, disable, or reload plugins
- Teleport players
- Modify worlds, blocks, economy, or permissions
- Change whitelist state
- Send network requests or upload data
- Write files outside the plugin data folder

Saved report files are written under `plugins/LaunchGuard/reports/` only. No files are written outside the plugin data folder.

## Installation

1. Drop the LaunchGuard jar into your server's `plugins/` directory.
2. Restart the server.
3. Grant `launchguard.use` and `launchguard.run` to staff who need to run checks. OPs have all LaunchGuard permissions by default.
4. Edit `plugins/LaunchGuard/checks.yml` to match your server's requirements.
5. Run `/launchguard run` to check your server.

## Quick Start

```bash
# Grant access to the LaunchGuard command
/lp user MyPlayer permission set launchguard.use true

# Grant permission to run checks
/lp user MyPlayer permission set launchguard.run true

# Optional: grant permission to reload configuration
/lp user MyPlayer permission set launchguard.reload true

# Optional: grant permission to view plugin inventory and dependency reports
/lp user MyPlayer permission set launchguard.plugins true

# Run checks
/launchguard run

# View installed plugin metadata
/launchguard plugins

# View dependency visibility only
/launchguard plugins dependencies

# View saved report history
/launchguard history

# View latest saved report
/launchguard history latest

# Reload after editing config
/launchguard reload
```

## Commands

| Command | Description |
|---|---|
| `/launchguard help` | Show help |
| `/launchguard run` | Run all pre-launch checks |
| `/launchguard plugins` | Show concise plugin inventory |
| `/launchguard plugins verbose` | Show plugin metadata details |
| `/launchguard plugins dependencies` | Show dependency visibility report |
| `/launchguard history` | List recently saved reports |
| `/launchguard history latest` | Show latest saved report content |
| `/launchguard reload` | Reload configuration files |
| `/launchguard version` | Show plugin version |

Aliases: `/lg`, `/preflight`

## Permissions

All permissions default to op-only.

| Permission | Default | Description |
|---|---|---|
| launchguard.use | op | Access to help and version subcommands |
| launchguard.run | op | Required for /launchguard run |
| launchguard.reload | op | Required for /launchguard reload |
| launchguard.plugins | op | Required for /launchguard plugins |
| launchguard.history | op | Required for /launchguard history |
| launchguard.admin | op | Full access; includes use, run, reload, plugins, and history as child permissions |

Note: `launchguard.use` alone does not permit `/launchguard run`, `/launchguard reload`, `/launchguard plugins`, or `/launchguard history`. Those subcommands each require their own permission. `launchguard.admin` grants `use`, `run`, `reload`, `plugins`, and `history` as child permissions.

## Configuration

All configuration files are in `plugins/LaunchGuard/`.

### config.yml

```yaml
settings:
  showPassedChecks: true    # Show PASS results in report
  reportToConsole: true     # Also log report to console
  prefix: "[LaunchGuard]"   # Message prefix
  runOnStartup: false       # Run preflight check on server start
  startupDelayTicks: 100    # Delay before startup check (ticks)
  saveReports: false        # Save report to plain text file
  reportsToKeep: 25         # Max report files to keep
```

### checks.yml

```yaml
checks:
  plugins:
    enabled: true
    required:
      - Vault
      - LuckPerms
  commands:
    enabled: true
    required:
      - spawn
      - warp
  worlds:
    enabled: true
    required:
      - world
      - world_nether
      - world_the_end
  locations:
    enabled: true
    entries:
      spawn:
        world: world
        x: 0
        y: 80
        z: 0
        safe: true
  permissions:
    enabled: true
    nodes:
      shouldExist:
        - essentials.spawn
      dangerous:
        - minecraft.command.op
        - bukkit.command.stop
  pluginInventory:
    enabled: false
    checkDependencies: true
    warnOnSoftDependencyMissing: true
```

### messages.yml

Customize all user-facing messages. See the generated file in `plugins/LaunchGuard/messages.yml`.

## Example Report

```
[LaunchGuard] LaunchGuard Preflight Report
----------------------------------------
[PASS] Plugin loaded: Vault
[PASS] Plugin loaded: LuckPerms
[FAIL] Plugin missing or disabled: PlaceholderAPI
       -> Install and enable the missing plugin before launch.
[PASS] Command registered: /spawn
[FAIL] Command missing: /shop
       -> Install or configure the plugin that provides this command.
[PASS] World exists: world
[FAIL] World missing: world_the_end
       -> Check the world folder name or load the world before launch.
[WARN] Location could not be fully checked because the chunk is not loaded: spawn
       -> Visit or load the area once, then run the check again.
[FAIL] Location config invalid: crates (x is missing or not numeric)
       -> Fix the location coordinates in checks.yml.
----------------------------------------
Result: NOT_READY
Passed: 4
Warnings: 1
Failures: 4
```

## Plugin Inventory Example

```
LaunchGuard Plugin Inventory

Plugins: 12 total, 12 enabled, 0 disabled

[PASS] LaunchGuard 0.3.0 enabled
[PASS] LuckPerms 5.x enabled
[PASS] Vault 1.x enabled
[PASS] PlaceholderAPI 2.x enabled

Run /launchguard plugins verbose for metadata details.
Run /launchguard plugins dependencies for dependency visibility.
```

## Safety

LaunchGuard Lite is read-only. It inspects server state and reports findings. It never executes commands, teleports players, modifies data, changes server behavior, sends network requests, or uploads data. You control what it checks through configuration files.

## Compatibility

Tested on:

* Paper 1.20.4 with Java 17
* Paper 1.20.6 with Java 21
* Paper 1.21.1 with Java 21
* Paper 1.21.8 with Java 21

Built with Java 17 target compatibility. Use the Java version required by your Paper server version.

Other Paper 1.20.x and 1.21.x builds may work where compatible Bukkit/Paper APIs are available, but only the versions listed above are tested.

## Known Limitations

- Location checks cannot inspect unloaded chunks (reports WARN instead)
- Command detection uses reflection-based command map access; some dynamically registered commands may not be detected
- Permission checks inspect globally registered nodes only, not group-level assignments
- Plugin inventory does not verify that each plugin is correctly configured. It reports installed plugin metadata, enabled state, and dependency visibility.
- Lite does not integrate with LuckPerms, Vault, or PlaceholderAPI
- Report output is text-only

## Troubleshooting

**Plugin does not load:**
Check the server console for errors. LaunchGuard is tested on Paper 1.20.4, 1.20.6, 1.21.1, and 1.21.8. Use the Java version required by your Paper server version.

**Cannot run commands:**
LaunchGuard permissions are op-only by default. Grant `launchguard.use` for basic command access, `launchguard.run` to run checks, and `launchguard.reload` to reload configuration. Use `launchguard.admin` to grant all permissions at once.

**Checks pass but server has issues:**
Add the missing plugin, world, or command to your checks.yml. Defaults are starting points; customize them.

**Broken YAML on reload:**
If config.yml, checks.yml, or messages.yml has invalid YAML, the reload fails and the previous valid configuration is kept. The console shows which file failed.

**No checks are enabled:**
Verify that `enabled: true` is set for at least one check category in checks.yml.

**Location shows WARN about unloaded chunk:**
Visit or teleport to the area once so the chunk loads, then run the check again.

## Roadmap

Released in v0.2.0:

- Plugin inventory report
- Dependency visibility report
- Optional plugin inventory preflight section

Released in v0.3.0:

- Startup preflight checks
- Saved plain text report files
- Report history commands

Not planned for LaunchGuard Lite:

- Executing arbitrary commands configured by server owners
- Modifying server state

## Building From Source

Requirements: JDK 17+

```bash
./gradlew build
```

The plugin JAR will be in `build/libs/LaunchGuard-0.3.0.jar`.

## Support

- **Bug reports and feature requests:** Open an issue on [GitHub Issues](https://github.com/Thaelith/Launch-Guard/issues).
- **Configuration help:** Use the "Configuration Help" issue template.
- Include your LaunchGuard version, server version, Java version, relevant checks.yml sections, and report output.
- Remove any secrets, passwords, or private webhook URLs before posting.
- Security-sensitive reports: Do not post secrets, exploit details, private server information, webhook URLs, or tokens in a public issue. If no private contact method is listed, open a minimal issue asking for a private contact method.
