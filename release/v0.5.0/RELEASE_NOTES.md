# LaunchGuard v0.5.0

LaunchGuard v0.5.0 adds local JSON and HTML exports, plus configuration validation for LaunchGuard config files.

This release includes the export work that was developed after v0.3.0 and was not published separately as v0.4.0.

## Added

- `/launchguard export json`
- `/launchguard export html`
- `/launchguard validate`
- `launchguard.export` permission
- `launchguard.validate` permission
- JSON report files under `plugins/LaunchGuard/exports/`
- Self-contained HTML report files under `plugins/LaunchGuard/exports/`
- Export retention with `settings.exportsToKeep`
- Versioned JSON schema with `schemaVersion: 1`
- Configuration validation for `config.yml`, `checks.yml`, and `messages.yml`

## JSON Export

```text
/launchguard export json
```

Writes a local JSON report under:

```text
plugins/LaunchGuard/exports/
```

JSON exports use `schemaVersion: 1`.

## HTML Export

```text
/launchguard export html
```

Writes a local self-contained HTML report under:

```text
plugins/LaunchGuard/exports/
```

HTML exports do not load external CSS, JavaScript, fonts, images, or CDN resources.

## Configuration Validation

```text
/launchguard validate
```

Validates:

* `config.yml`
* `checks.yml`
* `messages.yml`

Validation can report YAML parse errors, invalid types, missing sections, unknown keys, duplicate entries, empty values, and common configuration mistakes.

Validation does not modify files and does not reload configuration automatically.

## Safety

LaunchGuard remains read-only.

It does not:

* Execute plugin commands
* Execute configured commands
* Install plugins
* Download plugins
* Enable or disable plugins
* Reload plugins
* Teleport players
* Modify blocks
* Modify worlds
* Modify economy data
* Modify permission data
* Change whitelist state
* Send network requests
* Upload data
* Force-load chunks

Exports are written only under `plugins/LaunchGuard/exports/`.

Export retention deletes only `.json` and `.html` files inside the exports directory.

Validation reads LaunchGuard configuration files but does not modify them.

## Limitations

* Export is manual only.
* Validation does not perform cross-file validation.
* Validation does not check actual plugin runtime behavior.
* Validation does not replace `/launchguard run`.
* Message key validation uses a static expected-key list.

## Compatibility

* Tested on Paper 1.20.4 with Java 17
* Tested on Paper 1.20.6 with Java 21
* Tested on Paper 1.21.1 with Java 21
* Tested on Paper 1.21.8 with Java 21
* Built with Java 17 target compatibility
* Use the Java version required by your Paper server version
