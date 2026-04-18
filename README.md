# IgorBot

IgorBot is a Spring Boot Telegram bot with message handlers, scheduler features, and a small web UI for chat statistics.

## Why This Repository Is AI-Ready

This repository is structured so both humans and AI agents can onboard quickly:

- Project-level agent guidance: `AGENTS.md`
- Deep architecture and runbooks: `docs/`
- Cursor-specific behavior: `.cursor/rules/`
- GitHub Copilot-specific behavior: `.github/copilot-instructions.md` and `.github/instructions/`

## Source of Truth

When documentation and code disagree, trust these files:

1. Build/runtime versions: `pom.xml`
2. Profiles and environment variables: `src/main/resources/application.yml`
3. CI behavior: `.github/workflows/build_and_publish.yml`

## Tech Stack

- Java 21
- Spring Boot 3.5.9
- Maven build
- Spring Data JPA + Flyway
- PostgreSQL (production), H2 dependency available
- Telegram Bots API

## Quick Start

### 1) Prerequisites

- JDK 21
- Maven 3.9+ (or Docker)

### 2) Required Environment Variables

At minimum:

- `IGOR_BOT_USERNAME`
- `IGOR_BOT_TOKEN`

For full list, see `docs/reference/env-matrix.md`.

### 3) Run Locally

```powershell
mvn -B -ntp clean test
mvn -B -ntp spring-boot:run
```

Default active profile is `dev` unless `IGOR_BOT_ACTIVE_PROFILE` is set.

### 4) Build Artifact

```powershell
mvn -B -ntp clean package
```

Output: `target/IgorBot.jar`

### 5) Run with Docker

```powershell
docker build -t igorbot:local .
docker run --rm -p 8080:8080 `
  -e IGOR_BOT_USERNAME=... `
  -e IGOR_BOT_TOKEN=... `
  igorbot:local
```

## Repository Map

- Application entrypoint: `src/main/java/ru/javazen/telegram/bot/IgorBotApplication.java`
- XML bot pipeline: `src/main/resources/spring/botConfig.xml`
- Scheduler core: `src/main/java/ru/javazen/telegram/bot/scheduler/service/MessageSchedulerServiceImpl.java`
- Web layer: `src/main/java/ru/javazen/telegram/bot/web/`
- Security layer: `src/main/java/ru/javazen/telegram/bot/security/`

See architecture docs:

- `docs/architecture/project-map.md`
- `docs/architecture/update-flow.md`
- `docs/architecture/scheduler-flow.md`

## Validation

Cross-platform validation scripts:

- PowerShell: `scripts/verify.ps1`
- Bash: `scripts/verify.sh`

These scripts run the same baseline checks as expected from agent-driven contributions.

## Contributing

Before creating a PR:

1. Run `scripts/verify.ps1` (Windows) or `scripts/verify.sh` (Linux/macOS).
2. Update docs if behavior, architecture, or commands changed.
3. Keep changes scoped and avoid unrelated refactors.
