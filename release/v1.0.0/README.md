# LaunchGuard v1.0.0

Release package for LaunchGuard v1.0.0.

## Files

- `LaunchGuard-1.0.0.jar` — plugin JAR
- `SHA256SUMS.txt` — SHA-256 checksums
- `CHANGELOG.md` — release notes
- `marketplace-overview.md` — marketplace page content
- `marketplace-changelog.md` — marketplace changelog
- `release-checklist.md` — pre-release verification

## Installation

1. Place `LaunchGuard-1.0.0.jar` into your server's `plugins/` folder.
2. Restart the Paper server.
3. Run `/launchguard version` to verify.
4. Run `/launchguard validate` to check configuration.
5. Run `/launchguard run` to run pre-launch checks.

## Compatibility

Fully smoke-tested on:
- Paper 1.20.1 through 1.20.6
- Paper 1.21.1 through 1.21.10

Load-tested only on Paper 1.21.11.

Not available on PaperMC at QA time: 26.1, 26.2. Not tested: 26.3.

Built with Java 17 target compatibility. Use the Java version required by your Paper server version.

## Safety

LaunchGuard is read-only. It does not execute configured commands, modify server state, send network calls, or install, download, enable, disable, or reload other plugins.

## Publication Note

This folder contains v1.0.0 release assets. Do not publish, tag, or create a GitHub Release until explicitly approved.
