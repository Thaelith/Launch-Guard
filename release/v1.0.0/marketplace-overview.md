# LaunchGuard — Server Administration Utility

LaunchGuard is a read-only pre-launch check plugin for Paper Minecraft servers. It inspects configured plugin requirements, command registration, world availability, location safety, permission nodes, plugin metadata, dependency visibility, startup readiness, configuration diagnostics, baseline drift detection, and report/export integration.

## Features

- Pre-launch server health checks (`/launchguard run`)
- Plugin inventory and dependency visibility (`/launchguard plugins`)
- Saved preflight reports and report history (`/launchguard history`)
- JSON and HTML report exports (`/launchguard export json`, `/launchguard export html`)
- Configuration validation (`/launchguard validate`)
- Baseline drift detection (`/launchguard baseline save`, `list`, `compare`, `delete`)
- Baseline report and export integration (`/launchguard baseline history`, `export json`, `export html`)
- Optional startup baseline comparison
- Configurable report and export retention

## Commands

| Command | Description |
|---|---|
| `/launchguard help` | Show help |
| `/launchguard run` | Run pre-launch checks |
| `/launchguard plugins` | Plugin inventory and dependency visibility |
| `/launchguard history` | Saved report history |
| `/launchguard export json` | Export report as JSON |
| `/launchguard export html` | Export report as HTML |
| `/launchguard validate` | Validate configuration files |
| `/launchguard baseline save <name>` | Save a server-state baseline |
| `/launchguard baseline list` | List saved baselines |
| `/launchguard baseline compare <name>` | Compare server against baseline |
| `/launchguard baseline export json <name>` | Export baseline drift as JSON |
| `/launchguard baseline export html <name>` | Export baseline drift as HTML |
| `/launchguard baseline history` | List saved baseline reports |
| `/launchguard reload` | Reload configuration |
| `/launchguard version` | Show plugin version |

Aliases: `/lg`, `/preflight`

## Permissions

All permissions default to OP.

`launchguard.use`, `launchguard.run`, `launchguard.reload`, `launchguard.plugins`, `launchguard.history`, `launchguard.export`, `launchguard.validate`, `launchguard.baseline`, `launchguard.admin` (includes all)

## Configuration

All configuration files are in `plugins/LaunchGuard/`:

- `config.yml` — general settings, startup checks, report/export retention
- `checks.yml` — check categories, required plugins/commands/worlds/locations/permissions
- `messages.yml` — customizable user-facing messages

## Report, Export, and Baseline Folders

- `plugins/LaunchGuard/reports/` — plain-text preflight reports
- `plugins/LaunchGuard/exports/` — JSON and HTML exports
- `plugins/LaunchGuard/baselines/` — YAML baseline snapshots
- `plugins/LaunchGuard/reports/baseline/` — plain-text baseline drift reports

## Compatibility

Fully smoke-tested on:
- Paper 1.20.1 through 1.20.6
- Paper 1.21.1 through 1.21.10

Load-tested only on Paper 1.21.11.

Not available on PaperMC at QA time: 26.1, 26.2.
Not tested: 26.3.

Built with Java 17 target compatibility. Use the Java version required by your Paper server version.

## Safety

LaunchGuard is read-only. It does not execute plugin commands, execute configured commands, install/download/enable/disable/reload plugins, teleport players, modify blocks/worlds/economy/permissions/whitelist, send network requests, upload data, force-load chunks, capture player data, or modify server state.
