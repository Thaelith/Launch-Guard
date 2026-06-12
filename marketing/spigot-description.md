# LaunchGuard - Automated Pre-Launch Checks for Minecraft Servers

**Version:** 0.1.0 (Lite)
**Tested on:** Paper 1.20+
**Source:** https://github.com/Thaelith/Launch-Guard

---

## About

LaunchGuard is a server administration plugin that runs automated pre-launch checks. Before you open your server to players, run `/launchguard run` to verify that your plugins, commands, worlds, locations, and permissions are properly configured.

It produces a clean, technical report with [PASS], [WARN], [FAIL], and [INFO] labels.

## Features

- Checks required plugins are installed and enabled
- Checks required commands are registered
- Checks required worlds are loaded
- Checks locations for unsafe blocks (lava, fire, cactus, void, etc.)
- Flags missing expected permission nodes and dangerous registered nodes
- Configurable via YAML files
- Reload command with broken YAML recovery
- Report printed to chat and console

## Commands

| Command | Description |
|---|---|
| /launchguard run | Run pre-launch checks |
| /launchguard reload | Reload configuration |
| /launchguard help | Show help |
| /launchguard version | Show version |

Aliases: /lg, /preflight

## Permissions

All permissions are op-only by default:
- launchguard.use - Access the command
- launchguard.run - Run checks
- launchguard.reload - Reload config
- launchguard.admin - Full access (implies run and reload)

## Configuration

Edit `plugins/LaunchGuard/checks.yml` to match your server's requirements. Example configs for survival, skyblock, boxpvp, and lobby servers are included.

## Safety

LaunchGuard Lite v0.1 is read-only. It does not execute commands, modify blocks, teleport players, change permissions, or make network requests.

## Limitations

- Does not integrate with LuckPerms for group-level audits
- Does not perform economy checks
- Does not send Discord or webhook reports
- Does not run on startup (planned)

## Planned

HTML reports, Discord webhook, scheduled checks, startup checks, Vault economy checks, PlaceholderAPI validation, LuckPerms group audit, JSON export.

## Installation

1. Drop LaunchGuard-0.1.0.jar into plugins/
2. Restart server
3. Edit plugins/LaunchGuard/checks.yml
4. Run /launchguard run

## Support

Report issues on GitHub: https://github.com/Thaelith/Launch-Guard
