# Permissions

All LaunchGuard permissions default to op-only.

## Permission Nodes

| Permission | Default | Description |
|---|---|---|
| launchguard.use | op | Access to /launchguard command |
| launchguard.run | op | Run pre-launch checks |
| launchguard.reload | op | Reload configuration files |
| launchguard.admin | op | Full admin access (implies run and reload) |

## Granting Permissions

### Via LuckPerms

```
/lp user PlayerName permission set launchguard.use true
/lp user PlayerName permission set launchguard.run true
/lp group Staff permission set launchguard.use true
```

### Via commands.yml (server-level)

Not applicable. Use a permission plugin.

## Permission Behavior

- `launchguard.use` controls access to all subcommands.
- `launchguard.run` is required for `/launchguard run`. Without it, the player receives a permission denied message.
- `launchguard.reload` is required for `/launchguard reload`.
- `launchguard.admin` is a parent permission that includes `launchguard.run` and `launchguard.reload` as children.
- Console always has access to all commands regardless of permission settings.
- Normal players cannot use `/launchguard` by default. This is intentional.

## Security Notes

- Do not grant `launchguard.run` to untrusted players. The report may reveal server configuration details.
- Do not grant `launchguard.reload` to untrusted players. Reloading affects server operation.
- The plugin does not expose any commands that modify server state, so even with full access, users cannot harm the server through LaunchGuard.
