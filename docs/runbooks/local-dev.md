# Local Development Runbook

## Prerequisites

- JDK 21
- Maven 3.9+
- Optional: Docker

## Minimum Environment

Set at least:

- `IGOR_BOT_USERNAME`
- `IGOR_BOT_TOKEN`

Optional profile control:

- `IGOR_BOT_ACTIVE_PROFILE` (default `dev`)

See full matrix: `docs/reference/env-matrix.md`.

## Local Commands

```powershell
mvn -B -ntp test
mvn -B -ntp spring-boot:run
```

Package jar:

```powershell
mvn -B -ntp clean package
```

## Docker Path

```powershell
docker build -t igorbot:local .
docker run --rm -p 8080:8080 -e IGOR_BOT_USERNAME=... -e IGOR_BOT_TOKEN=... igorbot:local
```

## Common Troubleshooting

- Missing bot env vars -> app fails early at startup.
- Database profile issues -> verify `IGOR_BOT_ACTIVE_PROFILE` and corresponding DB env vars.
- Scheduler behavior mismatch -> inspect timezone assumptions in scheduler parsers/services.
