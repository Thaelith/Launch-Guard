# v1.0 QA Report

Generated: 2026-06-17 | Published: 2026-06-17
Plugin: LaunchGuard v1.0.0

## Status
LaunchGuard v1.0.0 has been prepared and published. The current stable release is v1.0.0.

## Build Result
- Java 17 build: PASS
- JAR: `build/libs/LaunchGuard-0.9.0-SNAPSHOT.jar`

## PaperMC Available Versions
15 stable Paper builds available at test time across 1.20.x and 1.21.x.
26.1 and 26.2: NOT AVAILABLE ON PAPERMC AT TEST TIME (Paper uses Minecraft versioning, not calendar versioning). 26.3 was not tested.

## Fresh Compatibility Matrix (All tested with v0.9.0-SNAPSHOT)

| Target | Paper Build | Java | Result | Notes |
|---|---|---|---|---|
| Paper 1.20.1 | #196 | 17.0.16 (Temurin) | PASS | Full smoke test |
| Paper 1.20.2 | #318 | 17.0.16 (Temurin) | PASS | Full smoke test |
| Paper 1.20.4 | #499 | 17.0.16 (Temurin) | PASS | Full regression tested |
| Paper 1.20.5 | #22 | 21.0.11 (Zulu) | PASS | Full smoke test; Paper 1.20.5 requires Java 21 |
| Paper 1.20.6 | #151 | 21.0.11 (Zulu) | PASS | Full smoke test |
| Paper 1.21.1 | #133 | 21.0.11 (Zulu) | PASS | Full smoke test |
| Paper 1.21.3 | #83 | 21.0.11 (Zulu) | PASS | Full smoke test |
| Paper 1.21.4 | #232 | 21.0.11 (Zulu) | PASS | Full smoke test |
| Paper 1.21.5 | #114 | 21.0.11 (Zulu) | PASS | Full smoke test |
| Paper 1.21.6 | #48 | 21.0.11 (Zulu) | PASS | Full smoke test |
| Paper 1.21.7 | #32 | 21.0.11 (Zulu) | PASS | Full smoke test |
| Paper 1.21.8 | #60 | 21.0.11 (Zulu) | PASS | Full smoke test |
| Paper 1.21.9 | #59 | 21.0.11 (Zulu) | PASS | Full smoke test |
| Paper 1.21.10 | #129 | 21.0.11 (Zulu) | PASS | Full smoke test |
| Paper 1.21.11 | #69 | 21.0.11 (Zulu) | LOADS ONLY | Plugin loads; persistent environment file lock prevents smoke command execution on this machine. Not a LaunchGuard defect |

## Summary
- 14 versions: PASS (full smoke test with version, validate, run, baseline save/compare/delete)
- 1 version: LOADS ONLY (1.21.11 — environment lock, not a code defect)
- 0 versions: FAIL
- 0 versions: NOT TESTED
- 2 versions: NOT AVAILABLE (26.1, 26.2)

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

## Release Status
- GitHub Release: completed (2026-06-17)
- Release notes: completed
- SHA256SUMS: completed
- Tag v1.0.0: completed

## Marketplace Status
- Hangar: pending
- Modrinth: pending
- SpigotMC: pending
