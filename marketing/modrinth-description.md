# LaunchGuard

Automated Pre-Launch Checks for Minecraft Servers

---

Catch missing plugins, missing commands, missing worlds, unsafe locations, and basic permission issues before players join.

LaunchGuard is a server administration utility that runs read-only checks and produces a clear PASS / WARN / FAIL report. Run it before opening your server, updating plugins, or launching a new season.

## What It Checks

- Required plugins (installed and enabled)
- Required commands (registered on the server)
- Required worlds (loaded and accessible)
- Safe locations (no lava, fire, cactus, void, etc.)
- Permission nodes (expected nodes present, dangerous nodes flagged)

## Commands

`/launchguard run` -- Run all checks
`/launchguard reload` -- Reload configuration
`/launchguard help` -- Show help
`/launchguard version` -- Show version

Aliases: `/lg`, `/preflight`

## Important

This is the Lite release. It is read-only. It inspects server state and reports issues. It does not execute commands, modify data, teleport players, or make network requests.

## Planned Features

HTML reports, Discord webhook reports, scheduled checks, startup checks, Vault economy checks, PlaceholderAPI validation, LuckPerms group audit, JSON export, Launch Mode, report history.

## Requirements

Paper 1.20+, Java 17+

## Links

- Source: https://github.com/Thaelith/Launch-Guard
