# Configuration

All configuration files are in `plugins/LaunchGuard/`. They are created automatically on first start.

## config.yml

Controls general plugin behavior.

```yaml
settings:
  showPassedChecks: true    # Include PASS results in report output
  reportToConsole: true     # Send report to console when run by a player
  prefix: "[LaunchGuard]"   # Prefix shown before all messages
```

| Key | Type | Default | Description |
|---|---|---|---|
| showPassedChecks | boolean | true | When false, [PASS] results are hidden from the report |
| reportToConsole | boolean | true | When true, reports from player commands are also printed to console |
| prefix | string | [LaunchGuard] | Prefix prepended to all messages |

## checks.yml

Controls which checks run and what they look for.

```yaml
checks:
  plugins:
    enabled: true         # Set to false to skip this check
    required:             # List of plugins that must be installed
      - Vault
      - LuckPerms

  commands:
    enabled: true
    required:             # List of commands that must be registered
      - spawn
      - warp

  worlds:
    enabled: true
    required:             # List of worlds that must be loaded
      - world
      - world_nether
      - world_the_end

  locations:
    enabled: true
    entries:              # Named locations to check for safety
      spawn:
        world: world
        x: 0
        y: 80
        z: 0
        safe: true        # true = location should be safe

  permissions:
    enabled: true
    nodes:
      shouldExist:        # Permission nodes that should be registered
        - essentials.spawn
      dangerous:          # Permission nodes that should be reviewed
        - minecraft.command.op
        - bukkit.command.stop

  pluginInventory:
    enabled: false        # Disabled by default; does not change /launchguard run unless enabled
    checkDependencies: true
    warnOnSoftDependencyMissing: true
```

### Location Entry Fields

| Field | Type | Required | Description |
|---|---|---|---|
| world | string | Yes | World name the location is in |
| x | number | Yes | X coordinate |
| y | number | Yes | Y coordinate |
| z | number | Yes | Z coordinate |
| safe | boolean | No | Whether the location should be safe (default: true) |

### Plugin Inventory Fields

| Field | Type | Default | Description |
|---|---|---|---|
| enabled | boolean | false | Adds concise plugin inventory and dependency visibility results to `/launchguard run` |
| checkDependencies | boolean | true | Checks hard and soft dependencies from visible plugin metadata |
| warnOnSoftDependencyMissing | boolean | true | Reports missing soft dependencies as [WARN] when true and [INFO] when false |

The plugin inventory check is read-only. It reports installed plugin metadata, enabled state, and dependency visibility. It does not verify that each plugin is correctly configured.

## messages.yml

All user-facing messages can be customized. The file is created with defaults on first start.

Messages support the `%version%` and `%count%` placeholders.

See `plugins/LaunchGuard/messages.yml` for the full list of customizable messages.
