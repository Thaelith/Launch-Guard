# Commands

The base `/launchguard` command requires `launchguard.use` permission (op-only by default). Subcommands that expose reports or reload configuration also require their own permission.

## /launchguard

Shows help. Same as `/launchguard help`.

Aliases: `/lg`, `/preflight`

## /launchguard help

Displays a list of available subcommands.

Permission: `launchguard.use`

## /launchguard run

Runs all enabled pre-launch checks and displays a report.

Permission: `launchguard.run` or `launchguard.admin`

The report includes:
- Individual check results labeled [PASS], [WARN], [FAIL], or [INFO]
- Suggestions for issues found
- Total passed, warning, and failure counts
- Final status: READY, READY_WITH_WARNINGS, or NOT_READY

If `reportToConsole: true` in config.yml, the report is also sent to the server console.

## /launchguard plugins

Shows a concise read-only plugin inventory report.

Permission: `launchguard.plugins` or `launchguard.admin`

The report includes:
- Total installed plugin count
- Enabled plugin count
- Disabled plugin count
- Plugin names
- Plugin versions
- Enabled or disabled status

This command does not execute plugin commands, enable plugins, disable plugins, reload plugins, download plugins, or modify server state.

## /launchguard plugins verbose

Shows detailed metadata for each installed plugin.

Permission: `launchguard.plugins` or `launchguard.admin`

The report includes:
- Status
- Version
- Main class
- API version
- Authors
- Hard dependencies
- Soft dependencies

Missing metadata is shown as `none` or `unknown`.

Plugin inventory does not verify that each plugin is correctly configured. It reports installed plugin metadata, enabled state, and dependency visibility.

## /launchguard plugins dependencies

Shows the dependency visibility report without the full inventory.

Permission: `launchguard.plugins` or `launchguard.admin`

The report reads hard and soft dependencies from visible plugin metadata:
- Existing dependencies are reported as [PASS]
- Missing hard dependencies are reported as [FAIL]
- Missing soft dependencies are reported as [WARN]

LaunchGuard does not attempt to install, download, enable, disable, reload, or fix plugins.

## /launchguard reload

Reloads configuration from disk. All three YAML files (config.yml, checks.yml, messages.yml) are reloaded.

Permission: `launchguard.reload` or `launchguard.admin`

If any file has invalid YAML, the reload fails for that file and the previous valid configuration is kept. An error message is shown in chat and the console.

## /launchguard history

Lists the most recent saved report files (up to 10).

Permission: `launchguard.history` or `launchguard.admin`

The output includes:
- Filename
- Last modified time
- File size

If no saved reports exist, an [INFO] message is displayed.

Report files are stored in `plugins/LaunchGuard/reports/`. This command is read-only and only lists files from the reports directory. No path traversal is possible.

## /launchguard history latest

Displays the content of the most recently saved report file.

Permission: `launchguard.history` or `launchguard.admin`

If the file is unusually large, the output is truncated with a notification. If no reports exist, an [INFO] message is displayed.

This command reads only from `plugins/LaunchGuard/reports/`. It does not accept arbitrary filenames or paths.

## /launchguard validate

Validates LaunchGuard configuration files and reports structural problems without running preflight checks.

Permission: `launchguard.validate` or `launchguard.admin`

Validates:
- `config.yml` -- type checks, invalid values, unknown keys
- `checks.yml` -- structure, duplicate entries, invalid coordinate types, missing sections
- `messages.yml` -- missing or empty keys, non-string values

Validation is read-only. It does not modify configuration files, reload config automatically, or run preflight checks.

## /launchguard version

Displays the plugin version.

Permission: `launchguard.use`

## /launchguard export json

Runs a fresh preflight check and exports the results as a JSON file.

Permission: `launchguard.export` or `launchguard.admin`

The JSON export includes:
- `schemaVersion` (versioned schema identifier)
- `generatedAt` (ISO 8601 timestamp)
- `source` (manual)
- `launchGuard.version`
- `server.name`, `server.version`, `server.bukkitVersion`
- `summary` (status, passed, warnings, failures, totalResults)
- `results[]` (checkId, severity, passed, message, suggestion)

JSON files are saved to:

```
plugins/LaunchGuard/exports/
```

File naming format:

```
YYYY-MM-DD_HH-mm-ss-SSS_manual.json
```

This command is read-only. It does not send network requests or include tokens, webhook URLs, full logs, or absolute file paths.

## /launchguard export html

Runs a fresh preflight check and exports the results as a static HTML file.

Permission: `launchguard.export` or `launchguard.admin`

The HTML export is self-contained:
- No external CSS, JavaScript, images, fonts, or CDN resources
- Works as a local file opened in a browser
- Includes header, summary, results table, and footer with safety note
- Text content is HTML-escaped

HTML files are saved to:

```
plugins/LaunchGuard/exports/
```

File naming format:

```
YYYY-MM-DD_HH-mm-ss-SSS_manual.html
```

This command is read-only. It does not send network requests or include tokens, webhook URLs, player IPs, full logs, or absolute file paths.

## /launchguard baseline save <name>

Captures current server state into a local YAML baseline file.

Permission: `launchguard.baseline` or `launchguard.admin`

Baseline files are saved to:

```
plugins/LaunchGuard/baselines/<name>.yml
```

Name rules: 1-32 characters, letters/numbers/underscore/dash only. No dots, slashes, spaces, or file extensions.

If a baseline with the same name already exists, returns an error. Does not overwrite silently.

This command does not execute commands, modify server state, or send network requests.

## /launchguard baseline list

Lists saved baselines with name, creation timestamp, server version, and plugin count.

Permission: `launchguard.baseline` or `launchguard.admin`

If no baselines exist, displays a clear message.

## /launchguard baseline compare <name>

Compares current server state against a saved baseline and prints a drift report.

Permission: `launchguard.baseline` or `launchguard.admin`

Drift statuses:
- `MATCHES_BASELINE` — current state matches baseline
- `DRIFT_DETECTED` — differences found in plugins, commands, worlds, or LaunchGuard configuration
- `BASELINE_INVALID` — baseline file is corrupt or unsupported schema version

Drift comparison reports:
- Removed/added plugins, commands, and worlds
- Plugin enabled state and version changes
- LaunchGuard check configuration changes (required plugins, commands, worlds, permissions, plugin inventory, location keys)

## /launchguard baseline delete <name>

Deletes a saved baseline file.

Permission: `launchguard.baseline` or `launchguard.admin`

Only deletes validated `.yml` baseline files inside `plugins/LaunchGuard/baselines/`. Does not delete directories or non-baseline files.

## Tab Completion

Tab completion is supported for all subcommands: help, run, plugins, history, reload, version, export, validate, baseline.

`/launchguard plugins` also supports tab completion for `verbose` and `dependencies`.

`/launchguard history` also supports tab completion for `latest`.

`/launchguard export` also supports tab completion for `json` and `html`.

`/launchguard baseline` also supports tab completion for `save`, `list`, `compare`, and `delete`.
