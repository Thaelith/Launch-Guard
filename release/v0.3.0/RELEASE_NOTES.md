# LaunchGuard v0.3.0

LaunchGuard v0.3.0 adds optional startup preflight checks, saved plain text report files, and basic report history commands.

## Added

- Optional startup preflight checks
- Saved plain text report files under `plugins/LaunchGuard/reports/`
- `/launchguard history`
- `/launchguard history latest`
- `launchguard.history` permission
- Report retention with `settings.reportsToKeep`

## Startup Checks

Startup checks can run automatically after the server starts.

Configuration:

```yaml
settings:
  runOnStartup: true
  startupDelayTicks: 100
```

Startup checks are read-only and use the same configured checks as `/launchguard run`.

## Saved Reports

Reports can be saved as plain text files.

Configuration:

```yaml
settings:
  saveReports: true
  reportsToKeep: 25
```

Saved reports are written to:

```text
plugins/LaunchGuard/reports/
```

## Report History

New commands:

```text
/launchguard history
/launchguard history latest
```

Permission:

```text
launchguard.history
```

## Safety

LaunchGuard remains read-only.

It does not:

* Execute plugin commands
* Execute configured commands
* Install plugins
* Download plugins
* Enable or disable plugins
* Reload plugins
* Teleport players
* Modify blocks
* Modify worlds
* Modify economy data
* Modify permission data
* Change whitelist state
* Send network requests
* Upload data
* Force-load chunks

Saved reports are written only under `plugins/LaunchGuard/reports/`.

Report history reads only from `plugins/LaunchGuard/reports/`.

Report retention deletes only `.txt` files inside the reports directory.

## Limitations

* Startup checks use a configurable fixed delay.
* Reports are plain text only.
* Report history has no search or filter support.
* Plugin inventory reports metadata, enabled state, and dependency visibility only.
* LaunchGuard does not verify full plugin runtime behavior or plugin configuration correctness.

## Compatibility

* Tested on Paper 1.20.4
* Java 17
