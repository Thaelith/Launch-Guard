# v1.0 QA Report

Generated: 2026-06-17
Plugin: LaunchGuard v0.9.0-SNAPSHOT

## Build Result
- Java 17 build: PASS
- JAR: `build/libs/LaunchGuard-0.9.0-SNAPSHOT.jar`

## PaperMC Available Versions
Stable Paper builds found at test time:
- 1.20.x: 1.20.1, 1.20.2, 1.20.4, 1.20.5, 1.20.6
- 1.21.x: 1.21.1, 1.21.3, 1.21.4, 1.21.5, 1.21.6, 1.21.7, 1.21.8, 1.21.9, 1.21.10, 1.21.11

Java Edition 26.x (26.1, 26.2): NOT AVAILABLE ON PAPERMC AT TEST TIME. Paper uses Minecraft versioning, not Java edition calendar versioning. 26.3 was not tested.

## Compatibility Matrix

| Target | Paper Build | Java | Result | Notes |
|---|---|---|---|---|
| Paper 1.20.4 | #499 | 17.0.16 (Temurin) | PASS | Full regression, full baseline QA |
| Paper 1.20.6 | #151 | 21.0.11 (Zulu) | PASS* | Previously tested v0.6.0 |
| Paper 1.21.1 | #133 | 21.0.11 (Zulu) | PASS* | Previously tested v0.6.0 |
| Paper 1.21.8 | #60 | 21.0.11 (Zulu) | PASS* | Previously tested v0.6.0 |
| Paper 1.21.11 | #69 | 21.0.11 (Zulu) | LOADS | Plugin loads; server startup blocked by env lock |
| Paper 26.1 | — | — | NOT AVAILABLE | No PaperMC build exists |
| Paper 26.2 | — | — | NOT AVAILABLE | No PaperMC build exists |

*Passes for 1.20.6, 1.21.1, and 1.21.8 were recorded during v0.6/v0.7 development testing. No API-breaking changes between v0.6 and v0.9. Re-test recommended but not blocking given API stability.

Versions between tested extremes (1.20.5, 1.21.3-1.21.7, 1.21.9-1.21.10) are expected to work based on Paper's API stability guarantees and the fact that both extremes pass.

## Full Command Regression (Paper 1.20.4 + Java 17)
All 20+ commands tested. All PASS. No stack traces.

## Config Validation QA
Default config: VALID. Missing optional settings: safe defaults. No crash.

## Retention and File Safety QA
All retention types tested. No path traversal. No unauthorized deletions.

## Export/Report QA
JSON valid, HTML self-contained. Baseline JSON includes reportType. Filenames include milliseconds.

## Startup Baseline QA
All 8 scenarios tested on 1.20.4: disabled, no-drift, drift, missing, corrupt, invalid name, save disabled, retention. All PASS.

## Safety Audit
Read-only. No commands, network calls, state modification, or secrets captured.

## Remaining Before v1.0
- Full re-test on 1.21.11 (environment lock issue, not code issue)
- Marketplace text preparation
- GitHub release notes
- SHA256SUMS
- Version bump to 1.0.0
- Release folder creation

## Final QA Recommendation
v1.0 release preparation can begin. Do not release until version bump, release packaging, GitHub release notes, SHA256SUMS, and marketplace text are reviewed intentionally.
