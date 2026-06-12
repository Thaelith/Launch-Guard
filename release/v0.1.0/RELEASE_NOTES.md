# LaunchGuard v0.1.0

LaunchGuard is a Paper-first Minecraft server administration plugin that runs read-only pre-launch checks. It inspects plugin state, command registration, world availability, location safety, and permission configuration, then produces a clear READY / NOT_READY report.

## Included in v0.1.0

- Required plugin checks
- Required command checks
- Required world checks
- Safe location checks
- Basic permission node checks
- Plain text chat and console report
- Reload command with YAML error recovery
- Default configuration files
- Example server check configs (survival, skyblock, boxpvp, lobby)
- GitHub Actions build workflow

## Safety Notes

LaunchGuard Lite v0.1.0 does not execute configured commands, teleport players, modify blocks, modify worlds, modify economy data, modify permission data, change whitelist state, or send network requests.

## Known Limitations

- Command detection uses a reflection-based command map fallback which may not cover all dynamically registered commands.
- Permission checks are node-level only, not group-level.
- Location checks cannot inspect unloaded chunks (reports WARN instead).
- LuckPerms, Vault, PlaceholderAPI, Discord, HTML, JSON, scheduled checks, startup checks, and Launch Mode are not included in Lite v0.1.0.

## Requirements

- Java 17 or newer
- Paper 1.20.4 or compatible Paper server version

## Installation

1. Download `LaunchGuard-0.1.0.jar`.
2. Place it in the server `plugins/` folder.
3. Start the server.
4. Edit `plugins/LaunchGuard/checks.yml` to match your server's requirements.
5. Run `/launchguard run`.

## Permissions

All permissions default to op-only.

| Permission | Description |
|---|---|
| launchguard.use | Access to help and version |
| launchguard.run | Required for /launchguard run |
| launchguard.reload | Required for /launchguard reload |
| launchguard.admin | Full access (includes use, run, reload) |

## Commands

| Command | Description |
|---|---|
| /launchguard help | Show help |
| /launchguard run | Run all enabled checks |
| /launchguard reload | Reload configuration files |
| /launchguard version | Show version |

Aliases: /lg, /preflight

## Checksum

See `SHA256SUMS.txt`.
