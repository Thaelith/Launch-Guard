# Installation

## Requirements

- Paper 1.20.4, 1.20.6, 1.21.1, or 1.21.8 (tested)
- Other Paper 1.20.x and 1.21.x builds may work where compatible Bukkit/Paper APIs are available
- Java 17 for Paper 1.20.4
- Java 21 for Paper 1.20.6 and 1.21.x
- Use the Java version required by your Paper server version

## Steps

1. Download `LaunchGuard-0.3.0.jar` from the releases page.

2. Place the JAR in your server's `plugins/` directory.

3. Start or restart the server.

4. Verify the plugin loaded by checking the console:
   ```
   [LaunchGuard] LaunchGuard enabled.
   ```

5. Configuration files are created automatically in `plugins/LaunchGuard/`:
   - `config.yml` -- general plugin settings, including startup checks and saved reports
   - `checks.yml` -- what to check
   - `messages.yml` -- user-facing messages

6. Edit `plugins/LaunchGuard/checks.yml` to match your server's plugins, commands, worlds, and locations.

7. Optional: enable startup checks and saved reports in `plugins/LaunchGuard/config.yml`:
   ```yaml
   settings:
     runOnStartup: true
     startupDelayTicks: 100
     saveReports: true
     reportsToKeep: 25
   ```

8. Grant permissions to staff who need to run checks. All LaunchGuard permissions are op-only by default.
   ```
   /lp user MyPlayer permission set launchguard.use true
   /lp user MyPlayer permission set launchguard.run true
   /lp user MyPlayer permission set launchguard.history true
   ```

9. Run your first check:
   ```
   /launchguard run
   ```

10. Optional: view saved report history after enabling report saving:
    ```
    /launchguard history
    /launchguard history latest
    ```

## Upgrading

1. Replace the old JAR with the new one.
2. Restart the server.
3. New configuration keys are automatically added with defaults.
4. Review `plugins/LaunchGuard/checks.yml` and `config.yml` for any new check options.
