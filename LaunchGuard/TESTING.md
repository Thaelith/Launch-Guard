# LaunchGuard Testing Guide

## Building

```bash
./gradlew build
```

The JAR is produced at `build/libs/LaunchGuard-0.1.0.jar`.

## Installing on a Paper Test Server

1. Copy `build/libs/LaunchGuard-0.1.0.jar` to your Paper server's `plugins/` directory.
2. Start the server or run `/reload confirm`.
3. Verify the plugin loads: check console for `LaunchGuard v0.1.0 enabled.`
4. Configuration files are created in `plugins/LaunchGuard/`.

## Test Cases

### 1. Basic Commands

| Command | Expected Result |
|---|---|
| `/launchguard` | Shows help listing all subcommands |
| `/launchguard help` | Shows help |
| `/launchguard version` | Shows `LaunchGuard version 0.1.0` |
| `/launchguard unknown` | Shows unknown subcommand error |
| `/lg help` | Same as `/launchguard help` (alias works) |
| `/lg run` | Same as `/launchguard run` |
| `/preflight run` | Same as `/launchguard run` |

### 2. Permission Testing

| Test | Expected Result |
|---|---|
| Console: `/launchguard run` | Runs checks (console has all permissions) |
| Console: `/launchguard reload` | Reloads successfully |
| Normal player: `/launchguard run` | Permission denied |
| Normal player: `/launchguard help` | Shows help (launchguard.use is default:true) |
| OP player: `/launchguard run` | Runs checks |
| OP player: `/launchguard reload` | Reloads successfully |

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
   - `[PASS]` or `[FAIL]` for Vault (depending on whether it is installed)
   - `[FAIL]` for NonExistentPlugin with suggestion text

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
   - Commands with leading `/` are normalized (e.g., `/spawn` is checked as `spawn`)
   - No commands are actually executed during the check

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

### 6. Location Safety

1. Edit `plugins/LaunchGuard/checks.yml`:
   ```yaml
   checks:
     locations:
       enabled: true
       entries:
         spawn:
           world: world
           x: 0
           y: 80
           z: 0
           safe: true
         void_check:
           world: world
           x: 0
           y: -64
           z: 0
           safe: true
         missing_world:
           world: doesnotexist
           x: 0
           y: 64
           z: 0
           safe: true
   ```
2. Run `/launchguard run`.
3. Expected:
   - `[PASS]` or appropriate result for `spawn` (depends on block at that location)
   - `[FAIL]` for `void_check` if Y is out of bounds
   - `[FAIL]` for `missing_world` with world missing message
4. Verify: no player is teleported, no blocks are modified.

### 7. Permission Node Checks

1. Edit `plugins/LaunchGuard/checks.yml`:
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
   - `[PASS]` for `bukkit.command.help` (registered by Bukkit)
   - `[WARN]` for `some.unknown.node` (not FAIL)
   - `[WARN]` for dangerous nodes that are registered

### 8. Broken YAML

1. Corrupt `plugins/LaunchGuard/config.yml` (e.g., replace a colon with an equals sign).
2. Run `/launchguard reload`.
3. Expected:
   - Reload reports failure to sender and console
   - Plugin does not crash
   - Previous valid configuration is kept
   - `/launchguard run` still works with previous config

### 9. Missing Config Files

1. Delete `plugins/LaunchGuard/checks.yml`.
2. Run `/launchguard reload`.
3. Expected:
   - File is recreated from defaults
   - Reload succeeds

### 10. Empty Check Lists

1. Edit checks so all `required:` lists are empty or the checks are disabled.
2. Run `/launchguard run`.
3. Expected:
   - If all checks disabled: message says no checks enabled
   - If checks enabled but lists empty: report shows zero results

### 11. Console Output

1. Run `/launchguard run` as a player.
2. Expected:
   - Report appears in player's chat
   - Report also appears in console (if `reportToConsole: true` in config.yml)

## Safety Verification Checklist

After running all tests, confirm:

- [ ] No arbitrary server commands were executed
- [ ] No players were teleported
- [ ] No blocks were modified
- [ ] No economy data was changed
- [ ] No permission data was changed
- [ ] The whitelist state is unchanged
- [ ] No files created outside `plugins/LaunchGuard/`
- [ ] No network requests were made
- [ ] No user data was collected or sent
