# LaunchGuard - Automated Pre-Launch Checks for Minecraft Servers

**Version:** 0.1.0 Lite
**Price:** Free
**Category:** Admin Tools
**Tested On:** Paper 1.20+

---

## Product Description

LaunchGuard is a server administration utility that performs automated pre-launch checks on your Minecraft server.

Before you open your server, update plugins, launch a new season, or disable the whitelist, run `/launchguard run`. The plugin inspects your server state and produces a clear report telling you what is ready and what needs attention.

## What It Checks

- **Required Plugins:** Is Vault installed? Is LuckPerms enabled? Checks every plugin you list.
- **Required Commands:** Is /spawn registered? Is /warp available? Confirms commands exist without executing them.
- **Required Worlds:** Are world, world_nether, and world_the_end loaded?
- **Safe Locations:** Is your spawn point on solid ground or in lava? Checks for fire, cactus, powder snow, void, and other hazards.
- **Permission Nodes:** Are expected permission nodes registered? Are dangerous nodes like `bukkit.command.stop` present?

## Example Report

```
[LaunchGuard] LaunchGuard Preflight Report
----------------------------------------
[PASS] Plugin loaded: Vault
[PASS] Plugin loaded: LuckPerms
[FAIL] Plugin missing or disabled: PlaceholderAPI
       -> Install and enable the missing plugin before launch.
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

## Why Use This

Server owners who manage multiple plugins and configurations know how easy it is to forget something. LaunchGuard provides a checklist you can run before every launch. It helps catch missing plugins, broken teleport points, unloaded worlds, and misconfigured permissions before players encounter them.

## What This Does Not Do

This is a Lite release. It inspects and reports. It does not:

- Execute commands
- Modify blocks or worlds
- Teleport players
- Change permissions or economy data
- Send network requests or collect analytics
- Write files outside its own data folder

It helps catch problems. It does not fix them for you.

## Commands

| Command | Permission | Description |
|---|---|---|
| /launchguard | launchguard.use | Show help |
| /launchguard run | launchguard.run | Run all checks |
| /launchguard reload | launchguard.reload | Reload config |
| /launchguard version | launchguard.use | Show version |

Aliases: /lg, /preflight
All permissions default to OP-only.

## Planned Features

HTML reports, Discord webhook reports, scheduled checks, startup checks, Vault economy checks, PlaceholderAPI checks, LuckPerms group audit, Launch Mode, report history, JSON export.

## Requirements

- Paper 1.20+ (may work on Spigot forks, not guaranteed)
- Java 17+

## Installation

1. Place the JAR in your `plugins/` folder
2. Restart the server
3. Edit `plugins/LaunchGuard/checks.yml`
4. Run `/launchguard run`

## Support

GitHub: https://github.com/Thaelith/Launch-Guard
Please report bugs and feature requests on the issue tracker.
