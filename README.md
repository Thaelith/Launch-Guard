# LaunchGuard

Automated pre-launch checks for Minecraft servers.

## Download

- GitHub Releases: https://github.com/Thaelith/Launch-Guard/releases
- Hangar: <Hangar [project link](https://hangar.papermc.io/Thaelith/LaunchGuard)>
- Modrinth: <Modrinth [project link](https://modrinth.com/plugin/launchguard)>
- SpigotMC: <Spigot [resource link](https://www.spigotmc.org/resources/launchguard.136118/)>

## What LaunchGuard Is

LaunchGuard is a server administration utility that runs automated checks before you open your server to players. It inspects plugin state, command registration, world availability, location safety, and permission configuration, then produces a clear READY / NOT_READY report.

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

## What LaunchGuard Does Not Do

This is the Lite version. It inspects state and reports issues. It does not:

- Execute arbitrary server commands
- Teleport players
- Modify worlds, blocks, economy, or permissions
- Change whitelist state
- Send network requests or upload data
- Write files outside the plugin data folder

## Installation

1. Drop `LaunchGuard-0.1.0.jar` into your server's `plugins/` directory.
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

# Run checks
/launchguard run

# Reload after editing config
/launchguard reload
```

## Commands

| Command | Description |
|---|---|
| `/launchguard help` | Show help |
| `/launchguard run` | Run all pre-launch checks |
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
| launchguard.admin | op | Full access; includes use, run, and reload as child permissions |

Note: `launchguard.use` alone does not permit `/launchguard run`. The `run` and `reload` subcommands each require their own permission. `launchguard.admin` grants `use`, `run`, and `reload` as child permissions.

## Configuration

All configuration files are in `plugins/LaunchGuard/`.

### config.yml

```yaml
settings:
  showPassedChecks: true    # Show PASS results in report
  reportToConsole: true     # Also log report to console
  prefix: "[LaunchGuard]"   # Message prefix
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

## Safety

LaunchGuard Lite v0.1 is read-only. It inspects server state and reports findings. It never executes commands, teleports players, modifies data, or changes server behavior. You control what it checks through configuration files.

## Known Limitations

- Location checks cannot inspect unloaded chunks (reports WARN instead)
- Command detection uses reflection-based command map access; some dynamically registered commands may not be detected
- Permission checks inspect globally registered nodes only, not group-level assignments
- Lite v0.1 does not integrate with LuckPerms, Vault, or PlaceholderAPI
- Report output is text-only (no HTML, JSON, or Discord output in Lite)

## Troubleshooting

**Plugin does not load:**
Check the server console for errors. LaunchGuard requires Paper 1.20+.

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

Planned for future versions (not included in Lite v0.1):

- HTML reports
- Discord webhook reports
- Scheduled checks
- Startup checks (run on server start)
- Vault economy checks
- PlaceholderAPI placeholder validation
- LuckPerms group audit
- Launch mode (automated pre-launch workflow)
- Report history
- JSON export

## Building From Source

Requirements: JDK 17+

```bash
./gradlew build
```

The plugin JAR will be in `build/libs/LaunchGuard-0.1.0.jar`.

## Support

- **Bug reports and feature requests:** Open an issue on [GitHub Issues](https://github.com/Thaelith/Launch-Guard/issues).
- **Configuration help:** Use the "Configuration Help" issue template.
- Include your LaunchGuard version, server version, Java version, relevant checks.yml sections, and report output.
- Remove any secrets, passwords, or private webhook URLs before posting.
- Security-sensitive reports: Do not post secrets, exploit details, private server information, webhook URLs, or tokens in a public issue. If no private contact method is listed, open a minimal issue asking for a private contact method.
