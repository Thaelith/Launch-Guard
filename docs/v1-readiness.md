# v1.0 Readiness

## Current Stable Marketplace Version
- v0.5.0 remains the stable marketplace version until v1.0.0 is published.

## Status
- v1.0.0 release preparation is in progress on branch `release/v1.0.0-prep`
- v1.0.0 has NOT been tagged, released, or published to marketplaces yet

## Development Features Since v0.5.0
- v0.6: Baseline drift detection
- v0.7: Baseline report save, history, JSON/HTML export integration
- v0.8: Optional startup baseline comparison
- v0.9: Stabilization, QA, docs, compatibility, and safety polish

## Required Before v1.0
- Full build passes with Java 17
- Paper 1.20.4 tested with Java 17
- Paper 1.20.6 tested with Java 21
- Paper 1.21.1 tested with Java 21
- Paper 1.21.8 tested with Java 21
- Existing commands regression tested
- Config validation tested
- Report/export retention tested
- Baseline save/list/compare/delete tested
- Baseline report/export/history tested
- Startup baseline compare tested with disabled, no-drift, drift, missing baseline, corrupt baseline, invalid name, save disabled, and retention cases
- Safety audit completed
- Marketplace text updated
- GitHub release notes prepared
- SHA256SUMS prepared

## Non-Goals for v1.0
- No web dashboard
- No Discord/webhooks
- No network uploads
- No analytics
- No automatic plugin installation or modification
- No full plugin runtime behavior testing

## Marketplace Release Rule
- Do not publish development snapshots.
- Publish v1.0.0 only after the release checklist passes.
- Publish a v0.5.x hotfix only if a critical issue affects the current marketplace stable release.
