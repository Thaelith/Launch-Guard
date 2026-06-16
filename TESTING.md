# Testing Guide

## Building

```bash
./gradlew build
```

The JAR is produced at `build/libs/LaunchGuard-0.3.0.jar`.

## Installing on a Paper Test Server

1. Copy `build/libs/LaunchGuard-0.3.0.jar` to your Paper server's `plugins/` directory.
2. Start the server or run `/reload confirm`.
3. Verify the plugin loads: check console for `LaunchGuard v0.3.0 enabled.`
4. Configuration files are created in `plugins/LaunchGuard/`.

Note: LaunchGuard permissions are op-only by default. Use an operator account or grant `launchguard.use` to test players.

## Test Cases

### 1. Basic Commands

| Command | Expected Result |
|---|---|
| `/launchguard` | Shows help listing all subcommands |
| `/launchguard help` | Shows help |
| `/launchguard version` | Shows `LaunchGuard version 0.3.0` |
| `/launchguard unknown` | Shows unknown subcommand error |
| `/lg help` | Same as `/launchguard help` (alias works) |
| `/lg run` | Same as `/launchguard run` |
| `/preflight run` | Same as `/launchguard run` |

### 2. Permission Testing

| Test | Expected Result |
|---|---|
| Normal player: `/launchguard` | Permission denied (default: op) |
| Normal player with launchguard.use: `/launchguard help` | Shows help |
| Normal player with launchguard.use only: `/launchguard run` | Permission denied (needs launchguard.run) |
| OP player: `/launchguard run` | Runs checks |
| OP player: `/launchguard reload` | Reloads successfully |
| Console: `/launchguard run` | Runs checks (console has all permissions) |

### 3. Plugin Checks

1. Edit `plugins/LaunchGuard/checks.yml`:
   ```yaml
   checks:
     plugins:
       enabled: true
       required:
         - Vault
         - NonExistentPlugin
   ```
2. Run `/launchguard run`.
3. Expected:
   - `[PASS]` or `[FAIL]` for Vault (depending on installation)
   - `[FAIL]` for NonExistentPlugin with suggestion

### 4. Command Checks

1. Edit `plugins/LaunchGuard/checks.yml`:
   ```yaml
   checks:
     commands:
       enabled: true
       required:
         - help
         - fakecommand
         - /spawn
   ```
2. Run `/launchguard run`.
3. Expected:
   - `[PASS]` for `help` (Minecraft built-in)
   - `[FAIL]` for `fakecommand`
   - Commands with leading `/` are normalized
   - No commands are executed

### 5. World Checks

1. Edit `plugins/LaunchGuard/checks.yml`:
   ```yaml
   checks:
     worlds:
       enabled: true
       required:
         - world
         - world_nether
         - world_the_end
         - nonexistent_world
   ```
2. Run `/launchguard run`.
3. Expected:
   - `[PASS]` for loaded worlds
   - `[FAIL]` for `nonexistent_world`

### 6. Location Safety - Unloaded Chunk

1. Configure a location in a part of the world that has never been visited.
2. Run `/launchguard run`.
3. Expected:
   - `[WARN]` with message about chunk not loaded (not PASS)
   - Suggestion: "Visit or load the area once, then run the check again."
4. Visit the area, run again.
5. Expected: Actual safety result (PASS or FAIL) appears.

### 7. Location Safety - Invalid Coordinates

1. Edit `plugins/LaunchGuard/checks.yml`:
   ```yaml
   checks:
     locations:
       enabled: true
       entries:
         bad_x:
           world: world
           y: 80
           z: 0
         bad_y:
           world: world
           x: 0
           y: abc
           z: 0
         out_of_bounds:
           world: world
           x: 0
           y: -999
           z: 0
         missing_world:
           world: doesnotexist
           x: 0
           y: 64
           z: 0
   ```
2. Run `/launchguard run`.
3. Expected:
   - `[FAIL]` for `bad_x` (x is missing or not numeric)
   - `[FAIL]` for `bad_y` (y is missing or not numeric)
   - `[FAIL]` for `out_of_bounds` (y is outside world height limits)
   - `[FAIL]` for `missing_world` (world not found)
4. No console stack traces for normal users.

### 8. Permission Node Checks

1. Edit checks.yml:
   ```yaml
   checks:
     permissions:
       enabled: true
       nodes:
         shouldExist:
           - bukkit.command.help
           - some.unknown.node
         dangerous:
           - minecraft.command.op
           - bukkit.command.stop
   ```
2. Run `/launchguard run`.
3. Expected:
   - `[PASS]` for `bukkit.command.help`
   - `[WARN]` for `some.unknown.node`
   - `[WARN]` for dangerous nodes that are registered

### 9. Broken YAML

1. Corrupt `plugins/LaunchGuard/config.yml` with invalid YAML.
2. Run `/launchguard reload`.
3. Expected:
   - Reload reports failure to sender and console
   - Plugin does not crash
   - Previous valid configuration is kept
   - `/launchguard run` still works

### 10. Missing Config Files

1. Delete `plugins/LaunchGuard/checks.yml`.
2. Run `/launchguard reload`.
3. Expected: File recreated from defaults, reload succeeds.

### 11. Console Output

1. Run `/launchguard run` as a player (with reportToConsole: true).
2. Expected: Report appears in console as well as chat.

### 12. Saved Report Files

1. Set `saveReports: true` in config.yml.
2. Run `/launchguard reload` and `/launchguard run`.
3. Expected:
   - `plugins/LaunchGuard/reports/` directory is created
   - A `YYYY-MM-DD_HH-mm-ss_manual.txt` file is created inside
   - Message shows the saved file path
4. Run `/launchguard history`.
5. Expected: Lists the saved report file with filename, modification time, and file size.
6. Run `/launchguard history latest`.
7. Expected: Displays the content of the most recent report file.

### 13. Startup Check Mode

1. Set `runOnStartup: true`, `startupDelayTicks: 100`, `saveReports: true` in config.yml.
2. Restart the server.
3. Expected:
   - Console shows "Running startup preflight check" after server start
   - Preflight report appears in console
   - A `YYYY-MM-DD_HH-mm-ss_startup.txt` file is created in `plugins/LaunchGuard/reports/`
   - Server continues normal startup without errors

### 14. Report Retention

1. Set `saveReports: true`, `reportsToKeep: 2` in config.yml.
2. Run `/launchguard run` at least 3 times.
3. Check `plugins/LaunchGuard/reports/`.
4. Expected: Only the 2 most recent `.txt` files remain. Older files are deleted.
5. No non-report files or directories in the reports folder are affected.

### 15. History Permissions

1. Not granting `launchguard.history` to a non-OP player.
2. Expected: `/launchguard history` shows permission denied.
3. Grant `launchguard.history`.
4. Expected: `/launchguard history` and `/launchguard history latest` work.
5. Grant `launchguard.admin`.
6. Expected: All history commands work.

## Safety Verification

After all tests, confirm:

- [ ] No commands were executed by checks
- [ ] No commands were executed by JSON export
- [ ] No players were teleported
- [ ] No blocks were modified
- [ ] No chunks were force-loaded
- [ ] No economy data changed
- [ ] No permission data changed
- [ ] Whitelist state unchanged
- [ ] No files created outside `plugins/LaunchGuard/`
- [ ] Report files only in `plugins/LaunchGuard/reports/`
- [ ] JSON exports only in `plugins/LaunchGuard/exports/`
- [ ] JSON exports do not include tokens, webhook URLs, or absolute paths
- [ ] No network requests made
- [ ] No commands executed during startup check
