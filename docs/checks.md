# Checks

LaunchGuard runs configured categories of checks. Each can be enabled or disabled in checks.yml.

## Required Plugins

Checks that each plugin in `checks.plugins.required` is installed and enabled.

Uses `Server.getPluginManager().getPlugin(name)` to detect plugins.

**Result: [PASS]** if the plugin is loaded and enabled.
**Result: [FAIL]** if the plugin is missing or disabled.

Suggestion: Install and enable the missing plugin before launch.

Notable: LaunchGuard does not attempt to download, install, or enable any plugin.

## Required Commands

Checks that each command in `checks.commands.required` is registered on the server.

Uses two strategies:
1. `Server.getPluginCommand(name)` -- catches commands declared in plugin.yml files
2. Reflection-based access to the server's `SimpleCommandMap` -- catches commands registered programmatically

Leading `/` characters in command names are automatically stripped.

**Result: [PASS]** if the command is found in either source.
**Result: [FAIL]** if the command is not registered.

Suggestion: Install or configure the plugin that provides this command.

Notable: LaunchGuard never executes any command during this check. It only inspects command registration.

## Required Worlds

Checks that each world in `checks.worlds.required` is loaded on the server.

Uses `Server.getWorld(name)` to detect worlds.

**Result: [PASS]** if the world is loaded.
**Result: [FAIL]** if the world is not loaded.

Suggestion: Check the world folder name or load the world before launch.

## Safe Locations

Checks configured locations for safety hazards.

For each location entry in `checks.locations.entries`:
- Validates that the world exists
- Validates that coordinates are present and numeric
- Validates that Y is within the world's height bounds
- If the chunk is loaded, checks the block and adjacent blocks
- If the chunk is not loaded, reports [WARN] without loading the chunk

**Unsafe blocks at the location or one block above:**
- LAVA, FIRE, SOUL_FIRE, CACTUS, SWEET_BERRY_BUSH, COBWEB, POWDER_SNOW

**Unsafe standable blocks (one block below):**
- AIR, CAVE_AIR, VOID_AIR, WATER, LAVA, FIRE, SOUL_FIRE, CACTUS, MAGMA_BLOCK, POWDER_SNOW, COBWEB, SWEET_BERRY_BUSH, WITHER_ROSE, POINTED_DRIPSTONE, CAMPFIRE, SOUL_CAMPFIRE

**Result: [PASS]** if the location is safe and `safe: true`.
**Result: [FAIL]** if coordinates are invalid, world is missing, Y is out of bounds, or blocks are unsafe.
**Result: [WARN]** if the chunk is not loaded or the location is expected unsafe.
**Result: [INFO]** if the location is safe but marked `safe: false`.

Notable: LaunchGuard does not teleport players, modify blocks, or force-load chunks.

## Permission Nodes

Checks permission node registration.

For `checks.permissions.nodes.shouldExist`:
- Checks if each node is known to the server's permission system via `PluginManager.getPermission(node)`
- **Result: [PASS]** if registered
- **Result: [WARN]** if not found

For `checks.permissions.nodes.dangerous`:
- Checks if each potentially dangerous node is registered
- **Result: [WARN]** if registered (prompts review)
- Nodes that are not registered produce no result

Notable: LaunchGuard does not integrate with LuckPerms. It only checks globally registered permission nodes. Group-level assignments are not inspected in Lite.

## Plugin Inventory

Note: The plugin inventory preflight section is part of unreleased v0.2.0-SNAPSHOT until v0.2.0 is published.

The optional plugin inventory check is disabled by default:

```yaml
checks:
  pluginInventory:
    enabled: false
    checkDependencies: true
    warnOnSoftDependencyMissing: true
```

When enabled, `/launchguard run` includes a concise plugin metadata and dependency visibility section. It does not dump every plugin in the preflight report.

The preflight section reports:
- Total plugin count
- Enabled plugin count
- Disabled plugin count
- Missing hard dependencies from visible plugin metadata
- Missing soft dependencies from visible plugin metadata

**Result: [PASS]** if dependency visibility is enabled and no missing dependencies are reported.
**Result: [INFO]** for inventory counts or missing soft dependencies when `warnOnSoftDependencyMissing: false`.
**Result: [WARN]** for missing soft dependencies when `warnOnSoftDependencyMissing: true`.
**Result: [FAIL]** for missing hard dependencies.

Notable: LaunchGuard reads plugin metadata only. It does not execute plugin commands, reload plugins, enable plugins, disable plugins, download plugins, or change server state.

Plugin inventory does not verify that each plugin is correctly configured. It reports installed plugin metadata, enabled state, and dependency visibility.
