# Release Checklist

Run through this list before publishing a new version.

## Build

- [ ] Run `./gradlew build` with no errors
- [ ] Confirm JAR in `build/libs/LaunchGuard-0.3.0.jar`
- [ ] Verify plugin.yml, config.yml, checks.yml, messages.yml in JAR

## Paper Server Test

- [ ] Install on Paper 1.20+ test server
- [ ] Confirm plugin enables without errors in console
- [ ] Confirm `LaunchGuard v0.3.0 enabled.` in console
- [ ] Confirm config files created in `plugins/LaunchGuard/`

## Commands

- [ ] `/launchguard` shows help (requires permission)
- [ ] `/launchguard help` shows help
- [ ] `/launchguard version` shows version
- [ ] `/launchguard run` executes checks
- [ ] `/launchguard history` lists saved reports (requires `launchguard.history`)
- [ ] `/launchguard history latest` shows latest report content
- [ ] `/launchguard reload` reloads config
- [ ] `/lg` alias works
- [ ] `/preflight` alias works
- [ ] Tab completion works for all subcommands (including history, history latest)

## Permissions

- [ ] Normal player cannot use `/launchguard` (default: op)
- [ ] Normal player with `launchguard.use` granted can access help
- [ ] OP player can use all commands
- [ ] Console can use all commands
- [ ] `launchguard.admin` works (includes run + reload + plugins + history)

## Check Results

- [ ] Missing required plugin reported as FAIL
- [ ] Present enabled plugin reported as PASS
- [ ] Missing command reported as FAIL
- [ ] Registered command reported as PASS
- [ ] Missing world reported as FAIL
- [ ] Loaded world reported as PASS
- [ ] Safe location with loaded chunk reported as PASS
- [ ] Unloaded chunk reported as WARN (not PASS)
- [ ] Invalid coordinate reported as FAIL (not silently defaulted)
- [ ] Missing world in location entry reported as FAIL
- [ ] Y outside world bounds reported as FAIL
- [ ] Registered dangerous permission reported as WARN
- [ ] Missing expected permission reported as WARN

## Startup Check

- [ ] `runOnStartup: false` does not run startup checks
- [ ] `runOnStartup: true` runs preflight check after configured delay
- [ ] Startup report appears in console only (not chat)
- [ ] Startup report saved as `*_startup.txt` when `saveReports: true`
- [ ] Startup check failure does not crash server

## Report Saving and History

- [ ] `saveReports: true` creates report files in `plugins/LaunchGuard/reports/`
- [ ] Report files are plain text, no color codes
- [ ] `/launchguard history` lists recent reports with metadata
- [ ] `/launchguard history latest` displays latest report content
- [ ] Report retention respects `reportsToKeep` setting
- [ ] Retention only deletes `.txt` files in the reports directory
- [ ] No files written outside `plugins/LaunchGuard/reports/`

## Config Robustness

- [ ] Broken YAML in config.yml: reload fails, previous config kept
- [ ] Broken YAML in checks.yml: reload fails, previous config kept
- [ ] Broken YAML in messages.yml: reload fails, previous config kept
- [ ] Missing config file: recreated from defaults
- [ ] Empty check lists: report shows zero results
- [ ] All checks disabled: message says no checks enabled

## Safety Verification

- [ ] No commands are executed by checks
- [ ] No players are teleported
- [ ] No blocks are modified
- [ ] No chunks are force-loaded
- [ ] No world data is changed
- [ ] No economy data is changed
- [ ] No permission data is changed
- [ ] Whitelist state unchanged
- [ ] No files written outside `plugins/LaunchGuard/`
- [ ] Report files only in `plugins/LaunchGuard/reports/`
- [ ] No network requests made
- [ ] No analytics collected

## Documentation

- [ ] README.md up to date
- [ ] CHANGELOG.md up to date
- [ ] docs/ pages accurate
- [ ] Example configs valid YAML
- [ ] Marketplace descriptions accurate

## Publishing

- [ ] Prepare screenshots or terminal captures of report output
- [ ] Publish on Hangar (free)
- [ ] Publish on Modrinth (free)
- [ ] Publish on Spigot (free)
- [ ] Publish on BuiltByBit if applicable
- [ ] Update GitHub releases

## Post-Release

- [ ] Monitor for bug reports
- [ ] Collect feedback on check coverage
- [ ] Prepare next milestone
