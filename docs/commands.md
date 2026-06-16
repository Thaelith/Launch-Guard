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

## Tab Completion

Tab completion is supported for all subcommands: help, run, plugins, history, reload, version, export.

`/launchguard plugins` also supports tab completion for `verbose` and `dependencies`.

`/launchguard history` also supports tab completion for `latest`.

`/launchguard export` also supports tab completion for `json` and `html`.
