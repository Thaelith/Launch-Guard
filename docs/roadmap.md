# Roadmap

LaunchGuard Lite v0.1 was the initial release focusing on essential read-only pre-launch checks.

## v0.2.0

- Plugin inventory report
- Plugin dependency visibility report
- Read-only plugin health overview
- Optional plugin inventory preflight section, disabled by default

## v0.3.0

- Optional startup preflight checks
- Saved plain text report files
- `/launchguard history` and `/launchguard history latest` commands
- Report retention (configurable max files)
- `launchguard.history` permission

## v0.5.0

- `/launchguard export json`
- `/launchguard export html`
- `/launchguard validate`
- `launchguard.export` and `launchguard.validate` permissions
- JSON report export with versioned schema
- Static HTML report export
- Configuration validation for config.yml, checks.yml, and messages.yml

## Future Candidates

- Additional location safety blocks
- More configurable report wording
- API for read-only third-party checks

## Not Planned

- Executing arbitrary commands configured by server owners
- Modifying server state (plugins, worlds, blocks, permissions)
- Teleporting players
- Installing, downloading, enabling, disabling, or reloading plugins
- Sending network requests or uploading data
