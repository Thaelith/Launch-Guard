# Permissions

All LaunchGuard permissions default to op-only.

## Permission Nodes

| Permission | Default | Description |
|---|---|---|
| launchguard.use | op | Access to /launchguard command |
| launchguard.run | op | Run pre-launch checks |
| launchguard.plugins | op | View plugin inventory and dependency reports |
| launchguard.history | op | View saved report history |
| launchguard.export | op | Export reports as JSON or HTML |
| launchguard.validate | op | Validate configuration files |
| launchguard.reload | op | Reload configuration files |
| launchguard.admin | op | Full admin access; includes use, run, plugins, history, export, validate, and reload |

## Granting Permissions

### Via LuckPerms

```
/lp user PlayerName permission set launchguard.use true
/lp user PlayerName permission set launchguard.run true
/lp user PlayerName permission set launchguard.plugins true
/lp group Staff permission set launchguard.use true
```

### Via commands.yml (server-level)

Not applicable. Use a permission plugin.

## Permission Behavior

- `launchguard.use` controls access to all subcommands.
- `launchguard.run` is required for `/launchguard run`. Without it, the player receives a permission denied message.
- `launchguard.plugins` is required for `/launchguard plugins`, `/launchguard plugins verbose`, and `/launchguard plugins dependencies`.
- `launchguard.history` is required for `/launchguard history` and `/launchguard history latest`.
- `launchguard.export` is required for `/launchguard export json` and `/launchguard export html`.
- `launchguard.validate` is required for `/launchguard validate`.
- `launchguard.reload` is required for `/launchguard reload`.
- `launchguard.history` includes `launchguard.use` as a child permission so staff can be granted history access directly.
- `launchguard.export` includes `launchguard.use` as a child permission so staff can be granted export access directly.
- `launchguard.plugins` includes `launchguard.use` as a child permission so staff can be granted inventory access directly.
- `launchguard.admin` is a parent permission that includes `launchguard.use`, `launchguard.run`, `launchguard.plugins`, `launchguard.history`, `launchguard.export`, and `launchguard.reload` as children.
- Console always has access to all commands regardless of permission settings.
- Normal players cannot use `/launchguard` by default. This is intentional.

## Security Notes

- Do not grant `launchguard.run` to untrusted players. The report may reveal server configuration details.
- Do not grant `launchguard.plugins` to untrusted players. The inventory report reveals installed plugin metadata and dependency names.
- Do not grant `launchguard.history` to untrusted players. Saved reports may contain server configuration details.
- Do not grant `launchguard.export` to untrusted players. JSON exports contain server version, plugin metadata, and check results.
- Do not grant `launchguard.reload` to untrusted players. Reloading affects server operation.
- The plugin does not expose any commands that modify server state, so even with full access, users cannot harm the server through LaunchGuard.
