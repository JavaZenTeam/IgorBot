# IgorBot repository instructions for Copilot

Use these instructions for any change in this repository.

## Source of truth

Always prefer:

1. `pom.xml` for versions/dependencies/build plugins
2. `src/main/resources/application.yml` for runtime profiles and env vars
3. `.github/workflows/build_and_publish.yml` for CI requirements

If docs conflict with these files, update docs as part of your change.

## Project layout essentials

- Java sources: `src/main/java/ru/javazen/telegram/bot/`
- Spring XML pipeline: `src/main/resources/spring/`
- DB migrations: `src/main/resources/db/migration/`
- Tests: `src/test/java/`
- Architecture docs and runbooks: `docs/`

## Change rules

- Keep edits scoped to the requested task.
- Do not commit secrets or credential values.
- Do not modify old Flyway migration files; add new migration files instead.
- Treat handler order in `spring/botConfig.xml` as behavior-sensitive.

## Validation

Before finishing non-trivial tasks, run:

```bash
mvn -B -ntp test
mvn -B -ntp clean package
```

Prefer helper scripts:

- `scripts/verify.ps1` (Windows/PowerShell)
- `scripts/verify.sh` (Unix-like)

## Documentation

When behavior or operations change, update relevant files:

- `README.md`
- `AGENTS.md`
- `docs/runbooks/*.md`
- `docs/architecture/*.md`
