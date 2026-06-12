# Troubleshooting

## Plugin does not load

**Symptom:** No `LaunchGuard v0.1.0 enabled.` message in console.

**Causes:**
- Server is not Paper 1.20+ (LaunchGuard uses Paper API)
- JAR file is corrupted (re-download)
- Another plugin is interfering

**Fix:**
Check the server console for error messages. LaunchGuard logs startup errors with its prefix.

## Cannot run commands

**Symptom:** `/launchguard` returns "You do not have permission to use this command."

**Cause:** LaunchGuard permissions default to op-only.

**Fix:**
Grant `launchguard.use` to the player or make them an operator:
```
/lp user PlayerName permission set launchguard.use true
```

## Checks pass but server has issues

**Symptom:** Report says READY but the server still has problems.

**Cause:** The default checked items in checks.yml are examples. They do not match your server's actual plugins and configuration.

**Fix:**
Edit `plugins/LaunchGuard/checks.yml` to list your server's actual required plugins, commands, worlds, and locations.

## Reload fails

**Symptom:** "Configuration reload failed. Check the console for details."

**Cause:** One of the YAML files has invalid syntax.

**Fix:**
Check the console for which file failed. Common YAML errors:
- Tabs instead of spaces for indentation
- Missing colons after keys
- Unquoted strings that look like special YAML values

The previous valid configuration is kept while you fix the error.

## Location shows WARN about unloaded chunk

**Symptom:** `[WARN] Location could not be fully checked because the chunk is not loaded: <name>`

**Cause:** No player has visited the area, so the chunk is not in memory.

**Fix:**
Visit or teleport to the area once, then run the check again. LaunchGuard intentionally does not force-load chunks.

## No checks are enabled

**Symptom:** "No checks are enabled. Check your checks.yml configuration."

**Cause:** All check categories have `enabled: false` in checks.yml.

**Fix:**
Set `enabled: true` for the checks you want to run.

## Location reports FAIL for invalid coordinates

**Symptom:** `[FAIL] Location config invalid: <name> (x is missing or not numeric)`

**Cause:** The location entry in checks.yml has missing, non-numeric, or out-of-range coordinates.

**Fix:**
Check the location entry in checks.yml. All of `world`, `x`, `y`, and `z` must be present and numeric. Y must be within the world's height bounds.

## Report looks different between console and chat

**Symptom:** Colored text in chat appears as plain text in console.

**Cause:** The console uses Adventure component rendering. Some text colors (AQUA, DARK_GRAY) may not be distinguishable in all terminals.

This is normal behavior. The report content is identical; only the color rendering differs.
