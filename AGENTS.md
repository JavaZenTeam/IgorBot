# IgorBot Agent Instructions

This file is the default onboarding context for AI coding agents in this repository.

## Mission

Deliver safe, reviewable changes with minimal repository exploration and explicit validation.

## Source Of Truth

Always trust these files first:

1. `pom.xml` for versions, dependencies, build plugins.
2. `src/main/resources/application.yml` for runtime profiles and env bindings.
3. `.github/workflows/build_and_publish.yml` for CI expectations.

If older docs conflict with these files, treat docs as stale and update them in the same change.

## Project Landmarks

- Entrypoint: `src/main/java/ru/javazen/telegram/bot/IgorBotApplication.java`
- Core bot: `src/main/java/ru/javazen/telegram/bot/CompositeBot.java`
- Handler chain contracts: `src/main/java/ru/javazen/telegram/bot/handler/base/`
- Handler registration order: `src/main/resources/spring/botConfig.xml`
- Scheduler domain: `src/main/java/ru/javazen/telegram/bot/scheduler/`
- Persistence: `src/main/java/ru/javazen/telegram/bot/model/` and `repository/`
- Web admin and chat views: `src/main/java/ru/javazen/telegram/bot/web/`
- Security: `src/main/java/ru/javazen/telegram/bot/security/`

## Golden Rules

1. Keep changes focused; avoid broad refactors unless explicitly requested.
2. Do not modify secrets, credentials, or token values in tracked files.
3. Do not edit DB migrations retroactively. Add new Flyway migration files instead.
4. Preserve handler ordering semantics from `botConfig.xml` unless the task explicitly requires reordering.
5. When changing behavior, update related docs in `docs/`.

## Trusted Commands

Run from repository root.

```powershell
mvn -B -ntp test
mvn -B -ntp clean package
mvn -B -ntp spring-boot:run
```

Quick full validation:

```powershell
./scripts/verify.ps1
```

On Unix-like environments:

```bash
./scripts/verify.sh
```

## Validation Expectations

- Execute tests for all non-trivial code changes.
- If build or tests are skipped, explain why in PR description.
- Prefer fixing lint/compile issues introduced by the current change before finishing.

## Documentation Map

- Overview and quickstart: `README.md`
- Architecture maps: `docs/architecture/`
- Runbooks: `docs/runbooks/`
- Environment variable matrix: `docs/reference/env-matrix.md`

## Safe Editing Boundaries

- High-risk files (handle carefully): `src/main/resources/application.yml`, `src/main/resources/spring/*.xml`, `src/main/resources/db/migration/**`.
- For scheduler changes, review both parser and service flows before editing.
- For web changes, verify security constraints in `security/` and controller mappings in `web/`.
