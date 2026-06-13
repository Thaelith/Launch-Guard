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

## /launchguard version

Displays the plugin version.

Permission: `launchguard.use`

## Tab Completion

Tab completion is supported for all subcommands: help, run, plugins, reload, version.

`/launchguard plugins` also supports tab completion for `verbose` and `dependencies`.
