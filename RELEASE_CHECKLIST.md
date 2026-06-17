# Release Checklist

## v1.0.0 Release Status

- [x] Version bumped to 1.0.0
- [x] Build completed
- [x] SHA256SUMS generated
- [x] Git tag v1.0.0 created
- [x] GitHub Release published
- [x] Marketplace overview prepared
- [x] Marketplace changelog prepared
- [x] Hangar updated
- [x] Modrinth updated
- [x] SpigotMC updated

## Build Verification
- [x] Run `./gradlew clean build`
- [x] Confirm JAR in `build/libs/LaunchGuard-1.0.0.jar`
- [x] Verify plugin.yml reports version 1.0.0

## Safety Verification
- [x] No commands are executed by checks
- [x] No network calls are made
- [x] No server state is modified
- [x] No files written outside `plugins/LaunchGuard/`

## Documentation
- [x] README up to date
- [x] CHANGELOG up to date
- [x] Compatibility wording accurate
