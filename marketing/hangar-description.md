# LaunchGuard

**Automated Pre-Launch Checks for Minecraft Servers**

Catch missing plugins, missing commands, missing worlds, unsafe locations, and basic permission issues before players join.

---

## What It Does

LaunchGuard is a server administration utility. You run one command before opening your server and it checks whether everything is in order. It produces a clear PASS / WARN / FAIL report so you know what to fix before players log in.

Use it before:
- Opening a new server
- Deploying plugin updates
- Launching a new season
- Disabling the whitelist

## Checks Included in Lite v0.1

- **Required Plugins:** detects missing or disabled plugins
- **Required Commands:** detects commands that should be registered but aren't
- **Required Worlds:** detects worlds that should be loaded but aren't
- **Safe Locations:** detects unsafe blocks (lava, fire, cactus, void, etc.) at configured coordinates
- **Permission Nodes:** flags missing expected permission nodes and dangerous registered nodes

## Commands

| Command | Description |
|---|---|
| `/launchguard run` | Run all enabled checks |
| `/launchguard reload` | Reload configuration from disk |
| `/launchguard help` | Show help |
| `/launchguard version` | Show version |

Aliases: `/lg`, `/preflight`

## Permissions

All permissions default to op-only.

| Permission | Description |
|---|---|
| launchguard.use | Access the command |
| launchguard.run | Run checks |
| launchguard.reload | Reload config |
| launchguard.admin | Full access (implies run and reload) |

## Example Output

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

LaunchGuard Lite v0.1 is read-only. It inspects server state and reports findings.

It does not:
- Execute commands
- Teleport players
- Modify blocks or worlds
- Change permissions or economy data
- Change the whitelist
- Send network requests or upload data
- Write files outside its data folder

## Limitations

- Lite v0.1 does not execute configured commands.
- Lite v0.1 does not perform live economy transactions.
- Lite v0.1 does not inspect LuckPerms groups.
- Lite v0.1 does not send Discord reports.
- Lite v0.1 does not guarantee the server is perfect.
- It helps catch common configuration problems before players join.

## Planned

- HTML reports
- Discord webhook reports
- Scheduled checks
- Startup checks
- Vault economy checks
- PlaceholderAPI validation
- LuckPerms group audit
- Launch Mode
- Report history
- JSON export

## Requirements

- Paper 1.20+
- Java 17+

## Support

Report bugs and request features on the GitHub repository.
