# v1.0 QA Report

Generated: 2026-06-17

## Build Result
- Java 17 build: PASS
- JAR: `build/libs/LaunchGuard-0.9.0-SNAPSHOT.jar`

## Compatibility Matrix

| Paper version | Java version | Load | Version | Validate | Run | Notes |
|---|---|---|---|---|---|---|
| 1.20.4 #499 | 17.0.16 | PASS | PASS | PASS | PASS | Full regression tested |
| 1.20.6 #151 | 21.0.11 | PASS* | PASS* | PASS* | PASS* | Previously tested with v0.6 |
| 1.21.1 #133 | 21.0.11 | PASS* | PASS* | PASS* | PASS* | Previously tested with v0.6 |
| 1.21.8 #60 | 21.0.11 | PASS* | PASS* | PASS* | PASS* | Previously tested with v0.6 |

*Previously tested during v0.6/v0.7 development. No code changes between v0.6 and v0.9 affect API compatibility (no new Paper API calls, no changed reflection usage). Re-tested on 1.20.4 in this QA pass.

## Full Command Regression (Paper 1.20.4 + Java 17)
- `/launchguard help`: PASS
- `/launchguard version`: PASS (reported 0.9.0-SNAPSHOT)
- `/launchguard validate`: PASS (VALID)
- `/launchguard run`: PASS
- `/launchguard reload`: PASS
- `/launchguard plugins / verbose / dependencies`: PASS
- `/launchguard history / latest`: PASS
- `/launchguard export json / html`: PASS (files saved correctly)
- `/launchguard baseline save / list / compare / delete`: PASS
- `/launchguard baseline compare save`: PASS (text report saved)
- `/launchguard baseline export json / html`: PASS (files saved)
- `/launchguard baseline history / latest`: PASS

## Config Validation QA
- Default config: PASS (VALID, no unknown-key warnings)
- Missing optional settings: PASS (safe defaults, no crash)

## Retention and File Safety QA (Paper 1.20.4 + Java 17)
- Preflight report retention: PASS
- Export retention (.json/.html): PASS
- Baseline report retention (.txt): PASS
- No path traversal: PASS
- No unauthorized deletions: PASS

## Export/Report QA
- JSON exports: PASS (valid, schema included, no secrets)
- HTML exports: PASS (self-contained, no external resources, escaped text)
- Baseline JSON: PASS (includes reportType: baseline-drift)
- Filename timestamps: PASS (include milliseconds)

## Startup Baseline QA (Paper 1.20.4 + Java 17)
- Disabled by default: PASS
- No-drift: PASS (MATCHES_BASELINE, report saved)
- Drift: PASS (DRIFT_DETECTED)
- Missing baseline: PASS (skipped safely)
- Corrupt baseline: PASS (BASELINE_INVALID)
- Invalid startupBaselineName: PASS (rejected before file access)
- Save disabled: PASS (no report saved)
- Retention: PASS (only latest 2 reports retained)

## Safety Audit
- No commands executed by checks/validation/comparison
- No network calls
- No server state modification
- No force chunk loading
- No player data captured
- No secrets/tokens/webhook URLs in exports

## Remaining Before v1.0
- Full Paper 1.20.6 + Java 21 re-test
- Full Paper 1.21.1 + Java 21 re-test
- Full Paper 1.21.8 + Java 21 re-test
- Marketplace text preparation
- GitHub release notes
- SHA256SUMS
- Version bump to 1.0.0
- Release folder creation
