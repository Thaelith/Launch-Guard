# Installation

## Requirements

- Paper 1.20 or newer (may work on Spigot 1.20+ but not tested)
- Java 17 or newer

## Steps

1. Download `LaunchGuard-0.1.0.jar` from the releases page.

2. Place the JAR in your server's `plugins/` directory.

3. Start or restart the server.

4. Verify the plugin loaded by checking the console:
   ```
   [LaunchGuard] LaunchGuard v0.1.0 enabled.
   ```

5. Configuration files are created automatically in `plugins/LaunchGuard/`:
   - `config.yml` -- general plugin settings
   - `checks.yml` -- what to check
   - `messages.yml` -- user-facing messages

6. Edit `plugins/LaunchGuard/checks.yml` to match your server's plugins, commands, worlds, and locations.

7. Grant permissions to staff who need to run checks. All LaunchGuard permissions are op-only by default.
   ```
   /lp user MyPlayer permission set launchguard.use true
   /lp user MyPlayer permission set launchguard.run true
   ```

8. Run your first check:
   ```
   /launchguard run
   ```

## Upgrading

1. Replace the old JAR with the new one.
2. Restart the server.
3. New configuration keys are automatically added with defaults.
4. Review `plugins/LaunchGuard/checks.yml` for any new check options.
