# Commands

All commands require `launchguard.use` permission (op-only by default).

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

## /launchguard reload

Reloads configuration from disk. All three YAML files (config.yml, checks.yml, messages.yml) are reloaded.

Permission: `launchguard.reload` or `launchguard.admin`

If any file has invalid YAML, the reload fails for that file and the previous valid configuration is kept. An error message is shown in chat and the console.

## /launchguard version

Displays the plugin version.

Permission: `launchguard.use`

## Tab Completion

Tab completion is supported for all subcommands: help, run, reload, version.
