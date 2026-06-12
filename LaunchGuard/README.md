# LaunchGuard

Automated pre-launch checks for Minecraft servers.

## What LaunchGuard Is

LaunchGuard is a server administration utility that runs automated checks before you open your server to players. It inspects plugin state, command registration, world availability, location safety, and permission configuration, then produces a clear READY / NOT_READY report.

Use LaunchGuard before:

- Opening a new server
- Updating plugins
- Launching a new season
- Disabling the whitelist
- Deploying to production

## What LaunchGuard Checks

**[PASS] indicates the check passed. [WARN] indicates a potential issue. [FAIL] indicates a problem that should be fixed before launch. [INFO] provides additional context.**

### Required Plugins
Checks each plugin listed in `checks.plugins.required`. Reports PASS if the plugin is loaded and enabled, FAIL if missing or disabled.

### Required Commands
Checks each command listed in `checks.commands.required`. Inspects both plugin.yml-registered commands and server-level command map entries. Reports PASS if the command is registered, FAIL if not found. Commands are inspected, never executed.

### Required Worlds
Checks each world listed in `checks.worlds.required`. Reports PASS if the world is loaded, FAIL if missing.

### Safe Locations
For each configured location, checks:
- The target world exists
- Y coordinate is within world bounds
- The block at the location is not lava, fire, cactus, powder snow, etc.
- The block below is solid enough to stand on
- If the chunk is not loaded, reports as safe with a note rather than loading the chunk

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
3. Edit `plugins/LaunchGuard/checks.yml` to match your server's requirements.
4. Run `/launchguard run` to check your server.

## Commands

| Command | Description |
|---|---|
| `/launchguard help` | Show help |
| `/launchguard run` | Run all pre-launch checks |
| `/launchguard reload` | Reload configuration files |
| `/launchguard version` | Show plugin version |

Aliases: `/lg`, `/preflight`

## Permissions

| Permission | Default | Description |
|---|---|---|
| launchguard.use | true | Access /launchguard command |
| launchguard.run | op | Run pre-launch checks |
| launchguard.reload | op | Reload configuration |
| launchguard.admin | op | Full admin access (implies run and reload) |

## Configuration

All configuration files are in `plugins/LaunchGuard/`.

### config.yml

```yaml
settings:
  showPassedChecks: true    # Show PASS results in report
  reportToConsole: true     # Also log report to console
  stopOnCriticalFail: false # Reserved for future use
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
[PASS] Location safe: spawn
----------------------------------------
Result: NOT_READY
Passed: 5
Warnings: 0
Failures: 2
```

## Safety

LaunchGuard Lite v0.1 is read-only. It inspects server state and reports findings. It never executes commands, teleports players, modifies data, or changes server behavior. You control what it checks through configuration files.

## Troubleshooting

**Plugin does not load:**
Check the server console for errors. LaunchGuard requires Paper 1.20+.

**Checks pass but server has issues:**
Add the missing plugin, world, or command to your checks.yml. Defaults are starting points; customize them.

**Broken YAML on reload:**
If config.yml, checks.yml, or messages.yml has invalid YAML, the reload fails and the previous valid configuration is kept. The console shows which file failed.

**No checks are enabled:**
Verify that `enabled: true` is set for at least one check category in checks.yml.

**Command returns "no permission":**
Grant `launchguard.run` to the user or make them an operator.

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
