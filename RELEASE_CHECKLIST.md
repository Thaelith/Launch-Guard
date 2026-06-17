# Release Checklist

## v1.0.0 Release Status

- [x] Version bumped to 1.0.0
- [x] Build completed
- [x] SHA256SUMS generated
- [x] Git tag v1.0.0 created
- [x] GitHub Release published
- [x] Marketplace overview prepared
- [x] Marketplace changelog prepared
- [ ] Hangar updated
- [ ] Modrinth updated
- [ ] SpigotMC updated

## Build Verification
- [ ] Run `./gradlew clean build`
- [ ] Confirm JAR in `build/libs/LaunchGuard-1.0.0.jar`
- [ ] Verify plugin.yml reports version 1.0.0

## Safety Verification
- [ ] No commands are executed by checks
- [ ] No network calls are made
- [ ] No server state is modified
- [ ] No files written outside `plugins/LaunchGuard/`

## Documentation
- [ ] README up to date
- [ ] CHANGELOG up to date
- [ ] Compatibility wording accurate
