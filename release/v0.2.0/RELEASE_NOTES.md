# LaunchGuard v0.2.0

LaunchGuard v0.2.0 adds read-only plugin inventory and dependency visibility for Paper servers.

## Added
- `/launchguard plugins`
- `/launchguard plugins verbose`
- `/launchguard plugins dependencies`
- `launchguard.plugins` permission
- Optional `pluginInventory` preflight integration

## Plugin Inventory

Plugin inventory reports:
- Installed plugin names
- Enabled/disabled state
- Plugin versions
- Main class
- API version
- Authors
- Hard dependencies
- Soft dependencies

## Safety

LaunchGuard remains read-only.

It does not:
- Execute plugin commands
- Execute configured commands
- Install plugins
- Download plugins
- Enable or disable plugins
- Reload plugins
- Modify worlds, blocks, economy, permissions, or whitelist state
- Send network requests

## Limitations
- Plugin inventory does not verify plugin configuration correctness.
- Plugin inventory does not fully test plugin runtime behavior.
- Missing hard dependencies may not appear if Paper prevents the dependent plugin from loading.

## Compatibility
- Tested on Paper 1.20.4
- Java 17
