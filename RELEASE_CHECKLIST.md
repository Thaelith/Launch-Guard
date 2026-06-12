# Release Checklist

Run through this list before publishing a new version.

## Build

- [ ] Run `./gradlew build` with no errors
- [ ] Confirm JAR in `build/libs/LaunchGuard-0.1.0.jar`
- [ ] Verify plugin.yml, config.yml, checks.yml, messages.yml in JAR

## Paper Server Test

- [ ] Install on Paper 1.20+ test server
- [ ] Confirm plugin enables without errors in console
- [ ] Confirm `LaunchGuard v0.1.0 enabled.` in console
- [ ] Confirm config files created in `plugins/LaunchGuard/`

## Commands

- [ ] `/launchguard` shows help (requires permission)
- [ ] `/launchguard help` shows help
- [ ] `/launchguard version` shows version
- [ ] `/launchguard run` executes checks
- [ ] `/launchguard reload` reloads config
- [ ] `/lg` alias works
- [ ] `/preflight` alias works
- [ ] Tab completion works for subcommands

## Permissions

- [ ] Normal player cannot use `/launchguard` (default: op)
- [ ] Normal player with `launchguard.use` granted can access help
- [ ] OP player can use all commands
- [ ] Console can use all commands
- [ ] `launchguard.admin` works (includes run + reload)

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
